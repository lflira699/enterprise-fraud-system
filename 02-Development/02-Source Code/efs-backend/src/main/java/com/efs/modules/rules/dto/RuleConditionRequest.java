package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class RuleConditionRequest {

    @NotNull
    private Short conditionOrder;

    @NotBlank
    @Size(max = 150)
    private String attributeName;

    @NotBlank
    @Size(max = 30)
    private String comparisonOperator;

    @NotNull
    private Map<String, Object> comparisonValue;

    @Size(max = 20)
    private String logicalOperator;

    @NotNull
    private Boolean isRequired;

    public Short getConditionOrder() {
        return conditionOrder;
    }

    public void setConditionOrder(Short conditionOrder) {
        this.conditionOrder = conditionOrder;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(String comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public Map<String, Object> getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(Map<String, Object> comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
}