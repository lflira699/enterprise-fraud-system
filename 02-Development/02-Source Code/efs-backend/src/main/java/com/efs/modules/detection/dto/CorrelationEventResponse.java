package com.efs.modules.detection.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CorrelationEventResponse {

    private UUID correlationEventId;
    private UUID correlationId;
    private UUID eventId;
    private String eventRole;
    private LocalDateTime createdAt;

    public UUID getCorrelationEventId() {
        return correlationEventId;
    }

    public void setCorrelationEventId(UUID correlationEventId) {
        this.correlationEventId = correlationEventId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventRole() {
        return eventRole;
    }

    public void setEventRole(String eventRole) {
        this.eventRole = eventRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}