package com.example.lifetracker.dtos;

public record UserSearchResponseDTO(
        Integer id,
        String name,
        String email
) {
}