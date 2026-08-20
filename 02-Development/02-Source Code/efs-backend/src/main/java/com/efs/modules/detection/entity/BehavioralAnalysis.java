package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "behavioral_analysis", schema = "detection")
public class BehavioralAnalysis {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "behavioral_analysis_id", nullable = false)
    private UUID behavioralAnalysisId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "analysis_status", nullable = false, length = 30)
    private String analysisStatus;

    @Column(name = "baseline_window_days")
    private Integer baselineWindowDays;

    @Column(name = "observed_window_start")
    private LocalDateTime observedWindowStart;

    @Column(name = "observed_window_end")
    private LocalDateTime observedWindowEnd;

    @Column(name = "amount_deviation", precision = 12, scale = 4)
    private BigDecimal amountDeviation;

    @Column(name = "frequency_deviation", precision = 12, scale = 4)
    private BigDecimal frequencyDeviation;

    @Column(name = "velocity_deviation", precision = 12, scale = 4)
    private BigDecimal velocityDeviation;

    @Column(name = "channel_deviation", precision = 12, scale = 4)
    private BigDecimal channelDeviation;

    @Column(name = "geographic_deviation", precision = 12, scale = 4)
    private BigDecimal geographicDeviation;

    @Column(name = "temporal_deviation", precision = 12, scale = 4)
    private BigDecimal temporalDeviation;

    @Column(name = "behavioral_confidence", precision = 8, scale = 4)
    private BigDecimal behavioralConfidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "behavioral_indicators", columnDefinition = "jsonb")
    private Map<String, Object> behavioralIndicators;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_context", columnDefinition = "jsonb")
    private Map<String, Object> analysisContext;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BehavioralAnalysis() {
    }

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