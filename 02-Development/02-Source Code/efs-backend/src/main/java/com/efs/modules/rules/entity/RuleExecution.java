package com.efs.modules.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rule_execution", schema = "rules")
public class RuleExecution {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "rule_id")
    private UUID ruleId;

    @Column(name = "rule_version_id")
    private UUID ruleVersionId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "execution_status", nullable = false, length = 30)
    private String executionStatus;

    @Column(name = "matched", nullable = false)
    private Boolean matched;

    @Column(name = "execution_time_ms", nullable = false)
    private Integer executionTimeMs;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "engine_instance", length = 100)
    private String engineInstance;

    public RuleExecution() {
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
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

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public Boolean getMatched() {
        return matched;
    }

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public String getEngineInstance() {
        return engineInstance;
    }

    public void setEngineInstance(String engineInstance) {
        this.engineInstance = engineInstance;
    }
}