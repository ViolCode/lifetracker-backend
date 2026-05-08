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
                dto.getTitle(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getPlannedDate(),
                dto.getStartTime(),
                dto.getEndTime()
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

    public EventResponseDTO toDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        mapEventToDTO(event, dto);
        dto.setId(event.getId());
        return dto;
    }

    public EventEntity toEntity(Event event) {
        EventEntity entity = new EventEntity();
        mapEventToEntity(event, entity);
        entity.setId(event.getId());
        entity.setOwnerId(1);
        return entity;
    }

    private void mapEventToDTO(Event source, EventResponseDTO target) {
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setLocation(source.getLocation());
        target.setPlannedDate(source.getPlannedDate());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
    }

    private void mapEventToEntity(Event source, EventEntity target) {
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setLocation(source.getLocation());
        target.setPlannedDate(source.getPlannedDate());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
    }
}