package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.Expense;

import java.util.List;

public interface ExpenseRepository {
    Expense save(Expense expense);
    List<Expense> getAll();
    List<Expense> getByEvent(int eventId);
}