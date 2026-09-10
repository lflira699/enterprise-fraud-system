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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
class TransactionRiskAssessmentContractIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "83838383-8383-8383-8383-838383838383"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "84848484-8484-8484-8484-848484848484"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "85858585-8585-8585-8585-858585858585"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "86868686-8686-8686-8686-868686868686"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "87878787-8787-8787-8787-878787878787"
            );

    private static final UUID SECOND_CORRELATION_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
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
                "UC031-RISK-CUSTOMER",
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
                "UC031-RISK-TRANSACTION",
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
    void shouldPersistProcessCorrelationSnapshotAndSuccessAudit() {

        RiskAssessmentResponse response =
                service.createRiskAssessment(
                        request()
                );

        entityManager.flush();

        UUID persistedCorrelationId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT correlation_id
                        FROM transaction.risk_assessment
                        WHERE risk_assessment_id = ?
                        """,
                        UUID.class,
                        response.getRiskAssessmentId()
                );

        assertEquals(
                CORRELATION_ID,
                persistedCorrelationId
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'TRANSACTION_RISK_ASSESSED'
                          AND entity_type =
                              'RISK_ASSESSMENT'
                          AND entity_id = ?
                          AND action = 'CALCULATE'
                          AND source_component =
                              'RISK_ENGINE'
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                          AND event_details
                              ->> 'transactionId' = ?
                          AND event_details
                              ->> 'riskAssessmentId' = ?
                          AND event_details
                              ->> 'modelName' IS NOT NULL
                          AND event_details
                              ->> 'modelVersion' IS NOT NULL
                          AND (
                              event_details
                                  ->> 'overallRiskScore'
                          )::numeric = 50
                          AND event_details
                              ->> 'riskLevel'
                              = 'MEDIUM'
                          AND event_details
                              ->> 'reused'
                              = 'false'
                          AND jsonb_exists(
                                  event_details
                                      -> 'factorScores',
                                  'RULES'
                              )
                          AND jsonb_exists(
                                  event_details
                                      -> 'factorScores',
                                  'DEVICE'
                              )
                          AND jsonb_exists(
                                  event_details
                                      -> 'factorWeights',
                                  'RULES'
                              )
                          AND jsonb_exists(
                                  event_details
                                      -> 'factorWeights',
                                  'DEVICE'
                              )
                        """,
                        Integer.class,
                        response.getRiskAssessmentId(),
                        CORRELATION_ID,
                        TRANSACTION_ID.toString(),
                        response.getRiskAssessmentId().toString()
                );

        assertEquals(
                1,
                auditCount
        );
    }

    @Test
    void shouldReuseAssessmentOnlyWithinSameEvaluationProcess() {

        RiskAssessmentRequest request =
                request();

        RiskAssessmentResponse first =
                service.createRiskAssessment(
                        request
                );

        RiskAssessmentResponse reused =
                service.createRiskAssessment(
                        request
                );

        entityManager.flush();

        assertEquals(
                first.getRiskAssessmentId(),
                reused.getRiskAssessmentId()
        );

        Integer assessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE transaction_id = ?
                          AND correlation_id = ?
                          AND assessment_type = 'TRANSACTION'
                          AND assessment_stage = 'FINAL'
                        """,
                        Integer.class,
                        TRANSACTION_ID,
                        CORRELATION_ID
                );

        assertEquals(
                1,
                assessmentCount
        );

        Integer eventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'RiskCalculated'
                          AND aggregate_type = 'RiskAssessment'
                          AND aggregate_id = ?
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        first.getRiskAssessmentId(),
                        CORRELATION_ID
                );

        assertEquals(
                1,
                eventCount
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'TRANSACTION_RISK_ASSESSED'
                          AND entity_type =
                              'RISK_ASSESSMENT'
                          AND entity_id = ?
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                        """,
                        Integer.class,
                        first.getRiskAssessmentId(),
                        CORRELATION_ID
                );

        assertEquals(
                2,
                auditCount
        );

        Integer reusedAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'TRANSACTION_RISK_ASSESSED'
                          AND entity_id = ?
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                          AND event_details
                              ->> 'reused' = 'true'
                        """,
                        Integer.class,
                        first.getRiskAssessmentId(),
                        CORRELATION_ID
                );

        assertEquals(
                1,
                reusedAuditCount
        );
    }

    @Test
    void shouldCreateNewAssessmentWhenEvaluationProcessChanges() {

        RiskAssessmentRequest request =
                request();

        RiskAssessmentResponse first =
                service.createRiskAssessment(
                        request
                );

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE transaction.transaction
                SET correlation_id = ?
                WHERE transaction_id = ?
                """,
                SECOND_CORRELATION_ID,
                TRANSACTION_ID
        );

        entityManager.clear();

        RiskAssessmentResponse second =
                service.createRiskAssessment(
                        request
                );

        entityManager.flush();

        assertNotEquals(
                first.getRiskAssessmentId(),
                second.getRiskAssessmentId()
        );

        Integer assessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE transaction_id = ?
                          AND assessment_type = 'TRANSACTION'
                          AND assessment_stage = 'FINAL'
                          AND correlation_id IN (?, ?)
                        """,
                        Integer.class,
                        TRANSACTION_ID,
                        CORRELATION_ID,
                        SECOND_CORRELATION_ID
                );

        assertEquals(
                2,
                assessmentCount
        );

        Integer eventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'RiskCalculated'
                          AND aggregate_type = 'RiskAssessment'
                          AND aggregate_id IN (?, ?)
                          AND correlation_id IN (?, ?)
                        """,
                        Integer.class,
                        first.getRiskAssessmentId(),
                        second.getRiskAssessmentId(),
                        CORRELATION_ID,
                        SECOND_CORRELATION_ID
                );

        assertEquals(
                2,
                eventCount
        );

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'TRANSACTION_RISK_ASSESSED'
                          AND entity_type =
                              'RISK_ASSESSMENT'
                          AND entity_id IN (?, ?)
                          AND correlation_id IN (?, ?)
                          AND event_result = 'SUCCESS'
                          AND event_details
                              ->> 'reused' = 'false'
                        """,
                        Integer.class,
                        first.getRiskAssessmentId(),
                        second.getRiskAssessmentId(),
                        CORRELATION_ID,
                        SECOND_CORRELATION_ID
                );

        assertEquals(
                2,
                successAuditCount
        );
    }

    private RiskAssessmentRequest request() {

        RiskAssessmentRequest request =
                new RiskAssessmentRequest();

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setAssessmentType(
                "TRANSACTION"
        );

        request.setAssessmentStage(
                "FINAL"
        );

        request.setAssessmentResult(
                "REVIEW"
        );

        BigDecimal score =
                new BigDecimal("50.00");

        request.setRulesScore(score);
        request.setBehavioralScore(score);
        request.setCustomerScore(score);
        request.setGeographicScore(score);
        request.setDeviceScore(score);

        request.setConfidenceScore(
                new BigDecimal("95.00")
        );

        request.setCreatedBy(
                CREATED_BY
        );

        request.setUpdatedBy(
                CREATED_BY
        );

        return request;
    }
}