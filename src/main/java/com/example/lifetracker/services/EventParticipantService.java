// com.example.lifetracker.services.EventParticipantService.java
package com.example.lifetracker.services;

import com.example.lifetracker.DTOs.ParticipantResponseDTO;
import com.example.lifetracker.DTOs.UserSearchResponseDTO;
import com.example.lifetracker.domains.EventParticipant;
import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.BadRequestException;
import com.example.lifetracker.exceptions.NotFoundException;
import com.example.lifetracker.persistence.EventParticipantRepository;
import com.example.lifetracker.persistence.EventRepository;
import com.example.lifetracker.persistence.UserRepository;
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventParticipantService {

    private final EventParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventParticipantService(
            EventParticipantRepository participantRepository,
            EventRepository eventRepository,
            UserRepository userRepository
    ) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<UserSearchResponseDTO> searchUsers(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return userRepository.searchUsers(query.trim()).stream()
                .map(user -> new UserSearchResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());
    }

    public ParticipantResponseDTO addParticipant(Integer eventId, Integer userIdToInvite) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        // Don't allow inviting yourself
        if (currentUserId.equals(userIdToInvite)) {
            throw new BadRequestException("You cannot invite yourself to an event");
        }

        // Check if event exists and user has access (is owner)
        eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        // Check if user exists
        User userToInvite = userRepository.findById(userIdToInvite)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if already a participant
        if (participantRepository.getByEventIdAndUserId(eventId, userIdToInvite).isPresent()) {
            throw new BadRequestException("User is already a participant");
        }

        EventParticipant participant = new EventParticipant(eventId, userIdToInvite, currentUserId);
        EventParticipant saved = participantRepository.save(participant);

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        return toResponseDTO(saved, userToInvite, currentUser);
    }

    public void removeParticipant(Integer eventId, Integer userId) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        participantRepository.deleteByEventIdAndUserId(eventId, userId);
    }

    public List<ParticipantResponseDTO> getEventParticipants(Integer eventId) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        List<EventParticipant> participants = participantRepository.getByEventId(eventId);

        return participants.stream()
                .map(participant -> {
                    User user = userRepository.findById(participant.getUserId())
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    User invitedBy = userRepository.findById(participant.getInvitedBy())
                            .orElseThrow(() -> new NotFoundException("Inviter not found"));
                    return toResponseDTO(participant, user, invitedBy);
                })
                .collect(Collectors.toList());
    }

    private ParticipantResponseDTO toResponseDTO(EventParticipant participant, User user, User invitedBy) {
        return new ParticipantResponseDTO(
                participant.getId(),
                user.getId(),
                user.getEmail(),
                user.getName(),
                participant.getIsConfirmed(),
                invitedBy.getName(),
                participant.getInvitedAt()
        );
    }
}