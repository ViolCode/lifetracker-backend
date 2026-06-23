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

    private Event(Builder builder) {
        validate(builder.title, builder.plannedDate, builder.startTime, builder.endTime);
        this.id = builder.id;
        this.ownerId = builder.ownerId;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.plannedDate = builder.plannedDate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
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
        this(new Builder().id(id).title(title).description(description).location(location)
                .plannedDate(plannedDate).startTime(startTime).endTime(endTime));
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer ownerId;
        private String title;
        private String description;
        private String location;
        private LocalDate plannedDate;
        private LocalTime startTime;
        private LocalTime endTime;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder ownerId(Integer ownerId) { this.ownerId = ownerId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder plannedDate(LocalDate date) { this.plannedDate = date; return this; }
        public Builder startTime(LocalTime time) { this.startTime = time; return this; }
        public Builder endTime(LocalTime time) { this.endTime = time; return this; }

        public Event build() { return new Event(this); }
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