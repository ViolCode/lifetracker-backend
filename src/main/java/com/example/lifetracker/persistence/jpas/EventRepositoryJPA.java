package com.example.lifetracker.persistence.jpas;
import com.example.lifetracker.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepositoryJPA extends JpaRepository<EventEntity, Integer> {

    List<EventEntity> findByOwnerId(
            Integer ownerId
    );

    List<EventEntity> findByOwnerIdAndPlannedDateBefore(
            Integer ownerId,
            LocalDate date
    );
}