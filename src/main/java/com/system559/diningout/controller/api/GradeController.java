package com.system559.diningout.controller.api;

import com.system559.diningout.dto.GradeDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Grade;
import com.system559.diningout.model.TicketTier;
import com.system559.diningout.repository.GradeRepository;
import com.system559.diningout.repository.TicketTierRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/grade")
public class GradeController {
    private final DtoMapper mapper;
    private final GradeRepository repository;
    private final TicketTierRepository tierRepository;

    @Autowired
    public GradeController(DtoMapper mapper,
                           GradeRepository repository,
                           TicketTierRepository tierRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.tierRepository = tierRepository;
    }

    @GetMapping
    public List<Grade> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Grade getById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Grade",id));
    }

    @PostMapping("/new")
    public Grade newGrade(@RequestBody GradeDto dto) {
        return repository.save(mapper.dtoToGrade(dto));
    }

    @PostMapping("/bulk-add")
    public List<Grade> newGrades(@RequestBody List<GradeDto> dtos) {
        List<Grade> grades = new ArrayList<>();
        for(GradeDto dto : dtos) {
            grades.add(repository.save(mapper.dtoToGrade(dto)));
        }
        return grades;
    }

    @PutMapping("/replace/{id}")
    public Grade replaceGrade(@PathVariable String id, @RequestBody GradeDto dto) {
        Grade replacement = mapper.dtoToGrade(dto);
        replacement.setId(id);
        return repository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Grade updateGrade(@PathVariable String id,
                             @PathVariable String field,
                             @PathVariable String value) {
        Grade update = repository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Grade",id));
        switch(field) {
            case("name"):
                update.setName(value);
                break;
            case("tier"):
                TicketTier newTier = tierRepository.findById(value)
                        .orElseThrow(() -> new RecordIdNotFoundException("TicketTier",value));
                update.setTier(newTier);
                break;
        }

        return repository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteGrade(@PathVariable String id) {
        repository.deleteById(id);
        return !repository.findById(id).isPresent();
    }
}
