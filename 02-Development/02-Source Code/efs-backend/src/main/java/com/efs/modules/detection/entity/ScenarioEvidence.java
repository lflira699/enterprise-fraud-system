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
@Table(name = "scenario_evidence", schema = "detection")
public class ScenarioEvidence {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "evidence_id", nullable = false)
    private UUID evidenceId;

    @Column(name = "scenario_version_id", nullable = false)
    private UUID scenarioVersionId;

    @Column(name = "evidence_type", nullable = false, length = 40)
    private String evidenceType;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    @Column(name = "source_reference", length = 250)
    private String sourceReference;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence_value", columnDefinition = "jsonb")
    private Map<String, Object> evidenceValue;

    @Column(name = "evidence_summary", columnDefinition = "TEXT")
    private String evidenceSummary;

    @Column(name = "confidence", precision = 8, scale = 4)
    private BigDecimal confidence;

    @Column(name = "observed_at")
    private LocalDateTime observedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScenarioEvidence() {
    }

    public UUID getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(UUID evidenceId) {
        this.evidenceId = evidenceId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}