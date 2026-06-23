package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Event {
    private final Integer id;
    private final Integer ownerId;
    private String title;
    private String description;
    private String location;
    private LocalDate plannedDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> participantIds;
    private Double plannedBudget;

    public Event(
            Integer id,
            String title,
            String description,
            String location,
            LocalDate plannedDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer ownerId
    ) {
        validate(title, plannedDate, startTime, endTime);

        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.plannedBudget = null;
        this.participantIds = new ArrayList<>();
    }

    public Event(
            Integer id,
            String title,
            String description,
            String location,
            LocalDate plannedDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this(id, title, description, location, plannedDate, startTime, endTime, (Integer) null);
    }

    public Event(
            String title,
            String description,
            String location,
            LocalDate plannedDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this(null, title, description, location, plannedDate, startTime, endTime);
    }

    public void update(
            String title,
            String description,
            String location,
            LocalDate plannedDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        validate(title, plannedDate, startTime, endTime);

        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void validate(
            String title,
            LocalDate plannedDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title is required");
        }

        if (plannedDate == null) {
            throw new ValidationException("Planned date is required");
        }

        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new ValidationException("End time cannot be before start time");
        }
    }
}