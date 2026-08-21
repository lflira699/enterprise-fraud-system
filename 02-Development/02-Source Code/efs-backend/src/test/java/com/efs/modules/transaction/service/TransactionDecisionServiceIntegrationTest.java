package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                RISK_ASSESSMENT_ID,
                TRANSACTION_ID,
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