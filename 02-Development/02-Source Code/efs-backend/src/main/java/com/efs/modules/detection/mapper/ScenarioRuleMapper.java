package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioRuleRequest;
import com.efs.modules.detection.dto.ScenarioRuleResponse;
import com.efs.modules.detection.entity.ScenarioRule;
import org.springframework.stereotype.Component;

@Component
public class ScenarioRuleMapper {

    public ScenarioRule toEntity(
            ScenarioRuleRequest request) {

        ScenarioRule scenarioRule =
                new ScenarioRule();

        scenarioRule.setScenarioVersionId(
                request.getScenarioVersionId()
        );

        scenarioRule.setRuleId(
                request.getRuleId()
        );

        scenarioRule.setRuleRole(
                request.getRuleRole()
        );

        scenarioRule.setRequired(
                request.getRequired()
        );

        scenarioRule.setEvaluationOrder(
                request.getEvaluationOrder()
        );

        return scenarioRule;
    }

    public ScenarioRuleResponse toResponse(
            ScenarioRule scenarioRule) {

        ScenarioRuleResponse response =
                new ScenarioRuleResponse();

        response.setScenarioRuleId(
                scenarioRule.getScenarioRuleId()
        );

        response.setScenarioVersionId(
                scenarioRule.getScenarioVersionId()
        );

        response.setRuleId(
                scenarioRule.getRuleId()
        );

        response.setRuleRole(
                scenarioRule.getRuleRole()
        );

        response.setRequired(
                scenarioRule.getRequired()
        );

        response.setEvaluationOrder(
                scenarioRule.getEvaluationOrder()
        );

        response.setCreatedAt(
                scenarioRule.getCreatedAt()
        );

        return response;
    }
}