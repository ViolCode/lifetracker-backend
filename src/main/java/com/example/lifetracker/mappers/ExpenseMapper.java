package com.example.lifetracker.mappers;

import com.example.lifetracker.DTOs.*;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.entities.ExpenseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toDomain(ExpenseRequestDTO dto) {
        return new Expense(
                dto.getEventId(),
                1, // hardcoded user
                dto.getAmount(),
                dto.getDescription()
        );
    }

    public ExpenseEntity toEntity(Expense expense) {
        ExpenseEntity e = new ExpenseEntity();

        e.setId(expense.getId());
        e.setEventId(expense.getEventId());
        e.setUserId(expense.getUserId());
        e.setAmount(expense.getAmount());
        e.setDescription(expense.getDescription());
        e.setCreatedAt(expense.getCreatedAt());

        return e;
    }

    public Expense toDomain(ExpenseEntity e) {
        return new Expense(
                e.getId(),
                e.getEventId(),
                e.getUserId(),
                e.getAmount(),
                e.getDescription(),
                e.getCreatedAt()
        );
    }

    public ExpenseResponseDTO toDTO(Expense e) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();

        dto.setId(e.getId());
        dto.setEventId(e.getEventId());
        dto.setAmount(e.getAmount());
        dto.setDescription(e.getDescription());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}