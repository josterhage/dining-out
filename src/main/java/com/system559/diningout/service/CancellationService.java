package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.CancellationToken;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.CancellationTokenRepository;
import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("cancellationService")
public class CancellationService {
    private final CancellationTokenRepository tokenRepository;
    private final GuestRepository guestRepository;
    private final TicketRepository ticketRepository;

    @Value("${STRIPE_SK}")
    private String stripeSk;

    @Autowired
    public CancellationService(CancellationTokenRepository tokenRepository,
                               GuestRepository guestRepository,
                               TicketRepository ticketRepository) {
        this.tokenRepository = tokenRepository;
        this.guestRepository = guestRepository;
        this.ticketRepository = ticketRepository;
    }

    public String startCancellation(String ticketId) {
        return tokenRepository
                .save(new CancellationToken(ticketRepository.findById(ticketId).orElseThrow(() -> new RecordIdNotFoundException("Ticket",ticketId))))
                        .getToken();
    }

    public void completeCancellation(String tokenId) throws StripeException {
        CancellationToken token = tokenRepository.findByToken(tokenId)
                .orElseThrow(() -> new RecordNotFoundException("CancellationToken","token",tokenId));

        Stripe.apiKey = stripeSk;
        RefundCreateParams params =
                RefundCreateParams.builder().setPaymentIntent(token.getTicket().getPaymentIntent()).build();

        List<Guest> guests = token.getTicket().getGuest().getPartnerIds().stream().map((id) -> guestRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Guest",id))).collect(Collectors.toList());

        for(Guest guest : guests) {
            guestRepository.delete(guest);
            ticketRepository.deleteByGuest(guest);
        }

        Refund.create(params);

        tokenRepository.delete(token);
    }

//    @Scheduled(fixedDelay = 1800000)
    public void clearStaleCancellations() {
        List<CancellationToken> tokens = tokenRepository.findAll();

        for(CancellationToken token : tokens) {
            if(token.isExpired()) {
                tokenRepository.delete(token);
            }
        }
    }
}
