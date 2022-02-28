package com.system559.diningout.service;

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
    private final ConfirmationService confirmationService;
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;
    private final MealRepository mealRepository;
    private final RequestRepository requestRepository;
    private final GradeRepository gradeRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public RsvpService(CancellationService cancellationService,
                       ConfirmationService confirmationService,
                       DtoMapper dtoMapper,
                       GuestRepository guestRepository,
                       MealRepository mealRepository,
                       RequestRepository requestRepository,
                       GradeRepository gradeRepository,
                       UnitRepository unitRepository) {
        this.cancellationService = cancellationService;
        this.confirmationService = confirmationService;
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
        this.mealRepository = mealRepository;
        this.requestRepository = requestRepository;
        this.gradeRepository = gradeRepository;
        this.unitRepository = unitRepository;
    }

    public CheckoutDto startRsvp(List<GuestDto> guests) {
        Guest primary = createRsvp(guests.get(0));
        if(guests.size() == 2) {
            guests.get(1).setPartnerId(primary.getId());
            Guest secondary = createRsvp(guests.get(1));
            primary.setPartner(secondary);
            primary = guestRepository.save(primary);
        }

        CheckoutDto checkout = new CheckoutDto();

        TicketTier tier = guests.size() == 2 ? getTopTier(guests) : primary.getGrade().getTier();

        checkout.setTier(tier);
        checkout.setQuantity(guests.size());
        checkout.setToken(confirmationService.createToken(primary));

        return checkout;
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
