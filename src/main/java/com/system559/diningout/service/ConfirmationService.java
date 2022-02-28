package com.system559.diningout.service;

import com.system559.diningout.exception.RecordNotFoundException;
import com.system559.diningout.exception.TokenExpiredException;
import com.system559.diningout.model.ConfirmationToken;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.ConfirmationTokenRepository;
//import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.lang.String.format;

@Service("confirmationService")
public class ConfirmationService {
    private final ConfirmationTokenRepository tokenRepository;
    private final EmailSenderService emailSenderService;
    private final GuestRepository guestRepository;
    //    private final GuestRepository guestRepository;
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @Autowired
    public ConfirmationService(ConfirmationTokenRepository tokenRepository,
                               EmailSenderService emailSenderService,
                               GuestRepository guestRepository) {
        this.tokenRepository = tokenRepository;
        this.emailSenderService = emailSenderService;
        this.guestRepository = guestRepository;
    }

    public String createToken(Guest guest) {
        ConfirmationToken token = new ConfirmationToken(guest);
        tokenRepository.save(token);
        return  token.getToken();
    }

    public ConfirmationToken sendToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RecordNotFoundException("ConfirmationToken","token",token));

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(confirmationToken.getGuest().getEmail());
        message.setSubject("111th MI Brigade Dining Out RSVP Confirmation");
        message.setFrom("dining-out-confirmation-do-not-reply@system559.com");
        //TODO: Confirm confirmation url
        message.setText(
                format("To confirm your RSVP, please click here: %s/confirmation/confirm/%s",
                        appHost, confirmationToken.getToken()));

        emailSenderService.sendEmail(message);

        return confirmationToken;
    }

    public Guest confirmGuest(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RecordNotFoundException("ConfirmationToken", "token", token));

        Guest guest = confirmationToken.getGuest();

        guest.setConfirmed(true);

        tokenRepository.delete(confirmationToken);

        return guestRepository.save(guest);
    }

    @Scheduled(fixedRate = 450000)
    public void clearExpired() {
        List<ConfirmationToken> tokens = tokenRepository.findAll();

        for(ConfirmationToken token : tokens) {
            if(token.isExpired()) {
                if(!Objects.isNull(token.getGuest().getPartner())) {
                    guestRepository.delete(token.getGuest().getPartner());
                }
                guestRepository.delete(token.getGuest());
                tokenRepository.delete(token);
            }
        }
    }
}
