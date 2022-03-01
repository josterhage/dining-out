package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.CheckoutRepository;
import com.system559.diningout.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.lang.String.format;

@Service("checkoutService")
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final EmailSenderService emailSenderService;
    private final GuestRepository guestRepository;
    private final TicketService ticketService;

    @Value("${STRIPE_SK}")
    private String stripeSk;
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @Autowired
    public CheckoutService(CheckoutRepository checkoutRepository,
                           EmailSenderService emailSenderService,
                           GuestRepository guestRepository,
                           TicketService ticketService) {
        this.checkoutRepository = checkoutRepository;
        this.emailSenderService = emailSenderService;
        this.guestRepository = guestRepository;
        this.ticketService = ticketService;
    }

    public CheckoutDto getPaymentIntent(Guest guest) throws StripeException {
        TicketTier tier = getTier(guest);
        long price = tier.getPrice();
        int quantity = Objects.isNull(guest.getPartner()) ? 1 : 2;
        long fee = Math.round((price * quantity) * 0.029) + 30;
        long total = (price * quantity) + fee;

        Stripe.apiKey = stripeSk;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(total)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        String clientSecret = PaymentIntent.create(params).getClientSecret();
        return CheckoutDto.builder()
                .clientSecret(clientSecret)
                .tierName(tier.getName())
                .tierPrice(tier.getPrice())
                .quantity(quantity)
                .fee(fee)
                .created(checkoutRepository.save(new Checkout(clientSecret, guest)).getCreated())
                .build();
    }

    public Guest confirmPayment(String paymentIntent, String clientSecret) {
        Checkout checkout = checkoutRepository.findByClientSecret(clientSecret)
                .orElseThrow(() -> new RecordNotFoundException("Checkout", "clientSecret", clientSecret));
        checkoutRepository.delete(checkout);
        checkout.getGuest().setConfirmed(true);
        if (!Objects.isNull(checkout.getGuest().getPartner())) {
            checkout.getGuest().getPartner().setConfirmed(true);
            guestRepository.save(checkout.getGuest().getPartner());
        }

        sendEmail(ticketService.createTicket(checkout.getGuest(),paymentIntent));

        return guestRepository.save(checkout.getGuest());
    }

    public void completeCancel(String cancelToken) {

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
                                "If you need to cancel please go to %s/cancel/%s.\n" +
                                "Thanks,\n" +
                                "The 111th Dining Out Team",
                        address,
                        ticket.getId(),
                        appHost,
                        ticket.getId()));

        emailSenderService.sendEmail(message);
    }

    private TicketTier getTier(Guest guest) {
        TicketTier tier = guest.getGrade().getTier();
        if (!Objects.isNull(guest.getPartner())) {
            TicketTier tier2 = guest.getPartner().getGrade().getTier();
            tier = tier2.getPrice() > tier.getPrice() ? tier2 : tier;
        }
        return tier;
    }
}
