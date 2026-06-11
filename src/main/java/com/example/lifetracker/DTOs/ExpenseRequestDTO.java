package com.example.lifetracker.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExpenseRequestDTO(
        @NotNull(message = "Event id is required")
        Integer eventId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        Double amount,
        String description
) {
}