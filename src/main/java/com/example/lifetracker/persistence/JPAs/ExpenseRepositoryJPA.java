package com.example.lifetracker.persistence.JPAs;

import com.example.lifetracker.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepositoryJPA
        extends JpaRepository<ExpenseEntity, Integer> {

    List<ExpenseEntity> findByUserId(
            Integer userId
    );

    List<ExpenseEntity> findByEventIdAndUserId(
            Integer eventId,
            Integer userId
    );

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM ExpenseEntity e
        WHERE e.eventId = :eventId
        AND e.userId = :userId
    """)
    Double getTotalByEvent(
            @Param("eventId") Integer eventId,
            @Param("userId") Integer userId
    );
}