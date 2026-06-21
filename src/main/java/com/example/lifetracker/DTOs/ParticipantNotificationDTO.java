package com.example.lifetracker.DTOs;

public record ParticipantNotificationDTO(
        String eventTitle,
        String participantName,
        boolean isRemoved
) {
    public ParticipantNotificationDTO(String eventTitle, String participantName) {
        this(eventTitle, participantName, false);
    }
}
