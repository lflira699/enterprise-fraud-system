package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class RiskAssessmentServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    @Autowired
    private RiskAssessmentServiceInterface service;

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
                "EFS-RISK-TEST-CUSTOMER",
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
                "EFS-RISK-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CORRELATION_ID,
                CREATED_BY,
                1
        );
    }

    @Test
    void shouldCalculateCreatePersistEventAndRetrieveRiskAssessment() {

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("99.00"),
                        "CRITICAL",
                        "PASS",
                        new BigDecimal("20.00"),
                        new BigDecimal("15.00"),
                        new BigDecimal("10.00"),
                        new BigDecimal("5.00"),
                        new BigDecimal("8.00")
                );

        RiskAssessmentResponse created =
                service.createRiskAssessment(
                        request
                );

        assertNotNull(
                created.getRiskAssessmentId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                new BigDecimal("11.60"),
                created.getOverallRiskScore()
        );

        assertEquals(
                "VERY_LOW",
                created.getRiskLevel()
        );

        assertEquals(
                "PASS",
                created.getAssessmentResult()
        );

        assertEquals(
                "EFS-RISK",
                created.getModelName()
        );

        assertEquals(
                "1.1",
                created.getModelVersion()
        );

        assertNotNull(
                created.getProcessingTimeMs()
        );

        assertTrue(
                created.getProcessingTimeMs() >= 0
        );

        entityManager.flush();

        OutboxRow outbox =
                findRiskCalculatedEvent(
                        created.getRiskAssessmentId()
                );

        assertNotNull(
                outbox
        );

        assertNotNull(
                outbox.id()
        );

        assertEquals(
                "RiskAssessment",
                outbox.aggregateType()
        );

        assertEquals(
                created.getRiskAssessmentId(),
                outbox.aggregateId()
        );

        assertEquals(
                "RiskCalculated",
                outbox.eventType()
        );

        assertEquals(
                CORRELATION_ID,
                outbox.correlationId()
        );

        assertEquals(
                "PENDING",
                outbox.status()
        );

        assertEquals(
                0,
                outbox.attemptCount()
        );

        assertEquals(
                outbox.id().toString(),
                outbox.messageId()
        );

        assertEquals(
                "RiskCalculated",
                outbox.envelopeEventType()
        );

        assertEquals(
                "1.0",
                outbox.schemaVersion()
        );

        assertEquals(
                "Risk Engine",
                outbox.producer()
        );

        assertEquals(
                CORRELATION_ID.toString(),
                outbox.envelopeCorrelationId()
        );

        assertEquals(
                created.getRiskAssessmentId().toString(),
                outbox.payloadRiskAssessmentId()
        );

        RiskAssessmentResponse retrieved =
                service.getRiskAssessmentById(
                        created.getRiskAssessmentId()
                );

        assertEquals(
                created.getRiskAssessmentId(),
                retrieved.getRiskAssessmentId()
        );

        assertEquals(
                new BigDecimal("11.60"),
                retrieved.getOverallRiskScore()
        );

        assertEquals(
                "VERY_LOW",
                retrieved.getRiskLevel()
        );
    }

    @Test
    void shouldIgnoreCallerSuppliedCalculatedValues() {

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00")
                );

        request.setModelName(
                "CALLER-MODEL"
        );

        request.setModelVersion(
                "999"
        );

        request.setProcessingTimeMs(
                999999L
        );

        RiskAssessmentResponse created =
                service.createRiskAssessment(
                        request
                );

        assertEquals(
                new BigDecimal("80.00"),
                created.getOverallRiskScore()
        );

        assertEquals(
                "CRITICAL",
                created.getRiskLevel()
        );

        assertEquals(
                "EFS-RISK",
                created.getModelName()
        );

        assertEquals(
                "1.1",
                created.getModelVersion()
        );

        assertNotNull(
                created.getProcessingTimeMs()
        );
    }

    @Test
    void shouldReuseExistingAssessmentWithoutDuplicatingRiskCalculatedEvent() {

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00")
                );

        RiskAssessmentResponse first =
                service.createRiskAssessment(
                        request
                );

        RiskAssessmentResponse second =
                service.createRiskAssessment(
                        request
                );

        assertEquals(
                first.getRiskAssessmentId(),
                second.getRiskAssessmentId()
        );

        assertEquals(
                new BigDecimal("60.00"),
                second.getOverallRiskScore()
        );

        assertEquals(
                "HIGH",
                second.getRiskLevel()
        );

        entityManager.flush();

        Long assessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE transaction_id = ?
                          AND assessment_type = ?
                          AND assessment_stage = ?
                        """,
                        Long.class,
                        TRANSACTION_ID,
                        "TRANSACTION",
                        "FINAL"
                );

        assertNotNull(
                assessmentCount
        );

        assertEquals(
                1L,
                assessmentCount.longValue()
        );

        Long outboxCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'RiskCalculated'
                          AND aggregate_type = 'RiskAssessment'
                          AND aggregate_id = ?
                        """,
                        Long.class,
                        first.getRiskAssessmentId()
                );

        assertNotNull(
                outboxCount
        );

        assertEquals(
                1L,
                outboxCount.longValue()
        );
    }

    @Test
    void shouldCreateNewAssessmentAndEventWhenRiskEvaluationDataChanges() {

        RiskAssessmentRequest firstRequest =
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00")
                );

        RiskAssessmentRequest secondRequest =
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("60.00"),
                        new BigDecimal("61.00")
                );

        RiskAssessmentResponse first =
                service.createRiskAssessment(
                        firstRequest
                );

        RiskAssessmentResponse second =
                service.createRiskAssessment(
                        secondRequest
                );

        assertNotEquals(
                first.getRiskAssessmentId(),
                second.getRiskAssessmentId()
        );

        entityManager.flush();

        Long assessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE transaction_id = ?
                          AND assessment_type = ?
                          AND assessment_stage = ?
                        """,
                        Long.class,
                        TRANSACTION_ID,
                        "TRANSACTION",
                        "FINAL"
                );

        assertNotNull(
                assessmentCount
        );

        assertEquals(
                2L,
                assessmentCount.longValue()
        );

        Long eventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'RiskCalculated'
                          AND aggregate_type = 'RiskAssessment'
                          AND aggregate_id IN (?, ?)
                        """,
                        Long.class,
                        first.getRiskAssessmentId(),
                        second.getRiskAssessmentId()
                );

        assertNotNull(
                eventCount
        );

        assertEquals(
                2L,
                eventCount.longValue()
        );
    }

    @Test
    void shouldRejectRiskCalculationWhenTransactionCorrelationIdIsMissing() {

        jdbcTemplate.update(
                """
                UPDATE transaction.transaction
                SET correlation_id = NULL
                WHERE transaction_id = ?
                """,
                TRANSACTION_ID
        );

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "PASS",
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00")
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.createRiskAssessment(
                                        request
                                )
                );

        assertEquals(
                "Transaction correlationId is required "
                        + "for RiskCalculated event",
                exception.getMessage()
        );

        entityManager.flush();

        Long assessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE transaction_id = ?
                        """,
                        Long.class,
                        TRANSACTION_ID
                );

        assertNotNull(
                assessmentCount
        );

        assertEquals(
                0L,
                assessmentCount.longValue()
        );

        Long eventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'RiskCalculated'
                          AND correlation_id = ?
                        """,
                        Long.class,
                        CORRELATION_ID
                );

        assertNotNull(
                eventCount
        );

        assertEquals(
                0L,
                eventCount.longValue()
        );
    }

    @Test
    void shouldReturnAssessmentsByTransactionAndLatest() {

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("99.00"),
                        "CRITICAL",
                        "PASS",
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20.00")
                )
        );

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00"),
                        new BigDecimal("80.00")
                )
        );

        List<RiskAssessmentResponse> assessments =
                service.getAssessmentsByTransaction(
                        TRANSACTION_ID
                );

        assertEquals(
                2,
                assessments.size()
        );

        RiskAssessmentResponse latest =
                service.getLatestAssessmentByTransaction(
                        TRANSACTION_ID
                );

        assertEquals(
                "FINAL",
                latest.getAssessmentStage()
        );

        assertEquals(
                new BigDecimal("80.00"),
                latest.getOverallRiskScore()
        );

        assertEquals(
                "CRITICAL",
                latest.getRiskLevel()
        );
    }

    @Test
    void shouldFilterRiskAssessments() {

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("99.00"),
                        "CRITICAL",
                        "PASS",
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00")
                )
        );

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("1.00"),
                        "LOW",
                        "REVIEW",
                        new BigDecimal("75.00"),
                        new BigDecimal("75.00"),
                        new BigDecimal("75.00"),
                        new BigDecimal("75.00"),
                        new BigDecimal("75.00")
                )
        );

        List<RiskAssessmentResponse> byType =
                service.getAssessmentsByTransactionAndType(
                        TRANSACTION_ID,
                        "TRANSACTION"
                );

        assertEquals(
                2,
                byType.size()
        );

        List<RiskAssessmentResponse> byRiskLevel =
                service.getAssessmentsByRiskLevel(
                        "HIGH"
                );

        assertEquals(
                1,
                byRiskLevel.size()
        );

        List<RiskAssessmentResponse> byResult =
                service.getAssessmentsByResult(
                        "REVIEW"
                );

        assertEquals(
                1,
                byResult.size()
        );
    }

    private OutboxRow findRiskCalculatedEvent(
            UUID riskAssessmentId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT
                    id,
                    aggregate_type,
                    aggregate_id,
                    event_type,
                    correlation_id,
                    status,
                    attempt_count,
                    payload ->> 'messageId'
                        AS message_id,
                    payload ->> 'eventType'
                        AS envelope_event_type,
                    payload ->> 'schemaVersion'
                        AS schema_version,
                    payload ->> 'producer'
                        AS producer,
                    payload ->> 'correlationId'
                        AS envelope_correlation_id,
                    payload -> 'payload'
                            ->> 'riskAssessmentId'
                        AS payload_risk_assessment_id
                FROM integration.outbox_event
                WHERE event_type = 'RiskCalculated'
                  AND aggregate_type = 'RiskAssessment'
                  AND aggregate_id = ?
                """,
                (resultSet, rowNumber) ->
                        new OutboxRow(
                                resultSet.getObject(
                                        "id",
                                        UUID.class
                                ),
                                resultSet.getString(
                                        "aggregate_type"
                                ),
                                resultSet.getObject(
                                        "aggregate_id",
                                        UUID.class
                                ),
                                resultSet.getString(
                                        "event_type"
                                ),
                                resultSet.getObject(
                                        "correlation_id",
                                        UUID.class
                                ),
                                resultSet.getString(
                                        "status"
                                ),
                                resultSet.getInt(
                                        "attempt_count"
                                ),
                                resultSet.getString(
                                        "message_id"
                                ),
                                resultSet.getString(
                                        "envelope_event_type"
                                ),
                                resultSet.getString(
                                        "schema_version"
                                ),
                                resultSet.getString(
                                        "producer"
                                ),
                                resultSet.getString(
                                        "envelope_correlation_id"
                                ),
                                resultSet.getString(
                                        "payload_risk_assessment_id"
                                )
                        ),
                riskAssessmentId
        );
    }

    private RiskAssessmentRequest buildRequest(
            String assessmentType,
            String assessmentStage,
            BigDecimal callerOverallRiskScore,
            String callerRiskLevel,
            String assessmentResult,
            BigDecimal rulesScore,
            BigDecimal behavioralScore,
            BigDecimal customerScore,
            BigDecimal geographicScore,
            BigDecimal deviceScore) {

        RiskAssessmentRequest request =
                new RiskAssessmentRequest();

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setAssessmentType(
                assessmentType
        );

        request.setAssessmentStage(
                assessmentStage
        );

        request.setOverallRiskScore(
                callerOverallRiskScore
        );

        request.setRiskLevel(
                callerRiskLevel
        );

        request.setAssessmentResult(
                assessmentResult
        );

        request.setRulesScore(
                rulesScore
        );

        request.setBehavioralScore(
                behavioralScore
        );

        request.setCustomerScore(
                customerScore
        );

        request.setGeographicScore(
                geographicScore
        );

        request.setDeviceScore(
                deviceScore
        );

        request.setConfidenceScore(
                new BigDecimal("95.00")
        );

        request.setModelName(
                "CALLER-MODEL"
        );

        request.setModelVersion(
                "999"
        );

        request.setProcessingTimeMs(
                999999L
        );

        return request;
    }

    private record OutboxRow(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            UUID correlationId,
            String status,
            int attemptCount,
            String messageId,
            String envelopeEventType,
            String schemaVersion,
            String producer,
            String envelopeCorrelationId,
            String payloadRiskAssessmentId) {
    }
}