package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioActivationRepository
        extends JpaRepository<ScenarioActivation, UUID> {

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.activationId = :activationId
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            """
    )
    Optional<ScenarioActivation> findScopedByActivationId(
            @Param("activationId") UUID activationId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.scenarioId = :scenarioId
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedByScenarioId(
            @Param("scenarioId") UUID scenarioId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.scenarioVersionId = :scenarioVersionId
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedByScenarioVersionId(
            @Param("scenarioVersionId") UUID scenarioVersionId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.transactionId = :transactionId
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedByTransactionId(
            @Param("transactionId") UUID transactionId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.customerId = :customerId
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedByCustomerId(
            @Param("customerId") UUID customerId,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.activationStatus = :activationStatus
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedByActivationStatus(
            @Param("activationStatus") String activationStatus,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT activation
            FROM ScenarioActivation activation
            WHERE activation.severity = :severity
              AND activation.organizationId = :organizationId
              AND (
                    :tenantId IS NULL
                    OR activation.tenantId = :tenantId
                  )
            ORDER BY activation.triggeredAt DESC
            """
    )
    List<ScenarioActivation> findScopedBySeverity(
            @Param("severity") String severity,
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT COUNT(DISTINCT activation.scenarioId)
            FROM ScenarioActivation activation
            WHERE activation.organizationId = :organizationId
              AND activation.tenantId = :tenantId
            """
    )
    long countDistinctScenariosByOrganizationIdAndTenantId(
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            """
            SELECT COUNT(DISTINCT activation.scenarioId)
            FROM ScenarioActivation activation
            WHERE activation.organizationId = :organizationId
              AND activation.tenantId IS NULL
            """
    )
    long countDistinctScenariosByOrganizationIdAndTenantIdIsNull(
            @Param("organizationId") UUID organizationId
    );
}