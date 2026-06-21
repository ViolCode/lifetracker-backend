package com.example.lifetracker.DTOs;

import jakarta.validation.constraints.NotNull;

public record ParticipantRequestDTO(
        @NotNull(message = "User ID is required")
        Integer userId,
        Boolean isConfirmed
) {
}