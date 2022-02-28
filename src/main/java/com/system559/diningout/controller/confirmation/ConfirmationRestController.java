package com.system559.diningout.controller.confirmation;

import com.system559.diningout.model.Guest;
import com.system559.diningout.service.ConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm")
public class ConfirmationRestController {
    private final ConfirmationService confirmationService;

    @Autowired
    public ConfirmationRestController(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @PostMapping("/send-message/{token}")
    public Guest sendMessage(@PathVariable String token) {
        return confirmationService.sendToken(token).getGuest();
    }
}
