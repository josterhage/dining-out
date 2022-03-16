package com.system559.diningout.controller.api;

import com.system559.diningout.dto.AdminTicketView;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Ticket;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin-ticket")
public class AdminTicketController {
    private final TicketRepository ticketRepository;

    @Autowired
    public AdminTicketController(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/byTicket/{ticketId}")
    public AdminTicketView getByTicketId(@PathVariable String ticketId) {
        return build(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RecordIdNotFoundException("Ticket",ticketId)));
    }

    @GetMapping("/byGuest/{guestId}")
    public AdminTicketView getByGuestId(@PathVariable String guestId) {
        return build(ticketRepository.findByGuestId(guestId)
                .orElseThrow(() -> new RecordIdNotFoundException("Guest",guestId)));
    }

    @GetMapping("/")
    public List<AdminTicketView> getAll() {
        return ticketRepository.findAll()
                .stream().map(this::build)
                .collect(Collectors.toList());
    }

    private AdminTicketView build(Ticket ticket) {
        return AdminTicketView.builder()
                .ticketSerial(ticket.getTicketSerial())
                .unit(ticket.getGuest().getUnit().getName())
                .address(TicketService.getPersonAddress(ticket.getGuest()))
                .lastName(ticket.getGuest().getLastName())
                .firstName(ticket.getGuest().getFirstName())
                .meal(ticket.getGuest().getMeal().getName())
                .email(ticket.getGuest().getEmail())
                .requestText(ticket.getGuest().getRequestText()).build();
    }
}
