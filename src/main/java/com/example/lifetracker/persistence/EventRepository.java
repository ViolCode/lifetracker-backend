package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<Event> getAll(Integer ownerId);
    Optional<Event> getById(int id, Integer ownerId);
    Event save(Event event, Integer ownerId);
    void delete(int id);
    List<Event> getPastEvents(Integer ownerId);
}