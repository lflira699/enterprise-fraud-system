package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioEvaluationRuleExecutionRepository
        extends JpaRepository<ScenarioEvaluationRuleExecution, UUID> {

    @Query(
            """
            SELECT relation
            FROM ScenarioEvaluationRuleExecution relation
            WHERE relation.evaluationRuleExecutionId =
                    :evaluationRuleExecutionId
              AND EXISTS (
                    SELECT evaluation.evaluationId
                    FROM ScenarioEvaluation evaluation
                    WHERE evaluation.evaluationId =
                            relation.evaluationId
                      AND evaluation.organizationId =
                            :organizationId
                      AND (
                            :tenantId IS NULL
                            OR evaluation.tenantId =
                                    :tenantId
                          )
                  )
            """
    )
    Optional<ScenarioEvaluationRuleExecution>
    findScopedByEvaluationRuleExecutionId(
            @Param("evaluationRuleExecutionId")
            UUID evaluationRuleExecutionId,
            @Param("organizationId")
            UUID organizationId,
            @Param("tenantId")
            UUID tenantId
    );

    @Query(
            """
            SELECT relation
            FROM ScenarioEvaluationRuleExecution relation
            WHERE relation.evaluationId = :evaluationId
              AND EXISTS (
                    SELECT evaluation.evaluationId
                    FROM ScenarioEvaluation evaluation
                    WHERE evaluation.evaluationId =
                            relation.evaluationId
                      AND evaluation.organizationId =
                            :organizationId
                      AND (
                            :tenantId IS NULL
                            OR evaluation.tenantId =
                                    :tenantId
                          )
                  )
            ORDER BY relation.createdAt ASC
            """
    )
    List<ScenarioEvaluationRuleExecution>
    findScopedByEvaluationId(
            @Param("evaluationId")
            UUID evaluationId,
            @Param("organizationId")
            UUID organizationId,
            @Param("tenantId")
            UUID tenantId
    );

    @Query(
            """
            SELECT relation
            FROM ScenarioEvaluationRuleExecution relation
            WHERE relation.executionId = :executionId
              AND EXISTS (
                    SELECT evaluation.evaluationId
                    FROM ScenarioEvaluation evaluation
                    WHERE evaluation.evaluationId =
                            relation.evaluationId
                      AND evaluation.organizationId =
                            :organizationId
                      AND (
                            :tenantId IS NULL
                            OR evaluation.tenantId =
                                    :tenantId
                          )
                  )
            ORDER BY relation.createdAt ASC
            """
    )
    List<ScenarioEvaluationRuleExecution>
    findScopedByExecutionId(
            @Param("executionId")
            UUID executionId,
            @Param("organizationId")
            UUID organizationId,
            @Param("tenantId")
            UUID tenantId
    );
}