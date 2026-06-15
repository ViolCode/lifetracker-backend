package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.LoginRequestDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.domains.User;
import com.example.lifetracker.security.UserPrincipal;
import com.example.lifetracker.services.AuthService;
import com.example.lifetracker.exceptions.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.lifetracker.exceptions.ConflictException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void registerReturns200() throws Exception {

        User user = new User(
                1,
                "test@test.com",
                "hashed",
                "John",
                false
        );

        when(authService.register(
                any(),
                any(),
                any()
        )).thenReturn(user);

        RegisterRequestDTO request =
                new RegisterRequestDTO(
                        "test@test.com",
                        "password123",
                        "John"
                );

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value("test@test.com"))
                .andExpect(jsonPath("$.name")
                        .value("John"));
    }

    @Test
    void loginReturns200() throws Exception {

        User user = new User(
                1,
                "test@test.com",
                "hashed",
                "John",
                false
        );

        UserPrincipal principal =
                new UserPrincipal(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        LoginRequestDTO request =
                new LoginRequestDTO(
                        "test@test.com",
                        "password123"
                );

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value("test@test.com"))
                .andExpect(jsonPath("$.name")
                        .value("John"));
    }

    @Test
    void registerReturns400ForInvalidRequest() throws Exception {

        RegisterRequestDTO request =
                new RegisterRequestDTO(
                        "",
                        "123",
                        ""
                );

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerReturns409WhenEmailAlreadyExists() throws Exception {

        when(authService.register(any(), any(), any()))
                .thenThrow(new ConflictException("Email already exists"));

        RegisterRequestDTO request =
                new RegisterRequestDTO(
                        "test@test.com",
                        "password123",
                        "John"
                );

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    @Test
    void loginReturns500WhenCredentialsAreInvalid() throws Exception {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequestDTO request =
                new LoginRequestDTO(
                        "test@test.com",
                        "wrongpassword"
                );

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void logoutReturns200() throws Exception {

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());
    }
}