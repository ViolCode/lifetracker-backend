package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository repository;
    private final EventRepository eventRepository;

    public ExpenseService(
            ExpenseRepository repository,
            EventRepository eventRepository
    ) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    public Expense create(Expense expense) {

        validate(expense);

        return repository.save(expense);
    }

    public List<Expense> getAll() {

        return repository.getAll();
    }

    public List<Expense> getByEvent(int eventId) {

        return repository.getByEvent(eventId);
    }

    public Double getTotalByEvent(int eventId) {

        return repository.getTotalByEvent(eventId);
    }

    public void delete(int id) {

        repository.delete(id);
    }

    private void validate(Expense expense) {

        eventRepository.getById(expense.getEventId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Event not found"
                        )
                );
    }

    public Event getEventForExpense(Expense expense) {

        return eventRepository
                .getById(expense.getEventId())
                .orElseThrow(() ->
                        new NotFoundException("Event not found")
                );
    }
}