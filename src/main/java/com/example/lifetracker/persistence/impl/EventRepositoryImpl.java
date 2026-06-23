package com.example.lifetracker.persistence.impl;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.jpas.EventRepositoryJPA;
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
    public List<Event> getAll(Integer ownerId) {

        return jpa.findByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Event> getById(int id, Integer ownerId) {

        return jpa.findById(id)
                .filter(event ->
                        ownerId.equals(
                                event.getOwnerId()
                        )
                )
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Event> findById(int id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Event save(Event event, Integer ownerId) {
        EventEntity entity = mapper.toEntity(event);
        entity.setOwnerId(ownerId);
        EventEntity saved = jpa.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void delete(int id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Event> getPastEvents(Integer ownerId) {

        return jpa.findByOwnerIdAndPlannedDateBefore(
                        ownerId,
                        LocalDate.now()
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}