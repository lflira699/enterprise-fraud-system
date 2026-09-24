package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluationServiceInterface {

    ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request,
            UUID organizationId,
            UUID tenantId
    );

    ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenario(
            UUID scenarioId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenarioVersion(
            UUID scenarioVersionId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByTransaction(
            UUID transactionId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByCustomer(
            UUID customerId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByStatus(
            String evaluationStatus,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByMatched(
            Boolean matched,
            UUID organizationId,
            UUID tenantId
    );
}