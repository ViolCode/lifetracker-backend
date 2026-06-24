package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private static final LocalDate FIXED_DATE = LocalDate.of(2024, Month.JANUARY, 15);

    @Test
    void shouldCreateValidEvent() {

        Event event = new Event(
                "Gym",
                "Workout",
                "Eindhoven",
                FIXED_DATE,
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
                        () -> new Event("", "desc", "loc", FIXED_DATE, null, null)
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
                () -> new Event("Gym", "desc", "loc", null, null, null)
        );
    }

    @Test
    void shouldThrowWhenEndTimeBeforeStartTime() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new Event("Gym", "desc", "loc", FIXED_DATE, LocalTime.of(
                                15, 0), LocalTime.of(14, 0))
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
                FIXED_DATE,
                null,
                null
        );

        event.update(
                "New",
                "new desc",
                "Amsterdam",
                FIXED_DATE.plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertEquals("New", event.getTitle());
        assertEquals("Amsterdam", event.getLocation());
    }

    @Test
    void shouldThrowWhenTitleContainsOnlySpaces() {

        assertThrows(
                ValidationException.class,
                () -> new Event("   ", "desc", "loc", FIXED_DATE, null, null)
        );
    }

    @Test
    void shouldThrowWhenUpdatingWithBlankTitle() {

        Event event = new Event(
                "Old",
                "desc",
                "loc",
                FIXED_DATE,
                null,
                null
        );

        assertThrows(
                ValidationException.class,
                () -> event.update("", "desc", "loc", FIXED_DATE, null, null)
        );
    }

    @Test
    void shouldThrowWhenUpdatingWithInvalidTimes() {

        Event event = new Event(
                "Old",
                "desc",
                "loc",
                FIXED_DATE,
                null,
                null
        );

        assertThrows(
                ValidationException.class,
                () -> event.update("New", "desc", "loc", FIXED_DATE, LocalTime.of(15, 0), LocalTime.of(14, 0))
        );
    }

    @Test
    void shouldAllowSameStartAndEndTime() {

        Event event = new Event(
                "Gym",
                "desc",
                "loc",
                FIXED_DATE,
                LocalTime.of(10, 0),
                LocalTime.of(10, 0)
        );

        assertNotNull(event);
    }
}