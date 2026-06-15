package com.example.lifetracker.DTOs;

import com.example.lifetracker.domains.Event;
import java.util.List;

public record EventWithParticipantsDTO(
        Event event,
        List<ParticipantResponseDTO> participants
) {
}