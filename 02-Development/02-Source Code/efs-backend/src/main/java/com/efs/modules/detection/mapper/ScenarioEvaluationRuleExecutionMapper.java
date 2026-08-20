package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import org.springframework.stereotype.Component;

@Component
public class ScenarioEvaluationRuleExecutionMapper {

    public ScenarioEvaluationRuleExecution toEntity(
            ScenarioEvaluationRuleExecutionRequest request) {

        ScenarioEvaluationRuleExecution relation =
                new ScenarioEvaluationRuleExecution();

        relation.setEvaluationId(
                request.getEvaluationId()
        );

        relation.setExecutionId(
                request.getExecutionId()
        );

        return relation;
    }

    public ScenarioEvaluationRuleExecutionResponse toResponse(
            ScenarioEvaluationRuleExecution relation) {

        ScenarioEvaluationRuleExecutionResponse response =
                new ScenarioEvaluationRuleExecutionResponse();

        response.setEvaluationRuleExecutionId(
                relation.getEvaluationRuleExecutionId()
        );

        response.setEvaluationId(
                relation.getEvaluationId()
        );

        response.setExecutionId(
                relation.getExecutionId()
        );

        response.setCreatedAt(
                relation.getCreatedAt()
        );

        return response;
    }
}