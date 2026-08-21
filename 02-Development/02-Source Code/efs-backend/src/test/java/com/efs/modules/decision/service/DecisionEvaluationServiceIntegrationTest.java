package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DecisionEvaluationServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    @Autowired
    private DecisionEvaluationServiceInterface service;

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
                "EFS-DECISION-ENGINE-TEST-CUSTOMER",
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
                "EFS-DECISION-ENGINE-TEST-TRANSACTION",
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

        insertRiskAssessment(
                "ALTO",
                new BigDecimal("90.00")
        );
    }

    @Test
    void shouldEvaluateHighRiskWithHighConfidence() {

        DecisionEvaluationRequest request =
                new DecisionEvaluationRequest();

        request.setRiskAssessmentId(
                RISK_ASSESSMENT_ID
        );

        request.setConfidenceLevel(
                "ALTA"
        );

        DecisionEvaluationResponse response =
                service.evaluateDecision(
                        request
                );

        assertEquals(
                RISK_ASSESSMENT_ID,
                response.getRiskAssessmentId()
        );

        assertEquals(
                TRANSACTION_ID,
                response.getTransactionId()
        );

        assertEquals(
                "ESCALATE",
                response.getDecisionType()
        );

        assertEquals(
                new BigDecimal("90.00"),
                response.getConfidenceScore()
        );

        assertEquals(
                "Escalar inmediatamente",
                response.getDecisionReason()
        );

        assertFalse(
                response.getFinalDecision()
        );
    }

    @Test
    void shouldRejectUnsupportedConfidenceLevel() {

        DecisionEvaluationRequest request =
                new DecisionEvaluationRequest();

        request.setRiskAssessmentId(
                RISK_ASSESSMENT_ID
        );

        request.setConfidenceLevel(
                "UNKNOWN"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluateDecision(request)
        );
    }

    @Test
    void shouldRejectUnconfiguredDecisionCombination() {

        DecisionEvaluationRequest request =
                new DecisionEvaluationRequest();

        request.setRiskAssessmentId(
                RISK_ASSESSMENT_ID
        );

        request.setConfidenceLevel(
                "MEDIA"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluateDecision(request)
        );
    }

    private void insertRiskAssessment(
            String riskLevel,
            BigDecimal confidenceScore) {

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
                riskLevel,
                "REVIEW",
                confidenceScore,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );
    }
}