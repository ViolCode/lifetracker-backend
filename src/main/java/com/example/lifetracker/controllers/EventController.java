package com.example.lifetracker.controllers;

import com.example.lifetracker.DTOs.EventRequestDTO;
import com.example.lifetracker.DTOs.EventResponseDTO;
import com.example.lifetracker.mappers.EventMapper;
import com.example.lifetracker.services.EventService;
import com.example.lifetracker.services.ExpenseService;
import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.DTOs.ParticipantRequestDTO;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;  // Add this import
import com.example.lifetracker.services.EventParticipantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;


@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private final EventMapper mapper;
    private final ExpenseService expenseService;
    private final EventParticipantService participantService;

    public EventController(
            EventService service,
            EventMapper mapper,
            ExpenseService expenseService,
            EventParticipantService participantService
    ) {
        this.service = service;
        this.mapper = mapper;
        this.expenseService = expenseService;
        this.participantService = participantService;
    }

    @GetMapping
    public List<EventResponseDTO> getAll() {
        return service.getAll()
                .stream()
                .map(event ->
                        mapper.toDTO(
                                event,
                                expenseService.getTotalByEvent(event.getId()),
                                null
                        )
                )
                .toList();
    }



    @GetMapping("/{id}")
    public EventResponseDTO getById(@PathVariable int id) {
        var eventWithParticipants = service.getEventWithParticipants(id);

        return mapper.toDTO(
                eventWithParticipants.event(),
                expenseService.getTotalByEvent(eventWithParticipants.event().getId()),
                eventWithParticipants.participants()
        );
    }

    @GetMapping("/history")
    public List<EventResponseDTO> getHistory() {
        return service.getPastEvents()
                .stream()
                .map(event ->
                        mapper.toDTO(
                                event,
                                expenseService.getTotalByEvent(event.getId()),
                                null
                        )
                )
                .toList();
    }

    @PostMapping
    public EventResponseDTO create(@Valid @RequestBody EventRequestDTO dto) {
        var saved = service.create(mapper.toDomain(dto));
        return mapper.toDTO(saved, 0.0, null);
    }

    @PutMapping("/{id}")
    public EventResponseDTO update(
            @PathVariable int id,
            @Valid @RequestBody EventRequestDTO dto
    ) {
        var updated = service.update(id, mapper.toDomain(dto));
        return mapper.toDTO(
                updated,
                expenseService.getTotalByEvent(updated.getId()),
                null
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PostMapping("/{id}/participants")
    public ParticipantResponseDTO addParticipant(
            @PathVariable int id,
            @Valid @RequestBody ParticipantRequestDTO request
    ) {
        return participantService.addParticipant(id, request.userId());
    }

    @DeleteMapping("/{id}/participants/{userId}")
    public void removeParticipant(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        participantService.removeParticipant(id, userId);
    }

    @GetMapping("/{id}/participants")
    public List<ParticipantResponseDTO> getParticipants(@PathVariable int id) {
        return participantService.getEventParticipants(id);
    }

    @GetMapping("/{id}/participants/search")
    public List<UserSearchResponseDTO> searchUsersToInvite(
            @PathVariable int id,
            @RequestParam String q
    ) {
        service.getById(id);
        return participantService.searchUsers(q);
    }
}