package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RuleMetricRequest {

    @NotNull
    private UUID ruleId;

    private UUID ruleVersionId;

    @NotNull
    private LocalDate metricDate;

    @NotNull
    private Long executionCount;

    @NotNull
    private Long matchCount;

    @NotNull
    private Long confirmedFraudCount;

    @NotNull
    private Long falsePositiveCount;

    private Long falseNegativeCount;

    @NotNull
    private BigDecimal averageExecutionMs;

    @NotNull
    private BigDecimal preventedAmount;

    @Size(max = 3)
    private String currencyCode;

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
}