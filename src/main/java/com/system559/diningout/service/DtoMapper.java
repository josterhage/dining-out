package com.system559.diningout.service;

import com.system559.diningout.dto.*;
import com.system559.diningout.exception.*;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("dtoMapperService")
public class DtoMapper {
    private final GradeRepository gradeRepository;
    private final GuestRepository guestRepository;
    private final MealRepository mealRepository;
    private final RequestRepository requestRepository;
    private final TicketRepository ticketRepository;
    private final TicketTierRepository ticketTierRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public DtoMapper(GradeRepository gradeRepository,
                     GuestRepository guestRepository,
                     MealRepository mealRepository,
                     RequestRepository requestRepository,
                     TicketRepository ticketRepository,
                     TicketTierRepository ticketTierRepository,
                     UnitRepository unitRepository) {
        this.gradeRepository = gradeRepository;
        this.guestRepository = guestRepository;
        this.mealRepository = mealRepository;
        this.requestRepository = requestRepository;
        this.ticketRepository = ticketRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.unitRepository = unitRepository;
    }

    public Grade dtoToGrade(GradeDto dto) {
        return Grade.builder()
                .name(dto.getName())
                .tier(ticketTierRepository.findById(dto.getTierId())
                        .orElseThrow(() -> new RecordNotFoundException("TicketTier","id",dto.getTierId())))
                .build();
    }

    public Guest dtoToGuest(GuestDto dto) {
        return Guest.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .grade(gradeRepository.findByName(dto.getGrade())
                        .orElseThrow(() -> new RecordNotFoundException("Grade","name",dto.getGrade())))
                .title(dto.getTitle())
                .meal(mealRepository.findByName(dto.getMeal())
                        .orElseThrow(() -> new RecordNotFoundException("Meal","name",dto.getMeal())))
                .requestText(dto.getRequestText())
                .unit(unitRepository.findByName(dto.getUnit())
                        .orElseThrow(() -> new RecordNotFoundException("Unit","name",dto.getUnit())))
                .email(dto.getEmail())
                .partner(guestRepository.findById(dto.getPartnerId()).orElse(null))
                .isConfirmed(dto.isConfirmed())
                .build();
    }

    public Meal dtoToMeal(MealDto dto) {
        return Meal.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public Request dtoToRequest(RequestDto dto) {
        return Request.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public Ticket dtoToTicket(TicketDto dto) {
        return Ticket.builder()
                .guest(guestRepository.findById(dto.getGuestId())
                        .orElseThrow(() -> new RecordIdNotFoundException("Guest", dto.getGuestId())))
                .tier(ticketTierRepository.findById(dto.getTierId())
                        .orElseThrow(() -> new RecordIdNotFoundException("TicketTier",dto.getTierId())))
                .chargeId(dto.getChargeId())
                .build();
    }

    public TicketTier dtoToTicketTier(TicketTierDto dto) {
        return TicketTier.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }

    public Unit dtoToUnit(UnitDto dto) {
        return Unit.builder()
                .name(dto.getName())
                .pocEmail(dto.getPocEmail())
                .build();
    }
}
