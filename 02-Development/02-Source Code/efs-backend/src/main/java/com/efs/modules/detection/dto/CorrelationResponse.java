package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CorrelationResponse {

    private UUID correlationId;
    private UUID customerId;
    private UUID transactionId;
    private String correlationKey;
    private String correlationType;
    private String correlationStatus;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Integer eventCount;
    private Short matchedRuleCount;
    private BigDecimal confidence;
    private Map<String, Object> correlationContext;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
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

    public String getCorrelationKey() {
        return correlationKey;
    }

    public void setCorrelationKey(String correlationKey) {
        this.correlationKey = correlationKey;
    }

    public String getCorrelationType() {
        return correlationType;
    }

    public void setCorrelationType(String correlationType) {
        this.correlationType = correlationType;
    }

    public String getCorrelationStatus() {
        return correlationStatus;
    }

    public void setCorrelationStatus(String correlationStatus) {
        this.correlationStatus = correlationStatus;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(LocalDateTime windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Short getMatchedRuleCount() {
        return matchedRuleCount;
    }

    public void setMatchedRuleCount(Short matchedRuleCount) {
        this.matchedRuleCount = matchedRuleCount;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public Map<String, Object> getCorrelationContext() {
        return correlationContext;
    }

    public void setCorrelationContext(
            Map<String, Object> correlationContext) {

        this.correlationContext = correlationContext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}