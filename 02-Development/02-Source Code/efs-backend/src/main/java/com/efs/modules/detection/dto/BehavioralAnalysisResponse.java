package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class BehavioralAnalysisResponse {

    private UUID behavioralAnalysisId;
    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private String analysisStatus;
    private Integer baselineWindowDays;
    private LocalDateTime observedWindowStart;
    private LocalDateTime observedWindowEnd;
    private BigDecimal amountDeviation;
    private BigDecimal frequencyDeviation;
    private BigDecimal velocityDeviation;
    private BigDecimal channelDeviation;
    private BigDecimal geographicDeviation;
    private BigDecimal temporalDeviation;
    private BigDecimal behavioralConfidence;
    private Map<String, Object> behavioralIndicators;
    private Map<String, Object> analysisContext;
    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;

    public UUID getBehavioralAnalysisId() {
        return behavioralAnalysisId;
    }

    public void setBehavioralAnalysisId(UUID behavioralAnalysisId) {
        this.behavioralAnalysisId = behavioralAnalysisId;
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

    public BigDecimal getAmountDeviation() {
        return amountDeviation;
    }

    public void setAmountDeviation(BigDecimal amountDeviation) {
        this.amountDeviation = amountDeviation;
    }

    public BigDecimal getFrequencyDeviation() {
        return frequencyDeviation;
    }

    public void setFrequencyDeviation(BigDecimal frequencyDeviation) {
        this.frequencyDeviation = frequencyDeviation;
    }

    public BigDecimal getVelocityDeviation() {
        return velocityDeviation;
    }

    public void setVelocityDeviation(BigDecimal velocityDeviation) {
        this.velocityDeviation = velocityDeviation;
    }

    public BigDecimal getChannelDeviation() {
        return channelDeviation;
    }

    public void setChannelDeviation(BigDecimal channelDeviation) {
        this.channelDeviation = channelDeviation;
    }

    public BigDecimal getGeographicDeviation() {
        return geographicDeviation;
    }

    public void setGeographicDeviation(BigDecimal geographicDeviation) {
        this.geographicDeviation = geographicDeviation;
    }

    public BigDecimal getTemporalDeviation() {
        return temporalDeviation;
    }

    public void setTemporalDeviation(BigDecimal temporalDeviation) {
        this.temporalDeviation = temporalDeviation;
    }

    public BigDecimal getBehavioralConfidence() {
        return behavioralConfidence;
    }

    public void setBehavioralConfidence(BigDecimal behavioralConfidence) {
        this.behavioralConfidence = behavioralConfidence;
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

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}