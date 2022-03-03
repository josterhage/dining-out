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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("rsvpService")
public class RsvpService {
    private final CancellationService cancellationService;
    private final CheckoutService checkoutService;
    private final ConfirmationService confirmationService;
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;
    private final MealRepository mealRepository;
    private final GradeRepository gradeRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public RsvpService(CancellationService cancellationService,
                       CheckoutService checkoutService,
                       ConfirmationService confirmationService,
                       DtoMapper dtoMapper,
                       GuestRepository guestRepository,
                       MealRepository mealRepository,
                       GradeRepository gradeRepository,
                       UnitRepository unitRepository) {
        this.cancellationService = cancellationService;
        this.checkoutService = checkoutService;
        this.confirmationService = confirmationService;
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
        this.mealRepository = mealRepository;
        this.gradeRepository = gradeRepository;
        this.unitRepository = unitRepository;
    }

    public CheckoutDto startRsvp(List<GuestDto> guests) throws StripeException {
        Guest primary = createRsvp(guests.get(0));
        if(guests.size() == 2) {
            guests.get(1).setPartnerId(primary.getId());
            Guest secondary = createRsvp(guests.get(1));
            primary.setPartner(secondary);
            primary = guestRepository.save(primary);
        }

        return checkoutService.getPaymentIntent(primary);
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
                .orElseThrow(() -> new RecordNameNotFoundException("Grade",dtos.get(0).getGrade()));
        Grade gradeTwo = gradeRepository.findByName(dtos.get(1).getGrade())
                .orElseThrow(() -> new RecordNameNotFoundException("Grade",dtos.get(1).getGrade()));

        return gradeOne.getTier().getPrice() > gradeTwo.getTier().getPrice() ? gradeOne.getTier() : gradeTwo.getTier();
    }
}
