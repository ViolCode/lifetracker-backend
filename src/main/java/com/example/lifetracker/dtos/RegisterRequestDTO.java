package com.example.lifetracker.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        @NotBlank(message = "Name is required")
        String name
) {
}