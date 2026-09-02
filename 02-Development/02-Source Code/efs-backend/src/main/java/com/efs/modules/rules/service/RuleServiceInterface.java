package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;

import java.util.List;
import java.util.UUID;

public interface RuleServiceInterface {

    RuleResponse createRule(
            RuleRequest request
    );

    List<RuleResponse> getRules();

    RuleResponse getRuleById(
            UUID ruleId
    );

    RuleResponse getRuleByCode(
            String ruleCode
    );

    List<RuleResponse> getRulesByStatus(
            String status
    );

    List<RuleResponse> getRulesByCategory(
            String category
    );

    List<RuleResponse> getRulesBySeverity(
            String severity
    );
}