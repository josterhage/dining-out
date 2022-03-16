package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.CheckoutRepository;
import com.system559.diningout.repository.GuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service("checkoutService")
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final DtoMapper dtoMapper;
    private final EmailSenderService emailSenderService;
    private final GuestRepository guestRepository;
    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    @Value("${STRIPE_SK}")
    private String stripeSk;

    @Autowired
    public CheckoutService(CheckoutRepository checkoutRepository,
                           DtoMapper dtoMapper,
                           EmailSenderService emailSenderService,
                           GuestRepository guestRepository,
                           TicketService ticketService) {
        this.checkoutRepository = checkoutRepository;
        this.dtoMapper = dtoMapper;
        this.emailSenderService = emailSenderService;
        this.guestRepository = guestRepository;
        this.ticketService = ticketService;
    }

    public CheckoutDto getPaymentIntent(Guest guest) throws StripeException {
        List<Guest> guests = new ArrayList<>();
        for(String id : guest.getPartnerIds()) {
            guests.add(guestRepository.findById(id)
                    .orElseThrow(() -> new RecordIdNotFoundException("Guest",id)));
        }

        List<TicketTier> tiers = getTierList(guests);

        long subtotal = getPurchaseSubtotal(tiers);

        long fee = getCardFee(subtotal);

        long total = subtotal + fee;

        Stripe.apiKey = stripeSk;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(total)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build())
                .build();

        String clientSecret = PaymentIntent.create(params).getClientSecret();

        return CheckoutDto.builder()
                .clientSecret(clientSecret)
                .ticketTiers(tiers.stream().map(dtoMapper::ticketTierToDto).collect(Collectors.toList()))
                .fee(fee)
                .total(total)
                .created(checkoutRepository.save(new Checkout(clientSecret, guest)).getCreated())
                .build();
    }

    private long getPurchaseSubtotal(List<TicketTier> tiers) {
        long subtotal = 0L;
        for(TicketTier tier : tiers) {
            subtotal += tier.getPrice();
        }
        return subtotal;
    }

    private long getCardFee(long subtotal) {
        return Math.round(subtotal * 0.029) + 30;
    }

    public Guest confirmPayment(String paymentIntent, String clientSecret)  {
        Checkout checkout = checkoutRepository.findByClientSecret(clientSecret)
                .orElseThrow(() -> new RecordNotFoundException("Checkout", "clientSecret", clientSecret));
        for(String guestId : checkout.getGuest().getPartnerIds()) {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new RecordIdNotFoundException("Guest",guestId));
            guest.setConfirmed(true);
            guestRepository.save(guest);
        }

        List<Ticket> tickets = ticketService.createTicket(checkout.getGuest(),paymentIntent);
        sendEmail(tickets.get(0));

        // maybe if it's here the system will stop throwing a RNFE
        checkoutRepository.delete(checkout);
        return checkout.getGuest();
    }

    public void abortCheckout(String clientSecret) throws StripeException{
        Checkout checkout = checkoutRepository.findByClientSecret(clientSecret)
                .orElseThrow(() -> new RecordNotFoundException("Checkout","clientSecret",clientSecret));

        //cancel the stripe PaymentIntent
        Stripe.apiKey = stripeSk;
        //TODO: Find a way to get the intentId that doesn't rely on the string length not changing
        String intentId = clientSecret.substring(0,27);
        logger.info(intentId);
        PaymentIntent resource = PaymentIntent.retrieve(intentId);
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
        resource.cancel(params);

        for(String guestId : checkout.getGuest().getPartnerIds()) {
            guestRepository.deleteById(guestId);
        }

        checkoutRepository.delete(checkout);
    }

    private void sendEmail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        String address =
                format("%s %s %s",
                        ticket.getGuest().getGrade().getName(),
                        ticket.getGuest().getFirstName(),
                        ticket.getGuest().getLastName());

        message.setTo(ticket.getGuest().getEmail());
        message.setSubject("111th MI Brigade Dining Out Ticket Purchase");
        message.setFrom("dining-out-confirmation-do-not-reply@system559.com");
        message.setText(
                format("%s,\n" +
                                "Thank you for buying your ticket(s) to the 111th MI BDE Dining Out.\n" +
                                "Your confirmation number is %s.\n" +
                                "If you need to cancel please go to https://111th-dining-out.system559.com/cancel/%s.\n" +
                                "Thanks,\n" +
                                "The 111th Dining Out Team",
                        address,
                        ticket.getId(),
                        ticket.getId()));

        emailSenderService.sendEmail(message);
    }

//    @Scheduled(fixedDelay = 900000)
    public void clearStaleCheckouts(){
        List<Checkout> allCheckouts = checkoutRepository.findAll();

        for(Checkout checkout : allCheckouts) {
            if(checkout.isExpired()) {
                try {
                    abortCheckout(checkout.getClientSecret());
                } catch(StripeException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static List<TicketTier> getTierList(List<Guest> guests) {
        List<TicketTier> tiers = new ArrayList<>();
        TicketTier topTier = getTier(guests);
        for(Guest guest : guests) {
            if(guest.getGrade().getName().equals("CIV")) {
                tiers.add(topTier);
            } else {
                tiers.add(guest.getGrade().getTier());
            }
        }

        return tiers;
    }

    public static TicketTier getTier(List<Guest> guests) {
        TicketTier tier = null;
        for(Guest guestIteration : guests) {
            if(Objects.isNull(tier)) {
                tier = guestIteration.getGrade().getTier();
                continue;
            }
            if(guestIteration.getGrade().getTier().getPrice() > tier.getPrice()) {
                tier = guestIteration.getGrade().getTier();
            }
        }
        return tier;
    }
}
