package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleConditionRequest;
import com.efs.modules.rules.dto.RuleConditionResponse;

import java.util.List;
import java.util.UUID;

public interface RuleConditionServiceInterface {

    RuleConditionResponse createRuleCondition(
            UUID ruleVersionId,
            RuleConditionRequest request
    );

    RuleConditionResponse getRuleConditionById(
            UUID conditionId
    );

    List<RuleConditionResponse> getRuleConditionsByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleConditionResponse> getRuleConditionsByAttributeName(
            String attributeName
    );
}