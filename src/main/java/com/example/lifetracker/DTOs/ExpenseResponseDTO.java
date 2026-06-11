package com.example.lifetracker.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponseDTO(
        Integer id,
        Integer eventId,
        String eventTitle,
        LocalDate eventPlannedDate,
        Double amount,
        String description,
        LocalDateTime createdAt
) {
}