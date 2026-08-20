package com.efs.modules.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "rule_condition", schema = "rules")
public class RuleCondition {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "condition_id", nullable = false)
    private UUID conditionId;

    @Column(name = "rule_version_id", nullable = false)
    private UUID ruleVersionId;

    @Column(name = "condition_order", nullable = false)
    private Short conditionOrder;

    @Column(name = "attribute_name", nullable = false, length = 150)
    private String attributeName;

    @Column(name = "comparison_operator", nullable = false, length = 30)
    private String comparisonOperator;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "comparison_value", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> comparisonValue;

    @Column(name = "logical_operator", length = 20)
    private String logicalOperator;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    public RuleCondition() {
    }

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