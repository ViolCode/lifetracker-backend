package com.example.lifetracker.persistence.JPAs;
import com.example.lifetracker.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepositoryJPA extends JpaRepository<EventEntity, Integer> {
}