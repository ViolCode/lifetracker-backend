package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
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

        return repository.save(event);
    }

    public Event update(int id, Event updated) {

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

    public List<Event> getPastEvents()
    {
        return repository.getPastEvents();
    }
}