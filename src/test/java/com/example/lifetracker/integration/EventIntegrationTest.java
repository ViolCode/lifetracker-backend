package com.example.lifetracker.integration;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.RegisterRequestDTO;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.entities.UserEntity;
import com.example.lifetracker.persistence.JPAs.EventRepositoryJPA;
import com.example.lifetracker.persistence.JPAs.UserRepositoryJPA;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpSession;
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
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepositoryJPA userRepositoryJPA;
    @Autowired
    private EventRepositoryJPA eventRepositoryJPA;
    @Autowired
    private ObjectMapper objectMapper;
    private Integer loggedInUserId;
    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        eventRepositoryJPA.deleteAll();
        userRepositoryJPA.deleteAll();

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "eventuser@test.com",
                "password123",
                "Event User"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String loginJson = "{\"email\":\"eventuser@test.com\",\"password\":\"password123\"}";

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession();

        UserEntity user = userRepositoryJPA.findByEmail("eventuser@test.com").orElse(null);
        loggedInUserId = user != null ? user.getId() : null;
    }

    @Test
    void testCreateAndGetEvent() throws Exception {
        EventRequestDTO eventRequest = new EventRequestDTO(
                "Birthday Party",
                "Celebrating birthday",
                "Amsterdam",
                LocalDate.of(2026, 12, 15),
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Birthday Party"))
                .andExpect(jsonPath("$.location").value("Amsterdam"))
                .andExpect(jsonPath("$.ownerId").value(loggedInUserId))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        Integer eventId = objectMapper.readTree(responseJson).get("id").asInt();

        EventEntity savedEvent = eventRepositoryJPA.findById(eventId).orElse(null);
        assertNotNull(savedEvent, "Event should be saved in database");
        assertEquals("Birthday Party", savedEvent.getTitle());
        assertEquals(loggedInUserId, savedEvent.getOwnerId());

        mockMvc.perform(get("/events/{id}", eventId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Birthday Party"))
                .andExpect(jsonPath("$.location").value("Amsterdam"))
                .andExpect(jsonPath("$.ownerId").value(loggedInUserId));
    }

    @Test
    void testGetAllEvents() throws Exception {
        EventRequestDTO event1 = new EventRequestDTO("Event One", null, "Rotterdam",
                LocalDate.of(2026, 11, 1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        EventRequestDTO event2 = new EventRequestDTO("Event Two", null, "Utrecht",
                LocalDate.of(2026, 11, 2), LocalTime.of(14, 0), LocalTime.of(16, 0));

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

        mockMvc.perform(get("/events")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateEvent() throws Exception {
        EventRequestDTO original = new EventRequestDTO("Original Title", "desc", "Den Haag",
                LocalDate.of(2026, 10, 10), LocalTime.of(9, 0), LocalTime.of(11, 0));

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Integer eventId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asInt();

        EventRequestDTO updated = new EventRequestDTO("Updated Title", "new desc", "Eindhoven",
                LocalDate.of(2026, 10, 15), LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(put("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.location").value("Eindhoven"));

        EventEntity savedEvent = eventRepositoryJPA.findById(eventId).orElse(null);
        assertNotNull(savedEvent);
        assertEquals("Updated Title", savedEvent.getTitle());
    }

    @Test
    void testDeleteEvent() throws Exception {
        EventRequestDTO eventRequest = new EventRequestDTO("To Be Deleted", null, "Breda",
                LocalDate.of(2026, 9, 5), LocalTime.of(8, 0), LocalTime.of(9, 0));

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Integer eventId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asInt();

        mockMvc.perform(delete("/events/{id}", eventId)
                        .session(session))
                .andExpect(status().isOk());

        assertFalse(eventRepositoryJPA.existsById(eventId), "Event should be deleted from database");
    }
}