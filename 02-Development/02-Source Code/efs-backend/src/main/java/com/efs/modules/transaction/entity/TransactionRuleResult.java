package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_rule_result", schema = "transaction")
public class TransactionRuleResult {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "rule_result_id", nullable = false)
    private UUID ruleResultId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "rule_version", nullable = false)
    private Integer ruleVersion;

    @Column(name = "execution_order", nullable = false)
    private Short executionOrder;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "evaluation_result", nullable = false, length = 20)
    private String evaluationResult;

    @Column(name = "risk_points", nullable = false, precision = 8, scale = 2)
    private BigDecimal riskPoints;

    @Column(name = "recommended_action", length = 30)
    private String recommendedAction;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    public TransactionRuleResult() {
    }

    public UUID getRuleResultId() {
        return ruleResultId;
    }

    public void setRuleResultId(UUID ruleResultId) {
        this.ruleResultId = ruleResultId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(Integer ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public Short getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Short executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(String evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public BigDecimal getRiskPoints() {
        return riskPoints;
    }

    public void setRiskPoints(BigDecimal riskPoints) {
        this.riskPoints = riskPoints;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}