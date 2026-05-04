package com.example.lifetracker.repositories;

import com.example.lifetracker.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {
    List<ExpenseEntity> findByEventId(Integer eventId);
}