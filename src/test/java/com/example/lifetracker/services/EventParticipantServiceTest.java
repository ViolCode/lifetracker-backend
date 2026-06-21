package com.example.lifetracker.services;

import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.domains.EventParticipant;
import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.BadRequestException;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventParticipantRepository;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.UserRepository;
import com.example.lifetracker.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventParticipantServiceTest {

    private EventParticipantRepository participantRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;
    private EventParticipantService service;

    private final User currentUser = new User(1, "owner@test.com", "password", "Owner", false);
    private final User otherUser = new User(2, "other@test.com", "password", "Other", false);
    private final Event sampleEvent = new Event(
            1, "Gym", "", "", LocalDate.now(), null, null, 1
    );

    @BeforeEach
    void setUp() {
        participantRepository = mock(EventParticipantRepository.class);
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        service = new EventParticipantService(participantRepository, eventRepository, userRepository, messagingTemplate);

        UserPrincipal principal = new UserPrincipal(currentUser);
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
    void getEventParticipants_returnsListWhenOwner() {
        EventParticipant participant = new EventParticipant(1, 1, 2, false, 1, LocalDateTime.now(), null);

        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));
        when(participantRepository.getByEventIdAndUserId(1, 1)).thenReturn(Optional.empty());
        when(participantRepository.getByEventId(1)).thenReturn(List.of(participant));
        when(userRepository.findById(2)).thenReturn(Optional.of(otherUser));
        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));

        List<ParticipantResponseDTO> result = service.getEventParticipants(1);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).userId());
    }

    @Test
    void getEventParticipants_throwsWhenNotOwnerOrParticipant() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.empty());
        when(participantRepository.getByEventIdAndUserId(1, 1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getEventParticipants(1));
    }

    @Test
    void addParticipant_addsSuccessfully() {
        EventParticipant saved = new EventParticipant(1, 1, 2, false, 1, LocalDateTime.now(), null);

        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.findById(2)).thenReturn(Optional.of(otherUser));
        when(participantRepository.getByEventIdAndUserId(1, 2)).thenReturn(Optional.empty());
        when(participantRepository.save(any(EventParticipant.class))).thenReturn(saved);
        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));

        ParticipantResponseDTO result = service.addParticipant(1, 2);

        assertEquals(2, result.userId());
        assertEquals("Other", result.userName());
    }

    @Test
    void addParticipant_throwsWhenInvitingSelf() {
        assertThrows(BadRequestException.class, () -> service.addParticipant(1, 1));
    }

    @Test
    void addParticipant_throwsWhenEventNotFound() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addParticipant(1, 2));
    }

    @Test
    void addParticipant_throwsWhenAlreadyParticipant() {
        EventParticipant existing = new EventParticipant(1, 1, 2, false, 1, LocalDateTime.now(), null);

        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.findById(2)).thenReturn(Optional.of(otherUser));
        when(participantRepository.getByEventIdAndUserId(1, 2)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> service.addParticipant(1, 2));
    }

    @Test
    void removeParticipant_removesSuccessfully() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.findById(2)).thenReturn(Optional.of(otherUser));

        service.removeParticipant(1, 2);

        verify(participantRepository).deleteByEventIdAndUserId(1, 2);
    }

    @Test
    void removeParticipant_throwsWhenEventNotFound() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.removeParticipant(1, 2));
    }

    @Test
    void searchUsersToInvite_returnsEmptyForShortQuery() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));

        List<UserSearchResponseDTO> result = service.searchUsersToInvite(1, "a");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchUsersToInvite_returnsResults() {
        when(eventRepository.getById(1, 1)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.searchUsers("ot")).thenReturn(List.of(otherUser));

        List<UserSearchResponseDTO> result = service.searchUsersToInvite(1, "ot");

        assertEquals(1, result.size());
        assertEquals("Other", result.get(0).name());
    }

    @Test
    void getParticipatedEventIds_returnsList() {
        EventParticipant p = new EventParticipant(1, 5, 1, false, 1, LocalDateTime.now(), null);
        when(participantRepository.getEventsByUserId(1)).thenReturn(List.of(p));

        List<Integer> result = service.getParticipatedEventIds(1);

        assertEquals(List.of(5), result);
    }

    @Test
    void deleteAllByEventId_callsRepository() {
        service.deleteAllByEventId(1);

        verify(participantRepository).deleteByEventId(1);
    }
}
