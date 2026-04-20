package com.example.lifetracker.DTOs;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class EventResponseDTO {
    private Integer id;
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
}