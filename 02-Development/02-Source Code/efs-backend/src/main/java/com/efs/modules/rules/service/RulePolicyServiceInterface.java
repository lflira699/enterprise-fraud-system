package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RulePolicyRequest;
import com.efs.modules.rules.dto.RulePolicyResponse;

import java.util.List;
import java.util.UUID;

public interface RulePolicyServiceInterface {

    RulePolicyResponse createRulePolicy(
            RulePolicyRequest request
    );

    RulePolicyResponse getRulePolicyById(
            UUID policyId
    );

    RulePolicyResponse getRulePolicyByCode(
            String policyCode
    );

    List<RulePolicyResponse> getRulePoliciesByStatus(
            String status
    );

    List<RulePolicyResponse> getRulePoliciesByOrganizationId(
            UUID organizationId
    );

    List<RulePolicyResponse> getRulePoliciesByTenantId(
            UUID tenantId
    );

    List<RulePolicyResponse> getRulePoliciesByType(
            String policyType
    );
}