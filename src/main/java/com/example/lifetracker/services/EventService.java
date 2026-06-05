package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository repository;

    public EventService(
            EventRepository repository
    ) {
        this.repository = repository;
    }

    public List<Event> getAll() {

        Integer userId = SecurityUtils.getCurrentUserId();

        return repository.getAll(userId);
    }

    public Event getById(int id) {

        Integer userId =
                SecurityUtils.getCurrentUserId();

        return repository
                .getById(id, userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Event not found"
                        )
                );
    }

    public Event create(Event event) {

        Integer userId =
                SecurityUtils.getCurrentUserId();

        return repository.save(event, userId);
    }

    public Event update(
            int id,
            Event updated
    ) {

        Integer userId =
                SecurityUtils.getCurrentUserId();

        Event existing =
                repository.getById(id, userId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Event not found"
                                )
                        );

        existing.update(
                updated.getTitle(),
                updated.getDescription(),
                updated.getLocation(),
                updated.getPlannedDate(),
                updated.getStartTime(),
                updated.getEndTime()
        );

        return repository.save(existing, userId);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    public List<Event> getPastEvents() {

        Integer userId = SecurityUtils.getCurrentUserId();

        return repository.getPastEvents(userId);
    }
}