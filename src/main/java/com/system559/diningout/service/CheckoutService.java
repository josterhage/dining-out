package com.system559.diningout.service;

import com.google.zxing.WriterException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.CheckoutRepository;
import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.util.TicketGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service("checkoutService")
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final EmailSenderService emailSenderService;
    private final GuestRepository guestRepository;
    private final JavaMailSender sender;
    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    @Value("${STRIPE_SK}")
    private String stripeSk;
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @Autowired
    public CheckoutService(CheckoutRepository checkoutRepository,
                           EmailSenderService emailSenderService,
                           GuestRepository guestRepository,
                           JavaMailSender sender,
                           TicketService ticketService) {
        this.checkoutRepository = checkoutRepository;
        this.emailSenderService = emailSenderService;
        this.guestRepository = guestRepository;
        this.sender = sender;
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

    public Guest confirmPayment(String paymentIntent, String clientSecret) throws IOException, WriterException, MessagingException {
        Checkout checkout = checkoutRepository.findByClientSecret(clientSecret)
                .orElseThrow(() -> new RecordNotFoundException("Checkout", "clientSecret", clientSecret));
        checkoutRepository.delete(checkout);
        checkout.getGuest().setConfirmed(true);
        if (!Objects.isNull(checkout.getGuest().getPartner())) {
            checkout.getGuest().getPartner().setConfirmed(true);
            guestRepository.save(checkout.getGuest().getPartner());
        }

        Ticket ticket = ticketService.createTicket(checkout.getGuest(),paymentIntent);
        sendEmail(ticket);

        return guestRepository.save(checkout.getGuest());
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
        PaymentIntent paymentIntent = resource.cancel(params);

        //delete the guest objects
        if(!Objects.isNull(checkout.getGuest().getPartner())) {
            guestRepository.delete(checkout.getGuest().getPartner());
        }

        guestRepository.delete(checkout.getGuest());

        checkoutRepository.delete(checkout);
    }

    private void sendEmail(Ticket ticket) throws MessagingException, IOException, WriterException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setTo(ticket.getGuest().getEmail());
        helper.setFrom("dining-out-confirmation-do-not-reply@system559.com");
        helper.setSubject("111th MI Brigade Dining Out Ticket Purchase");
        helper.setText(TicketService.getPersonAddress(ticket.getGuest()) + "\n" +
                "Thank you for buying your ticket(s) to the 111th MI BDE Dining Out.\n" +
                "A .PDF file with your ticket is attached to this message, and your confirmation number is " + ticket.getId() + "\n" +
                "If you need to cancel please go to " + appHost + "/cancel/" + ticket.getId() + "\n" +
                "Thank you,\n" +
                "The 111th Dining Out Team");
        helper.addAttachment(TicketService.getPersonFullName(ticket.getGuest()).replaceAll("\\s",""),ticketService.createPdf(ticket),"application/pdf");
        sender.send(message);

//        SimpleMailMessage message = new SimpleMailMessage();
//        String address =
//                format("%s %s %s",
//                        ticket.getGuest().getGrade().getName(),
//                        ticket.getGuest().getFirstName(),
//                        ticket.getGuest().getLastName());
//
//        message.setTo(ticket.getGuest().getEmail());
//        message.setSubject("111th MI Brigade Dining Out Ticket Purchase");
//        message.setFrom("dining-out-confirmation-do-not-reply@system559.com");
//        message.setText(
//                format("%s,\n" +
//                                "Thank you for buying your ticket(s) to the 111th MI BDE Dining Out.\n" +
//                                "Your confirmation number is %s.\n" +
//                                "If you need to cancel please go to %s/cancel/%s.\n" +
//                                "Thanks,\n" +
//                                "The 111th Dining Out Team",
//                        address,
//                        ticket.getId(),
//                        appHost,
//                        ticket.getId()));
//
//        emailSenderService.sendEmail(message);
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
