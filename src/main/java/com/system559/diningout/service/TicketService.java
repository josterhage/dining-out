package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.system559.diningout.model.TicketTier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ticketService")
@Transactional
public class TicketService {
    @Value("${APPLICATION_HOST}")
    private String appHost;
    @Value("${STRIPE_SK}")
    private String stripeSk;

    SessionCreateParams.LineItem.PriceData createPriceData(TicketTier tier) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount(tier.getPrice())
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(tier.getName())
                                .build())
                .build();
    }

    SessionCreateParams.LineItem createSessionLineItem(TicketTier tier, int quantity) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(tier))
                .setQuantity((long)quantity)
                .build();
    }

    public Session createSession(TicketTier tier, int quantity) throws StripeException {
        String successUrl = appHost + "/checkout/success";
        String failedUrl = appHost + "/checkout/failed";

        Stripe.apiKey = stripeSk;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failedUrl)
                .addLineItem(createSessionLineItem(tier,quantity))
                .setSuccessUrl(successUrl)
                .build();

        return Session.create(params);
    }
}
