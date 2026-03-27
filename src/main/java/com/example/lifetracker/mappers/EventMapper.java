package com.example.lifetracker.mappers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.EventEntity;

public class EventMapper {

    public static EventEntity toEntity(EventRequestDTO dto) {
        EventEntity entity = new EventEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setLocation(dto.getLocation());
        entity.setPlannedDate(dto.getPlannedDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setPlannedBudget(dto.getPlannedBudget());
        return entity;
    }

    public static EventResponseDTO toDTO(EventEntity entity) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setLocation(entity.getLocation());
        dto.setPlannedDate(entity.getPlannedDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setPlannedBudget(entity.getPlannedBudget());
        return dto;
    }
}