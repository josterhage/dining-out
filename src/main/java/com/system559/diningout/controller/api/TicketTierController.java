package com.system559.diningout.controller.api;

import com.system559.diningout.dto.TicketTierDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.TicketTier;
import com.system559.diningout.repository.TicketTierRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ticket-tier")
public class TicketTierController {
    private final DtoMapper dtoMapper;
    private final TicketTierRepository ticketTierRepository;

    @Autowired
    public TicketTierController(DtoMapper dtoMapper,
                                TicketTierRepository ticketTierRepository){
        this.dtoMapper =dtoMapper;
        this.ticketTierRepository=ticketTierRepository;
    }

    @GetMapping
    public List<TicketTier> getAll() {
        return ticketTierRepository.findAll();
    }

    @GetMapping("/{id}")
    public TicketTier getById(@PathVariable String id){
        return ticketTierRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("TicketTier",id));
    }

    @PostMapping("/new")
    public TicketTier newTicketTier(@RequestBody TicketTierDto dto) {
        return ticketTierRepository.save(dtoMapper.dtoToTicketTier(dto));
    }

    @PostMapping("/bulk-add")
    public List<TicketTier> newTicketTiers(@RequestBody List<TicketTierDto> dtos) {
        List<TicketTier> ticketTiers = new ArrayList<>();
        for(TicketTierDto dto : dtos) {
            ticketTiers.add(ticketTierRepository.save(dtoMapper.dtoToTicketTier(dto)));
        }
        return ticketTiers;
    }

    @PutMapping("/replace/{id}")
    public TicketTier replaceTicketTier(@PathVariable String id, @RequestBody TicketTierDto dto) {
        TicketTier replacement = dtoMapper.dtoToTicketTier(dto);
        replacement.setId(id);
        return ticketTierRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public TicketTier updateTicketTier(@PathVariable String id,
                                       @PathVariable String field,
                                       @PathVariable String value) {
        TicketTier update = ticketTierRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("TicketTier",id));

        switch(field){
            case("name"):
                update.setName(value);
                break;
            case("description"):
                update.setDescription(value);
                break;
            case("price"):
                update.setPrice(Integer.parseInt("value"));
                break;
        }

        return ticketTierRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteTicketTier(@PathVariable String id) {
        ticketTierRepository.deleteById(id);
        return !ticketTierRepository.findById(id).isPresent();
    }
}
