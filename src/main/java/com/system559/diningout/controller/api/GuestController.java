package com.system559.diningout.controller.api;

import com.system559.diningout.dto.GuestDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.exception.RecordNameNotFoundException;
import com.system559.diningout.model.*;
import com.system559.diningout.repository.*;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
public class GuestController {
    private final DtoMapper dtoMapper;
    private final GuestRepository guestRepository;
    private final GradeRepository gradeRepository;
    private final MealRepository mealRepository;
    private final RequestRepository requestRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public GuestController(DtoMapper dtoMapper,
                           GuestRepository guestRepository,
                           GradeRepository gradeRepository,
                           MealRepository mealRepository,
                           RequestRepository requestRepository,
                           UnitRepository unitRepository) {
        this.dtoMapper = dtoMapper;
        this.guestRepository = guestRepository;
        this.gradeRepository = gradeRepository;
        this.mealRepository = mealRepository;
        this.requestRepository = requestRepository;
        this.unitRepository = unitRepository;
    }

    @GetMapping
    public List<Guest> getAll() {
        return guestRepository.findAll();
    }

    @GetMapping("/{id}")
    public Guest getById(@PathVariable String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Guest",id));
    }

    @PostMapping("/new")
    public Guest newGuest(@RequestBody GuestDto dto) {
        return guestRepository.save(dtoMapper.dtoToGuest(dto));
    }

    @PutMapping("/replace/{id}")
    public Guest replaceGuest(@PathVariable String id, @RequestBody GuestDto dto) {
        Guest replacement = dtoMapper.dtoToGuest(dto);
        replacement.setId(id);
        return guestRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Guest updateGuest(@PathVariable String id, @PathVariable String field, @PathVariable String value) {
        Guest update = guestRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Guest",id));

        switch (field) {
            case ("firstName"):
                update.setFirstName(value);
                break;
            case ("lastName"):
                update.setLastName(value);
                break;
            case ("grade"):
                Grade grade = gradeRepository.findByName(value)
                        .orElseThrow(() -> new RecordNameNotFoundException("Grade",value));
                update.setGrade(grade);
                break;
            case("meal"):
                Meal meal = mealRepository.findByName(value)
                        .orElseThrow(() -> new RecordNameNotFoundException("Meal",value));
                update.setMeal(meal);
                break;
            case("requestText"):
                update.setRequestText(value);
                break;
            case("unit"):
                Unit unit = unitRepository.findByName(value)
                        .orElseThrow(()->new RecordNameNotFoundException("Unit",value));
                update.setUnit(unit);
                break;
            case("email"):
                update.setEmail(value);
                break;
            case("partner"):
                Guest partner = guestRepository.findById(value)
                        .orElseThrow(() -> new RecordIdNotFoundException("Guest",value));
                update.setPartner(partner);
                break;
            case("isConfirmed"):
                update.setConfirmed(Boolean.parseBoolean(value));
                break;
        }

        return guestRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteGuest(@PathVariable String id) {
        guestRepository.deleteById(id);
        return !guestRepository.findById(id).isPresent();
    }
}
