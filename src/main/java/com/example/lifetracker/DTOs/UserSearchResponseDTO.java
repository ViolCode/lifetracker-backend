package com.example.lifetracker.DTOs;

public record UserSearchResponseDTO(
        Integer id,
        String name,
        String email
) {
}