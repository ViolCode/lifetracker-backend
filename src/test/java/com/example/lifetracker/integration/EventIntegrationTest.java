package com.example.lifetracker.integration;

import com.example.lifetracker.dtos.EventRequestDTO;
import com.example.lifetracker.dtos.RegisterRequestDTO;
import com.example.lifetracker.entities.EventEntity;
import com.example.lifetracker.entities.UserEntity;
import com.example.lifetracker.persistence.jpas.EventRepositoryJPA;
import com.example.lifetracker.persistence.jpas.UserRepositoryJPA;
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
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepositoryJPA userRepositoryJPA;
    @Autowired private EventRepositoryJPA eventRepositoryJPA;
    @Autowired private ObjectMapper objectMapper;

    private Integer loggedInUserId;
    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        eventRepositoryJPA.deleteAll();
        userRepositoryJPA.deleteAll();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequestDTO("eventuser@test.com", "password123", "Event User"))))
                .andExpect(status().isOk());

        session = login("eventuser@test.com", "password123");

        UserEntity user = userRepositoryJPA.findByEmail("eventuser@test.com").orElseThrow();
        loggedInUserId = user.getId();
    }

    @Test
    void createEvent_persistsAndReturnsEventWithOwner() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Birthday Party", "Celebrating birthday", "Amsterdam",
                LocalDate.of(2026, Month.DECEMBER, 15), LocalTime.of(19, 0), LocalTime.of(23, 0)
        );

        MvcResult result = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Birthday Party"))
                .andExpect(jsonPath("$.location").value("Amsterdam"))
                .andExpect(jsonPath("$.ownerId").value(loggedInUserId))
                .andReturn();

        int eventId = parseId(result);

        EventEntity saved = eventRepositoryJPA.findById(eventId).orElse(null);
        assertNotNull(saved, "Event should be saved in database");
        assertEquals("Birthday Party", saved.getTitle());
        assertEquals(loggedInUserId, saved.getOwnerId());
    }

    @Test
    void getEventById_returnsCorrectData() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "Annual Review", "Year-end review", "Den Haag",
                LocalDate.of(2026, Month.DECEMBER, 31), LocalTime.of(9, 0), LocalTime.of(17, 0)
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
    void getAllEvents_returnsAllCreatedEvents() throws Exception {
        EventRequestDTO event1 = new EventRequestDTO("Event One", null, "Rotterdam",
                LocalDate.of(2026, Month.NOVEMBER, 1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        EventRequestDTO event2 = new EventRequestDTO("Event Two", null, "Utrecht",
                LocalDate.of(2026, Month.NOVEMBER, 2), LocalTime.of(14, 0), LocalTime.of(16, 0));

        mockMvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event1)).session(session)).andExpect(status().isOk());
        mockMvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event2)).session(session)).andExpect(status().isOk());

        mockMvc.perform(get("/events").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateEvent_persistsChanges() throws Exception {
        EventRequestDTO original = new EventRequestDTO("Original Title", "desc", "Den Haag",
                LocalDate.of(2026, Month.OCTOBER, 10), LocalTime.of(9, 0), LocalTime.of(11, 0));

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        int eventId = parseId(createResult);

        EventRequestDTO updated = new EventRequestDTO("Updated Title", "new desc", "Eindhoven",
                LocalDate.of(2026, Month.OCTOBER, 15), LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(put("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.location").value("Eindhoven"));

        assertEquals("Updated Title", eventRepositoryJPA.findById(eventId).orElseThrow().getTitle());
    }

    @Test
    void deleteEvent_removesFromDatabase() throws Exception {
        EventRequestDTO request = new EventRequestDTO("To Be Deleted", null, "Breda",
                LocalDate.of(2026, Month.SEPTEMBER, 5), LocalTime.of(8, 0), LocalTime.of(9, 0));

        MvcResult createResult = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        int eventId = parseId(createResult);

        mockMvc.perform(delete("/events/{id}", eventId).session(session))
                .andExpect(status().isOk());

        assertFalse(eventRepositoryJPA.existsById(eventId), "Event should be deleted from database");
    }

    @Test
    void createEvent_missingTitle_returns400() throws Exception {
        EventRequestDTO request = new EventRequestDTO(
                "", "Some description", "Amsterdam",
                LocalDate.of(2026, Month.NOVEMBER, 10), null, null
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
                LocalDate.of(2026, Month.OCTOBER, 5), null, null
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
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
