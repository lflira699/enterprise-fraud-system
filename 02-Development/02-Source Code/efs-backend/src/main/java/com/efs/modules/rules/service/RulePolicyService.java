package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RulePolicyRequest;
import com.efs.modules.rules.dto.RulePolicyResponse;
import com.efs.modules.rules.entity.RulePolicy;
import com.efs.modules.rules.mapper.RulePolicyMapper;
import com.efs.modules.rules.repository.RulePolicyRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RulePolicyService
        implements RulePolicyServiceInterface {

    private final RulePolicyRepository rulePolicyRepository;
    private final RulePolicyMapper rulePolicyMapper;

    public RulePolicyService(
            RulePolicyRepository rulePolicyRepository,
            RulePolicyMapper rulePolicyMapper) {

        this.rulePolicyRepository = rulePolicyRepository;
        this.rulePolicyMapper = rulePolicyMapper;
    }

    @Override
    @Transactional
    public RulePolicyResponse createRulePolicy(
            RulePolicyRequest request) {

        RulePolicy policy =
                rulePolicyMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);

        RulePolicy savedPolicy =
                rulePolicyRepository.save(policy);

        return rulePolicyMapper.toResponse(savedPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public RulePolicyResponse getRulePolicyById(
            UUID policyId) {

        RulePolicy policy =
                rulePolicyRepository
                        .findByPolicyId(policyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule policy not found: " + policyId
                                )
                        );

        return rulePolicyMapper.toResponse(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public RulePolicyResponse getRulePolicyByCode(
            String policyCode) {

        RulePolicy policy =
                rulePolicyRepository
                        .findByPolicyCode(policyCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule policy not found: " + policyCode
                                )
                        );

        return rulePolicyMapper.toResponse(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RulePolicyResponse> getRulePoliciesByStatus(
            String status) {

        return rulePolicyRepository
                .findByStatusOrderByPriorityAsc(status)
                .stream()
                .map(rulePolicyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RulePolicyResponse> getRulePoliciesByOrganizationId(
            UUID organizationId) {

        return rulePolicyRepository
                .findByOrganizationIdOrderByPriorityAsc(organizationId)
                .stream()
                .map(rulePolicyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RulePolicyResponse> getRulePoliciesByTenantId(
            UUID tenantId) {

        return rulePolicyRepository
                .findByTenantIdOrderByPriorityAsc(tenantId)
                .stream()
                .map(rulePolicyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RulePolicyResponse> getRulePoliciesByType(
            String policyType) {

        return rulePolicyRepository
                .findByPolicyTypeOrderByPriorityAsc(policyType)
                .stream()
                .map(rulePolicyMapper::toResponse)
                .toList();
    }
}