package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ScenarioEvaluationResponse {

    private UUID evaluationId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID transactionId;
    private UUID customerId;
    private String evaluationStatus;
    private Boolean matched;
    private Short ruleCount;
    private Short matchedRuleCount;
    private Short requiredEvidenceCount;
    private Short availableEvidenceCount;
    private BigDecimal confidence;
    private BigDecimal riskContribution;
    private LocalDateTime evaluatedAt;
    private Long evaluationDurationMs;
    private Map<String, Object> evaluationContext;
    private LocalDateTime createdAt;

    public UUID getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(UUID evaluationId) {
        this.evaluationId = evaluationId;
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

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(String evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public Boolean getMatched() {
        return matched;
    }

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public Short getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(Short ruleCount) {
        this.ruleCount = ruleCount;
    }

    public Short getMatchedRuleCount() {
        return matchedRuleCount;
    }

    public void setMatchedRuleCount(Short matchedRuleCount) {
        this.matchedRuleCount = matchedRuleCount;
    }

    public Short getRequiredEvidenceCount() {
        return requiredEvidenceCount;
    }

    public void setRequiredEvidenceCount(Short requiredEvidenceCount) {
        this.requiredEvidenceCount = requiredEvidenceCount;
    }

    public Short getAvailableEvidenceCount() {
        return availableEvidenceCount;
    }

    public void setAvailableEvidenceCount(Short availableEvidenceCount) {
        this.availableEvidenceCount = availableEvidenceCount;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public BigDecimal getRiskContribution() {
        return riskContribution;
    }

    public void setRiskContribution(BigDecimal riskContribution) {
        this.riskContribution = riskContribution;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Long getEvaluationDurationMs() {
        return evaluationDurationMs;
    }

    public void setEvaluationDurationMs(Long evaluationDurationMs) {
        this.evaluationDurationMs = evaluationDurationMs;
    }

    public Map<String, Object> getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(Map<String, Object> evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}