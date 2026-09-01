package com.efs.modules.alert.controller;

import com.efs.modules.alert.entity.Alert;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlertOptimisticLockingIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "61616161-6161-6161-6161-616161616161"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "62626262-6262-6262-6262-626262626262"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "63636363-6363-6363-6363-636363636363"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "64646464-6464-6464-6464-646464646464"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "65656565-6565-6565-6565-656565656565"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "67676767-6767-6767-6767-676767676767"
            );

    private static final UUID CHANGED_BY =
            UUID.fromString(
                    "68686868-6868-6868-6868-686868686868"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

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
                "EFS-ALERT-OPT-LOCK-ORG",
                "EFS Alert Optimistic Lock Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

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
                "EFS-ALERT-OPT-LOCK-CUSTOMER",
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
                "EFS-ALERT-OPT-LOCK-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("500.00"),
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
                new BigDecimal("91.00"),
                "ALTO",
                "REVIEW",
                new BigDecimal("93.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_decision (
                    decision_id,
                    transaction_id,
                    risk_assessment_id,
                    decision_type,
                    decision_source,
                    confidence_score,
                    decision_reason,
                    decision_timestamp,
                    is_final
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                DECISION_ID,
                TRANSACTION_ID,
                RISK_ASSESSMENT_ID,
                "ESCALATE",
                "DECISION_ENGINE",
                new BigDecimal("93.00"),
                "Escalar inmediatamente",
                LocalDateTime.now(),
                false
        );

        jdbcTemplate.update(
                """
                INSERT INTO alert.alert (
                    alert_id,
                    customer_id,
                    transaction_id,
                    decision_id,
                    risk_assessment_id,
                    alert_type,
                    category,
                    priority,
                    status,
                    risk_score,
                    generated_at,
                    created_at,
                    updated_at,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ALERT_ID,
                CUSTOMER_ID,
                TRANSACTION_ID,
                DECISION_ID,
                RISK_ASSESSMENT_ID,
                "FRAUD",
                "TRANSACTION",
                "HIGH",
                "NEW",
                new BigDecimal("91.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1
        );
    }

    @Test
    void shouldReturnConflictWhenAlertIsModifiedConcurrently()
            throws Exception {

        Alert staleAlert =
                entityManager.find(
                        Alert.class,
                        ALERT_ID
                );

        assertEquals(
                1,
                staleAlert.getRecordVersion()
        );

        int updatedRows =
                jdbcTemplate.update(
                        """
                        UPDATE alert.alert
                        SET assigned_team = ?,
                            updated_at = ?,
                            record_version =
                                record_version + 1
                        WHERE alert_id = ?
                        """,
                        "CONCURRENT_TEAM",
                        LocalDateTime.now(),
                        ALERT_ID
                );

        assertEquals(
                1,
                updatedRows
        );

        Integer databaseRecordVersion =
                jdbcTemplate.queryForObject(
                        """
                        SELECT record_version
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        ALERT_ID
                );

        assertEquals(
                2,
                databaseRecordVersion
        );

        assertEquals(
                1,
                staleAlert.getRecordVersion()
        );

        String requestBody =
                """
                {
                    "status": "IN_PROGRESS",
                    "changedBy": "%s",
                    "changeReason": "Concurrent status update"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/status",
                                ALERT_ID
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "ALERT_CONCURRENT_MODIFICATION"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Alert was modified by another transaction: "
                                                + ALERT_ID
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/alerts/"
                                                + ALERT_ID
                                                + "/status"
                                )
                );
    }
}