package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.ExpenseRepository;
import com.example.lifetracker.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

        User user = new User(
                1,
                "test@test.com",
                "password",
                "Test User",
                false
        );

        UserPrincipal principal = new UserPrincipal(user);

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
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

        when(eventRepository.getById(1, 1))
                .thenReturn(Optional.of(event));

        when(repository.save(expense))
                .thenReturn(expense);

        Expense result = service.create(expense);

        assertEquals(20.0, result.getAmount());

        verify(repository).save(expense);
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {

        Expense expense = new Expense(
                999,
                1,
                20.0,
                "Food"
        );

        when(eventRepository.getById(999, 1))
                .thenReturn(Optional.empty());

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

    @Test
    void shouldGetEventForExpense() {

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

        when(eventRepository.getById(1, 1))
                .thenReturn(Optional.of(event));

        Event result =
                service.getEventForExpense(expense);

        assertEquals("Gym", result.getTitle());
    }

    @Test
    void shouldThrowWhenGettingEventForExpenseAndEventMissing() {

        Expense expense = new Expense(
                999,
                1,
                20.0,
                "Food"
        );

        when(eventRepository.getById(999, 1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getEventForExpense(expense)
        );
    }
}