package com.efs.modules.rules.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RuleActionResponse {

    private UUID actionId;
    private UUID ruleVersionId;
    private String actionType;
    private Short executionOrder;
    private Map<String, Object> parameterJson;
    private Boolean isAsync;
    private LocalDateTime createdAt;

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