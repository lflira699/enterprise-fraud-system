package com.efs.modules.rules.dto;

import java.util.Map;
import java.util.UUID;

public class RuleParameterResponse {

    private UUID parameterId;
    private UUID ruleVersionId;
    private String parameterName;
    private String parameterType;
    private Map<String, Object> parameterValue;
    private Boolean isSensitive;
    private String validationExpression;

    public UUID getParameterId() {
        return parameterId;
    }

    public void setParameterId(UUID parameterId) {
        this.parameterId = parameterId;
    }

    public UUID getRuleVersionId() {
        return ruleVersionId;
    }

    public void setRuleVersionId(UUID ruleVersionId) {
        this.ruleVersionId = ruleVersionId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public Map<String, Object> getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(Map<String, Object> parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(Boolean isSensitive) {
        this.isSensitive = isSensitive;
    }

    public String getValidationExpression() {
        return validationExpression;
    }

    public void setValidationExpression(String validationExpression) {
        this.validationExpression = validationExpression;
    }
}