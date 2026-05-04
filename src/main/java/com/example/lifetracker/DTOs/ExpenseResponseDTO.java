package com.example.lifetracker.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExpenseResponseDTO {
    private Integer id;
    private Integer eventId;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
}