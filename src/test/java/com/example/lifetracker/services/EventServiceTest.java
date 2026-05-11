package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventRepository repository;
    private EventService service;

    @BeforeEach
    void setUp() {

        repository = Mockito.mock(EventRepository.class);

        service = new EventService(repository);
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

        when(repository.getAll())
                .thenReturn(List.of(event));

        List<Event> result = service.getAll();

        assertEquals(1, result.size());

        verify(repository).getAll();
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

        when(repository.getById(1))
                .thenReturn(Optional.of(event));

        Event result = service.getById(1);

        assertEquals("Gym", result.getTitle());
    }

    @Test
    void shouldThrowWhenEventNotFound() {

        when(repository.getById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getById(1)
        );
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

        when(repository.save(event))
                .thenReturn(event);

        Event result = service.create(event);

        assertEquals("Gym", result.getTitle());

        verify(repository).save(event);
    }

    @Test
    void shouldDeleteEvent() {

        service.delete(1);

        verify(repository).delete(1);
    }
}