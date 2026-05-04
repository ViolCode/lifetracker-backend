package com.example.lifetracker.domains;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Event {

    private final Integer id;
    private String title;
    private String description;
    private String location;
    private LocalDate plannedDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Event(String title, String description, String location,
                 LocalDate plannedDate, LocalTime startTime,
                 LocalTime endTime) {
        this(null, title, description, location, plannedDate, startTime, endTime);
    }

    public Event(Integer id, String title, String description, String location,
                 LocalDate plannedDate, LocalTime startTime,
                 LocalTime endTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void update(String title, String description, String location,
                       LocalDate plannedDate, LocalTime startTime,
                       LocalTime endTime) {

        this.title = title;
        this.description = description;
        this.location = location;
        this.plannedDate = plannedDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}