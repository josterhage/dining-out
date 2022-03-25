package com.system559.diningout.service;

import com.google.zxing.WriterException;
import com.system559.diningout.model.Ticket;
import com.system559.diningout.repository.TicketRepository;
import com.system559.diningout.util.TicketGenerator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service("ticketPdfService")
public class TicketPdfService {
    private final JavaMailSender javaMailSender;
    private final TicketRepository ticketRepository;
    private final Logger logger = LoggerFactory.getLogger(TicketPdfService.class);

    @Autowired
    public TicketPdfService(JavaMailSender javaMailSender,
                            TicketRepository ticketRepository) {
        this.javaMailSender = javaMailSender;
        this.ticketRepository = ticketRepository;
    }

    public PDDocument getTickets(String emailAddress) throws IOException, WriterException {
        TicketGenerator ticketGenerator = new TicketGenerator();
        return ticketGenerator.createTickets(getTicketsForEmailAddress(emailAddress));
    }

    public void sendTicketEmail(String emailAddress) throws IOException, WriterException, MessagingException {
        PDDocument document = getTickets(emailAddress);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setTo(emailAddress);
        helper.setBcc("josterhage@system559.com");
        helper.setFrom("dining-out-confirmation-do-not-reply@system559.com");
        helper.setSubject("111th MI BDE Leaders Forum and Dining Out Ticket(s)");
        helper.setText("Leaders Forum and Dining out Guest,\n" +
                "Attached is/are your ticket(s) to the 111th MI BDE Leaders Forum and dining out.\n" +
                "You may print them or show them using your smartphone's PDF reader app.\n" +
                "Each ticket has a QR code with the Task Force Phoenix phone number. If you need a ride after the event, scan the code or tap it from your smartphone's PDF reader app to call Task Force Phoenix for a ride home.\n\n" +
                "Thank you,\n" +
                "The 111th Leaders Forum and Dining Out team");
        helper.addAttachment(getFileName(emailAddress),TicketGenerator.getInputStream(document),"application/pdf");
        logger.info("Sending file " + getFileName(emailAddress) + " to " + emailAddress);
        javaMailSender.send(message);
    }

    private String getFileName(String emailAddress) {
        List<Ticket> tickets = getTicketsForEmailAddress(emailAddress);
        StringBuilder fileName = new StringBuilder("111thDiningOutTickets");
        for(Ticket ticket : tickets) {
            fileName.append("-");
            fileName.append(ticket.getTicketSerial());
        }
        fileName.append(".pdf");
        return fileName.toString();
    }

    private List<Ticket> getTicketsForEmailAddress(String emailAddress) {
        return ticketRepository.findByGuestEmail(emailAddress);
    }

    public void sendTickets(Set<String> addresses) throws MessagingException, IOException, WriterException {
        for(String address : addresses) {
            sendTicketEmail(address);
        }
    }
}
