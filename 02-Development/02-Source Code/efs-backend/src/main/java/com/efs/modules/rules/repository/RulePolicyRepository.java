package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RulePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RulePolicyRepository
        extends JpaRepository<RulePolicy, UUID> {

    Optional<RulePolicy> findByPolicyId(
            UUID policyId
    );

    Optional<RulePolicy> findByPolicyCode(
            String policyCode
    );

    List<RulePolicy> findByStatusOrderByPriorityAsc(
            String status
    );

    List<RulePolicy> findByOrganizationIdOrderByPriorityAsc(
            UUID organizationId
    );

    List<RulePolicy> findByTenantIdOrderByPriorityAsc(
            UUID tenantId
    );

    List<RulePolicy> findByPolicyTypeOrderByPriorityAsc(
            String policyType
    );
}