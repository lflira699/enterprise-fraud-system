package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleActivationRequest;
import com.efs.modules.rules.dto.RuleDeactivationRequest;
import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleTestingRequest;
import com.efs.modules.rules.dto.RuleUpdateRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface RuleAccessServiceInterface {

    RuleResponse createRule(
            RuleRequest request,
            SecurityContext securityContext
    );

    List<RuleResponse> getRules(
            SecurityContext securityContext
    );

    RuleResponse getRuleById(
            UUID ruleId,
            SecurityContext securityContext
    );

    RuleResponse getRuleByCode(
            String ruleCode,
            SecurityContext securityContext
    );

    List<RuleResponse> getRulesByStatus(
            String status,
            SecurityContext securityContext
    );

    List<RuleResponse> getRulesByCategory(
            String category,
            SecurityContext securityContext
    );

    List<RuleResponse> getRulesBySeverity(
            String severity,
            SecurityContext securityContext
    );

    RuleVersionResponse updateRule(
            UUID ruleId,
            RuleUpdateRequest request,
            SecurityContext securityContext
    );

    RuleResponse activateRule(
            UUID ruleId,
            RuleActivationRequest request,
            SecurityContext securityContext
    );

    RuleResponse deactivateRule(
            UUID ruleId,
            RuleDeactivationRequest request,
            SecurityContext securityContext
    );

    RuleSimulationResponse testRule(
            UUID ruleId,
            UUID ruleVersionId,
            RuleTestingRequest request,
            SecurityContext securityContext
    );
}