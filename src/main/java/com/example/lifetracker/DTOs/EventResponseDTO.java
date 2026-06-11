package com.example.lifetracker.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventResponseDTO(
        Integer id,
        String title,
        String description,
        String location,
        LocalDate plannedDate,
        LocalTime startTime,
        LocalTime endTime,
        Double totalExpenses
) {
}