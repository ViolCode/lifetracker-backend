package com.example.lifetracker.mappers;

import com.example.lifetracker.dtos.ExpenseRequestDTO;
import com.example.lifetracker.dtos.ExpenseResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.entities.ExpenseEntity;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toDomain(ExpenseRequestDTO dto) {
        return new Expense(
                dto.eventId(),
                SecurityUtils.getCurrentUserId(),
                dto.amount(),
                dto.description()
        );
    }

    public ExpenseEntity toEntity(Expense expense) {

        ExpenseEntity entity = new ExpenseEntity();

        entity.setId(expense.getId());
        entity.setEventId(expense.getEventId());
        entity.setUserId(expense.getUserId());
        entity.setAmount(expense.getAmount());
        entity.setDescription(expense.getDescription());
        entity.setCreatedAt(expense.getCreatedAt());

        return entity;
    }

    public Expense toDomain(ExpenseEntity entity) {

        return new Expense(
                entity.getId(),
                entity.getEventId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    public ExpenseResponseDTO toDTO(Expense expense, Event event, String addedByName) {
        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getEventId(),
                event.getTitle(),
                event.getPlannedDate(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getCreatedAt(),
                addedByName
        );
    }
}