package com.efs.modules.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "integration_event",
        schema = "integration"
)
public class IntegrationEvent {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(
            name = "event_name",
            nullable = false,
            length = 100
    )
    private String eventName;

    @Column(
            name = "event_version",
            nullable = false,
            length = 30
    )
    private String eventVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "event_payload",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> eventPayload;

    @Column(
            name = "published_at",
            nullable = false
    )
    private LocalDateTime publishedAt;

    @Column(
            name = "publisher",
            nullable = false,
            length = 100
    )
    private String publisher;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    public IntegrationEvent() {
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