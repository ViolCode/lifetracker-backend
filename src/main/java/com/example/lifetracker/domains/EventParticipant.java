// com.example.lifetracker.domains.EventParticipant.java
package com.example.lifetracker.domains;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class EventParticipant {
    private final Integer id;
    private final Integer eventId;
    private final Integer userId;
    private final Boolean isConfirmed;
    private final Integer invitedBy;
    private final LocalDateTime invitedAt;
    private final LocalDateTime confirmedAt;

    public EventParticipant(
            Integer id,
            Integer eventId,
            Integer userId,
            Boolean isConfirmed,
            Integer invitedBy,
            LocalDateTime invitedAt,
            LocalDateTime confirmedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.isConfirmed = isConfirmed != null && isConfirmed;
        this.invitedBy = invitedBy;
        this.invitedAt = invitedAt;
        this.confirmedAt = confirmedAt;
    }

    public EventParticipant(Integer eventId, Integer userId, Integer invitedBy) {
        this(null, eventId, userId, false, invitedBy, LocalDateTime.now(), null);
    }
}