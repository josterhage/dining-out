package com.system559.diningout.service;

import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.repository.SaluteRepository;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.repository.TicketSerialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("ticketService")
public class TicketService {
    private final GuestRepository guestRepository;
    private final TicketRepository ticketRepository;
    private final TicketSerialRepository ticketSerialRepository;
    private final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    public TicketService(GuestRepository guestRepository,
                         TicketRepository ticketRepository,
                         TicketSerialRepository ticketSerialRepository) {
        this.guestRepository = guestRepository;
        this.ticketRepository = ticketRepository;
        this.ticketSerialRepository = ticketSerialRepository;
    }

    public List<Ticket> createTicket(Guest guest, String paymentIntent) {
        List<Guest> guests =
                guest.getPartnerIds().stream().map((id) -> guestRepository.findById(id)
                        .orElseThrow(() -> new RecordIdNotFoundException("Guest", id))).collect(Collectors.toList());
        TicketTier tier = CheckoutService.getTier(guests);

        List<Ticket> tickets = new ArrayList<>();

        for (Guest guestIteration : guests) {
            logger.info(guestIteration.getMeal().getName());
            tickets.add(Ticket.builder()
                    .guest(guestIteration)
                    .paymentIntent(paymentIntent)
                    .ticketSerial(nextSerial())
                    .ticketTier(tier)
                    .build());
        }

        return ticketRepository.saveAll(tickets);
    }

    private long nextSerial() {
        TicketSerial serial = ticketSerialRepository.findAll().get(0);
        long next = serial.getNextTicketSerial();
        serial.setNextTicketSerial(next + 1);
        ticketSerialRepository.save(serial);
        return next;
    }

    public static String getPersonAddress(Guest guest) {
        for (String grade : Grade.civilianGrades) {
            if (grade.equals(guest.getGrade().getName())) {
                // not every guest that should have a salute has one
                return Objects.isNull(guest.getSalute()) ? "" : guest.getSalute().getName();
            }
        }
        return guest.getGrade().getName();
    }

    public static String getPersonFullName(Guest guest) {
        return getPersonAddress(guest) + " " + guest.getFirstName() + " " + guest.getLastName();
    }
}
