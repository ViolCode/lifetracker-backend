package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exeptions.NotFoundException;
import com.example.lifetracker.exeptions.ValidationException;
import com.example.lifetracker.persistence.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;

    private Event validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new Event(
                1, "Team Lunch", "Monthly lunch", "Office",
                LocalDate.of(2025, 6, 15),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );
    }

    @Test
    void getAll_returnsAllEvents() {
        when(repository.getAll()).thenReturn(List.of(validEvent));

        List<Event> result = service.getAll();

        assertThat(result).hasSize(1).contains(validEvent);
        verify(repository).getAll();
    }

    @Test
    void getAll_returnsEmptyList_whenNoEvents() {
        when(repository.getAll()).thenReturn(List.of());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getById_returnsEvent_whenFound() {
        when(repository.getById(1)).thenReturn(Optional.of(validEvent));

        Event result = service.getById(1);

        assertThat(result).isEqualTo(validEvent);
    }

    @Test
    void getById_throwsNotFoundException_whenNotFound() {
        when(repository.getById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Event not found");
    }


    @Test
    void create_savesAndReturnsEvent_whenValid() {
        when(repository.save(validEvent)).thenReturn(validEvent);

        Event result = service.create(validEvent);

        assertThat(result).isEqualTo(validEvent);
        verify(repository).save(validEvent);
    }

    @Test
    void create_throwsValidationException_whenTitleIsNull() {
        Event event = new Event(null, null, "Office",
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> service.create(event))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Title is required");

        verify(repository, never()).save(any());
    }

    @Test
    void create_throwsValidationException_whenTitleIsBlank() {
        Event event = new Event("   ", null, "Office",
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> service.create(event))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Title is required");
    }

    @Test
    void create_throwsValidationException_whenPlannedDateIsNull() {
        Event event = new Event("Title", null, "Office",
                null, LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> service.create(event))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Planned date is required");
    }

    @Test
    void create_throwsValidationException_whenEndTimeBeforeStartTime() {
        Event event = new Event("Title", null, "Office",
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(9, 0));

        assertThatThrownBy(() -> service.create(event))
                .isInstanceOf(ValidationException.class)
                .hasMessage("End time cannot be before start time");
    }

    @Test
    void create_doesNotThrow_whenTimesAreNull() {
        Event event = new Event("Title", null, "Office",
                LocalDate.now(), null, null);
        when(repository.save(event)).thenReturn(event);

        assertThatCode(() -> service.create(event)).doesNotThrowAnyException();
    }

    @Test
    void create_doesNotThrow_whenStartTimeIsNullAndEndTimeSet() {
        Event event = new Event("Title", null, "Office",
                LocalDate.now(), null, LocalTime.of(10, 0));
        when(repository.save(event)).thenReturn(event);

        assertThatCode(() -> service.create(event)).doesNotThrowAnyException();
    }

    @Test
    void update_updatesAndSavesEvent_whenValid() {
        Event updated = new Event("Updated", "desc", "Location",
                LocalDate.of(2025, 7, 1), LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(repository.getById(1)).thenReturn(Optional.of(validEvent));
        when(repository.save(validEvent)).thenReturn(validEvent);

        Event result = service.update(1, updated);

        assertThat(result).isNotNull();
        verify(repository).save(validEvent);
    }

    @Test
    void update_throwsNotFoundException_whenEventDoesNotExist() {
        Event updated = new Event("Updated", "desc", "Location",
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(repository.getById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, updated))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Event not found");

        verify(repository, never()).save(any());
    }

    @Test
    void update_throwsValidationException_whenUpdatedEventIsInvalid() {
        Event invalid = new Event(null, null, "Office",
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> service.update(1, invalid))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Title is required");

        verify(repository, never()).getById(anyInt());
        verify(repository, never()).save(any());
    }


    @Test
    void delete_callsRepositoryDelete() {
        service.delete(1);
        verify(repository).delete(1);
    }
}