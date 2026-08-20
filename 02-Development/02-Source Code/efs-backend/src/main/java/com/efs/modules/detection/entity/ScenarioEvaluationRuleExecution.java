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
@Table(
        name = "scenario_evaluation_rule_execution",
        schema = "detection"
)
public class ScenarioEvaluationRuleExecution {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "evaluation_rule_execution_id",
            nullable = false
    )
    private UUID evaluationRuleExecutionId;

    @Column(name = "evaluation_id", nullable = false)
    private UUID evaluationId;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScenarioEvaluationRuleExecution() {
    }

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