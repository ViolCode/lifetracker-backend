package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.AuthResponseDTO;
import com.example.lifetracker.DTOs.LoginRequestDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.security.UserPrincipal;
import com.example.lifetracker.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {

        var user = service.register(dto.email(), dto.password(), dto.name());

        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    @PostMapping("/login")
    public AuthResponseDTO login(
            @RequestBody LoginRequestDTO dto,
            HttpServletRequest request
    ) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        return new AuthResponseDTO(
                principal.getId(),
                principal.getUser().getEmail(),
                principal.getUser().getName()
        );
    }
}
