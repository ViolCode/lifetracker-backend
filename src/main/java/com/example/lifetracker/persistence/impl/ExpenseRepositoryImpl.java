package com.example.lifetracker.persistence.impl;

import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.entities.ExpenseEntity;
import com.example.lifetracker.mappers.ExpenseMapper;
import com.example.lifetracker.persistence.ExpenseRepository;
import com.example.lifetracker.persistence.JPAs.ExpenseRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseRepositoryJPA jpa;
    private final ExpenseMapper mapper;

    public ExpenseRepositoryImpl(ExpenseRepositoryJPA jpa, ExpenseMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Expense save(Expense expense) {
        ExpenseEntity saved = jpa.save(mapper.toEntity(expense));
        return mapper.toDomain(saved);
    }

    @Override
    public List<Expense> getAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Expense> getByEvent(int eventId) {
        return jpa.findByEventId(eventId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Double getTotalByEvent(int eventId) {

        return jpa.getTotalByEvent(eventId);
    }

    @Override
    public void delete(int evenIid) {
        jpa.deleteById(evenIid);
    }
}