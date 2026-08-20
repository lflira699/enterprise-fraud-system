package com.efs.modules.detection.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScenarioEvaluationRuleExecutionResponse {

    private UUID evaluationRuleExecutionId;
    private UUID evaluationId;
    private UUID executionId;
    private LocalDateTime createdAt;

    public UUID getEvaluationRuleExecutionId() {
        return evaluationRuleExecutionId;
    }

    public void setEvaluationRuleExecutionId(
            UUID evaluationRuleExecutionId) {

        this.evaluationRuleExecutionId =
                evaluationRuleExecutionId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}