package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventParticipantRepository;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.ExpenseRepository;
import com.example.lifetracker.persistence.UserRepository;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository repository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository repository,
            EventRepository eventRepository,
            EventParticipantRepository participantRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
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
        return repository.getAllByEvent(eventId);
    }

    public Double getTotalByEvent(int eventId) {
        return repository.getTotalByEventAllUsers(eventId);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    private void validate(Expense expense) {
        Integer userId = SecurityUtils.getCurrentUserId();
        boolean isOwner = eventRepository.getById(expense.getEventId(), userId).isPresent();
        boolean isParticipant = participantRepository.getByEventIdAndUserId(expense.getEventId(), userId).isPresent();
        if (!isOwner && !isParticipant) {
            throw new NotFoundException("Event not found");
        }
    }

    public Event getEventForExpense(Expense expense) {
        return eventRepository
                .findById(expense.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public String getUserNameForExpense(Expense expense) {
        return userRepository.findById(expense.getUserId())
                .map(user -> user.getName())
                .orElse("Unknown");
    }
}