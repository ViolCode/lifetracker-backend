package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.AuthResponseDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {

        var user = service.register(
                dto.email(),
                dto.password(),
                dto.name()
        );

        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}