package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.ConfirmationToken;
import com.system559.diningout.model.Guest;
import com.system559.diningout.model.PendingPayment;
import com.system559.diningout.repository.ConfirmationTokenRepository;
import com.system559.diningout.repository.PendingPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("checkoutService")
public class CheckoutService {
    private final ConfirmationService confirmationService;
    private final ConfirmationTokenRepository tokenRepository;
    private final PendingPaymentRepository pendingPaymentRepository;
    @Value("${STRIPE_SK}")
    private String stripeSk;

    @Autowired
    public CheckoutService(ConfirmationService confirmationService,
                           ConfirmationTokenRepository tokenRepository,
                           PendingPaymentRepository pendingPaymentRepository) {
        this.confirmationService = confirmationService;
        this.tokenRepository = tokenRepository;
        this.pendingPaymentRepository = pendingPaymentRepository;
    }

    public String getPaymentIntent(String token) throws StripeException {
        //get price
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                        .orElseThrow(() -> new RecordNotFoundException("ConfirmationToken", "token", token));

        Guest guest = confirmationToken.getGuest();

        long price = getPrice(guest);
        if (!Objects.isNull(guest.getPartner())) {
            price *= 2;
        }
        price = (long) Math.round(price * 1.029);
        price += 30;

        Stripe.apiKey = stripeSk;

        //create intent params
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(price)
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        String clientSecret = PaymentIntent.create(params).getClientSecret();
        pendingPaymentRepository.save(new PendingPayment(clientSecret,token));
        return clientSecret;
    }

    public Guest confirmPayment(String clientSecret) {
        PendingPayment pendingPayment = pendingPaymentRepository.findByClientSecret(clientSecret)
                .orElseThrow(() -> new RecordNotFoundException("PendingPayment", "clientSecret", clientSecret));

        pendingPaymentRepository.delete(pendingPayment);

        return confirmationService.confirmGuest(pendingPayment.getToken());
    }

    public long getPrice(Guest guest) {
        long price = guest.getGrade().getTier().getPrice();
        if(!Objects.isNull(guest.getPartner())) {
            long price2 = guest.getPartner().getGrade().getTier().getPrice();
            price = Math.max(price2, price);
        }
        return price;
    }
}
