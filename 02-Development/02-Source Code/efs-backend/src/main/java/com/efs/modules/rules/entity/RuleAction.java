package com.efs.modules.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "rule_action", schema = "rules")
public class RuleAction {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "action_id", nullable = false)
    private UUID actionId;

    @Column(name = "rule_version_id", nullable = false)
    private UUID ruleVersionId;

    @Column(name = "action_type", nullable = false, length = 40)
    private String actionType;

    @Column(name = "execution_order", nullable = false)
    private Short executionOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameter_json", columnDefinition = "jsonb")
    private Map<String, Object> parameterJson;

    @Column(name = "is_async", nullable = false)
    private Boolean isAsync;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RuleAction() {
    }

    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    public UUID getRuleVersionId() {
        return ruleVersionId;
    }

    public void setRuleVersionId(UUID ruleVersionId) {
        this.ruleVersionId = ruleVersionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Short getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Short executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Map<String, Object> getParameterJson() {
        return parameterJson;
    }

    public void setParameterJson(Map<String, Object> parameterJson) {
        this.parameterJson = parameterJson;
    }

    public Boolean getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(Boolean isAsync) {
        this.isAsync = isAsync;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}