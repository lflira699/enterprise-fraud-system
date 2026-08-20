package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleExecutionRepository
        extends JpaRepository<RuleExecution, UUID> {

    Optional<RuleExecution> findByExecutionId(
            UUID executionId
    );

    List<RuleExecution> findByRuleIdOrderByExecutedAtDesc(
            UUID ruleId
    );

    List<RuleExecution> findByRuleVersionIdOrderByExecutedAtDesc(
            UUID ruleVersionId
    );

    List<RuleExecution> findByPolicyIdOrderByExecutedAtDesc(
            UUID policyId
    );

    List<RuleExecution> findByTransactionIdOrderByExecutedAtDesc(
            UUID transactionId
    );

    List<RuleExecution> findByExecutionStatusOrderByExecutedAtDesc(
            String executionStatus
    );
}