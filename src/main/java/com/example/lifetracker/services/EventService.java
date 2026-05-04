package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.exeptions.ValidationException;
import com.example.lifetracker.persistence.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<Event> getAll() {
        return repository.getAll();
    }

    public Event getById(int id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event create(Event event) {
        validate(event);
        return repository.save(event);
    }

    public Event update(int id, Event updated) {
        validate(updated);

        Event existing = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        existing.update(
                updated.getTitle(),
                updated.getDescription(),
                updated.getLocation(),
                updated.getPlannedDate(),
                updated.getStartTime(),
                updated.getEndTime()
        );

        return repository.save(existing);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    private void validate(Event event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new ValidationException("Title is required");
        }

        if (event.getPlannedDate() == null) {
            throw new ValidationException("Planned date is required");
        }

        if (event.getStartTime() != null && event.getEndTime() != null &&
                event.getEndTime().isBefore(event.getStartTime())) {
            throw new ValidationException("End time cannot be before start time");
        }
    }
}