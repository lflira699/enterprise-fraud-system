package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import jakarta.persistence.EntityManager;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class DecisionExecutionServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "99999999-9999-9999-9999-999999999999"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
            );

    @Autowired
    private DecisionExecutionServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
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
                "EFS-DECISION-EXECUTION-TEST-CUSTOMER",
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
                    correlation_id,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                TRANSACTION_ID,
                "EFS-DECISION-EXECUTION-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("150.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CORRELATION_ID,
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
                new BigDecimal("90.00"),
                "ALTO",
                "REVIEW",
                new BigDecimal("92.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );
    }

    @Test
    void shouldEvaluateAndPersistDecision() {

        DecisionEvaluationRequest request =
                new DecisionEvaluationRequest();

        request.setRiskAssessmentId(
                RISK_ASSESSMENT_ID
        );

        request.setConfidenceLevel(
                "ALTA"
        );

        TransactionDecisionResponse response =
                service.evaluateAndPersistDecision(
                        request
                );

        entityManager.flush();

        assertNotNull(
                response.getDecisionId()
        );

        assertEquals(
                TRANSACTION_ID,
                response.getTransactionId()
        );

        assertEquals(
                RISK_ASSESSMENT_ID,
                response.getRiskAssessmentId()
        );

        assertEquals(
                "ESCALATE",
                response.getDecisionType()
        );

        assertEquals(
                "DECISION_ENGINE",
                response.getDecisionSource()
        );

        assertEquals(
                new BigDecimal("92.00"),
                response.getConfidenceScore()
        );

        assertEquals(
                "Escalar inmediatamente",
                response.getDecisionReason()
        );

        assertFalse(
                response.getFinalDecision()
        );

        Integer persistedCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.transaction_decision
                        WHERE transaction_id = ?
                          AND risk_assessment_id = ?
                          AND decision_type = ?
                          AND decision_source = ?
                        """,
                        Integer.class,
                        TRANSACTION_ID,
                        RISK_ASSESSMENT_ID,
                        "ESCALATE",
                        "DECISION_ENGINE"
                );

        assertEquals(
                1,
                persistedCount
        );

        Integer outboxCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                          AND correlation_id = ?
                          AND status = ?
                          AND attempt_count = ?
                        """,
                        Integer.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated",
                        CORRELATION_ID,
                        "PENDING",
                        0
                );

        assertEquals(
                1,
                outboxCount
        );

        String outboxDecisionId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT payload -> 'payload' ->> 'decisionId'
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                        """,
                        String.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated"
                );

        assertEquals(
                response.getDecisionId().toString(),
                outboxDecisionId
        );

        String outboxMessageId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT payload ->> 'messageId'
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                        """,
                        String.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated"
                );

        assertNotNull(
                outboxMessageId
        );

        String outboxSchemaVersion =
                jdbcTemplate.queryForObject(
                        """
                        SELECT payload ->> 'schemaVersion'
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                        """,
                        String.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated"
                );

        assertEquals(
                "1.0",
                outboxSchemaVersion
        );

        String outboxProducer =
                jdbcTemplate.queryForObject(
                        """
                        SELECT payload ->> 'producer'
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                        """,
                        String.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated"
                );

        assertEquals(
                "Decision Engine",
                outboxProducer
        );

        String outboxCorrelationId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT payload ->> 'correlationId'
                        FROM integration.outbox_event
                        WHERE aggregate_type = ?
                          AND aggregate_id = ?
                          AND event_type = ?
                        """,
                        String.class,
                        "TransactionDecision",
                        response.getDecisionId(),
                        "DecisionGenerated"
                );

        assertEquals(
                CORRELATION_ID.toString(),
                outboxCorrelationId
        );
    }
}
