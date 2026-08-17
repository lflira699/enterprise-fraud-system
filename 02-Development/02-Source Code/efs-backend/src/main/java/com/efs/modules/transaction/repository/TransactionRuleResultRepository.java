package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionRuleResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRuleResultRepository
        extends JpaRepository<TransactionRuleResult, UUID> {

    Optional<TransactionRuleResult> findByRuleResultId(
            UUID ruleResultId
    );

    List<TransactionRuleResult> findByTransactionIdOrderByExecutedAtAsc(
            UUID transactionId
    );

    List<TransactionRuleResult> findByRuleIdOrderByExecutedAtDesc(
            UUID ruleId
    );

    List<TransactionRuleResult> findByEvaluationResultOrderByExecutedAtDesc(
            String evaluationResult
    );
}