package com.example.lifetracker.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventResponseDTO(
        Integer id,
        Integer ownerId,
        String title,
        String description,
        String location,
        LocalDate plannedDate,
        LocalTime startTime,
        LocalTime endTime,
        Double totalExpenses,
        Double plannedBudget,
        List<ParticipantResponseDTO> participants
) {
}