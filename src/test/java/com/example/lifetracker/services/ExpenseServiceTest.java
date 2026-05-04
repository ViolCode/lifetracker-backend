package com.example.lifetracker.services;

import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.persistence.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;

    private Expense validExpense;

    @BeforeEach
    void setUp() {
        validExpense = new Expense(1, 10, 1, 49.99, "Dinner", LocalDateTime.now());
    }

    @Test
    void create_savesAndReturnsExpense_whenValid() {
        when(repository.save(validExpense)).thenReturn(validExpense);

        Expense result = service.create(validExpense);

        assertThat(result).isEqualTo(validExpense);
        verify(repository).save(validExpense);
    }

    @Test
    void create_throwsIllegalArgumentException_whenAmountIsNull() {
        Expense expense = new Expense(10, 1, null, "Dinner");

        assertThatThrownBy(() -> service.create(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense must be greater than 0");

        verify(repository, never()).save(any());
    }

    @Test
    void create_throwsIllegalArgumentException_whenAmountIsZero() {
        Expense expense = new Expense(10, 1, 0.0, "Dinner");

        assertThatThrownBy(() -> service.create(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense must be greater than 0");
    }

    @Test
    void create_throwsIllegalArgumentException_whenAmountIsNegative() {
        Expense expense = new Expense(10, 1, -5.0, "Dinner");

        assertThatThrownBy(() -> service.create(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense must be greater than 0");
    }

    @Test
    void create_savesExpense_withVerySmallPositiveAmount() {
        Expense expense = new Expense(10, 1, 0.01, "Tiny");
        when(repository.save(expense)).thenReturn(expense);

        assertThatCode(() -> service.create(expense)).doesNotThrowAnyException();
    }

    @Test
    void getAll_returnsAllExpenses() {
        when(repository.getAll()).thenReturn(List.of(validExpense));

        List<Expense> result = service.getAll();

        assertThat(result).hasSize(1).contains(validExpense);
        verify(repository).getAll();
    }

    @Test
    void getAll_returnsEmptyList_whenNoExpenses() {
        when(repository.getAll()).thenReturn(List.of());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getByEvent_returnsExpensesForEvent() {
        when(repository.getByEvent(10)).thenReturn(List.of(validExpense));

        List<Expense> result = service.getByEvent(10);

        assertThat(result).hasSize(1).contains(validExpense);
        verify(repository).getByEvent(10);
    }

    @Test
    void getByEvent_returnsEmptyList_whenNoExpensesForEvent() {
        when(repository.getByEvent(999)).thenReturn(List.of());

        assertThat(service.getByEvent(999)).isEmpty();
    }
}