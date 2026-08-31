package com.efs.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RiskDecisionAlertCaseEndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID organizationId;
    private UUID customerId;
    private UUID createdBy;

    @BeforeEach
    void setUp() {

        organizationId =
                UUID.randomUUID();

        customerId =
                UUID.randomUUID();

        createdBy =
                UUID.randomUUID();

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
                organizationId,
                "E2E-ORG-"
                        + organizationId
                                .toString()
                                .substring(0, 8),
                "EFS End To End Organization",
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
                customerId,
                "E2E-CUSTOMER-"
                        + customerId
                                .toString()
                                .substring(0, 8),
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );
    }

    @Test
    void shouldExecuteRiskDecisionAlertCaseFlowEndToEnd()
            throws Exception {

        /*
         * ---------------------------------------------------------
         * 1. TRANSACTION
         * ---------------------------------------------------------
         */

        String transactionReference =
                "E2E-TXN-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        String transactionRequest =
                """
                {
                    "transactionReference": "%s",
                    "customerId": "%s",
                    "organizationId": "%s",
                    "transactionType": "PAYMENT",
                    "amount": 1250.00,
                    "currencyCode": "GTQ",
                    "createdBy": "%s"
                }
                """.formatted(
                        transactionReference,
                        customerId,
                        organizationId,
                        createdBy
                );

        String transactionResponse =
                mockMvc.perform(
                                post("/api/v1/transactions")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                transactionRequest
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionId"
                                ).exists()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionReference"
                                ).value(
                                        transactionReference
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.customerId"
                                ).value(
                                        customerId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.organizationId"
                                ).value(
                                        organizationId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionStatus"
                                ).value(
                                        "RECEIVED"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.finalDecision"
                                ).value(
                                        "PENDING"
                                )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode transactionJson =
                objectMapper.readTree(
                        transactionResponse
                );

        UUID transactionId =
                UUID.fromString(
                        transactionJson
                                .get(
                                        "transactionId"
                                )
                                .asText()
                );

        /*
         * ---------------------------------------------------------
         * 2. RISK ASSESSMENT
         * ---------------------------------------------------------
         */

        String riskAssessmentRequest =
                """
                {
                    "transactionId": "%s",
                    "assessmentType": "TRANSACTION",
                    "assessmentStage": "DECISION",
                    "overallRiskScore": 90.00,
                    "riskLevel": "ALTO",
                    "assessmentResult": "REVIEW",
                    "rulesScore": 35.00,
                    "behavioralScore": 20.00,
                    "customerScore": 15.00,
                    "geographicScore": 10.00,
                    "deviceScore": 10.00,
                    "confidenceScore": 92.00,
                    "modelName": "EFS-RISK",
                    "modelVersion": "1.0",
                    "processingTimeMs": 12
                }
                """.formatted(
                        transactionId
                );

        String riskAssessmentResponse =
                mockMvc.perform(
                                post(
                                        "/api/v1/risk-assessments"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                riskAssessmentRequest
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.riskAssessmentId"
                                ).exists()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionId"
                                ).value(
                                        transactionId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.assessmentType"
                                ).value(
                                        "TRANSACTION"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.assessmentStage"
                                ).value(
                                        "DECISION"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.riskLevel"
                                ).value(
                                        "ALTO"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.assessmentResult"
                                ).value(
                                        "REVIEW"
                                )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode riskAssessmentJson =
                objectMapper.readTree(
                        riskAssessmentResponse
                );

        UUID riskAssessmentId =
                UUID.fromString(
                        riskAssessmentJson
                                .get(
                                        "riskAssessmentId"
                                )
                                .asText()
                );

        /*
         * ---------------------------------------------------------
         * 3. DECISION ENGINE
         * ---------------------------------------------------------
         */

        String decisionRequest =
                """
                {
                    "riskAssessmentId": "%s",
                    "confidenceLevel": "ALTA"
                }
                """.formatted(
                        riskAssessmentId
                );

        String decisionResponse =
                mockMvc.perform(
                                post(
                                        "/api/v1/decisions/execute"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                decisionRequest
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.decisionId"
                                ).exists()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionId"
                                ).value(
                                        transactionId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.riskAssessmentId"
                                ).value(
                                        riskAssessmentId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.decisionType"
                                ).value(
                                        "ESCALATE"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.decisionSource"
                                ).value(
                                        "DECISION_ENGINE"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.decisionReason"
                                ).value(
                                        "Escalar inmediatamente"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.finalDecision"
                                ).value(
                                        false
                                )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode decisionJson =
                objectMapper.readTree(
                        decisionResponse
                );

        UUID decisionId =
                UUID.fromString(
                        decisionJson
                                .get(
                                        "decisionId"
                                )
                                .asText()
                );

        /*
         * ---------------------------------------------------------
         * 4. ALERT
         * ---------------------------------------------------------
         */

        String alertRequest =
                """
                {
                    "decisionId": "%s",
                    "alertType": "FRAUD",
                    "category": "TRANSACTION",
                    "severity": "HIGH",
                    "priority": "HIGH",
                    "riskScore": 90.00
                }
                """.formatted(
                        decisionId
                );

        String alertResponse =
                mockMvc.perform(
                                post(
                                        "/api/v1/alerts"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                alertRequest
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.alertId"
                                ).exists()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.customerId"
                                ).value(
                                        customerId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionId"
                                ).value(
                                        transactionId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.riskAssessmentId"
                                ).value(
                                        riskAssessmentId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.decisionId"
                                ).value(
                                        decisionId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.status"
                                ).value(
                                        "NEW"
                                )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode alertJson =
                objectMapper.readTree(
                        alertResponse
                );

        UUID alertId =
                UUID.fromString(
                        alertJson
                                .get(
                                        "alertId"
                                )
                                .asText()
                );

        /*
         * ---------------------------------------------------------
         * 5. INVESTIGATION CASE FROM ALERT
         * ---------------------------------------------------------
         */

        String caseNumber =
                "E2E-CASE-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        String caseRequest =
                """
                {
                    "alertId": "%s",
                    "caseNumber": "%s",
                    "organizationId": "%s",
                    "caseType": "FRAUD_INVESTIGATION"
                }
                """.formatted(
                        alertId,
                        caseNumber,
                        organizationId
                );

        String caseResponse =
                mockMvc.perform(
                                post(
                                        "/api/v1/cases/from-alert"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                caseRequest
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.caseId"
                                ).exists()
                        )
                        .andExpect(
                                jsonPath(
                                        "$.caseNumber"
                                ).value(
                                        caseNumber
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.organizationId"
                                ).value(
                                        organizationId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.customerId"
                                ).value(
                                        customerId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.transactionId"
                                ).value(
                                        transactionId.toString()
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.category"
                                ).value(
                                        "TRANSACTION"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.severity"
                                ).value(
                                        "HIGH"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.priority"
                                ).value(
                                        "NORMAL"
                                )
                        )
                        .andExpect(
                                jsonPath(
                                        "$.currentStatus"
                                ).value(
                                        "OPEN"
                                )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode caseJson =
                objectMapper.readTree(
                        caseResponse
                );

        UUID caseId =
                UUID.fromString(
                        caseJson
                                .get(
                                        "caseId"
                                )
                                .asText()
                );

        /*
         * ---------------------------------------------------------
         * 6. API RETRIEVAL VALIDATION
         * ---------------------------------------------------------
         */

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/{alertId}",
                                alertId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.decisionId"
                        ).value(
                                decisionId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.transactionId"
                        ).value(
                                transactionId.toString()
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.caseId"
                        ).value(
                                caseId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.customerId"
                        ).value(
                                customerId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.transactionId"
                        ).value(
                                transactionId.toString()
                        )
                );

        /*
         * ---------------------------------------------------------
         * 7. CROSS-MODULE PERSISTENCE AND TRACEABILITY
         * ---------------------------------------------------------
         */

        Integer transactionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.transaction
                        WHERE transaction_id = ?
                          AND customer_id = ?
                          AND organization_id = ?
                        """,
                        Integer.class,
                        transactionId,
                        customerId,
                        organizationId
                );

        assertEquals(
                1,
                transactionCount
        );

        Integer riskAssessmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.risk_assessment
                        WHERE risk_assessment_id = ?
                          AND transaction_id = ?
                          AND risk_level = ?
                        """,
                        Integer.class,
                        riskAssessmentId,
                        transactionId,
                        "ALTO"
                );

        assertEquals(
                1,
                riskAssessmentCount
        );

        Integer decisionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.transaction_decision
                        WHERE decision_id = ?
                          AND transaction_id = ?
                          AND risk_assessment_id = ?
                          AND decision_type = ?
                          AND decision_source = ?
                        """,
                        Integer.class,
                        decisionId,
                        transactionId,
                        riskAssessmentId,
                        "ESCALATE",
                        "DECISION_ENGINE"
                );

        assertEquals(
                1,
                decisionCount
        );

        Integer alertCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert
                        WHERE alert_id = ?
                          AND customer_id = ?
                          AND transaction_id = ?
                          AND risk_assessment_id = ?
                          AND decision_id = ?
                        """,
                        Integer.class,
                        alertId,
                        customerId,
                        transactionId,
                        riskAssessmentId,
                        decisionId
                );

        assertEquals(
                1,
                alertCount
        );

        Integer caseCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case
                        WHERE case_id = ?
                          AND customer_id = ?
                          AND transaction_id = ?
                        """,
                        Integer.class,
                        caseId,
                        customerId,
                        transactionId
                );

        assertEquals(
                1,
                caseCount
        );

        Integer caseAlertLinkCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_alert
                        WHERE case_id = ?
                          AND source_alert_id = ?
                          AND transaction_id = ?
                        """,
                        Integer.class,
                        caseId,
                        alertId,
                        transactionId
                );

        assertEquals(
                1,
                caseAlertLinkCount
        );
    }
}