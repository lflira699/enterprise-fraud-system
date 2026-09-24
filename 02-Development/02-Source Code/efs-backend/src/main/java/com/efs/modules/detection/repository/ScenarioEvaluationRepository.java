package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioEvaluationRepository
        extends JpaRepository<ScenarioEvaluation, UUID> {

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.evaluationId = :evaluationId
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            """
    )
    Optional<ScenarioEvaluation> findScopedByEvaluationId(
            @Param("evaluationId") UUID evaluationId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.scenarioId = :scenarioId
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByScenarioId(
            @Param("scenarioId") UUID scenarioId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.scenarioVersionId = :scenarioVersionId
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByScenarioVersionId(
            @Param("scenarioVersionId") UUID scenarioVersionId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.transactionId = :transactionId
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByTransactionId(
            @Param("transactionId") UUID transactionId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.customerId = :customerId
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByCustomerId(
            @Param("customerId") UUID customerId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.evaluationStatus = :evaluationStatus
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByEvaluationStatus(
            @Param("evaluationStatus") String evaluationStatus,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT evaluation
            FROM ScenarioEvaluation evaluation
            WHERE evaluation.matched = :matched
              AND evaluation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR evaluation.tenantId = :tenantId
                  )
            ORDER BY evaluation.evaluatedAt DESC
            """
    )
    List<ScenarioEvaluation> findScopedByMatched(
            @Param("matched") Boolean matched,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );
}