package com.example.lifetracker.controllers;

import com.example.lifetracker.dtos.ParticipantRequestDTO;
import com.example.lifetracker.dtos.ParticipantResponseDTO;
import com.example.lifetracker.dtos.UserSearchResponseDTO;
import com.example.lifetracker.services.EventParticipantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/participants")
public class ParticipantController {

    private final EventParticipantService service;

    public ParticipantController(EventParticipantService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParticipantResponseDTO> getAll(@PathVariable int eventId) {
        return service.getEventParticipants(eventId);
    }

    @PostMapping
    public ParticipantResponseDTO add(
            @PathVariable int eventId,
            @Valid @RequestBody ParticipantRequestDTO dto
    ) {
        return service.addParticipant(eventId, dto.userId());
    }

    @DeleteMapping("/{userId}")
    public void remove(
            @PathVariable int eventId,
            @PathVariable int userId
    ) {
        service.removeParticipant(eventId, userId);
    }

    @GetMapping("/search")
    public List<UserSearchResponseDTO> search(
            @PathVariable int eventId,
            @RequestParam String q
    ) {
        return service.searchUsersToInvite(eventId, q);
    }
}
