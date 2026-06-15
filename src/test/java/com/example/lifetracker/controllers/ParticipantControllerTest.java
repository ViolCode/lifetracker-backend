package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.ParticipantRequestDTO;
import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;
import com.example.lifetracker.exceptions.BadRequestException;
import com.example.lifetracker.exceptions.GlobalExceptionHandler;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.services.EventParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private EventParticipantService service;

    @InjectMocks
    private ParticipantController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final ParticipantResponseDTO sampleParticipant = new ParticipantResponseDTO(
            1, 2, "other@test.com", "Other User", false, "Owner", LocalDateTime.of(2026, 5, 11, 10, 0)
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(service.getEventParticipants(1)).thenReturn(List.of(sampleParticipant));

        mockMvc.perform(get("/events/1/participants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[0].userName").value("Other User"));
    }

    @Test
    void getAll_returns404WhenEventNotFound() throws Exception {
        when(service.getEventParticipants(999))
                .thenThrow(new NotFoundException("Event not found or you don't have permission"));

        mockMvc.perform(get("/events/999/participants"))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_returns200WhenValid() throws Exception {
        ParticipantRequestDTO request = new ParticipantRequestDTO(2, null);

        when(service.addParticipant(eq(1), eq(2))).thenReturn(sampleParticipant);

        mockMvc.perform(post("/events/1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void add_returns400WhenUserIdMissing() throws Exception {
        String body = "{\"userId\": null}";

        mockMvc.perform(post("/events/1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_returns400WhenInvitingSelf() throws Exception {
        ParticipantRequestDTO request = new ParticipantRequestDTO(1, null);

        when(service.addParticipant(1, 1))
                .thenThrow(new BadRequestException("You cannot invite yourself to an event"));

        mockMvc.perform(post("/events/1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void remove_returns200() throws Exception {
        doNothing().when(service).removeParticipant(1, 2);

        mockMvc.perform(delete("/events/1/participants/2"))
                .andExpect(status().isOk());
    }

    @Test
    void remove_returns404WhenEventNotFound() throws Exception {
        doThrow(new NotFoundException("Event not found or you don't have permission"))
                .when(service).removeParticipant(999, 2);

        mockMvc.perform(delete("/events/999/participants/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void search_returns200WithResults() throws Exception {
        UserSearchResponseDTO user = new UserSearchResponseDTO(2, "Other User", "other@test.com");

        when(service.searchUsersToInvite(1, "ot")).thenReturn(List.of(user));

        mockMvc.perform(get("/events/1/participants/search").param("q", "ot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Other User"));
    }

    @Test
    void search_returnsEmptyListForShortQuery() throws Exception {
        when(service.searchUsersToInvite(1, "a")).thenReturn(List.of());

        mockMvc.perform(get("/events/1/participants/search").param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
