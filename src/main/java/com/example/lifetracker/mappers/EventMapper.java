package com.example.lifetracker.mappers;

import com.example.lifetracker.dtos.EventRequestDTO;
import com.example.lifetracker.dtos.EventResponseDTO;
import com.example.lifetracker.dtos.ParticipantResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

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
        return Event.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .plannedDate(entity.getPlannedDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

    public EventResponseDTO toDTO(
            Event event,
            Double totalExpenses,
            List<ParticipantResponseDTO> participants
    ) {
        return new EventResponseDTO(
                event.getId(),
                event.getOwnerId(),
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
}
