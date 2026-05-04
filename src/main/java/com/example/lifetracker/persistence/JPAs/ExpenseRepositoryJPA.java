package com.example.lifetracker.persistence.JPAs;

import com.example.lifetracker.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepositoryJPA extends JpaRepository<ExpenseEntity, Integer> {
    List<ExpenseEntity> findByEventId(Integer eventId);
}