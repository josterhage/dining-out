package com.system559.diningout.controller.api;

import com.system559.diningout.dto.SaluteDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Salute;
import com.system559.diningout.repository.SaluteRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salute")
public class SaluteController {
    private final DtoMapper mapper;
    private final SaluteRepository repository;

    @Autowired
    public SaluteController(DtoMapper mapper,
                            SaluteRepository repository){
        this.mapper=mapper;
        this.repository=repository;
    }

    @GetMapping
    public List<Salute> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Salute getById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Salute",id));
    }

    @PostMapping("/new")
    public Salute newSalute(@RequestBody SaluteDto dto) {
        return repository.save(mapper.dtoToSalute(dto));
    }

    @PostMapping("/bulk-add")
    public List<Salute> newSalutes(@RequestBody List<SaluteDto> dtos) {
        List<Salute> salutes = new ArrayList<>();
        for(SaluteDto dto : dtos) {
            salutes.add(repository.save(mapper.dtoToSalute(dto)));
        }
        return salutes;
    }

    @PutMapping("/replace/{id}")
    public Salute replaceSalute(@PathVariable String id, @RequestBody SaluteDto dto) {
        Salute replacement = mapper.dtoToSalute(dto);
        replacement.setId(id);
        return repository.save(replacement);
    }

    @PatchMapping("/update/{id}/grade/{value}")
    public Salute updateSalute(@PathVariable String id,
                               @PathVariable String value) {
        Salute update = repository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Salute",id));
        update.setName(value);
        return update;
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteSalute(@PathVariable String id) {
        repository.deleteById(id);
        return !repository.findById(id).isPresent();
    }
}
