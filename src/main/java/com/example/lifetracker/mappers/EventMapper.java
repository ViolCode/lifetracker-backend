package com.example.lifetracker.mappers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.EventParticipant;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.entities.EventParticipantEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class EventMapper {

    private final UserMapper userMapper;
    private final EventParticipantMapper participantMapper;

    public EventMapper(UserMapper userMapper, EventParticipantMapper participantMapper) {
        this.userMapper = userMapper;
        this.participantMapper = participantMapper;
    }

    public Event toDomain(EventRequestDTO dto) {
        return new Event(
                dto.title(),
                dto.description(),
                dto.location(),
                dto.plannedDate(),
                dto.startTime(),
                dto.endTime()
        );
    }

    public Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPlannedDate(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }

    public EventResponseDTO toDTO(Event event, Double totalExpenses, List<ParticipantResponseDTO> participants) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getPlannedDate(),
                event.getStartTime(),
                event.getEndTime(),
                totalExpenses,
                event.getPlannedBudget(),
                participants != null ? participants : List.of()
        );
    }

    public EventEntity toEntity(Event event) {
        EventEntity entity = new EventEntity();
        entity.setId(event.getId());
        entity.setTitle(event.getTitle());
        entity.setDescription(event.getDescription());
        entity.setLocation(event.getLocation());
        entity.setPlannedDate(event.getPlannedDate());
        entity.setStartTime(event.getStartTime());
        entity.setEndTime(event.getEndTime());
        entity.setPlannedBudget(event.getPlannedBudget());
        return entity;
    }

    public EventParticipant toDomain(EventParticipantEntity entity) {
        return new EventParticipant(
                entity.getId(),
                entity.getEventId(),
                entity.getUserId(),
                entity.getIsConfirmed(),
                entity.getInvitedBy(),
                entity.getInvitedAt(),
                entity.getConfirmedAt()
        );
    }

    public EventParticipantEntity toEntity(EventParticipant participant) {
        EventParticipantEntity entity = new EventParticipantEntity();
        entity.setId(participant.getId());
        entity.setEventId(participant.getEventId());
        entity.setUserId(participant.getUserId());
        entity.setIsConfirmed(participant.getIsConfirmed());
        entity.setInvitedBy(participant.getInvitedBy());
        entity.setInvitedAt(participant.getInvitedAt());
        entity.setConfirmedAt(participant.getConfirmedAt());
        return entity;
    }
}