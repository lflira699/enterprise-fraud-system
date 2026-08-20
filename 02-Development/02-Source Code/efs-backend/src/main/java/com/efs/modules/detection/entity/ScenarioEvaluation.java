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
@Table(name = "scenario_evaluation", schema = "detection")
public class ScenarioEvaluation {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "evaluation_id", nullable = false)
    private UUID evaluationId;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(name = "scenario_version_id", nullable = false)
    private UUID scenarioVersionId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "evaluation_status", nullable = false, length = 30)
    private String evaluationStatus;

    @Column(name = "matched", nullable = false)
    private Boolean matched;

    @Column(name = "rule_count", nullable = false)
    private Short ruleCount;

    @Column(name = "matched_rule_count", nullable = false)
    private Short matchedRuleCount;

    @Column(name = "required_evidence_count", nullable = false)
    private Short requiredEvidenceCount;

    @Column(name = "available_evidence_count", nullable = false)
    private Short availableEvidenceCount;

    @Column(name = "confidence", precision = 8, scale = 4)
    private BigDecimal confidence;

    @Column(name = "risk_contribution", precision = 8, scale = 4)
    private BigDecimal riskContribution;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "evaluation_duration_ms")
    private Long evaluationDurationMs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evaluation_context", columnDefinition = "jsonb")
    private Map<String, Object> evaluationContext;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScenarioEvaluation() {
    }

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

    public void setRequiredEvidenceCount(
            Short requiredEvidenceCount) {
        this.requiredEvidenceCount =
                requiredEvidenceCount;
    }

    public Short getAvailableEvidenceCount() {
        return availableEvidenceCount;
    }

    public void setAvailableEvidenceCount(
            Short availableEvidenceCount) {
        this.availableEvidenceCount =
                availableEvidenceCount;
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

    public void setRiskContribution(
            BigDecimal riskContribution) {
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

    public void setEvaluationDurationMs(
            Long evaluationDurationMs) {
        this.evaluationDurationMs =
                evaluationDurationMs;
    }

    public Map<String, Object> getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(
            Map<String, Object> evaluationContext) {
        this.evaluationContext =
                evaluationContext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}