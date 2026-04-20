package com.example.lifetracker.domains;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Event {

    private final Integer id;

    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private String location;
    @Setter
    private LocalDate plannedDate;
    @Setter
    private LocalTime startTime;
    @Setter
    private LocalTime endTime;
    @Setter
    private Double plannedBudget;

    // Constructor for new events (no id yet)
    public Event(String title, String description, String location,
                 LocalDate plannedDate, LocalTime startTime,
                 LocalTime endTime, Double plannedBudget) {

        this.id = null;
        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.plannedBudget = plannedBudget;
    }

    // Constructor for existing events (from DB)
    public Event(Integer id, String title, String description, String location,
                 LocalDate plannedDate, LocalTime startTime,
                 LocalTime endTime, Double plannedBudget) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.plannedBudget = plannedBudget;
    }
}