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
@Table(name = "scenario_activation", schema = "detection")
public class ScenarioActivation {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "activation_id", nullable = false)
    private UUID activationId;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(name = "scenario_version_id", nullable = false)
    private UUID scenarioVersionId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "activation_status", nullable = false, length = 30)
    private String activationStatus;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "confidence", precision = 8, scale = 4)
    private BigDecimal confidence;

    @Column(name = "risk_score", precision = 12, scale = 4)
    private BigDecimal riskScore;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "activation_reason", columnDefinition = "TEXT")
    private String activationReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "decision_context", columnDefinition = "jsonb")
    private Map<String, Object> decisionContext;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScenarioActivation() {
    }

    public UUID getActivationId() {
        return activationId;
    }

    public void setActivationId(UUID activationId) {
        this.activationId = activationId;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(UUID scenarioId) {
        this.scenarioId = scenarioId;
    }

    public UUID getScenarioVersionId() {
        return scenarioVersionId;
    }

    public void setScenarioVersionId(UUID scenarioVersionId) {
        this.scenarioVersionId = scenarioVersionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getActivationReason() {
        return activationReason;
    }

    public void setActivationReason(String activationReason) {
        this.activationReason = activationReason;
    }

    public Map<String, Object> getDecisionContext() {
        return decisionContext;
    }

    public void setDecisionContext(Map<String, Object> decisionContext) {
        this.decisionContext = decisionContext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}