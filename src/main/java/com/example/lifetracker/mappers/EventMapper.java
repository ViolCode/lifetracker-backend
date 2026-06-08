package com.example.lifetracker.mappers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import org.springframework.stereotype.Component;

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

    public EventResponseDTO toDTO(Event event, Double totalExpenses)
    {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getPlannedDate(),
                event.getStartTime(),
                event.getEndTime(),
                totalExpenses
        );
    }

    public EventEntity toEntity(
            Event event
    ) {

        EventEntity entity =
                new EventEntity();

        entity.setId(event.getId());
        entity.setTitle(event.getTitle());
        entity.setDescription(event.getDescription());
        entity.setLocation(event.getLocation());
        entity.setPlannedDate(event.getPlannedDate());
        entity.setStartTime(event.getStartTime());
        entity.setEndTime(event.getEndTime());

        return entity;
    }
}