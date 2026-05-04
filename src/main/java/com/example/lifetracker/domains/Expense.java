package com.example.lifetracker.domains;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Expense {

    private final Integer id;
    private final Integer eventId;
    private final Integer userId;
    private Double amount;
    private String description;
    private final LocalDateTime createdAt;

    public Expense(Integer eventId, Integer userId, Double amount, String description) {
        this(null, eventId, userId, amount, description, LocalDateTime.now());
    }

    public Expense(Integer id, Integer eventId, Integer userId,
                   Double amount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }

    public void update(Double amount, String description) {
        this.amount = amount;
        this.description = description;
    }
}