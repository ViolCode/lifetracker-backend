package com.example.lifetracker.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String location;
    private LocalDate plannedDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer ownerId;
}