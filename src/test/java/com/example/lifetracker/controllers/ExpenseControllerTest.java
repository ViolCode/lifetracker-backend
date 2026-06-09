package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.ExpenseRequestDTO;
import com.example.lifetracker.DTOs.ExpenseResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exceptions.GlobalExceptionHandler;
import com.example.lifetracker.mappers.ExpenseMapper;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {
    @Mock
    private ExpenseService service;
    @Mock
    private ExpenseMapper mapper;
    @InjectMocks
    private ExpenseController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Expense sampleExpense;
    private Event sampleEvent;
    private ExpenseResponseDTO sampleResponse;
    private ExpenseRequestDTO sampleRequest;

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

        sampleExpense = new Expense(
                1,
                10,
                1,
                49.99,
                "Dinner",
                LocalDateTime.now()
        );

        sampleEvent = new Event(
                10,
                "Birthday",
                "Party",
                "Amsterdam",
                LocalDate.now(),
                null,
                null
        );

        sampleResponse = new ExpenseResponseDTO(
                1,
                10,
                "Birthday",
                LocalDate.now(),
                49.99,
                "Dinner",
                sampleExpense.getCreatedAt()
        );

        sampleRequest = new ExpenseRequestDTO(
                10,
                49.99,
                "Dinner"
        );
    }

    @Test
    void create_returns200_whenValid() throws Exception {

        when(mapper.toDomain(any(ExpenseRequestDTO.class)))
                .thenReturn(sampleExpense);

        when(service.create(sampleExpense))
                .thenReturn(sampleExpense);

        when(service.getEventForExpense(sampleExpense))
                .thenReturn(sampleEvent);

        when(mapper.toDTO(sampleExpense, sampleEvent))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(49.99))
                .andExpect(jsonPath("$.eventTitle").value("Birthday"));
    }

    @Test
    void getAll_returns200WithList() throws Exception {

        when(service.getAll())
                .thenReturn(List.of(sampleExpense));

        when(service.getEventForExpense(sampleExpense))
                .thenReturn(sampleEvent);

        when(mapper.toDTO(sampleExpense, sampleEvent))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description")
                        .value("Dinner"));
    }

    @Test
    void getAll_returnsEmptyList() throws Exception {

        when(service.getAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByEvent_returns200WithList() throws Exception {

        when(service.getByEvent(10))
                .thenReturn(List.of(sampleExpense));

        when(service.getEventForExpense(sampleExpense))
                .thenReturn(sampleEvent);

        when(mapper.toDTO(sampleExpense, sampleEvent))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/expenses/event/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId")
                        .value(10));
    }

    @Test
    void create_returns400_whenAmountInvalid() throws Exception {

        ExpenseRequestDTO request =
                new ExpenseRequestDTO(
                        10,
                        0.0,
                        "Dinner"
                );

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returns200() throws Exception {
        mockMvc.perform(delete("/expenses/1")).andExpect(status().isOk());
    }
}