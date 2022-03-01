package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Ticket;
import com.system559.diningout.model.TicketTier;
import com.system559.diningout.repository.CancellationTokenRepository;
import com.system559.diningout.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service("ticketService")
public class TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(Guest guest, String paymentIntent) {
        Ticket primary = Ticket.builder()
                .guest(guest)
                .paymentIntent(paymentIntent)
                .build();

        if(!Objects.isNull(guest.getPartner())){
            Ticket secondary = Ticket.builder()
                    .guest(guest.getPartner())
                    .paymentIntent(paymentIntent)
                    .build();
            ticketRepository.save(secondary);
        }

        return ticketRepository.save(primary);
    }
}
