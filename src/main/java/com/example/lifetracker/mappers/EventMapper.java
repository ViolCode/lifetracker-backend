package com.example.lifetracker.mappers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // DTO → Domain
    public Event toDomain(EventRequestDTO dto) {
        return new Event(
                dto.getTitle(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getPlannedDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getPlannedBudget()
        );
    }

    // Domain → DTO
    public EventResponseDTO toDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setPlannedDate(event.getPlannedDate());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setPlannedBudget(event.getPlannedBudget());
        return dto;
    }

    // Entity → Domain
    public Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPlannedDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getPlannedBudget()
        );
    }

    // Domain → Entity
    public EventEntity toEntity(Event event) {
        EventEntity entity = new EventEntity();
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