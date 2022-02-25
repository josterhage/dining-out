package com.system559.diningout.controller.api;

import com.system559.diningout.dto.RequestDto;
import com.system559.diningout.exception.RecordIdNotFoundException;
import com.system559.diningout.model.Request;
import com.system559.diningout.repository.RequestRepository;
import com.system559.diningout.service.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
public class RequestController {
    private DtoMapper dtoMapper;
    private RequestRepository requestRepository;

    @Autowired
    public RequestController(DtoMapper dtoMapper,
                             RequestRepository requestRepository) {
        this.dtoMapper = dtoMapper;
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    @GetMapping("/{id}")
    public Request getById(@PathVariable String id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Request",id));
    }

    @PostMapping("/new")
    public Request newRequest(@RequestBody RequestDto dto) {
        return requestRepository.save(dtoMapper.dtoToRequest(dto));
    }

    @PutMapping("/replace/{id}")
    public Request replaceRequest(@PathVariable String id, @RequestBody RequestDto dto) {
        Request replacement = dtoMapper.dtoToRequest(dto);
        replacement.setId(id);
        return requestRepository.save(replacement);
    }

    @PatchMapping("/update/{id}/{field}/{value}")
    public Request updateRequest(@PathVariable String id, @PathVariable String field, @PathVariable String value) {
        Request update = requestRepository.findById(id)
                .orElseThrow(() -> new RecordIdNotFoundException("Request",id));
        switch(field){
            case("name"):
                update.setName(value);
                break;
            case("description"):
                update.setDescription(value);
                break;
        }

        return requestRepository.save(update);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteRequest(@PathVariable String id) {
        requestRepository.deleteById(id);
        return requestRepository.findById(id).isEmpty();
    }
}
