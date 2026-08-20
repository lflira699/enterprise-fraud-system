package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ScenarioVersionRequest {

    @NotNull
    private UUID scenarioId;

    @NotNull
    private Integer versionNumber;

    @NotBlank
    @Size(max = 30)
    private String versionStatus;

    @NotNull
    private Long correlationWindowSeconds;

    private Integer maximumProcessingTimeMs;

    private Integer minimumEvents;

    private BigDecimal minimumConfidence;

    @NotBlank
    @Size(max = 30)
    private String activationMode;

    private Map<String, Object> configuration;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

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
}