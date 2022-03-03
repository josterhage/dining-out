package com.system559.diningout.service;

import com.google.zxing.WriterException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.CancellationTokenRepository;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.repository.TicketSerialRepository;
import com.system559.diningout.util.TicketGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Service("ticketService")
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketSerialRepository ticketSerialRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         TicketSerialRepository ticketSerialRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketSerialRepository = ticketSerialRepository;
    }

    public Ticket createTicket(Guest guest, String paymentIntent) throws IOException, WriterException {
        TicketTier tier = determineTier(guest);

        Ticket primary = Ticket.builder()
                .guest(guest)
                .paymentIntent(paymentIntent)
                .ticketSerial(nextSerial())
                .ticketTier(tier)
                .build();


        if (!Objects.isNull(guest.getPartner())) {
            Ticket secondary = Ticket.builder()
                    .guest(guest.getPartner())
                    .paymentIntent(paymentIntent)
                    .ticketSerial(nextSerial())
                    .ticketTier(tier)
                    .build();
            ticketRepository.save(secondary);
        }

        return ticketRepository.save(primary);
    }

    public InputStreamSource createPdf(Ticket ticket) throws IOException, WriterException {
        TicketGenerator generator = new TicketGenerator();
        generator.getTickets().add(getTicketData(ticket));

        if(!Objects.isNull(ticket.getGuest().getPartner())) {
            generator.getTickets().add(getTicketData(ticketRepository.findByGuest(ticket.getGuest().getPartner())
                    .orElseThrow(() -> new RecordNotFoundException("Ticket","guestId",ticket.getGuest().getPartner().getId()))));
        }

        generator.buildPdf();

        return generator.getInputStream();
    }

    private TicketGenerator.TicketData getTicketData(Ticket ticket) {
        return new TicketGenerator.TicketData(ticket.getTicketSerial(), getPersonAddress(ticket.getGuest())
                + " " + ticket.getGuest().getFirstName()
                + " " + ticket.getGuest().getLastName(),
                ticket.getId());
    }

    private long nextSerial() {
        TicketSerial serial = ticketSerialRepository.findAll().get(0);
        long next = serial.getNextTicketSerial();
        serial.setNextTicketSerial(next + 1);
        ticketSerialRepository.save(serial);
        return next;
    }

    private TicketTier determineTier(Guest guest) {
        TicketTier tier = guest.getGrade().getTier();
        if (!Objects.isNull(guest.getPartner())) {
            TicketTier tier2 = guest.getPartner().getGrade().getTier();
            tier = tier2.getPrice() > tier.getPrice() ? tier2 : tier;
        }

        return tier;
    }

    public static String getPersonAddress(Guest guest) {
        for (String grade : Grade.civilianGrades) {
            if (grade.equals(guest.getGrade().getName())) {
                return guest.getSalute().getName();
            }
        }
        return guest.getGrade().getName();
    }

    public static String getPersonFullName(Guest guest) {
        return new String(getPersonAddress(guest) + " " + guest.getFirstName() + " " + guest.getLastName());
    }
}
