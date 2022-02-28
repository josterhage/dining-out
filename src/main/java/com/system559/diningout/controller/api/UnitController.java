package com.system559.diningout.controller.api;

import com.system559.diningout.dto.UnitDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Unit;
import com.system559.diningout.repository.UnitRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitController {
    private final DtoMapper dtoMapper;
    private final UnitRepository unitRepository;

    @Autowired
    public UnitController(DtoMapper dtoMapper,
                          UnitRepository unitRepository){
        this.dtoMapper = dtoMapper;
        this.unitRepository = unitRepository;
    }

    @GetMapping
    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Unit getById(@PathVariable String id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Unit",id));
    }

    @PostMapping("/new")
    public Unit newUnit(@RequestBody UnitDto dto) {
        return unitRepository.save(dtoMapper.dtoToUnit(dto));
    }

    @PostMapping("/bulk-add")
    public List<Unit> newUnits(@RequestBody List<UnitDto> dtos) {
        List<Unit> units = new ArrayList<>();
        for(UnitDto dto : dtos) {
            units.add(unitRepository.save(dtoMapper.dtoToUnit(dto)));
        }
        return units;
    }

    @PutMapping("/replace/{id}")
    public Unit replaceUnit(@PathVariable String id, @RequestBody UnitDto dto) {
        Unit replacement = dtoMapper.dtoToUnit(dto);
        replacement.setId(id);
        return unitRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Unit updateUnitName(@PathVariable String id, @PathVariable String field, @PathVariable String value){
        Unit update = unitRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Unit",id));

        switch(field) {
            case("name"):
                update.setName(value);
                break;
            case("pocEmail"):
                update.setPocEmail(value);
                break;
        }
        return unitRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteUnit(@PathVariable String id) {
        unitRepository.deleteById(id);
        return !unitRepository.findById(id).isPresent();
    }
}
