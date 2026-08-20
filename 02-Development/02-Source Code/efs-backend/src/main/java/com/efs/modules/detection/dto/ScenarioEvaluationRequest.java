package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ScenarioEvaluationRequest {

    @NotNull
    private UUID scenarioId;

    @NotNull
    private UUID scenarioVersionId;

    private UUID transactionId;

    private UUID customerId;

    @NotNull
    @Size(max = 30)
    private String evaluationStatus;

    @NotNull
    private Boolean matched;

    private Short ruleCount;

    private Short matchedRuleCount;

    private Short requiredEvidenceCount;

    private Short availableEvidenceCount;

    private BigDecimal confidence;

    private BigDecimal riskContribution;

    private Long evaluationDurationMs;

    private Map<String, Object> evaluationContext;

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
}