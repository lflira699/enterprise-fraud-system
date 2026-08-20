package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleActionRequest;
import com.efs.modules.rules.dto.RuleActionResponse;

import java.util.List;
import java.util.UUID;

public interface RuleActionServiceInterface {

    RuleActionResponse createRuleAction(
            UUID ruleVersionId,
            RuleActionRequest request
    );

    RuleActionResponse getRuleActionById(
            UUID actionId
    );

    List<RuleActionResponse> getRuleActionsByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleActionResponse> getRuleActionsByType(
            String actionType
    );
}