package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class RuleParameterRequest {

    @NotBlank
    @Size(max = 100)
    private String parameterName;

    @NotBlank
    @Size(max = 30)
    private String parameterType;

    @NotNull
    private Map<String, Object> parameterValue;

    @NotNull
    private Boolean isSensitive;

    private String validationExpression;

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