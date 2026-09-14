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

    Optional<ScenarioActivation> findByActivationId(
            UUID activationId
    );

    List<ScenarioActivation>
    findByScenarioIdOrderByTriggeredAtDesc(
            UUID scenarioId
    );

    List<ScenarioActivation>
    findByScenarioVersionIdOrderByTriggeredAtDesc(
            UUID scenarioVersionId
    );

    List<ScenarioActivation>
    findByTransactionIdOrderByTriggeredAtDesc(
            UUID transactionId
    );

    List<ScenarioActivation>
    findByCustomerIdOrderByTriggeredAtDesc(
            UUID customerId
    );

    List<ScenarioActivation>
    findByActivationStatusOrderByTriggeredAtDesc(
            String activationStatus
    );

    List<ScenarioActivation>
    findBySeverityOrderByTriggeredAtDesc(
            String severity
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
