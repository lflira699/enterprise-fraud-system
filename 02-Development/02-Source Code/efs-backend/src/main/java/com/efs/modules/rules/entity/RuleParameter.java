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
@Table(name = "rule_parameter", schema = "rules")
public class RuleParameter {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "parameter_id", nullable = false)
    private UUID parameterId;

    @Column(name = "rule_version_id", nullable = false)
    private UUID ruleVersionId;

    @Column(name = "parameter_name", nullable = false, length = 100)
    private String parameterName;

    @Column(name = "parameter_type", nullable = false, length = 30)
    private String parameterType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameter_value", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> parameterValue;

    @Column(name = "is_sensitive", nullable = false)
    private Boolean isSensitive;

    @Column(name = "validation_expression", columnDefinition = "TEXT")
    private String validationExpression;

    public RuleParameter() {
    }

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