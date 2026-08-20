package com.efs.modules.detection.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScenarioRuleResponse {

    private UUID scenarioRuleId;
    private UUID scenarioVersionId;
    private UUID ruleId;
    private String ruleRole;
    private Boolean required;
    private Short evaluationOrder;
    private LocalDateTime createdAt;

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