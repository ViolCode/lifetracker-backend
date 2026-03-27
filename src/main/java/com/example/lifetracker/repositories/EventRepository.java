package com.example.lifetracker.repositories;

import com.example.lifetracker.domains.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
}
