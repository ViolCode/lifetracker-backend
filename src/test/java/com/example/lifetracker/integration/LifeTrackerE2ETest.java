package com.example.lifetracker.integration;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.ExpenseRequestDTO;
import com.example.lifetracker.DTOs.ParticipantRequestDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.persistence.JPAs.EventRepositoryJPA;
import com.example.lifetracker.persistence.JPAs.ExpenseRepositoryJPA;
import com.example.lifetracker.persistence.JPAs.UserRepositoryJPA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LifeTrackerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepositoryJPA userRepositoryJPA;
    @Autowired private EventRepositoryJPA eventRepositoryJPA;
    @Autowired private ExpenseRepositoryJPA expenseRepositoryJPA;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        expenseRepositoryJPA.deleteAll();
        eventRepositoryJPA.deleteAll();
        userRepositoryJPA.deleteAll();
    }

    @Test
    void fullLifecycle_registerLoginCreateEventAddExpenseInviteParticipant() throws Exception {
        register("owner@e2e.com", "pass1234", "Owner User");
        register("guest@e2e.com", "pass1234", "Guest User");

        int guestId = userRepositoryJPA.findByEmail("guest@e2e.com")
                .orElseThrow().getId();

        MockHttpSession ownerSession = login("owner@e2e.com", "pass1234");

        EventRequestDTO eventRequest = new EventRequestDTO(
                "Team Dinner", "Annual team event", "Amsterdam",
                LocalDate.of(2026, 12, 20), LocalTime.of(18, 0), LocalTime.of(22, 0)
        );

        MvcResult createEventResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest))
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Dinner"))
                .andExpect(jsonPath("$.location").value("Amsterdam"))
                .andReturn();

        int eventId = parseId(createEventResult);

        ExpenseRequestDTO expenseRequest = new ExpenseRequestDTO(eventId, 75.50, "Restaurant bill");

        MvcResult createExpenseResult = mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75.50))
                .andExpect(jsonPath("$.description").value("Restaurant bill"))
                .andReturn();

        int expenseId = parseId(createExpenseResult);

        mockMvc.perform(get("/expenses/event/{eventId}", eventId)
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(expenseId))
                .andExpect(jsonPath("$[0].amount").value(75.50));

        ParticipantRequestDTO participantRequest = new ParticipantRequestDTO(guestId, false);

        mockMvc.perform(post("/events/{eventId}/participants", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantRequest))
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(guestId))
                .andExpect(jsonPath("$.userName").value("Guest User"));

        mockMvc.perform(get("/events/{eventId}/participants", eventId)
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(guestId));

        mockMvc.perform(delete("/events/{eventId}/participants/{userId}", eventId, guestId)
                        .session(ownerSession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/events/{eventId}/participants", eventId)
                        .session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(delete("/expenses/{id}", expenseId)
                        .session(ownerSession))
                .andExpect(status().isOk());

        assertFalse(expenseRepositoryJPA.existsById(expenseId), "Expense should be deleted");

        mockMvc.perform(delete("/events/{id}", eventId)
                        .session(ownerSession))
                .andExpect(status().isOk());

        assertFalse(eventRepositoryJPA.existsById(eventId), "Event should be deleted");
    }

    private void register(String email, String password, String name) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequestDTO(email, password, name))))
                .andExpect(status().isOk());
    }

    private MockHttpSession login(String email, String password) throws Exception {
        String loginJson = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession();
    }

    private int parseId(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asInt();
    }
}