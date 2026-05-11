package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void shouldCreateValidEvent() {

        Event event = new Event(
                "Gym",
                "Workout",
                "Eindhoven",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertEquals("Gym", event.getTitle());
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new Event(
                                "",
                                "desc",
                                "loc",
                                LocalDate.now(),
                                null,
                                null
                        )
                );

        assertEquals(
                "Title is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenPlannedDateIsNull() {

        assertThrows(
                ValidationException.class,
                () -> new Event(
                        "Gym",
                        "desc",
                        "loc",
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldThrowWhenEndTimeBeforeStartTime() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new Event(
                                "Gym",
                                "desc",
                                "loc",
                                LocalDate.now(),
                                LocalTime.of(15, 0),
                                LocalTime.of(14, 0)
                        )
                );

        assertEquals(
                "End time cannot be before start time",
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateEvent() {

        Event event = new Event(
                "Old",
                "desc",
                "loc",
                LocalDate.now(),
                null,
                null
        );

        event.update(
                "New",
                "new desc",
                "Amsterdam",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertEquals("New", event.getTitle());
        assertEquals("Amsterdam", event.getLocation());
    }
}