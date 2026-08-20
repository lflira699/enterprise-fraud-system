package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioEvaluationRuleExecutionRepository
        extends JpaRepository<ScenarioEvaluationRuleExecution, UUID> {

    Optional<ScenarioEvaluationRuleExecution>
    findByEvaluationRuleExecutionId(
            UUID evaluationRuleExecutionId
    );

    List<ScenarioEvaluationRuleExecution>
    findByEvaluationIdOrderByCreatedAtAsc(
            UUID evaluationId
    );

    List<ScenarioEvaluationRuleExecution>
    findByExecutionIdOrderByCreatedAtAsc(
            UUID executionId
    );
}