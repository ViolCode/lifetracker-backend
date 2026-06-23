package com.example.lifetracker.dtos;

import jakarta.validation.constraints.NotNull;

public record ParticipantRequestDTO(
        @NotNull(message = "User ID is required")
        Integer userId,
        Boolean isConfirmed
) {
}