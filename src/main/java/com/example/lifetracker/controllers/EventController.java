package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import org.springframework.web.bind.annotation.*;
import com.example.lifetracker.services.EventService;

import java.util.List;
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public EventResponseDTO create(@RequestBody EventRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EventResponseDTO update(@PathVariable int id, @RequestBody EventRequestDTO dto) {
        return service.update(id, dto);
    }
}