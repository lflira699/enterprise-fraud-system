package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(
            value = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM transaction.risk_assessment AS ra
                        WHERE ra.risk_assessment_id = :riskAssessmentId
                          AND ra.transaction_id = :transactionId
                          AND ra.deleted_at IS NULL
                    )
                    """,
            nativeQuery = true
    )
    boolean existsActiveRiskAssessmentForTransaction(
            @Param("riskAssessmentId") UUID riskAssessmentId,
            @Param("transactionId") UUID transactionId
    );
}