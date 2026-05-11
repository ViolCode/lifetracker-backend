package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<Event> getAll();
    Optional<Event> getById(int id);
    Event save(Event event);
    void delete(int id);
    List<Event> getPastEvents();
}