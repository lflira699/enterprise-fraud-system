package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluationRuleExecutionServiceInterface {

    ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request,
            UUID organizationId,
            UUID tenantId
    );

    ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(
            UUID evaluationId,
            UUID organizationId,
            UUID tenantId
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(
            UUID executionId,
            UUID organizationId,
            UUID tenantId
    );
}