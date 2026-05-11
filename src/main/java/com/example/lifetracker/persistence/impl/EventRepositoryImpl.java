package com.example.lifetracker.persistence.impl;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.JPAs.EventRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final EventRepositoryJPA jpa;
    private final EventMapper mapper;

    public EventRepositoryImpl(EventRepositoryJPA jpa, EventMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public List<Event> getAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Event> getById(int id) {
        return jpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Event save(Event event) {
        EventEntity saved = jpa.save(mapper.toEntity(event));
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(int id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Event> getPastEvents() {

        return jpa.findByPlannedDateBefore(LocalDate.now())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}