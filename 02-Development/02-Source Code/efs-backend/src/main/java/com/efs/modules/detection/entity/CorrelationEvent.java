package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "correlation_event", schema = "detection")
public class CorrelationEvent {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "correlation_event_id", nullable = false)
    private UUID correlationEventId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_role", length = 40)
    private String eventRole;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CorrelationEvent() {
    }

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