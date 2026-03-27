package com.example.lifetracker.DTOs;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String location;
    private LocalDate plannedDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double plannedBudget;
}