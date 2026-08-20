package com.efs.modules.rules.dto;

import java.util.Map;
import java.util.UUID;

public class RuleConditionResponse {

    private UUID conditionId;
    private UUID ruleVersionId;
    private Short conditionOrder;
    private String attributeName;
    private String comparisonOperator;
    private Map<String, Object> comparisonValue;
    private String logicalOperator;
    private Boolean isRequired;

    public UUID getConditionId() {
        return conditionId;
    }

    public void setConditionId(UUID conditionId) {
        this.conditionId = conditionId;
    }

    public UUID getRuleVersionId() {
        return ruleVersionId;
    }

    public void setRuleVersionId(UUID ruleVersionId) {
        this.ruleVersionId = ruleVersionId;
    }

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