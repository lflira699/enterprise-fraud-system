package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleParameterRequest;
import com.efs.modules.rules.dto.RuleParameterResponse;

import java.util.List;
import java.util.UUID;

public interface RuleParameterServiceInterface {

    RuleParameterResponse createRuleParameter(
            UUID ruleVersionId,
            RuleParameterRequest request
    );

    RuleParameterResponse getRuleParameterById(
            UUID parameterId
    );

    List<RuleParameterResponse> getRuleParametersByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleParameterResponse> getRuleParametersByName(
            String parameterName
    );
}