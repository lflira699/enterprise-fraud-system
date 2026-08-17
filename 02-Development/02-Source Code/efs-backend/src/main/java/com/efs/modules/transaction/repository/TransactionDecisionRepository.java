package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDecisionRepository
        extends JpaRepository<TransactionDecision, UUID> {

    Optional<TransactionDecision> findByDecisionId(
            UUID decisionId
    );

    List<TransactionDecision> findByTransactionIdOrderByDecisionTimestampDesc(
            UUID transactionId
    );

    List<TransactionDecision> findByDecisionTypeOrderByDecisionTimestampDesc(
            String decisionType
    );

    List<TransactionDecision> findByDecisionSourceOrderByDecisionTimestampDesc(
            String decisionSource
    );

    List<TransactionDecision> findByFinalDecisionOrderByDecisionTimestampDesc(
            Boolean finalDecision
    );
}