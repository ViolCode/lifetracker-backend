package com.example.lifetracker.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseRequestDTO {
    private Integer eventId;
    private Double amount;
    private String description;
}