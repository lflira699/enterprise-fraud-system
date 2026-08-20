package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioActivationServiceInterface {

    ScenarioActivationResponse createScenarioActivation(
            ScenarioActivationRequest request
    );

    ScenarioActivationResponse getScenarioActivationById(
            UUID activationId
    );

    List<ScenarioActivationResponse> getActivationsByScenario(
            UUID scenarioId
    );

    List<ScenarioActivationResponse> getActivationsByScenarioVersion(
            UUID scenarioVersionId
    );

    List<ScenarioActivationResponse> getActivationsByTransaction(
            UUID transactionId
    );

    List<ScenarioActivationResponse> getActivationsByCustomer(
            UUID customerId
    );

    List<ScenarioActivationResponse> getActivationsByStatus(
            String activationStatus
    );

    List<ScenarioActivationResponse> getActivationsBySeverity(
            String severity
    );
}