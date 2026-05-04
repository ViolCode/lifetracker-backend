package com.example.lifetracker.services;

import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.entities.ExpenseEntity;
import com.example.lifetracker.mappers.ExpenseMapper;
import com.example.lifetracker.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;
    private final ExpenseMapper mapper;

    public ExpenseService(ExpenseRepository repository, ExpenseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Expense create(Expense expense) {

        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Expense must be greater than 0");
        }

        ExpenseEntity saved = repository.save(mapper.toEntity(expense));
        return mapper.toDomain(saved);
    }

    public List<Expense> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Expense> getByEvent(int eventId) {
        return repository.findByEventId(eventId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}