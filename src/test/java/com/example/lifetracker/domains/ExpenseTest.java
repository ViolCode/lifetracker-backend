package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    @Test
    void shouldCreateExpense() {

        Expense expense = new Expense(
                1,
                1,
                50.0,
                "Food"
        );

        assertEquals(50.0, expense.getAmount());
    }

    @Test
    void shouldThrowWhenAmountIsZero() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new Expense(
                                1,
                                1,
                                0.0,
                                "Food"
                        )
                );

        assertEquals(
                "Expense amount must be greater than 0",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenAmountIsNegative() {

        assertThrows(
                ValidationException.class,
                () -> new Expense(
                        1,
                        1,
                        -5.0,
                        "Food"
                )
        );
    }

    @Test
    void shouldUpdateExpense() {

        Expense expense = new Expense(
                1,
                1,
                10.0,
                "Old"
        );

        expense.update(20.0, "New");

        assertEquals(20.0, expense.getAmount());
        assertEquals("New", expense.getDescription());
    }
}