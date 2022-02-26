package com.system559.diningout.service;

import com.system559.diningout.exception.TokenExpiredException;
import com.system559.diningout.model.ConfirmationToken;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.ConfirmationTokenRepository;
//import com.system559.diningout.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service("confirmationService")
public class ConfirmationService {
    private final ConfirmationTokenRepository tokenRepository;
    private final EmailSenderService emailSenderService;
    //    private final GuestRepository guestRepository;
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @Autowired
    public ConfirmationService(ConfirmationTokenRepository tokenRepository,
                               EmailSenderService emailSenderService/*,
                               GuestRepository guestRepository*/) {
        this.tokenRepository = tokenRepository;
        this.emailSenderService = emailSenderService;
//        this.guestRepository = guestRepository;
    }

    public String sendToken(Guest guest) {
        ConfirmationToken token = new ConfirmationToken(guest);
        tokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(guest.getEmail());
        message.setSubject("111th MI Brigade Dining Out RSVP Confirmation");
        message.setFrom("dining-out-confirmation-do-not-reply@system559.com");
        message.setText(
                format("To confirm your RSVP, please click here: %s/api/public/rsvp/confirm/%s",
                        appHost, token.getToken()));

        emailSenderService.sendEmail(message);

        return token.getToken();
    }

    public Optional<Guest> getGuestFromToken(String token) throws TokenExpiredException {
        Optional<ConfirmationToken> optionalToken = tokenRepository.findByToken(token);

        if (!optionalToken.isPresent()) {
            return Optional.empty();
        }

        ConfirmationToken confirmationToken = optionalToken.get();

        if(confirmationToken.isExpired()) {
            throw new TokenExpiredException(confirmationToken.getToken());
        }

        tokenRepository.delete(confirmationToken);

        return Optional.of(confirmationToken.getGuest());
    }
}
