package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioActivation;
import org.springframework.data.jpa.repository.JpaRepository;

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
}