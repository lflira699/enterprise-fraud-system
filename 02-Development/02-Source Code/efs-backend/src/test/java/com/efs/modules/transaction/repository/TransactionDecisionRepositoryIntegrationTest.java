package com.efs.modules.transaction.repository;

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
class TransactionDecisionRepositoryIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID NON_EXISTENT_RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "99999999-9999-9999-9999-999999999999"
            );

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
                "EFS-V118-CUSTOMER",
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
                "EFS-V118-TRANSACTION",
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
    void shouldRejectDecisionWithNonExistentRiskAssessment() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO transaction.transaction_decision (
                            transaction_id,
                            risk_assessment_id,
                            decision_type,
                            decision_source,
                            confidence_score,
                            decision_reason,
                            decision_timestamp,
                            is_final
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        TRANSACTION_ID,
                        NON_EXISTENT_RISK_ASSESSMENT_ID,
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("90.00"),
                        "V118 foreign key validation",
                        LocalDateTime.now(),
                        false
                )
        );
    }
}