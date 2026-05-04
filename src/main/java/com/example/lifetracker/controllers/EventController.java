package com.example.lifetracker.controllers;
import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.mappers.EventMapper;
import org.springframework.web.bind.annotation.*;
import com.example.lifetracker.services.EventService;
import java.util.List;
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private final EventMapper mapper;

    public EventController(EventService service, EventMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EventResponseDTO> getAll() {
        return service.getAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getById(@PathVariable int id) {
        return mapper.toDTO(service.getById(id));
    }

    @PostMapping
    public EventResponseDTO create(@RequestBody EventRequestDTO dto) {
        return mapper.toDTO(service.create(mapper.toDomain(dto)));
    }

    @PutMapping("/{id}")
    public EventResponseDTO update(@PathVariable int id,
                                   @RequestBody EventRequestDTO dto) {
        return mapper.toDTO(service.update(id, mapper.toDomain(dto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}