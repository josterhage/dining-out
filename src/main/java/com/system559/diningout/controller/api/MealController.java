package com.system559.diningout.controller.api;

import com.system559.diningout.dto.MealDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Meal;
import com.system559.diningout.repository.MealRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal")
public class MealController {
    private final DtoMapper dtoMapper;
    private final MealRepository mealRepository;

    @Autowired
    public MealController(DtoMapper dtoMapper,
                          MealRepository mealRepository) {
        this.dtoMapper = dtoMapper;
        this.mealRepository = mealRepository;
    }

    @GetMapping
    public List<Meal> getAll() {
        return mealRepository.findAll();
    }

    @GetMapping("/{id}")
    public Meal getById(@PathVariable String id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Meal",id));
    }

    @PostMapping("/new")
    public Meal newMeal(@RequestBody MealDto dto) {
        return mealRepository.save(dtoMapper.dtoToMeal(dto));
    }

    @PutMapping("/replace/{id}")
    public Meal replaceMeal(@PathVariable String id, @RequestBody MealDto dto) {
        Meal replacement = dtoMapper.dtoToMeal(dto);
        replacement.setId(id);
        return mealRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Meal updateMeal(@PathVariable String id, @PathVariable String field, @PathVariable String value){
        Meal update = mealRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Meal",value));
        switch(field) {
            case("name"):
                update.setName(value);
                break;
            case("description"):
                update.setDescription(value);
                break;
        }
        return mealRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteMeal(@PathVariable String id) {
        mealRepository.deleteById(id);
        return mealRepository.findById(id).isEmpty();
    }
}
