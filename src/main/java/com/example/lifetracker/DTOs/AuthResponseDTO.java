package com.example.lifetracker.DTOs;

public record AuthResponseDTO(
        Integer id,
        String email,
        String name
) {
}