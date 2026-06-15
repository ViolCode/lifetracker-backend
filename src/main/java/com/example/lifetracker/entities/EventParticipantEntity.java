// com.example.lifetracker.entities.EventParticipantEntity.java
package com.example.lifetracker.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "event_participants")
public class EventParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer eventId;
    @Column(nullable = false)
    private Integer userId;
    @Column(nullable = false)
    private Boolean isConfirmed = false;
    @Column(nullable = false)
    private Integer invitedBy;
    @Column(nullable = false)
    private LocalDateTime invitedAt;
    private LocalDateTime confirmedAt;
}