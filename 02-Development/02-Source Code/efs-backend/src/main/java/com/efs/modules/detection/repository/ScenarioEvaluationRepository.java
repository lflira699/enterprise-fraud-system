package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioEvaluationRepository
        extends JpaRepository<ScenarioEvaluation, UUID> {

    Optional<ScenarioEvaluation> findByEvaluationId(
            UUID evaluationId
    );

    List<ScenarioEvaluation>
    findByScenarioIdOrderByEvaluatedAtDesc(
            UUID scenarioId
    );

    List<ScenarioEvaluation>
    findByScenarioVersionIdOrderByEvaluatedAtDesc(
            UUID scenarioVersionId
    );

    List<ScenarioEvaluation>
    findByTransactionIdOrderByEvaluatedAtDesc(
            UUID transactionId
    );

    List<ScenarioEvaluation>
    findByCustomerIdOrderByEvaluatedAtDesc(
            UUID customerId
    );

    List<ScenarioEvaluation>
    findByEvaluationStatusOrderByEvaluatedAtDesc(
            String evaluationStatus
    );

    List<ScenarioEvaluation>
    findByMatchedOrderByEvaluatedAtDesc(
            Boolean matched
    );
}