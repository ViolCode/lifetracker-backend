package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.security.UserPrincipal;
import com.example.lifetracker.services.EventParticipantService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventRepository repository;
    private EventParticipantService participantService;
    private EventService service;

    @BeforeEach
    void setUp() {
        repository = mock(EventRepository.class);
        participantService = mock(EventParticipantService.class);
        service = new EventService(repository, participantService);

        User user = new User(
                1,
                "test@test.com",
                "password",
                "Test User",
                false
        );

        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAllEvents() {
        Event event = new Event(
                1,
                "Gym",
                "",
                "",
                LocalDate.now(),
                null,
                null
        );

        when(repository.getAll(1)).thenReturn(List.of(event));
        when(participantService.getParticipatedEventIds(1)).thenReturn(List.of());
        List<Event> result = service.getAll();
        assertEquals(1, result.size());
        verify(repository).getAll(1);
    }

    @Test
    void shouldReturnEventById() {
        Event event = new Event(
                1,
                "Gym",
                "",
                "",
                LocalDate.now(),
                null,
                null
        );

        when(repository.getById(1, 1)).thenReturn(Optional.of(event));
        Event result = service.getById(1);
        assertEquals("Gym", result.getTitle());
        verify(repository).getById(1, 1);
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        when(repository.getById(eq(1), anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(1));
    }

    @Test
    void shouldCreateEvent() {
        Event event = new Event(
                "Gym",
                "",
                "",
                LocalDate.now(),
                null,
                null
        );

        when(repository.save(event, 1)).thenReturn(event);
        Event result = service.create(event);
        assertEquals("Gym", result.getTitle());
        verify(repository).save(event, 1);
    }

    @Test
    void shouldReturnPastEvents() {

        Event event = new Event(
                1,
                "Old Event",
                "",
                "",
                LocalDate.now().minusDays(1),
                null,
                null
        );

        when(repository.getPastEvents(1)).thenReturn(List.of(event));
        List<Event> result = service.getPastEvents();
        assertEquals(1, result.size());
        verify(repository).getPastEvents(1);
    }

    @Test
    void shouldDeleteEvent() {
        Event event = new Event(
                1,
                "Gym",
                "",
                "",
                LocalDate.now(),
                null,
                null
        );

        when(repository.getById(1, 1)).thenReturn(Optional.of(event));
        service.delete(1);
        verify(repository).getById(1, 1);
        verify(repository).delete(1);
    }
}