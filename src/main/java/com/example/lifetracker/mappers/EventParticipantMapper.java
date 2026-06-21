package com.example.lifetracker.mappers;

import com.example.lifetracker.domains.EventParticipant;
import com.example.lifetracker.entities.EventParticipantEntity;
import org.springframework.stereotype.Component;

@Component
public class EventParticipantMapper {

    public EventParticipant toDomain(EventParticipantEntity entity) {
        return new EventParticipant(
                entity.getId(),
                entity.getEventId(),
                entity.getUserId(),
                entity.getIsConfirmed(),
                entity.getInvitedBy(),
                entity.getInvitedAt(),
                entity.getConfirmedAt()
        );
    }

    public EventParticipantEntity toEntity(EventParticipant participant) {
        EventParticipantEntity entity = new EventParticipantEntity();
        entity.setId(participant.getId());
        entity.setEventId(participant.getEventId());
        entity.setUserId(participant.getUserId());
        entity.setIsConfirmed(participant.getIsConfirmed());
        entity.setInvitedBy(participant.getInvitedBy());
        entity.setInvitedAt(participant.getInvitedAt());
        entity.setConfirmedAt(participant.getConfirmedAt());
        return entity;
    }
}