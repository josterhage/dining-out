package com.system559.diningout.controller.view;

import com.system559.diningout.model.Grade;
import com.system559.diningout.model.Meal;
import com.system559.diningout.model.Unit;
import com.system559.diningout.repository.GradeRepository;
import com.system559.diningout.repository.MealRepository;
import com.system559.diningout.repository.UnitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/form")
public class FormController {
    private final UnitRepository unitRepository;
    private final GradeRepository gradeRepository;
    private final MealRepository mealRepository;

    public FormController(UnitRepository unitRepository,
                          GradeRepository gradeRepository,
                          MealRepository mealRepository) {
        this.unitRepository=unitRepository;
        this.gradeRepository=gradeRepository;
        this.mealRepository=mealRepository;
    }

    @GetMapping("/rsvp-form")
    public String getRsvpForm(Model model) {
        model.addAttribute("units",unitRepository.findAll().stream().map(Unit::getName).collect(Collectors.toList()));
        model.addAttribute("grades",gradeRepository.findAll().stream().map(Grade::getName).collect(Collectors.toList()));
        model.addAttribute("meals",mealRepository.findAll().stream().map(Meal::getName).collect(Collectors.toList()));
        return "rsvp-form";
    }
}
