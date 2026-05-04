package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.exeptions.ValidationException;
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
        validate(event);
        EventEntity saved = repository.save(mapper.toEntity(event));
        return mapper.toDomain(saved);
    }

    public Event update(int id, Event updated) {
        validate(updated);

        EventEntity existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setPlannedDate(updated.getPlannedDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());

        EventEntity saved = repository.save(existing);

        return mapper.toDomain(saved);
    }

    public void delete(int id) {
        EventEntity existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        repository.delete(existing);
    }

    private void validate(Event event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new ValidationException("Title is required");
        }

        if (event.getPlannedDate() == null) {
            throw new ValidationException("Planned date is required");
        }

        if (event.getPlannedBudget() != null && event.getPlannedBudget() < 0) {
            throw new ValidationException("Budget cannot be negative");
        }

        if (event.getStartTime() != null && event.getEndTime() != null &&
                event.getEndTime().isBefore(event.getStartTime())) {
            throw new ValidationException("End time cannot be before start time");
        }
    }
}