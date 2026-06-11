package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exceptions.GlobalExceptionHandler;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.services.EventService;
import com.example.lifetracker.services.ExpenseService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService service;
    @Mock
    private EventMapper mapper;
    @Mock
    private ExpenseService expenseService;
    @InjectMocks
    private EventController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Event sampleEvent;
    private EventRequestDTO sampleRequest;
    private EventResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        objectMapper.registerModule(
                new JavaTimeModule()
        );

        sampleEvent = new Event(
                1,
                "Gym",
                "Workout session",
                "Eindhoven",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        sampleRequest = new EventRequestDTO(
                "Gym",
                "Workout session",
                "Eindhoven",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        sampleResponse = new EventResponseDTO(
                1,
                "Gym",
                "Workout session",
                "Eindhoven",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                25.50
        );
    }

    @Test
    void getAll_returns200WithList() throws Exception {

        when(service.getAll())
                .thenReturn(List.of(sampleEvent));

        when(expenseService.getTotalByEvent(1))
                .thenReturn(25.50);

        when(mapper.toDTO(sampleEvent, 25.50))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Gym"))
                .andExpect(jsonPath("$[0].location").value("Eindhoven"))
                .andExpect(jsonPath("$[0].totalExpenses").value(25.50));
    }

    @Test
    void getAll_returnsEmptyList() throws Exception {

        when(service.getAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_returns200() throws Exception {

        when(service.getById(1))
                .thenReturn(sampleEvent);

        when(expenseService.getTotalByEvent(1))
                .thenReturn(25.50);

        when(mapper.toDTO(sampleEvent, 25.50))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Gym"));
    }

    @Test
    void getById_returns404_whenEventDoesNotExist() throws Exception {

        when(service.getById(999))
                .thenThrow(
                        new NotFoundException(
                                "Event not found"
                        )
                );

        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Event not found"));
    }

    @Test
    void getHistory_returns200WithList() throws Exception {

        when(service.getPastEvents())
                .thenReturn(List.of(sampleEvent));

        when(expenseService.getTotalByEvent(1))
                .thenReturn(25.50);

        when(mapper.toDTO(sampleEvent, 25.50))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/events/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create_returns200_whenValid() throws Exception {

        when(mapper.toDomain(any(EventRequestDTO.class)))
                .thenReturn(sampleEvent);

        when(service.create(sampleEvent))
                .thenReturn(sampleEvent);

        when(mapper.toDTO(sampleEvent, 0.0))
                .thenReturn(
                        new EventResponseDTO(
                                1,
                                "Gym",
                                "Workout session",
                                "Eindhoven",
                                LocalDate.of(2026, 5, 11),
                                LocalTime.of(10, 0),
                                LocalTime.of(12, 0),
                                0.0
                        )
                );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Gym"));
    }

    @Test
    void create_returns400_whenTitleIsMissing() throws Exception {

        EventRequestDTO invalidRequest = new EventRequestDTO(
                "",
                "Workout",
                "Eindhoven",
                LocalDate.now(),
                null,
                null
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns200_whenValid() throws Exception {

        when(mapper.toDomain(any(EventRequestDTO.class)))
                .thenReturn(sampleEvent);

        when(service.update(eq(1), any(Event.class)))
                .thenReturn(sampleEvent);

        when(expenseService.getTotalByEvent(1))
                .thenReturn(25.50);

        when(mapper.toDTO(sampleEvent, 25.50))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Gym"));
    }

    @Test
    void update_returns404_whenEventDoesNotExist() throws Exception {

        when(mapper.toDomain(any(EventRequestDTO.class)))
                .thenReturn(sampleEvent);

        when(service.update(eq(999), any(Event.class)))
                .thenThrow(
                        new NotFoundException(
                                "Event not found"
                        )
                );

        mockMvc.perform(put("/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Event not found"));
    }

    @Test
    void delete_returns200() throws Exception {

        doNothing()
                .when(service)
                .delete(1);

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns404_whenEventDoesNotExist() throws Exception {

        doThrow(
                new NotFoundException(
                        "Event not found"
                )
        )
                .when(service)
                .delete(999);

        mockMvc.perform(delete("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Event not found"));
    }

    @Test
    void update_returns400_whenTitleIsMissing() throws Exception {

        EventRequestDTO invalidRequest = new EventRequestDTO(
                "",
                "Workout",
                "Eindhoven",
                LocalDate.now(),
                null,
                null
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHistory_returnsEmptyList() throws Exception {

        when(service.getPastEvents())
                .thenReturn(List.of());

        mockMvc.perform(get("/events/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
