package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransactionDecisionServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "cccccccc-cccc-cccc-cccc-cccccccccccc"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "dddddddd-dddd-dddd-dddd-dddddddddddd"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb"
            );

    @Autowired
    private TransactionDecisionServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO customer.customer (
                    customer_id,
                    customer_number,
                    customer_type,
                    risk_level,
                    risk_score,
                    customer_status,
                    record_status,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CUSTOMER_ID,
                "EFS-DECISION-TEST-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-TRANSACTION-DECISION-ORG",
                "EFS Transaction Decision Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                TRANSACTION_ID,
                "EFS-DECISION-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    organization_id,
                    assessment_type,
                    assessment_stage,
                    overall_risk_score,
                    risk_level,
                    assessment_result,
                    confidence_score,
                    assessment_timestamp,
                    created_at,
                    updated_at,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                RISK_ASSESSMENT_ID,
                TRANSACTION_ID,
                ORGANIZATION_ID,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("85.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );
    }

    @Test
    void shouldCreateAndRetrieveDecision() {

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("90.00"),
                        "Integration test decision",
                        false
                );

        TransactionDecisionResponse created =
                service.createDecision(
                        TRANSACTION_ID,
                        request
                );

        assertNotNull(
                created.getDecisionId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                RISK_ASSESSMENT_ID,
                created.getRiskAssessmentId()
        );

        assertEquals(
                "REVIEW",
                created.getDecisionType()
        );

        assertEquals(
                "RISK_ENGINE",
                created.getDecisionSource()
        );

        assertEquals(
                new BigDecimal("90.00"),
                created.getConfidenceScore()
        );

        TransactionDecisionResponse retrieved =
                service.getDecisionById(
                        created.getDecisionId()
                );

        assertEquals(
                created.getDecisionId(),
                retrieved.getDecisionId()
        );

        assertEquals(
                RISK_ASSESSMENT_ID,
                retrieved.getRiskAssessmentId()
        );
    }

    @Test
    void shouldReturnDecisionsByTransaction() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Initial decision",
                        false
                )
        );

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "APPROVE",
                        "ANALYST",
                        new BigDecimal("95.00"),
                        "Final decision",
                        true
                )
        );

        List<TransactionDecisionResponse> decisions =
                service.getDecisionsByTransactionId(
                        TRANSACTION_ID
                );

        assertEquals(
                2,
                decisions.size()
        );

        assertEquals(
                RISK_ASSESSMENT_ID,
                decisions.get(0).getRiskAssessmentId()
        );
    }

    @Test
    void shouldFilterDecisions() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Automated review",
                        false
                )
        );

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "APPROVE",
                        "ANALYST",
                        new BigDecimal("95.00"),
                        "Analyst approval",
                        true
                )
        );

        List<TransactionDecisionResponse> byType =
                service.getDecisionsByType(
                        "APPROVE"
                );

        assertEquals(
                1,
                byType.size()
        );

        List<TransactionDecisionResponse> bySource =
                service.getDecisionsBySource(
                        "RISK_ENGINE"
                );

        assertEquals(
                1,
                bySource.size()
        );

        List<TransactionDecisionResponse> byFinalStatus =
                service.getDecisionsByFinalStatus(
                        true
                );

        assertEquals(
                1,
                byFinalStatus.size()
        );
    }

    @Test
    void shouldRejectCreateWhenTransactionIsSoftDeleted() {

        softDeleteTransaction(TRANSACTION_ID);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createDecision(
                        TRANSACTION_ID,
                        buildRequest(
                                "REVIEW",
                                "RISK_ENGINE",
                                new BigDecimal("80.00"),
                                "Deleted transaction",
                                false
                        )
                )
        );
    }

    @Test
    void shouldHideDecisionByIdWhenTransactionIsSoftDeleted() {

        TransactionDecisionResponse created =
                service.createDecision(
                        TRANSACTION_ID,
                        buildRequest(
                                "REVIEW",
                                "RISK_ENGINE",
                                new BigDecimal("80.00"),
                                "Hidden by parent",
                                false
                        )
                );

        softDeleteTransaction(TRANSACTION_ID);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDecisionById(
                        created.getDecisionId()
                )
        );
    }

    @Test
    void shouldRejectTransactionListWhenTransactionIsSoftDeleted() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Deleted parent list",
                        false
                )
        );

        softDeleteTransaction(TRANSACTION_ID);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDecisionsByTransactionId(
                        TRANSACTION_ID
                )
        );
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromTypeFilter() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "SOFT_DELETE_TYPE",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Type filter",
                        false
                )
        );

        softDeleteTransaction(TRANSACTION_ID);

        assertEquals(
                0,
                service.getDecisionsByType(
                        "SOFT_DELETE_TYPE"
                ).size()
        );
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromSourceFilter() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "REVIEW",
                        "SOFT_DELETE_SOURCE",
                        new BigDecimal("80.00"),
                        "Source filter",
                        false
                )
        );

        softDeleteTransaction(TRANSACTION_ID);

        assertEquals(
                0,
                service.getDecisionsBySource(
                        "SOFT_DELETE_SOURCE"
                ).size()
        );
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromFinalFilter() {

        service.createDecision(
                TRANSACTION_ID,
                buildRequest(
                        "SOFT_DELETE_FINAL",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Final filter",
                        true
                )
        );

        softDeleteTransaction(TRANSACTION_ID);

        assertEquals(
                0,
                service.getDecisionsByFinalStatus(true).size()
        );
    }

    @Test
    void shouldRejectUnknownRiskAssessment() {

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Unknown risk assessment",
                        false
                );

        request.setRiskAssessmentId(UUID.randomUUID());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createDecision(
                        TRANSACTION_ID,
                        request
                )
        );
    }

    @Test
    void shouldRejectRiskAssessmentFromDifferentTransaction() {

        UUID otherTransactionId = UUID.randomUUID();
        UUID otherRiskAssessmentId = UUID.randomUUID();

        insertAdditionalTransaction(otherTransactionId);

        insertRiskAssessment(
                otherRiskAssessmentId,
                otherTransactionId
        );

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Cross transaction risk assessment",
                        false
                );

        request.setRiskAssessmentId(otherRiskAssessmentId);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createDecision(
                        TRANSACTION_ID,
                        request
                )
        );
    }

    @Test
    void shouldRejectSoftDeletedRiskAssessment() {

        jdbcTemplate.update(
                """
                UPDATE transaction.risk_assessment
                SET deleted_at = CURRENT_TIMESTAMP
                WHERE risk_assessment_id = ?
                """,
                RISK_ASSESSMENT_ID
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createDecision(
                        TRANSACTION_ID,
                        buildRequest(
                                "REVIEW",
                                "RISK_ENGINE",
                                new BigDecimal("80.00"),
                                "Deleted risk assessment",
                                false
                        )
                )
        );
    }

    private void softDeleteTransaction(
            UUID targetTransactionId) {

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE transaction.transaction
                SET deleted_at = CURRENT_TIMESTAMP
                WHERE transaction_id = ?
                """,
                targetTransactionId
        );

        entityManager.clear();
    }

    private void insertAdditionalTransaction(
            UUID additionalTransactionId) {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                additionalTransactionId,
                "EFS-DECISION-SECOND-" + additionalTransactionId,
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );
    }

    private void insertRiskAssessment(
            UUID assessmentId,
            UUID assessmentTransactionId) {

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    organization_id,
                    assessment_type,
                    assessment_stage,
                    overall_risk_score,
                    risk_level,
                    assessment_result,
                    confidence_score,
                    assessment_timestamp,
                    created_at,
                    updated_at,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                assessmentId,
                assessmentTransactionId,
                ORGANIZATION_ID,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("85.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                now,
                now,
                now,
                0
        );
    }

    private TransactionDecisionRequest buildRequest(
            String decisionType,
            String decisionSource,
            BigDecimal confidenceScore,
            String decisionReason,
            Boolean finalDecision
    ) {

        TransactionDecisionRequest request =
                new TransactionDecisionRequest();

        request.setRiskAssessmentId(
                RISK_ASSESSMENT_ID
        );

        request.setDecisionType(
                decisionType
        );

        request.setDecisionSource(
                decisionSource
        );

        request.setConfidenceScore(
                confidenceScore
        );

        request.setDecisionReason(
                decisionReason
        );

        request.setDecisionTimestamp(
                LocalDateTime.now()
        );

        request.setFinalDecision(
                finalDecision
        );

        return request;
    }
}