package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.*;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.mappers.ExpenseMapper;
import com.example.lifetracker.services.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;
    private final ExpenseMapper mapper;

    public ExpenseController(ExpenseService service, ExpenseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ExpenseResponseDTO create(@RequestBody ExpenseRequestDTO dto) {
        return mapper.toDTO(service.create(mapper.toDomain(dto)));
    }

    @GetMapping
    public List<ExpenseResponseDTO> getAll() {
        return service.getAll().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/event/{eventId}")
    public List<ExpenseResponseDTO> getByEvent(@PathVariable int eventId) {
        return service.getByEvent(eventId).stream().map(mapper::toDTO).toList();
    }
}
