package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluationAccessServiceInterface {

    ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request,
            SecurityContext securityContext
    );

    ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenario(
            UUID scenarioId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByScenarioVersion(
            UUID scenarioVersionId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByCustomer(
            UUID customerId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByStatus(
            String evaluationStatus,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationResponse> getEvaluationsByMatched(
            Boolean matched,
            SecurityContext securityContext
    );

    ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request,
            SecurityContext securityContext
    );

    ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(
            UUID evaluationId,
            SecurityContext securityContext
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(
            UUID executionId,
            SecurityContext securityContext
    );
}