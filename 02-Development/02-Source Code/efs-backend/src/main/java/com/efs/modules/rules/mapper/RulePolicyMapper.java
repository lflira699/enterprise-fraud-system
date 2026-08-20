package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RulePolicyRequest;
import com.efs.modules.rules.dto.RulePolicyResponse;
import com.efs.modules.rules.entity.RulePolicy;
import org.springframework.stereotype.Component;

@Component
public class RulePolicyMapper {

    public RulePolicy toEntity(
            RulePolicyRequest request) {

        RulePolicy policy =
                new RulePolicy();

        policy.setPolicyCode(
                request.getPolicyCode()
        );

        policy.setPolicyName(
                request.getPolicyName()
        );

        policy.setDescription(
                request.getDescription()
        );

        policy.setPolicyType(
                request.getPolicyType()
        );

        policy.setOrganizationId(
                request.getOrganizationId()
        );

        policy.setTenantId(
                request.getTenantId()
        );

        policy.setStatus(
                request.getStatus()
        );

        policy.setEffectiveFrom(
                request.getEffectiveFrom()
        );

        policy.setEffectiveTo(
                request.getEffectiveTo()
        );

        policy.setPriority(
                request.getPriority()
        );

        return policy;
    }

    public RulePolicyResponse toResponse(
            RulePolicy policy) {

        RulePolicyResponse response =
                new RulePolicyResponse();

        response.setPolicyId(
                policy.getPolicyId()
        );

        response.setPolicyCode(
                policy.getPolicyCode()
        );

        response.setPolicyName(
                policy.getPolicyName()
        );

        response.setDescription(
                policy.getDescription()
        );

        response.setPolicyType(
                policy.getPolicyType()
        );

        response.setOrganizationId(
                policy.getOrganizationId()
        );

        response.setTenantId(
                policy.getTenantId()
        );

        response.setStatus(
                policy.getStatus()
        );

        response.setEffectiveFrom(
                policy.getEffectiveFrom()
        );

        response.setEffectiveTo(
                policy.getEffectiveTo()
        );

        response.setPriority(
                policy.getPriority()
        );

        response.setCreatedAt(
                policy.getCreatedAt()
        );

        response.setUpdatedAt(
                policy.getUpdatedAt()
        );

        return response;
    }
}