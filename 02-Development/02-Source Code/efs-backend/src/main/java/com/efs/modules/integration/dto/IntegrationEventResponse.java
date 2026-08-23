package com.efs.modules.integration.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class IntegrationEventResponse {

    private UUID eventId;
    private String eventName;
    private String eventVersion;
    private Map<String, Object> eventPayload;
    private LocalDateTime publishedAt;
    private String publisher;
    private String status;

    public IntegrationEventResponse() {
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }

    public Map<String, Object> getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(Map<String, Object> eventPayload) {
        this.eventPayload = eventPayload;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}