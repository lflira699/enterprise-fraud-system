package com.efs.modules.alert.repository;

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
class AlertRepositoryIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "11111111-aaaa-bbbb-cccc-111111111111"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "22222222-aaaa-bbbb-cccc-222222222222"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-aaaa-bbbb-cccc-333333333333"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "44444444-aaaa-bbbb-cccc-444444444444"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "55555555-aaaa-bbbb-cccc-555555555555"
            );

    private static final UUID NON_EXISTENT_TRANSACTION_ID =
            UUID.fromString(
                    "66666666-aaaa-bbbb-cccc-666666666666"
            );

    private static final UUID NON_EXISTENT_DECISION_ID =
            UUID.fromString(
                    "77777777-aaaa-bbbb-cccc-777777777777"
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
                "EFS-V120-CUSTOMER",
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
                "EFS-V120-TRANSACTION",
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
                INSERT INTO transaction.transaction_decision (
                    decision_id,
                    transaction_id,
                    decision_type,
                    decision_source,
                    confidence_score,
                    decision_reason,
                    decision_timestamp,
                    is_final
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                DECISION_ID,
                TRANSACTION_ID,
                "REVIEW",
                "RISK_ENGINE",
                new BigDecimal("90.00"),
                "V120 foreign key validation fixture",
                LocalDateTime.now(),
                false
        );
    }

    @Test
    void shouldRejectAlertWithNonExistentTransaction() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO alert.alert (
                            transaction_id,
                            decision_id,
                            alert_type,
                            priority,
                            status,
                            risk_score,
                            generated_at,
                            created_at,
                            updated_at
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        NON_EXISTENT_TRANSACTION_ID,
                        DECISION_ID,
                        "FRAUD",
                        "HIGH",
                        "NEW",
                        new BigDecimal("90.00"),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldRejectAlertWithNonExistentDecision() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO alert.alert (
                            transaction_id,
                            decision_id,
                            alert_type,
                            priority,
                            status,
                            risk_score,
                            generated_at,
                            created_at,
                            updated_at
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        TRANSACTION_ID,
                        NON_EXISTENT_DECISION_ID,
                        "FRAUD",
                        "HIGH",
                        "NEW",
                        new BigDecimal("90.00"),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }
}