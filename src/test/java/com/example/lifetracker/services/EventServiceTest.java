package com.example.lifetracker.services;

/*import com.example.lifetracker.domains.EventEntity;
import com.example.lifetracker.exeptions.NotFoundExeption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService();
    }

    @Test
    void shouldCreateEvent() {

        EventEntity event = new EventEntity(
                0,
                "Test Event",
                "Test Description",
                LocalDate.of(2026,5,10),
                LocalTime.of(10,0),
                LocalTime.of(12,0),
                "Utrecht",
                100
        );

        EventEntity created = eventService.createEvent(event);

        assertEquals("Test Event", created.getTitle());
        assertEquals("Utrecht", created.getLocation());
    }

    @Test
    void shouldGetEventById() {

        EventEntity event = eventService.getEventById(1);

        assertNotNull(event);
        assertEquals(1, event.getId());
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {

        assertThrows(NotFoundExeption.class, () -> {
            eventService.getEventById(999);
        });

    }
}*/