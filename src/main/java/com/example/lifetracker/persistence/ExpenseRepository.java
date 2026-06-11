package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.Expense;

import java.util.List;

public interface ExpenseRepository {
    Expense save(Expense expense);
    List<Expense> getAll(Integer userId);
    List<Expense> getByEvent(int eventId, Integer userId);
    Double getTotalByEvent(int eventId, Integer userId);
    void delete(int id);
}