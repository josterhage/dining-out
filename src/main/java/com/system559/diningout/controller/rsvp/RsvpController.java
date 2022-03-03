package com.system559.diningout.controller.rsvp;

import com.stripe.exception.StripeException;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rsvp")
public class RsvpController {
    private final RsvpService rsvpService;

    @Autowired
    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @PostMapping("/start")
    public CheckoutDto startRsvp(@RequestBody List<GuestDto> guests) throws StripeException {
        return rsvpService.startRsvp(guests);
    }

    @PostMapping("/abort/{clientSecret}")
    public void abortRsvp(@PathVariable String clientSecret) throws StripeException {
        rsvpService.abortRsvp(clientSecret);
    }
}
