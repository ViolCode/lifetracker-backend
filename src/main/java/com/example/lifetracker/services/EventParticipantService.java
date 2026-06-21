package com.example.lifetracker.services;

import com.example.lifetracker.DTOs.ParticipantNotificationDTO;
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
import com.example.lifetracker.security.SecurityUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventParticipantService {

    private final EventParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public EventParticipantService(
            EventParticipantRepository participantRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ParticipantResponseDTO> getEventParticipants(Integer eventId) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        boolean isOwner = eventRepository.getById(eventId, currentUserId).isPresent();
        boolean isParticipant = participantRepository.getByEventIdAndUserId(eventId, currentUserId).isPresent();

        if (!isOwner && !isParticipant) {
            throw new NotFoundException("Event not found or you don't have permission");
        }

        return participantRepository.getByEventId(eventId)
                .stream()
                .map(participant -> {
                    User user = userRepository.findById(participant.getUserId())
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    User invitedBy = userRepository.findById(participant.getInvitedBy())
                            .orElseThrow(() -> new NotFoundException("Inviter not found"));
                    return toResponseDTO(participant, user, invitedBy);
                })
                .toList();
    }

    public ParticipantResponseDTO addParticipant(Integer eventId, Integer userIdToInvite) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        if (currentUserId.equals(userIdToInvite)) {
            throw new BadRequestException("You cannot invite yourself to an event");
        }

        Event event = eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        User userToInvite = userRepository.findById(userIdToInvite)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (participantRepository.getByEventIdAndUserId(eventId, userIdToInvite).isPresent()) {
            throw new BadRequestException("User is already a participant");
        }

        EventParticipant saved = participantRepository.save(
                new EventParticipant(eventId, userIdToInvite, currentUserId)
        );

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        notifyParticipantAdded(event, userToInvite);

        return toResponseDTO(saved, userToInvite, currentUser);
    }

    public void removeParticipant(Integer eventId, Integer userId) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        Event event = eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        User removedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        participantRepository.deleteByEventIdAndUserId(eventId, userId);

        notifyParticipantRemoved(event, removedUser);
    }

    public List<UserSearchResponseDTO> searchUsersToInvite(Integer eventId, String query) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        eventRepository.getById(eventId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Event not found or you don't have permission"));

        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return userRepository.searchUsers(query.trim())
                .stream()
                .map(user -> new UserSearchResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }

    public List<Integer> getParticipatedEventIds(Integer userId) {
        return participantRepository.getEventsByUserId(userId)
                .stream()
                .map(EventParticipant::getEventId)
                .toList();
    }

    public void deleteAllByEventId(Integer eventId) {
        participantRepository.deleteByEventId(eventId);
    }

    private void notifyParticipantAdded(Event event, User newParticipant) {
        messagingTemplate.convertAndSend(
                "/topic/notifications." + event.getOwnerId(),
                new ParticipantNotificationDTO(event.getTitle(), newParticipant.getName())
        );
    }

    private void notifyParticipantRemoved(Event event, User removedParticipant) {
        messagingTemplate.convertAndSend(
                "/topic/notifications." + event.getOwnerId(),
                new ParticipantNotificationDTO(event.getTitle(), removedParticipant.getName(), true)
        );
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
