package com.efs.modules.detection.entity;

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
@Table(name = "timeline_event", schema = "detection")
public class TimelineEvent {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "timeline_event_id", nullable = false)
    private UUID timelineEventId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_source", length = 80)
    private String eventSource;

    @Column(name = "event_reference_id")
    private UUID eventReferenceId;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "event_summary", length = 500)
    private String eventSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map<String, Object> eventData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TimelineEvent() {
    }

    public UUID getTimelineEventId() {
        return timelineEventId;
    }

    public void setTimelineEventId(UUID timelineEventId) {
        this.timelineEventId = timelineEventId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public UUID getEventReferenceId() {
        return eventReferenceId;
    }

    public void setEventReferenceId(UUID eventReferenceId) {
        this.eventReferenceId = eventReferenceId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(String eventSummary) {
        this.eventSummary = eventSummary;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}