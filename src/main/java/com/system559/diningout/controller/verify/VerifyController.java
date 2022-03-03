package com.system559.diningout.controller.verify;

import com.system559.diningout.model.Ticket;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/verify")
public class VerifyController {
    private final TicketRepository ticketRepository;

    @Autowired
    public VerifyController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/{ticketId}")
    public String verifyTicket(@PathVariable String ticketId, Model model) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if(optionalTicket.isPresent()){
            model.addAttribute("verified","true");
            model.addAttribute("name",TicketService.getPersonFullName(optionalTicket.get().getGuest()));
            model.addAttribute("ticketSerial",optionalTicket.get().getTicketSerial());
        } else {
            model.addAttribute("verified","false");
            model.addAttribute("ticketId",ticketId);
        }
        return "verify";
    }
}
