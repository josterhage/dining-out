package com.system559.diningout.service;

import com.system559.diningout.exception.TokenExpiredException;
import com.system559.diningout.model.CancellationToken;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.CancellationTokenRepository;
import com.system559.diningout.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service("cancellationService")
public class CancellationService {
    private final CancellationTokenRepository tokenRepository;
    private final EmailSenderService emailSenderService;
    private final GuestRepository guestRepository;
    @Value("${APPLICATION_HOST")
    private String appHost;

    @Autowired
    public CancellationService(CancellationTokenRepository tokenRepository,
                               EmailSenderService emailSenderService,
                               GuestRepository guestRepository) {
        this.tokenRepository = tokenRepository;
        this.emailSenderService = emailSenderService;
        this.guestRepository = guestRepository;
    }

    public String sendToken(Guest guest) {
        CancellationToken token = new CancellationToken(guest);
        tokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(guest.getEmail());
        message.setSubject("111th MI Brigade Dining Out Cancellation Process");
        message.setFrom("dining-out-cancellation-do-not-reply@system559.com");
        message.setText(
                "You are receiving this message because someone requested to cancel your reservation for the 111th MI Brigade Dining out.\n"
                + "If you did not request this action, ignore this message.\n"
                + format("Otherwise, please follow this link to complete your cancellation: %s/api/public/rsvp/cancel/%s",
                        appHost,token.getToken()));

        emailSenderService.sendEmail(message);

        return token.getToken();
    }

    public boolean confirmToken(String token) throws TokenExpiredException {
        Optional<CancellationToken> optionalToken = tokenRepository.findByToken(token);

        if(!optionalToken.isPresent()) {
            return false;
        }

        CancellationToken cancellationToken = optionalToken.get();

        if(cancellationToken.isExpired()) {
            throw new TokenExpiredException(cancellationToken.getToken());
        }

        tokenRepository.delete(cancellationToken);

        return true;
    }
}
