package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private ExpenseRepository repository;
    private EventRepository eventRepository;
    private ExpenseService service;

    @BeforeEach
    void setUp() {

        repository = mock(ExpenseRepository.class);
        eventRepository = mock(EventRepository.class);

        service = new ExpenseService(
                repository,
                eventRepository
        );
    }

    @Test
    void shouldCreateExpense() {

        Expense expense = new Expense(
                1,
                1,
                20.0,
                "Food"
        );

        Event event = new Event(
                1,
                "Gym",
                "",
                "",
                LocalDate.now(),
                null,
                null
        );

        when(eventRepository.getById(eq(1), anyInt())).thenReturn(Optional.of(event));
        when(repository.save(expense)).thenReturn(expense);

        Expense result = service.create(expense);

        assertEquals(20.0, result.getAmount());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {

        Expense expense = new Expense(
                999,
                1,
                20.0,
                "Food"
        );

        when(eventRepository.getById(eq(999), anyInt())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.create(expense)
        );
    }

    @Test
    void shouldDeleteExpense() {
        service.delete(1);
        verify(repository).delete(1);
    }
}