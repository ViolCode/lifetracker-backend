package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.EventParticipant;
import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository {
    EventParticipant save(EventParticipant participant);
    List<EventParticipant> getByEventId(Integer eventId);
    Optional<EventParticipant> getByEventIdAndUserId(Integer eventId, Integer userId);
    void deleteByEventIdAndUserId(Integer eventId, Integer userId);
    void deleteByEventId(Integer eventId);
    List<EventParticipant> getEventsByUserId(Integer userId);
}