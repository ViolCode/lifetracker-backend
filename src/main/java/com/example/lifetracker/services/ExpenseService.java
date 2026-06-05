package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.ExpenseRepository;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository repository;
    private final EventRepository eventRepository;

    public ExpenseService(ExpenseRepository repository, EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    public Expense create(Expense expense) {
        validate(expense);
        return repository.save(expense);
    }

    public List<Expense> getAll() {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository.getAll(userId);
    }

    public List<Expense> getByEvent(int eventId) {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository.getByEvent(eventId, userId);
    }

    public Double getTotalByEvent(int eventId) {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository.getTotalByEvent(eventId, userId);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    private void validate(Expense expense) {
        Integer userId = SecurityUtils.getCurrentUserId();
        eventRepository
                .getById(expense.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event getEventForExpense(Expense expense) {
        Integer userId = SecurityUtils.getCurrentUserId();

        return eventRepository
                .getById(expense.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
}