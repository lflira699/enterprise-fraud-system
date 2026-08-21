package com.efs.modules.decision.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DecisionControllerIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "12121212-1212-1212-1212-121212121212"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "23232323-2323-2323-2323-232323232323"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "34343434-3434-3434-3434-343434343434"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "45454545-4545-4545-4545-454545454545"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "56565656-5656-5656-5656-565656565656"
            );

    @Autowired
    private MockMvc mockMvc;

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
                "EFS-DECISION-CONTROLLER-TEST-CUSTOMER",
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
                "EFS-DECISION-CONTROLLER-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("250.00"),
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
    void shouldEvaluateDecisionThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "riskAssessmentId": "%s",
                    "confidenceLevel": "ALTA"
                }
                """.formatted(
                        RISK_ASSESSMENT_ID
                );

        mockMvc.perform(
                        post("/api/v1/decisions/evaluate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(RISK_ASSESSMENT_ID.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.decisionType")
                                .value("ESCALATE")
                )
                .andExpect(
                        jsonPath("$.decisionReason")
                                .value("Escalar inmediatamente")
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value(false)
                );
    }

    @Test
    void shouldEvaluateAndPersistDecisionThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "riskAssessmentId": "%s",
                    "confidenceLevel": "ALTA"
                }
                """.formatted(
                        RISK_ASSESSMENT_ID
                );

        mockMvc.perform(
                        post("/api/v1/decisions/execute")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(RISK_ASSESSMENT_ID.toString())
                )
                .andExpect(
                        jsonPath("$.decisionType")
                                .value("ESCALATE")
                )
                .andExpect(
                        jsonPath("$.decisionSource")
                                .value("DECISION_ENGINE")
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value(false)
                );
    }
}