package com.system559.diningout.controller.checkout;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.system559.diningout.dto.StripeResponse;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.GuestRepository;
import com.system559.diningout.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final GuestRepository guestRepository;
    private final TicketService ticketService;

    public CheckoutController(GuestRepository guestRepository,
                              TicketService ticketService) {
        this.guestRepository=guestRepository;
        this.ticketService=ticketService;
    }

    @PostMapping("/create-checkout-session/{guestId}")
    public RedirectView checkoutSession(@PathVariable String guestId) throws StripeException {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RecordIdNotFoundException("Guest",guestId));
        Session session = ticketService.createSession(guest.getGrade().getTier(),Objects.isNull(guest.getPartner()) ? 1 : 2);
        return new RedirectView(session.getUrl());
    }

//
//    @PostMapping("/create-checkout-session/{guestId}")
//    public ResponseEntity<StripeResponse> checkoutSession(@PathVariable String guestId) throws StripeException {
//        Guest guest = guestRepository.findById(guestId)
//                .orElseThrow(() -> new RecordIdNotFoundException("Guest",guestId));
//        Session session = ticketService.createSession(guest.getGrade().getTier(), Objects.isNull(guest.getPartner()) ? 1 : 2);
//        StripeResponse stripeResponse = new StripeResponse(session.getId());
//        return new ResponseEntity<StripeResponse>(stripeResponse, HttpStatus.OK);
//    }
}
