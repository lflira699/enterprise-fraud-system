package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scenario_rule", schema = "detection")
public class ScenarioRule {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "scenario_rule_id", nullable = false)
    private UUID scenarioRuleId;

    @Column(name = "scenario_version_id", nullable = false)
    private UUID scenarioVersionId;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "rule_role", length = 30)
    private String ruleRole;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Column(name = "evaluation_order")
    private Short evaluationOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScenarioRule() {
    }

    public UUID getScenarioRuleId() {
        return scenarioRuleId;
    }

    public void setScenarioRuleId(UUID scenarioRuleId) {
        this.scenarioRuleId = scenarioRuleId;
    }

    public UUID getScenarioVersionId() {
        return scenarioVersionId;
    }

    public void setScenarioVersionId(UUID scenarioVersionId) {
        this.scenarioVersionId = scenarioVersionId;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleRole() {
        return ruleRole;
    }

    public void setRuleRole(String ruleRole) {
        this.ruleRole = ruleRole;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Short getEvaluationOrder() {
        return evaluationOrder;
    }

    public void setEvaluationOrder(Short evaluationOrder) {
        this.evaluationOrder = evaluationOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}