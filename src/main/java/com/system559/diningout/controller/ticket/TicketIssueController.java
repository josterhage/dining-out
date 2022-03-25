package com.system559.diningout.controller.ticket;

import com.google.zxing.WriterException;
import com.system559.diningout.service.TicketPdfService;
import com.system559.diningout.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/ticketIssue")
public class TicketIssueController {
    private final TicketService ticketService;
    private final TicketPdfService ticketPdfService;
    private final Logger logger = LoggerFactory.getLogger(TicketIssueController.class);

    @Autowired
    public TicketIssueController(TicketService ticketService,
                                 TicketPdfService ticketPdfService) {
        this.ticketService = ticketService;
        this.ticketPdfService = ticketPdfService;
    }

    @GetMapping("/test/{email}")
    public String test(@PathVariable String email) throws IOException, WriterException, MessagingException {
        ticketPdfService.sendTicketEmail(email);
        return "OK";
    }

    @GetMapping("/all")
    public String sendAll() throws IOException, WriterException, MessagingException {
        Set<String> addresses = ticketService.getUniqueEmailAddresses();
        ticketPdfService.sendTickets(addresses);
        return "OK";
    }
}
