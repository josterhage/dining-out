package com.system559.diningout.service;

import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.exception.*;
import com.system559.diningout.model.Guest;
import com.system559.diningout.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service("rsvpService")
public class RsvpService {
    private final ConfirmationService confirmationService;
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;
    private final MealRepository mealRepository;
    private final RequestRepository requestRepository;
    private final GradeRepository gradeRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public RsvpService(ConfirmationService confirmationService,
                       DtoMapper dtoMapper,
                       GuestRepository guestRepository,
                       MealRepository mealRepository,
                       RequestRepository requestRepository,
                       GradeRepository gradeRepository,
                       UnitRepository unitRepository) {
        this.confirmationService = confirmationService;
        this.dtoMapper = dtoMapper;
        this.guestRepository=guestRepository;
        this.mealRepository=mealRepository;
        this.requestRepository=requestRepository;
        this.gradeRepository = gradeRepository;
        this.unitRepository=unitRepository;
    }

    public Guest createRsvp(GuestDto dto)
            throws RecordNotFoundException {
        //validate entries
        validate(dto);

        dto.setConfirmed(false);

        //create guest entity
        Guest guest = dtoMapper.dtoToGuest(dto);

        guest = guestRepository.save(guest);

        //TODO: send confirmation email
        confirmationService.sendToken(guest);

        //save to data store
        return guest;
    }

    public Guest createRsvp(GuestDto primary, GuestDto secondary) {
        validate(primary);
        validate(secondary);

        primary.setConfirmed(false);
        //the secondary guest is controlled by the primary
        secondary.setEmail(primary.getEmail());
        secondary.setConfirmed(false);

        Guest primaryGuest = guestRepository.save(dtoMapper.dtoToGuest(primary));
        Guest secondaryGuest = guestRepository.save(dtoMapper.dtoToGuest(secondary));

        confirmationService.sendToken(primaryGuest);

        primaryGuest.setPartner(secondaryGuest);
        secondaryGuest.setPartner(primaryGuest);

        guestRepository.save(secondaryGuest);

        return primaryGuest;
    }

    public Optional<Guest> confirm(String token) throws TokenExpiredException {
        Optional<Guest> optionalGuest = confirmationService.getGuestFromToken(token);

        if(!optionalGuest.isPresent()) {
            return Optional.empty();
        }

        //the guest from the token is assumed to be valid
        Guest guest = optionalGuest.get();

        guest.setConfirmed(true);

        if(!Objects.isNull(guest.getPartner())) {
            Guest partner = guest.getPartner();
            partner.setConfirmed(true);
            guestRepository.save(partner);
        }

        return Optional.of(guest);
    }


    //TODO: Refactor to use String guestId
    /*public boolean cancel(GuestDto dto) {
        boolean noPartner = true;
        if(dto.getPartnerId() != null && !dto.getPartnerId().equals("")) {
            guestRepository.deleteById(dto.getPartnerId());
            noPartner = guestRepository.findById(dto.getPartnerId()).isEmpty();
        }
        guestRepository.deleteById(dto.getId());
        return guestRepository.findById(dto.getId()).isEmpty() && noPartner;
    }*/

    private void validate(GuestDto dto) {
        gradeRepository.findByName(dto.getTitle())
                .orElseThrow(() -> new RecordNotFoundException("Grade","name",dto.getTitle()));

        mealRepository.findByName(dto.getMeal())
                .orElseThrow(() -> new RecordNotFoundException("Meal","name",dto.getMeal()));

        for(String request : dto.getRequests()) {
            requestRepository.findRequestByName(request)
                    .orElseThrow(() -> new RecordNotFoundException("Recquest","name",request));
        }

        unitRepository.findByName(dto.getUnit())
                .orElseThrow(() -> new RecordNotFoundException("Unit","name",dto.getUnit()));
    }
}
