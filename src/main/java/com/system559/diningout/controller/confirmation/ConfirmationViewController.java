package com.system559.diningout.controller.confirmation;

import com.system559.diningout.model.Guest;
import com.system559.diningout.service.ConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/confirmation")
public class ConfirmationViewController {
    private final ConfirmationService confirmationService;

    @Autowired
    public ConfirmationViewController(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @GetMapping("/confirm/{token}")
    public String confirmRsvp(@PathVariable String token, Model model) {
        Guest guest = confirmationService.confirmGuest(token);
        model.addAttribute("confirmed", true);
        model.addAttribute("grade", guest.getGrade().getName());
        model.addAttribute("firstName", guest.getFirstName());
        model.addAttribute("lastName", guest.getLastName());
        return "index";
    }
}
