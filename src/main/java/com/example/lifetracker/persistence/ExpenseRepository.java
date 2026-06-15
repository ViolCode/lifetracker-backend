package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.Expense;

import java.util.List;

public interface ExpenseRepository {
    Expense save(Expense expense);
    List<Expense> getAll(Integer userId);
    List<Expense> getByEvent(int eventId, Integer userId);
    List<Expense> getAllByEvent(int eventId);
    Double getTotalByEvent(int eventId, Integer userId);
    Double getTotalByEventAllUsers(int eventId);
    void delete(int id);
}