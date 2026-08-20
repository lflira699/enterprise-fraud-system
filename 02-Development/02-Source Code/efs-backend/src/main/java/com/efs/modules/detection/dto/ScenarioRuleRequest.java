package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class ScenarioRuleRequest {

    @NotNull
    private UUID scenarioVersionId;

    @NotNull
    private UUID ruleId;

    @Size(max = 30)
    private String ruleRole;

    @NotNull
    private Boolean required;

    private Short evaluationOrder;

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
}