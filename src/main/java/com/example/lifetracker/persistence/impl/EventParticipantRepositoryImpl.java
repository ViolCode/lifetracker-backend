package com.example.lifetracker.persistence.impl;

import com.example.lifetracker.domains.EventParticipant;
import com.example.lifetracker.entities.EventParticipantEntity;
import com.example.lifetracker.mappers.EventParticipantMapper;
import com.example.lifetracker.persistence.EventParticipantRepository;
import com.example.lifetracker.persistence.JPAs.EventParticipantRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventParticipantRepositoryImpl implements EventParticipantRepository {

    private final EventParticipantRepositoryJPA jpa;
    private final EventParticipantMapper mapper;

    public EventParticipantRepositoryImpl(EventParticipantRepositoryJPA jpa, EventParticipantMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public EventParticipant save(EventParticipant participant) {
        EventParticipantEntity entity = mapper.toEntity(participant);
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public List<EventParticipant> getByEventId(Integer eventId) {
        return jpa.findByEventId(eventId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<EventParticipant> getByEventIdAndUserId(Integer eventId, Integer userId) {
        return jpa.findByEventIdAndUserId(eventId, userId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByEventIdAndUserId(Integer eventId, Integer userId) {
        jpa.deleteByEventIdAndUserId(eventId, userId);
    }

    @Override
    public void deleteByEventId(Integer eventId) {
        jpa.deleteByEventId(eventId);
    }

    @Override
    public List<EventParticipant> getEventsByUserId(Integer userId) {
        return jpa.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}