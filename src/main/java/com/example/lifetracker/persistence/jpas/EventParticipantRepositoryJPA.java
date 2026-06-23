package com.example.lifetracker.persistence.jpas;

import com.example.lifetracker.entities.EventParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EventParticipantRepositoryJPA extends JpaRepository<EventParticipantEntity, Integer> {
    List<EventParticipantEntity> findByEventId(Integer eventId);
    List<EventParticipantEntity> findByUserId(Integer userId);
    Optional<EventParticipantEntity> findByEventIdAndUserId(Integer eventId, Integer userId);
    @Modifying
    @Query("DELETE FROM EventParticipantEntity e WHERE e.eventId = :eventId AND e.userId = :userId")
    void deleteByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Integer userId);
    @Modifying
    @Query("DELETE FROM EventParticipantEntity e WHERE e.eventId = :eventId")
    void deleteByEventId(@Param("eventId") Integer eventId);
}