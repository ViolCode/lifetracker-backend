package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.ExpenseRequestDTO;
import com.example.lifetracker.DTOs.ExpenseResponseDTO;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.mappers.ExpenseMapper;
import com.example.lifetracker.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;
    private final ExpenseMapper mapper;

    public ExpenseController(
            ExpenseService service,
            ExpenseMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ExpenseResponseDTO create(
            @Valid @RequestBody ExpenseRequestDTO dto
    ) {

        Expense saved = service.create(
                mapper.toDomain(dto)
        );

        return mapper.toDTO(
                saved,
                service.getEventForExpense(saved),
                service.getUserNameForExpense(saved)
        );
    }

    @GetMapping
    public List<ExpenseResponseDTO> getAll() {

        return service.getAll()
                .stream()
                .map(expense ->
                        mapper.toDTO(
                                expense,
                                service.getEventForExpense(expense),
                                service.getUserNameForExpense(expense)
                        )
                )
                .toList();
    }

    @GetMapping("/event/{eventId}")
    public List<ExpenseResponseDTO> getByEvent(
            @PathVariable int eventId
    ) {

        return service.getByEvent(eventId)
                .stream()
                .map(expense ->
                        mapper.toDTO(
                                expense,
                                service.getEventForExpense(expense),
                                service.getUserNameForExpense(expense)
                        )
                )
                .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable int id
    ) {

        service.delete(id);
    }
}