package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CorrelationEventRequest {

    @NotNull
    private UUID correlationId;

    @NotNull
    private UUID eventId;

    @Size(max = 40)
    private String eventRole;

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
}