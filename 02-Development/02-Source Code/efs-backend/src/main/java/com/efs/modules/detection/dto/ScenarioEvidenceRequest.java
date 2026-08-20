package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ScenarioEvidenceRequest {

    @NotNull
    private UUID scenarioVersionId;

    @NotBlank
    @Size(max = 40)
    private String evidenceType;

    @NotBlank
    @Size(max = 50)
    private String sourceType;

    @Size(max = 250)
    private String sourceReference;

    private Map<String, Object> evidenceValue;

    private String evidenceSummary;

    private BigDecimal confidence;

    private LocalDateTime observedAt;

    public UUID getScenarioVersionId() {
        return scenarioVersionId;
    }

    public void setScenarioVersionId(UUID scenarioVersionId) {
        this.scenarioVersionId = scenarioVersionId;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(String sourceReference) {
        this.sourceReference = sourceReference;
    }

    public Map<String, Object> getEvidenceValue() {
        return evidenceValue;
    }

    public void setEvidenceValue(Map<String, Object> evidenceValue) {
        this.evidenceValue = evidenceValue;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(LocalDateTime observedAt) {
        this.observedAt = observedAt;
    }
}