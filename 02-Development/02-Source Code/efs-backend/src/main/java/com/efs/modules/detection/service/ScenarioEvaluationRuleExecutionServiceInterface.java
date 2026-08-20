package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluationRuleExecutionServiceInterface {

    ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request
    );

    ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(
            UUID evaluationId
    );

    List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(
            UUID executionId
    );
}