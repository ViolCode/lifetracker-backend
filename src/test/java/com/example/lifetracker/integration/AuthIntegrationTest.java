package com.example.lifetracker.integration;

import com.example.lifetracker.dtos.RegisterRequestDTO;
import com.example.lifetracker.persistence.jpas.UserRepositoryJPA;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepositoryJPA userRepositoryJPA;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepositoryJPA.deleteAll();
    }

    @Test
    void register_withValidData_returnsUserAndPersists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("user@test.com", "password123", "Test User");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.id").exists());

        assert userRepositoryJPA.findByEmail("user@test.com").isPresent();
    }

    @Test
    void register_thenLogin_sessionIsAuthenticated() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("login@test.com", "password123", "Login User");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        String loginJson = "{\"email\":\"login@test.com\",\"password\":\"password123\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@test.com"))
                .andExpect(jsonPath("$.name").value("Login User"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("duplicate@test.com", "password123", "User One");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_withInvalidData_returns400() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("", "123", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("auth@test.com", "correctpass", "Auth User");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        String loginJson = "{\"email\":\"auth@test.com\",\"password\":\"wrongpass\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withoutSession_returns403() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_invalidatesSession() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("logout@test.com", "password123", "Logout User");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        String loginJson = "{\"email\":\"logout@test.com\",\"password\":\"password123\"}";

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(get("/events").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/logout").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/events").session(session))
                .andExpect(status().isForbidden());
    }
}
