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

    public ExpenseRepositoryImpl(
            ExpenseRepositoryJPA jpa,
            ExpenseMapper mapper
    ) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Expense save(
            Expense expense
    ) {

        ExpenseEntity saved =
                jpa.save(
                        mapper.toEntity(expense)
                );

        return mapper.toDomain(saved);
    }

    @Override
    public List<Expense> getAll(
            Integer userId
    ) {

        return jpa.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Expense> getByEvent(
            int eventId,
            Integer userId
    ) {

        return jpa.findByEventIdAndUserId(
                        eventId,
                        userId
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Double getTotalByEvent(
            int eventId,
            Integer userId
    ) {

        return jpa.getTotalByEvent(
                eventId,
                userId
        );
    }

    @Override
    public void delete(int id) {
        jpa.deleteById(id);
    }
}