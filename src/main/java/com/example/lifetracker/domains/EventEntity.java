package com.example.lifetracker.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String location;

    @Column(name = "planned_date")
    private LocalDate plannedDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "planned_budget")
    private Double plannedBudget;

    @Column(name = "owner_id")
    private Integer ownerId;
}