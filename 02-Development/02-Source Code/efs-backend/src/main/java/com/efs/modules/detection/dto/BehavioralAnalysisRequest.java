package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class BehavioralAnalysisRequest {

    @NotNull
    private UUID customerId;

    private UUID transactionId;

    private UUID correlationId;

    @NotNull
    private String analysisStatus;

    private Integer baselineWindowDays;

    private LocalDateTime observedWindowStart;

    private LocalDateTime observedWindowEnd;

    private Map<String, Object> behavioralIndicators;

    private Map<String, Object> analysisContext;

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

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public Integer getBaselineWindowDays() {
        return baselineWindowDays;
    }

    public void setBaselineWindowDays(Integer baselineWindowDays) {
        this.baselineWindowDays = baselineWindowDays;
    }

    public LocalDateTime getObservedWindowStart() {
        return observedWindowStart;
    }

    public void setObservedWindowStart(LocalDateTime observedWindowStart) {
        this.observedWindowStart = observedWindowStart;
    }

    public LocalDateTime getObservedWindowEnd() {
        return observedWindowEnd;
    }

    public void setObservedWindowEnd(LocalDateTime observedWindowEnd) {
        this.observedWindowEnd = observedWindowEnd;
    }

    public Map<String, Object> getBehavioralIndicators() {
        return behavioralIndicators;
    }

    public void setBehavioralIndicators(
            Map<String, Object> behavioralIndicators) {
        this.behavioralIndicators = behavioralIndicators;
    }

    public Map<String, Object> getAnalysisContext() {
        return analysisContext;
    }

    public void setAnalysisContext(
            Map<String, Object> analysisContext) {
        this.analysisContext = analysisContext;
    }
}