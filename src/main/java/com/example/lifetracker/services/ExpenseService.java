package com.example.lifetracker.services;

import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.persistence.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense create(Expense expense) {

        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Expense must be greater than 0");
        }

        return repository.save(expense);
    }

    public List<Expense> getAll() {
        return repository.getAll();
    }

    public List<Expense> getByEvent(int eventId) {
        return repository.getByEvent(eventId);
    }

}