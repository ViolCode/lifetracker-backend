package com.example.lifetracker.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequestDTO(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        String location,
        @NotNull(message = "Planned date is required")
        LocalDate plannedDate,
        LocalTime startTime,
        LocalTime endTime
) {
}