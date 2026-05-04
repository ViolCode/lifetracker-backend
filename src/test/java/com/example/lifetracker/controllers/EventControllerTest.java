package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exeptions.GlobalExceptionHandler;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.exeptions.ValidationException;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock private EventService service;
    @Mock private EventMapper mapper;

    @InjectMocks private EventController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Event sampleEvent;
    private EventResponseDTO sampleResponse;
    private EventRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleEvent = new Event(1, "Team Lunch", "Monthly", "Office",
                LocalDate.of(2025, 6, 15), LocalTime.of(12, 0), LocalTime.of(13, 0));

        sampleResponse = new EventResponseDTO();
        sampleResponse.setId(1);
        sampleResponse.setTitle("Team Lunch");
        sampleResponse.setDescription("Monthly");
        sampleResponse.setLocation("Office");
        sampleResponse.setPlannedDate(LocalDate.of(2025, 6, 15));
        sampleResponse.setStartTime(LocalTime.of(12, 0));
        sampleResponse.setEndTime(LocalTime.of(13, 0));

        sampleRequest = new EventRequestDTO();
        sampleRequest.setTitle("Team Lunch");
        sampleRequest.setDescription("Monthly");
        sampleRequest.setLocation("Office");
        sampleRequest.setPlannedDate(LocalDate.of(2025, 6, 15));
        sampleRequest.setStartTime(LocalTime.of(12, 0));
        sampleRequest.setEndTime(LocalTime.of(13, 0));
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(service.getAll()).thenReturn(List.of(sampleEvent));
        when(mapper.toDTO(sampleEvent)).thenReturn(sampleResponse);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Team Lunch"));
    }

    @Test
    void getAll_returns200WithEmptyList() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getById_returns200_whenFound() throws Exception {
        when(service.getById(1)).thenReturn(sampleEvent);
        when(mapper.toDTO(sampleEvent)).thenReturn(sampleResponse);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Team Lunch"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(service.getById(99)).thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(get("/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Event not found"));
    }

    @Test
    void create_returns200_whenValid() throws Exception {
        when(mapper.toDomain(any(EventRequestDTO.class))).thenReturn(sampleEvent);
        when(service.create(sampleEvent)).thenReturn(sampleEvent);
        when(mapper.toDTO(sampleEvent)).thenReturn(sampleResponse);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Lunch"));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        when(mapper.toDomain(any(EventRequestDTO.class))).thenReturn(sampleEvent);
        when(service.create(any())).thenThrow(new ValidationException("Title is required"));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Title is required"));
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        when(mapper.toDomain(any(EventRequestDTO.class))).thenReturn(sampleEvent);
        when(service.update(eq(1), any())).thenReturn(sampleEvent);
        when(mapper.toDTO(sampleEvent)).thenReturn(sampleResponse);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        when(mapper.toDomain(any(EventRequestDTO.class))).thenReturn(sampleEvent);
        when(service.update(eq(99), any())).thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(put("/events/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Event not found"));
    }

    @Test
    void update_returns400_whenValidationFails() throws Exception {
        when(mapper.toDomain(any(EventRequestDTO.class))).thenReturn(sampleEvent);
        when(service.update(anyInt(), any())).thenThrow(new ValidationException("End time cannot be before start time"));

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("End time cannot be before start time"));
    }

    @Test
    void delete_returns200_whenSuccessful() throws Exception {
        doNothing().when(service).delete(1);

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isOk());

        verify(service).delete(1);
    }
}