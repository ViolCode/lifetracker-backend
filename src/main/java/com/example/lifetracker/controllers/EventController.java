package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.services.EventService;
import com.example.lifetracker.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private final EventMapper mapper;
    private final ExpenseService expenseService;

    public EventController(
            EventService service,
            EventMapper mapper,
            ExpenseService expenseService
    ) {
        this.service = service;
        this.mapper = mapper;
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<EventResponseDTO> getAll() {

        return service.getAll()
                .stream()
                .map(event ->
                        mapper.toDTO(
                                event,
                                expenseService.getTotalByEvent(event.getId())
                        )
                )
                .toList();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getById(
            @PathVariable int id
    ) {

        var event = service.getById(id);

        return mapper.toDTO(
                event,
                expenseService.getTotalByEvent(event.getId())
        );
    }

    @GetMapping("/history")
    public List<EventResponseDTO> getHistory() {

        return service.getPastEvents()
                .stream()
                .map(event ->
                        mapper.toDTO(
                                event,
                                expenseService.getTotalByEvent(event.getId())
                        )
                )
                .toList();
    }

    @PostMapping
    public EventResponseDTO create(
            @Valid @RequestBody EventRequestDTO dto
    ) {

        var saved = service.create(
                mapper.toDomain(dto)
        );

        return mapper.toDTO(saved, 0.0);
    }

    @PutMapping("/{id}")
    public EventResponseDTO update(
            @PathVariable int id,
            @Valid @RequestBody EventRequestDTO dto
    ) {

        var updated = service.update(
                id,
                mapper.toDomain(dto)
        );

        return mapper.toDTO(
                updated,
                expenseService.getTotalByEvent(updated.getId())
        );
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable int id
    ) {

        service.delete(id);
    }
}