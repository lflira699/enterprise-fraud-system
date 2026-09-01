package com.efs.modules.risk.controller;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RiskAssessmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID customerId;
    private UUID transactionId;
    private UUID organizationId;
    private UUID createdBy;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        createdBy = UUID.randomUUID();

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
                "RISK-CTRL-" + customerId.toString().substring(0, 8),
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
                "RISK-CTRL-" + transactionId,
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
    void shouldCreateRiskAssessment()
            throws Exception {

        RiskAssessmentRequest request =
                buildRequest(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("25.00"),
                        "LOW",
                        "PASS"
                );

        mockMvc.perform(
                        post("/api/v1/risk-assessments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskAssessmentId").exists())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.assessmentType")
                                .value("TRANSACTION")
                )
                .andExpect(
                        jsonPath("$.assessmentStage")
                                .value("INITIAL")
                )
                .andExpect(
                        jsonPath("$.overallRiskScore")
                                .value(25.0)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$.assessmentResult")
                                .value("PASS")
                )
                .andExpect(
                        jsonPath("$.confidenceScore")
                                .value(95.0)
                )
                .andExpect(
                        jsonPath("$.assessmentTimestamp")
                                .exists()
                )
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldRejectInvalidRiskAssessmentRequest()
            throws Exception {

        RiskAssessmentRequest request =
                buildRequest(
                        null,
                        "INITIAL",
                        new BigDecimal("25.00"),
                        "LOW",
                        "PASS"
                );

        mockMvc.perform(
                        post("/api/v1/risk-assessments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetRiskAssessmentById()
            throws Exception {

        JsonNode created =
                createAssessment(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("30.00"),
                        "LOW",
                        "PASS"
                );

        UUID riskAssessmentId =
                UUID.fromString(
                        created.get("riskAssessmentId").asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-assessments/{riskAssessmentId}",
                                riskAssessmentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(riskAssessmentId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.overallRiskScore")
                                .value(30.0)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownRiskAssessment()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/risk-assessments/{riskAssessmentId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAssessmentsByTransaction()
            throws Exception {

        JsonNode first =
                createAssessment(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("20.00"),
                        "LOW",
                        "PASS"
                );

        JsonNode second =
                createAssessment(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("80.00"),
                        "HIGH",
                        "REVIEW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-assessments/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(
                        jsonPath("$[*].riskAssessmentId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "riskAssessmentId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].riskAssessmentId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "riskAssessmentId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetLatestAssessmentByTransaction()
            throws Exception {

        createAssessment(
                "TRANSACTION",
                "INITIAL",
                new BigDecimal("20.00"),
                "LOW",
                "PASS"
        );

        JsonNode latest =
                createAssessment(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("80.00"),
                        "HIGH",
                        "REVIEW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-assessments/transaction/{transactionId}/latest",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(
                                        latest.get(
                                                "riskAssessmentId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.assessmentStage")
                                .value("FINAL")
                )
                .andExpect(
                        jsonPath("$.overallRiskScore")
                                .value(80.0)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("HIGH")
                );
    }

    @Test
    void shouldGetAssessmentsByTransactionAndType()
            throws Exception {

        JsonNode expected =
                createAssessment(
                        "TRANSACTION",
                        "INITIAL",
                        new BigDecimal("35.00"),
                        "MEDIUM",
                        "REVIEW"
                );

        createAssessment(
                "CUSTOMER",
                "FINAL",
                new BigDecimal("10.00"),
                "LOW",
                "PASS"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-assessments/transaction/{transactionId}/type/{assessmentType}",
                                transactionId,
                                "TRANSACTION"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(
                        jsonPath("$[*].riskAssessmentId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "riskAssessmentId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].assessmentType")
                                .value(hasItem("TRANSACTION"))
                );
    }

    @Test
    void shouldSearchAssessmentsByRiskLevel()
            throws Exception {

        JsonNode expected =
                createAssessment(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("85.00"),
                        "HIGH",
                        "REVIEW"
                );

        createAssessment(
                "TRANSACTION",
                "INITIAL",
                new BigDecimal("15.00"),
                "LOW",
                "PASS"
        );

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param("riskLevel", "HIGH")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(
                        jsonPath("$.content[*].riskAssessmentId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "riskAssessmentId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$.content[*].riskLevel")
                                .value(hasItem("HIGH"))
                )
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void shouldSearchAssessmentsByResult()
            throws Exception {

        JsonNode expected =
                createAssessment(
                        "TRANSACTION",
                        "FINAL",
                        new BigDecimal("70.00"),
                        "HIGH",
                        "REVIEW"
                );

        createAssessment(
                "TRANSACTION",
                "INITIAL",
                new BigDecimal("15.00"),
                "LOW",
                "PASS"
        );

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param("assessmentResult", "REVIEW")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(
                        jsonPath("$.content[*].riskAssessmentId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "riskAssessmentId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$.content[*].assessmentResult")
                                .value(hasItem("REVIEW"))
                )
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void shouldReturnEmptyPageWhenNoAssessmentsExist()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    private JsonNode createAssessment(
            String assessmentType,
            String assessmentStage,
            BigDecimal overallRiskScore,
            String riskLevel,
            String assessmentResult)
            throws Exception {

        RiskAssessmentRequest request =
                buildRequest(
                        assessmentType,
                        assessmentStage,
                        overallRiskScore,
                        riskLevel,
                        assessmentResult
                );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/risk-assessments")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                request
                                                        )
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse().getContentAsString()
        );
    }

    private RiskAssessmentRequest buildRequest(
            String assessmentType,
            String assessmentStage,
            BigDecimal overallRiskScore,
            String riskLevel,
            String assessmentResult) {

        RiskAssessmentRequest request =
                new RiskAssessmentRequest();

        request.setTransactionId(transactionId);
        request.setAssessmentType(assessmentType);
        request.setAssessmentStage(assessmentStage);
        request.setOverallRiskScore(overallRiskScore);
        request.setRiskLevel(riskLevel);
        request.setAssessmentResult(assessmentResult);

        request.setRulesScore(
                new BigDecimal("20.00")
        );

        request.setMachineLearningScore(
                new BigDecimal("18.00")
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

        request.setModelName("EFS-RISK");
        request.setModelVersion("1.0");
        request.setProcessingTimeMs(12L);

        return request;
    }
}