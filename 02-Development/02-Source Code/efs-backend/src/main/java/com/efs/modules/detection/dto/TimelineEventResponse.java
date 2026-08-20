package com.efs.modules.detection.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class TimelineEventResponse {

    private UUID timelineEventId;
    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private String eventType;
    private String eventSource;
    private UUID eventReferenceId;
    private LocalDateTime eventTimestamp;
    private Integer sequenceNumber;
    private String eventSummary;
    private Map<String, Object> eventData;
    private LocalDateTime createdAt;

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