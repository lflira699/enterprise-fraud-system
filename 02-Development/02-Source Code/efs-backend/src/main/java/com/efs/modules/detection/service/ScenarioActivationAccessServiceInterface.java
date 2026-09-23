package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface ScenarioActivationAccessServiceInterface {

    ScenarioActivationResponse createScenarioActivation(
            ScenarioActivationRequest request,
            SecurityContext securityContext
    );

    ScenarioActivationResponse getScenarioActivationById(
            UUID activationId,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsByScenario(
            UUID scenarioId,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsByScenarioVersion(
            UUID scenarioVersionId,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsByCustomer(
            UUID customerId,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsByStatus(
            String activationStatus,
            SecurityContext securityContext
    );

    List<ScenarioActivationResponse> getActivationsBySeverity(
            String severity,
            SecurityContext securityContext
    );
}