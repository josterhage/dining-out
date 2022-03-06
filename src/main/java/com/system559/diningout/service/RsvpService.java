package com.system559.diningout.service;

import com.stripe.exception.StripeException;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("rsvpService")
public class RsvpService {
    private final CheckoutService checkoutService;
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;

    @Autowired
    public RsvpService(CheckoutService checkoutService,
                       DtoMapper dtoMapper,
                       GuestRepository guestRepository) {
        this.checkoutService = checkoutService;
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
    }

    public CheckoutDto startRsvp(List<GuestDto> dtos) throws StripeException {
        List<Guest> guests = new ArrayList<>();
        for (GuestDto dto : dtos) {
            guests.add(dtoMapper.dtoToGuest(dto));
        }

        List<String> partnerIds = new ArrayList<>();

        for (Guest guest : guests) {
            guestRepository.save(guest);
            partnerIds.add(guest.getId());
            guest.setPartnerIds(partnerIds);
        }

        guestRepository.saveAll(guests);

        return checkoutService.getPaymentIntent(guests.get(0));
    }

    public void abortRsvp(String clientSecret) throws StripeException {
        checkoutService.abortCheckout(clientSecret);
    }
}
