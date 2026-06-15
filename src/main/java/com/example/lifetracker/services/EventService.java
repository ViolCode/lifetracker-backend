package com.example.lifetracker.services;

import com.example.lifetracker.DTOs.EventWithParticipantsDTO;
import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventParticipantRepository;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private final EventRepository repository;
    private final EventParticipantService participantService;
    private final EventParticipantRepository participantRepository; // Add this

    public EventService(
            EventRepository repository,
            EventParticipantService participantService,
            EventParticipantRepository participantRepository  // Add this parameter
    ) {
        this.repository = repository;
        this.participantService = participantService;
        this.participantRepository = participantRepository;  // Add this
    }

    public List<Event> getAll() {
        Integer userId = SecurityUtils.getCurrentUserId();

        List<Event> ownedEvents = repository.getAll(userId);

        // Fix: Use getEventsByUserId method
        List<Event> participatedEvents = participantRepository.getEventsByUserId(userId)
                .stream()
                .map(participant -> repository.getById(participant.getEventId(), participant.getEventId())
                        .orElseThrow(() -> new NotFoundException("Event not found")))
                .toList();

        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(ownedEvents);
        allEvents.addAll(participatedEvents);

        return allEvents.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public Event getById(int id) {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository
                .getById(id, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event create(Event event) {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository.save(event, userId);
    }

    public Event update(int id, Event updated) {
        Integer userId = SecurityUtils.getCurrentUserId();

        Event existing = repository.getById(id, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        existing.update(
                updated.getTitle(),
                updated.getDescription(),
                updated.getLocation(),
                updated.getPlannedDate(),
                updated.getStartTime(),
                updated.getEndTime()
        );

        return repository.save(existing, userId);
    }

    public void delete(int id) {
        Integer userId = SecurityUtils.getCurrentUserId();
        repository.getById(id, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // Also delete all participants when event is deleted
        participantRepository.deleteByEventId(id);
        repository.delete(id);
    }

    public List<Event> getPastEvents() {
        Integer userId = SecurityUtils.getCurrentUserId();
        return repository.getPastEvents(userId);
    }

    public EventWithParticipantsDTO getEventWithParticipants(int id) {
        Event event = getById(id);
        List<ParticipantResponseDTO> participants = participantService.getEventParticipants(id);
        return new EventWithParticipantsDTO(event, participants);
    }
}