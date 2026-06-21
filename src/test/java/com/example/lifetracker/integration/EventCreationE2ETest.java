package com.example.lifetracker.integration;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.persistence.JPAs.EventRepositoryJPA;
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
public class EventCreationE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepositoryJPA userRepositoryJPA;
    @Autowired private EventRepositoryJPA eventRepositoryJPA;
    @Autowired private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        eventRepositoryJPA.deleteAll();
        userRepositoryJPA.deleteAll();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequestDTO("creator@e2e.com", "pass1234", "Event Creator"))))
                .andExpect(status().isOk());

        session = login("creator@e2e.com", "pass1234");
    }

    @Test
    void createEvent_withValidData_persistsAndReturnsEvent() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Team Lunch", "Monthly team lunch", "Eindhoven",
                LocalDate.of(2026, 12, 15), LocalTime.of(12, 0), LocalTime.of(13, 30)
        );

        MvcResult result = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Team Lunch"))
                .andExpect(jsonPath("$.description").value("Monthly team lunch"))
                .andExpect(jsonPath("$.location").value("Eindhoven"))
                .andReturn();

        int eventId = parseId(result);
        assertTrue(eventRepositoryJPA.existsById(eventId), "Event should be saved in database");
    }

    @Test
    void createEvent_missingTitle_returns400() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "", "Some description", "Amsterdam",
                LocalDate.of(2026, 11, 10), null, null
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_missingDate_returns400() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "No Date Event", null, "Rotterdam",
                null, null, null
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_withoutAuthentication_returns403() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Unauthorized Event", null, "Utrecht",
                LocalDate.of(2026, 10, 5), null, null
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createEvent_thenRetrieveById_returnsCorrectData() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Annual Review", "Year-end review", "Den Haag",
                LocalDate.of(2026, 12, 31), LocalTime.of(9, 0), LocalTime.of(17, 0)
        );

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        int eventId = parseId(createResult);

        mockMvc.perform(get("/events/{id}", eventId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Annual Review"))
                .andExpect(jsonPath("$.location").value("Den Haag"));
    }

    @Test
    void createMultipleEvents_thenGetAll_returnsAll() throws Exception {
        EventRequestDTO event1 = new EventRequestDTO(
                "Workshop", null, "Breda",
                LocalDate.of(2026, 11, 5), LocalTime.of(10, 0), LocalTime.of(12, 0)
        );
        EventRequestDTO event2 = new EventRequestDTO(
                "Conference", null, "Maastricht",
                LocalDate.of(2026, 11, 20), LocalTime.of(9, 0), LocalTime.of(18, 0)
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event1))
                        .session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event2))
                        .session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/events").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createEvent_thenUpdateIt_persistsChanges() throws Exception {
        EventRequestDTO original = new EventRequestDTO(
                "Old Title", "Old desc", "Tilburg",
                LocalDate.of(2026, 10, 10), LocalTime.of(8, 0), LocalTime.of(10, 0)
        );

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        int eventId = parseId(createResult);

        EventRequestDTO updated = new EventRequestDTO(
                "New Title", "New desc", "Nijmegen",
                LocalDate.of(2026, 10, 20), LocalTime.of(9, 0), LocalTime.of(11, 0)
        );

        mockMvc.perform(put("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.location").value("Nijmegen"));

        assertEquals("New Title", eventRepositoryJPA.findById(eventId).orElseThrow().getTitle());
    }

    @Test
    void createEvent_thenDeleteIt_removesFromDatabase() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Temporary Event", null, "Groningen",
                LocalDate.of(2026, 9, 1), null, null
        );

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        int eventId = parseId(createResult);
        assertTrue(eventRepositoryJPA.existsById(eventId));

        mockMvc.perform(delete("/events/{id}", eventId).session(session))
                .andExpect(status().isOk());

        assertFalse(eventRepositoryJPA.existsById(eventId), "Event should be deleted from database");
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