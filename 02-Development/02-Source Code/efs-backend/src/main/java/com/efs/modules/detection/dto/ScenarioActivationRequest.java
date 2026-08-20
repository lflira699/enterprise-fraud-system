package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ScenarioActivationRequest {

    @NotNull
    private UUID scenarioId;

    @NotNull
    private UUID scenarioVersionId;

    private UUID transactionId;

    private UUID customerId;

    @NotNull
    @Size(max = 30)
    private String activationStatus;

    @NotNull
    @Size(max = 20)
    private String severity;

    private BigDecimal confidence;

    private BigDecimal riskScore;

    private String activationReason;

    private Map<String, Object> decisionContext;

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
}