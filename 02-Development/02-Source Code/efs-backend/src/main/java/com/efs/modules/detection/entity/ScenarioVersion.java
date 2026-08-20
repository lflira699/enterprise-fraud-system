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
@Table(name = "scenario_version", schema = "detection")
public class ScenarioVersion {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "scenario_version_id", nullable = false)
    private UUID scenarioVersionId;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "version_status", nullable = false, length = 30)
    private String versionStatus;

    @Column(name = "correlation_window_seconds", nullable = false)
    private Long correlationWindowSeconds;

    @Column(name = "maximum_processing_time_ms")
    private Integer maximumProcessingTimeMs;

    @Column(name = "minimum_events")
    private Integer minimumEvents;

    @Column(name = "minimum_confidence", precision = 8, scale = 4)
    private BigDecimal minimumConfidence;

    @Column(name = "activation_mode", nullable = false, length = 30)
    private String activationMode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration", columnDefinition = "jsonb")
    private Map<String, Object> configuration;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ScenarioVersion() {
    }

    public UUID getScenarioVersionId() {
        return scenarioVersionId;
    }

    public void setScenarioVersionId(UUID scenarioVersionId) {
        this.scenarioVersionId = scenarioVersionId;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(UUID scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }

    public Long getCorrelationWindowSeconds() {
        return correlationWindowSeconds;
    }

    public void setCorrelationWindowSeconds(Long correlationWindowSeconds) {
        this.correlationWindowSeconds = correlationWindowSeconds;
    }

    public Integer getMaximumProcessingTimeMs() {
        return maximumProcessingTimeMs;
    }

    public void setMaximumProcessingTimeMs(Integer maximumProcessingTimeMs) {
        this.maximumProcessingTimeMs = maximumProcessingTimeMs;
    }

    public Integer getMinimumEvents() {
        return minimumEvents;
    }

    public void setMinimumEvents(Integer minimumEvents) {
        this.minimumEvents = minimumEvents;
    }

    public BigDecimal getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(BigDecimal minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public String getActivationMode() {
        return activationMode;
    }

    public void setActivationMode(String activationMode) {
        this.activationMode = activationMode;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
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