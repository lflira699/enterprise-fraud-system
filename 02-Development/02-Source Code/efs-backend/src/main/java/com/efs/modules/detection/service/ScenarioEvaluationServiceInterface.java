package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluationServiceInterface {

    ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request
    );

    ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenario(
            UUID scenarioId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenarioVersion(
            UUID scenarioVersionId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByTransaction(
            UUID transactionId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByCustomer(
            UUID customerId
    );

    List<ScenarioEvaluationResponse> getEvaluationsByStatus(
            String evaluationStatus
    );

    List<ScenarioEvaluationResponse> getEvaluationsByMatched(
            Boolean matched
    );
}