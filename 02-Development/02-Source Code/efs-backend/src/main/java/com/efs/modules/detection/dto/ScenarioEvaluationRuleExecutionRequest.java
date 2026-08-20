package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ScenarioEvaluationRuleExecutionRequest {

    @NotNull
    private UUID evaluationId;

    @NotNull
    private UUID executionId;

    public UUID getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(UUID evaluationId) {
        this.evaluationId = evaluationId;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }
}