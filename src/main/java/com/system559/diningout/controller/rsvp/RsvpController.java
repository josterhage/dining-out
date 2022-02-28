package com.system559.diningout.controller.rsvp;

import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CheckoutDto startRsvp(@RequestBody List<GuestDto> guests) {
        return rsvpService.startRsvp(guests);
    }
}
