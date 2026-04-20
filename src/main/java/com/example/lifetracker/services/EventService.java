package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    public EventService(EventRepository repository, EventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<Event> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Event getById(int id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event create(Event event) {
        EventEntity saved = repository.save(mapper.toEntity(event));
        return mapper.toDomain(saved);
    }

    public Event update(int id, Event updated) {
        EventEntity existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setPlannedDate(updated.getPlannedDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setPlannedBudget(updated.getPlannedBudget());

        return mapper.toDomain(repository.save(existing));
    }
}