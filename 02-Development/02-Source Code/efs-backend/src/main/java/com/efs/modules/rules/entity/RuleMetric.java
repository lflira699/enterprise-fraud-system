package com.efs.modules.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rule_metric", schema = "rules")
public class RuleMetric {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "metric_id", nullable = false)
    private UUID metricId;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "rule_version_id")
    private UUID ruleVersionId;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "execution_count", nullable = false)
    private Long executionCount;

    @Column(name = "match_count", nullable = false)
    private Long matchCount;

    @Column(name = "confirmed_fraud_count", nullable = false)
    private Long confirmedFraudCount;

    @Column(name = "false_positive_count", nullable = false)
    private Long falsePositiveCount;

    @Column(name = "false_negative_count")
    private Long falseNegativeCount;

    @Column(name = "average_execution_ms", nullable = false, precision = 12, scale = 2)
    private BigDecimal averageExecutionMs;

    @Column(name = "prevented_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal preventedAmount;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public RuleMetric() {
    }

    public UUID getMetricId() {
        return metricId;
    }

    public void setMetricId(UUID metricId) {
        this.metricId = metricId;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public UUID getRuleVersionId() {
        return ruleVersionId;
    }

    public void setRuleVersionId(UUID ruleVersionId) {
        this.ruleVersionId = ruleVersionId;
    }

    public LocalDate getMetricDate() {
        return metricDate;
    }

    public void setMetricDate(LocalDate metricDate) {
        this.metricDate = metricDate;
    }

    public Long getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Long executionCount) {
        this.executionCount = executionCount;
    }

    public Long getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Long matchCount) {
        this.matchCount = matchCount;
    }

    public Long getConfirmedFraudCount() {
        return confirmedFraudCount;
    }

    public void setConfirmedFraudCount(Long confirmedFraudCount) {
        this.confirmedFraudCount = confirmedFraudCount;
    }

    public Long getFalsePositiveCount() {
        return falsePositiveCount;
    }

    public void setFalsePositiveCount(Long falsePositiveCount) {
        this.falsePositiveCount = falsePositiveCount;
    }

    public Long getFalseNegativeCount() {
        return falseNegativeCount;
    }

    public void setFalseNegativeCount(Long falseNegativeCount) {
        this.falseNegativeCount = falseNegativeCount;
    }

    public BigDecimal getAverageExecutionMs() {
        return averageExecutionMs;
    }

    public void setAverageExecutionMs(BigDecimal averageExecutionMs) {
        this.averageExecutionMs = averageExecutionMs;
    }

    public BigDecimal getPreventedAmount() {
        return preventedAmount;
    }

    public void setPreventedAmount(BigDecimal preventedAmount) {
        this.preventedAmount = preventedAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
