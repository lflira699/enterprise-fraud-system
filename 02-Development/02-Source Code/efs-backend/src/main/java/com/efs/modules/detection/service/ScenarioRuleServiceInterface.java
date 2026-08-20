package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioRuleRequest;
import com.efs.modules.detection.dto.ScenarioRuleResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioRuleServiceInterface {

    ScenarioRuleResponse createScenarioRule(
            ScenarioRuleRequest request
    );

    ScenarioRuleResponse getScenarioRuleById(
            UUID scenarioRuleId
    );

    List<ScenarioRuleResponse> getScenarioRulesByScenarioVersion(
            UUID scenarioVersionId
    );

    List<ScenarioRuleResponse> getScenarioRulesByRule(
            UUID ruleId
    );

    List<ScenarioRuleResponse> getRequiredScenarioRules(
            UUID scenarioVersionId
    );
}