package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.ExpenseRequestDTO;
import com.example.lifetracker.DTOs.ExpenseResponseDTO;
import com.example.lifetracker.domains.Expense;
import com.example.lifetracker.exeptions.GlobalExceptionHandler;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock private ExpenseService service;
    @Mock private ExpenseMapper mapper;

    @InjectMocks private ExpenseController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Expense sampleExpense;
    private ExpenseResponseDTO sampleResponse;
    private ExpenseRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleExpense = new Expense(1, 10, 1, 49.99, "Dinner", LocalDateTime.now());

        sampleResponse = new ExpenseResponseDTO();
        sampleResponse.setId(1);
        sampleResponse.setEventId(10);
        sampleResponse.setAmount(49.99);
        sampleResponse.setDescription("Dinner");
        sampleResponse.setCreatedAt(sampleExpense.getCreatedAt());

        sampleRequest = new ExpenseRequestDTO();
        sampleRequest.setEventId(10);
        sampleRequest.setAmount(49.99);
        sampleRequest.setDescription("Dinner");
    }

    @Test
    void create_returns200_whenValid() throws Exception {
        when(mapper.toDomain(any(ExpenseRequestDTO.class))).thenReturn(sampleExpense);
        when(service.create(sampleExpense)).thenReturn(sampleExpense);
        when(mapper.toDTO(sampleExpense)).thenReturn(sampleResponse);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(49.99));
    }

    @Test
    void create_returns400_whenAmountIsInvalid() throws Exception {
        when(mapper.toDomain(any(ExpenseRequestDTO.class))).thenReturn(sampleExpense);
        when(service.create(any())).thenThrow(new IllegalArgumentException("Expense must be greater than 0"));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns500_whenUnexpectedErrorOccurs() throws Exception {
        when(mapper.toDomain(any(ExpenseRequestDTO.class))).thenReturn(sampleExpense);
        when(service.create(any())).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Something went wrong"));
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(service.getAll()).thenReturn(List.of(sampleExpense));
        when(mapper.toDTO(sampleExpense)).thenReturn(sampleResponse);

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Dinner"));
    }

    @Test
    void getAll_returns200WithEmptyList() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByEvent_returns200WithList() throws Exception {
        when(service.getByEvent(10)).thenReturn(List.of(sampleExpense));
        when(mapper.toDTO(sampleExpense)).thenReturn(sampleResponse);

        mockMvc.perform(get("/expenses/event/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(10));
    }

    @Test
    void getByEvent_returns200WithEmptyList_whenNoExpenses() throws Exception {
        when(service.getByEvent(999)).thenReturn(List.of());

        mockMvc.perform(get("/expenses/event/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}