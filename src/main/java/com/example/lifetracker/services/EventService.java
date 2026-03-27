package com.example.lifetracker.services;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.EventEntity;
import com.example.lifetracker.exeptions.NotFoundExeption;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<EventResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EventMapper::toDTO)
                .toList();
    }

    public EventResponseDTO getById(int id) {
        EventEntity event = repository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Event not found"));

        return EventMapper.toDTO(event);
    }

    public EventResponseDTO create(EventRequestDTO dto) {
        EventEntity entity = EventMapper.toEntity(dto);
        return EventMapper.toDTO(repository.save(entity));
    }

    public EventResponseDTO update(int id, EventRequestDTO dto) {
        EventEntity existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Event not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setLocation(dto.getLocation());
        existing.setPlannedDate(dto.getPlannedDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setPlannedBudget(dto.getPlannedBudget());

        return EventMapper.toDTO(repository.save(existing));
    }

}