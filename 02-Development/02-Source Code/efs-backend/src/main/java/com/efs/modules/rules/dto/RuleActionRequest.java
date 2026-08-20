package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class RuleActionRequest {

    @NotBlank
    @Size(max = 40)
    private String actionType;

    @NotNull
    private Short executionOrder;

    private Map<String, Object> parameterJson;

    @NotNull
    private Boolean isAsync;

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
}