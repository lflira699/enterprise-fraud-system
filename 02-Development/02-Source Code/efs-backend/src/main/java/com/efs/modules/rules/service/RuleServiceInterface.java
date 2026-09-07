package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleDeactivationRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;

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

    RuleVersionResponse updateRule(
            UUID ruleId,
            RuleUpdateRequest request
    );

    RuleResponse activateRule(
            UUID ruleId,
            RuleActivationRequest request
    );

    RuleResponse deactivateRule(
            UUID ruleId,
            RuleDeactivationRequest request
    );
}