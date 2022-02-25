package com.system559.diningout.controller.api;

import com.system559.diningout.dto.TicketDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Ticket;
import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.repository.TicketTierRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    private DtoMapper dtoMapper;
    private GuestRepository guestRepository;
    private TicketRepository ticketRepository;
    private TicketTierRepository ticketTierRepository;

    @Autowired
    public TicketController(DtoMapper dtoMapper,
                            GuestRepository guestRepository,
                            TicketRepository ticketRepository,
                            TicketTierRepository ticketTierRepository){
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
        this.ticketRepository = ticketRepository;
        this.ticketTierRepository = ticketTierRepository;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ticket getById(@PathVariable String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Ticket",id));
    }

    @PostMapping("/new")
    public Ticket newTicket(@RequestBody TicketDto dto) {
        return ticketRepository.save(dtoMapper.dtoToTicket(dto));
    }

    @PutMapping("/replace/{id}")
    public Ticket replaceTicket(@PathVariable String id, @RequestBody TicketDto dto) {
        Ticket replacement = dtoMapper.dtoToTicket(dto);
        replacement.setId(id);
        return ticketRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Ticket updateTicket(@PathVariable String id, @PathVariable String field, @PathVariable String value) {
        Ticket update = ticketRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Ticket",id));
        switch(field) {
            case("guest"):
                update.setGuest(guestRepository.findById(value)
                        .orElseThrow(() -> new RecordIdNotFoundException("Guest",value)));
                break;
            case("tier"):
                update.setTier(ticketTierRepository.findById(value)
                        .orElseThrow(() -> new RecordIdNotFoundException("TicketTier",value)));
                break;
            case("chargeId"):
                update.setChargeId(value);
                break;
        }
        return ticketRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteTicket(@PathVariable String id) {
        ticketRepository.deleteById(id);
        return ticketRepository.findById(id).isEmpty();
    }
}
