package com.example.lifetracker.DTOs;

import java.time.LocalDateTime;

public record ParticipantResponseDTO(
        Integer id,
        Integer userId,
        String userEmail,
        String userName,
        Boolean isConfirmed,
        String invitedByName,
        LocalDateTime invitedAt
) {
}