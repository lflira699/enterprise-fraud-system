package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CorrelationRequest {

    private UUID customerId;

    private UUID transactionId;

    @NotBlank
    @Size(max = 120)
    private String correlationKey;

    @NotBlank
    @Size(max = 40)
    private String correlationType;

    @NotBlank
    @Size(max = 30)
    private String correlationStatus;

    @NotNull
    private LocalDateTime windowStart;

    @NotNull
    private LocalDateTime windowEnd;

    @NotNull
    private Integer eventCount;

    @NotNull
    private Short matchedRuleCount;

    private Map<String, Object> correlationContext;

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

    public Map<String, Object> getCorrelationContext() {
        return correlationContext;
    }

    public void setCorrelationContext(
            Map<String, Object> correlationContext) {

        this.correlationContext = correlationContext;
    }
}