package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Autowired
    private RiskAssessmentServiceInterface service;

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
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                CREATED_BY,
                1
        );
    }

    @Test
    void shouldCreateAndRetrieveRiskAssessment() {

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("25.00"),
                        "LOW",
                        "PASS"
                );

        RiskAssessmentResponse created =
                service.createRiskAssessment(request);

        assertNotNull(created.getRiskAssessmentId());
        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );
        assertEquals(
                new BigDecimal("25.00"),
                created.getOverallRiskScore()
        );
        assertEquals(
                "LOW",
                created.getRiskLevel()
        );
        assertEquals(
                "PASS",
                created.getAssessmentResult()
        );

        RiskAssessmentResponse retrieved =
                service.getRiskAssessmentById(
                        created.getRiskAssessmentId()
                );

        assertEquals(
                created.getRiskAssessmentId(),
                retrieved.getRiskAssessmentId()
        );
    }

    @Test
    void shouldReturnAssessmentsByTransactionAndLatest() {

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("20.00"),
                        "LOW",
                        "PASS"
                )
        );

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("80.00"),
                        "HIGH",
                        "REVIEW"
                )
        );

        List<RiskAssessmentResponse> assessments =
                service.getAssessmentsByTransaction(
                        TRANSACTION_ID
                );

        assertEquals(2, assessments.size());

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
    }

    @Test
    void shouldFilterRiskAssessments() {

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("30.00"),
                        "LOW",
                        "PASS"
                )
        );

        service.createRiskAssessment(
                buildRequest(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("75.00"),
                        "HIGH",
                        "REVIEW"
                )
        );

        List<RiskAssessmentResponse> byType =
                service.getAssessmentsByTransactionAndType(
                        TRANSACTION_ID,
                        "TRANSACTION"
                );

        assertEquals(2, byType.size());

        List<RiskAssessmentResponse> byRiskLevel =
                service.getAssessmentsByRiskLevel(
                        "HIGH"
                );

        assertEquals(1, byRiskLevel.size());

        List<RiskAssessmentResponse> byResult =
                service.getAssessmentsByResult(
                        "REVIEW"
                );

        assertEquals(1, byResult.size());
    }

    private RiskAssessmentRequest buildRequest(
            String assessmentType,
            String assessmentStage,
            BigDecimal overallRiskScore,
            String riskLevel,
            String assessmentResult
    ) {
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
                overallRiskScore
        );

        request.setRiskLevel(
                riskLevel
        );

        request.setAssessmentResult(
                assessmentResult
        );

        request.setRulesScore(
                new BigDecimal("20.00")
        );

        request.setBehavioralScore(
                new BigDecimal("15.00")
        );

        request.setCustomerScore(
                new BigDecimal("10.00")
        );

        request.setGeographicScore(
                new BigDecimal("5.00")
        );

        request.setDeviceScore(
                new BigDecimal("8.00")
        );

        request.setConfidenceScore(
                new BigDecimal("95.00")
        );

        request.setModelName(
                "EFS-RISK"
        );

        request.setModelVersion(
                "1.0"
        );

        request.setProcessingTimeMs(
                12L
        );

        return request;
    }
}