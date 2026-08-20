package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ScenarioActivationResponse {

    private UUID activationId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID transactionId;
    private UUID customerId;
    private String activationStatus;
    private String severity;
    private BigDecimal confidence;
    private BigDecimal riskScore;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String activationReason;
    private Map<String, Object> decisionContext;
    private LocalDateTime createdAt;

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

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
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