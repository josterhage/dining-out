package com.system559.diningout.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.system559.diningout.dto.CheckoutDto;
import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.exception.*;
import com.system559.diningout.model.Grade;
import com.system559.diningout.model.Guest;
import com.system559.diningout.model.TicketTier;
import com.system559.diningout.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("rsvpService")
public class RsvpService {
    private final CancellationService cancellationService;
    private final CheckoutService checkoutService;
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;
    private final MealRepository mealRepository;
    private final GradeRepository gradeRepository;
    private final UnitRepository unitRepository;
    private final Logger logger = LoggerFactory.getLogger(RsvpService.class);

    @Autowired
    public RsvpService(CancellationService cancellationService,
                       CheckoutService checkoutService,
                       DtoMapper dtoMapper,
                       GuestRepository guestRepository,
                       MealRepository mealRepository,
                       GradeRepository gradeRepository,
                       UnitRepository unitRepository) {
        this.cancellationService = cancellationService;
        this.checkoutService = checkoutService;
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
        this.mealRepository = mealRepository;
        this.gradeRepository = gradeRepository;
        this.unitRepository = unitRepository;
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

    public Guest createRsvp(GuestDto dto)
            throws RecordNotFoundException {
        //validate entries
        validate(dto);

        dto.setConfirmed(false);

        //create guest entity
        Guest guest = dtoMapper.dtoToGuest(dto);

        guest = guestRepository.save(guest);

        return guest;
    }

    public void abortRsvp(String clientSecret) throws StripeException {
        checkoutService.abortCheckout(clientSecret);
    }

    private void validate(GuestDto dto) {
        gradeRepository.findByName(dto.getGrade())
                .orElseThrow(() -> new RecordNotFoundException("Grade", "name", dto.getGrade()));

        mealRepository.findByName(dto.getMeal())
                .orElseThrow(() -> new RecordNotFoundException("Meal", "name", dto.getMeal()));

        unitRepository.findByName(dto.getUnit())
                .orElseThrow(() -> new RecordNotFoundException("Unit", "name", dto.getUnit()));
    }

    private TicketTier getTopTier(List<GuestDto> dtos) {
        Grade gradeOne = gradeRepository.findByName(dtos.get(0).getGrade())
                .orElseThrow(() -> new RecordNameNotFoundException("Grade", dtos.get(0).getGrade()));
        Grade gradeTwo = gradeRepository.findByName(dtos.get(1).getGrade())
                .orElseThrow(() -> new RecordNameNotFoundException("Grade", dtos.get(1).getGrade()));

        return gradeOne.getTier().getPrice() > gradeTwo.getTier().getPrice() ? gradeOne.getTier() : gradeTwo.getTier();
    }
}
