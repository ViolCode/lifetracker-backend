package com.example.lifetracker.dtos;

public record AuthResponseDTO(
        Integer id,
        String email,
        String name
) {
}