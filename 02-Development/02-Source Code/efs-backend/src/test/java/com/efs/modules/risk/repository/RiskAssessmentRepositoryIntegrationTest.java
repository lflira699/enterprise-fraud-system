package com.efs.modules.risk.repository;

import com.efs.modules.risk.entity.RiskAssessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RiskAssessmentRepositoryIntegrationTest {

    private UUID transactionId;

    @Autowired
    private RiskAssessmentRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        UUID customerId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        transactionId = UUID.randomUUID();

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
                customerId,
                "V115-" + customerId.toString().substring(0, 8),
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
                transactionId,
                "V115-RISK-" + transactionId,
                customerId,
                organizationId,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                createdBy,
                1
        );
    }

    @Test
    void shouldRejectNegativeOverallRiskScore() {

        RiskAssessment assessment =
                buildAssessment(
                        new BigDecimal("-0.01"),
                        new BigDecimal("50.00")
                );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(assessment)
        );
    }

    @Test
    void shouldRejectConfidenceScoreGreaterThanOneHundred() {

        RiskAssessment assessment =
                buildAssessment(
                        new BigDecimal("25.00"),
                        new BigDecimal("100.01")
                );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(assessment)
        );
    }

    private RiskAssessment buildAssessment(
            BigDecimal overallRiskScore,
            BigDecimal confidenceScore) {

        LocalDateTime now = LocalDateTime.now();

        RiskAssessment assessment =
                new RiskAssessment();

        assessment.setTransactionId(transactionId);
        assessment.setAssessmentType("TRANSACTION");
        assessment.setAssessmentStage("INITIAL");
        assessment.setOverallRiskScore(overallRiskScore);
        assessment.setRiskLevel("LOW");
        assessment.setAssessmentResult("PASS");
        assessment.setConfidenceScore(confidenceScore);
        assessment.setAssessmentTimestamp(now);
        assessment.setCreatedAt(now);
        assessment.setUpdatedAt(now);
        assessment.setRecordVersion(0);

        return assessment;
    }
}