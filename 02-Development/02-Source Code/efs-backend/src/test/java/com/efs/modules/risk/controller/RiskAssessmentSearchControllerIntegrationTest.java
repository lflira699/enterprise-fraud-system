package com.efs.modules.risk.controller;

import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RiskAssessmentSearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecurityContextProvider
            securityContextProvider;

    @MockitoBean
    private AuditEventServiceInterface
            auditEventService;

    private UUID customerId;
    private UUID transactionId;
    private UUID organizationId;
    private UUID createdBy;
    private UUID correlationId;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        createdBy = UUID.randomUUID();
        correlationId = UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        createdBy,
                        null,
                        null,
                        Set.of(),
                        Set.of(
                                "risk.assessment.view"
                        ),
                        Set.of()
                );

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
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
                "RISK-SEARCH-"
                        + customerId.toString().substring(0, 8),
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
                transactionId,
                "RISK-SEARCH-" + transactionId,
                customerId,
                organizationId,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                correlationId,
                createdBy,
                1
        );
    }

    @Test
    void shouldUseDefaultPaginationAndSort()
            throws Exception {

        UUID olderAssessmentId =
                createAssessment(
                        "LOW",
                        "PASS",
                        new BigDecimal("10.00")
                );

        UUID newerAssessmentId =
                createAssessment(
                        "CRITICAL",
                        "REVIEW",
                        new BigDecimal("80.00")
                );

        updateAssessmentTimestamp(
                olderAssessmentId,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        8,
                        0
                )
        );

        updateAssessmentTimestamp(
                newerAssessmentId,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.content[0].riskAssessmentId"
                        ).value(
                                newerAssessmentId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[1].riskAssessmentId"
                        ).value(
                                olderAssessmentId.toString()
                        )
                )
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void shouldPaginateAndSortByAssessmentTimestamp()
            throws Exception {

        UUID firstAssessmentId =
                createAssessment(
                        "LOW",
                        "PASS",
                        new BigDecimal("10.00")
                );

        UUID secondAssessmentId =
                createAssessment("MEDIUM",
                        "REVIEW",
                        new BigDecimal("50.00")
                );

        UUID thirdAssessmentId =
                createAssessment(
                        "CRITICAL",
                        "REVIEW",
                        new BigDecimal("90.00")
                );

        updateAssessmentTimestamp(
                firstAssessmentId,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        8,
                        0
                )
        );

        updateAssessmentTimestamp(
                secondAssessmentId,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        9,
                        0
                )
        );

        updateAssessmentTimestamp(
                thirdAssessmentId,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param("page", "1")
                                .param("size", "1")
                                .param(
                                        "sort",
                                        "assessmentTimestamp"
                                )
                                .param(
                                        "direction",
                                        "ASC"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.content[0].riskAssessmentId"
                        ).value(
                                secondAssessmentId.toString()
                        )
                )
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(true));
    }

    @Test
    void shouldCombineCanonicalRiskAssessmentFilters()
            throws Exception {

        UUID expectedAssessmentId =
                createAssessment("MEDIUM",
                        "REVIEW",
                        new BigDecimal("50.00")
                );

        createAssessment("MEDIUM",
                "PASS",
                new BigDecimal("55.00")
        );

        createAssessment(
                "LOW",
                "REVIEW",
                new BigDecimal("10.00")
        );

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param(
                                        "riskLevel",
                                        "MEDIUM"
                                )
                                .param(
                                        "assessmentResult",
                                        "REVIEW"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.content.length()"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].riskAssessmentId"
                        ).value(
                                expectedAssessmentId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].riskLevel"
                        ).value("MEDIUM")
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].assessmentResult"
                        ).value("REVIEW")
                )
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldRejectNegativePage()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param("page", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param("size", "101")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                );
    }

    @Test
    void shouldRejectUnsupportedSortField()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param(
                                        "sort",
                                        "overallRiskScore"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                );
    }

    @Test
    void shouldRejectUnsupportedSortDirection()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/risk-assessments")
                                .param(
                                        "direction",
                                        "SIDEWAYS"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                );
    }

    private UUID createAssessment(
            String riskLevel,
            String assessmentResult,
            BigDecimal factorScore)
            throws Exception {

        RiskAssessmentRequest request =
                new RiskAssessmentRequest();

        request.setTransactionId(
                transactionId
        );

        request.setAssessmentType(
                "TRANSACTION"
        );

        request.setAssessmentStage(
                "FINAL"
        );

        request.setOverallRiskScore(
                factorScore
        );

        request.setRiskLevel(
                riskLevel
        );

        request.setAssessmentResult(
                assessmentResult
        );

        request.setRulesScore(
                factorScore
        );

        request.setMachineLearningScore(
                new BigDecimal("18.00")
        );

        request.setBehavioralScore(
                factorScore
        );

        request.setCustomerScore(
                factorScore
        );

        request.setGeographicScore(
                factorScore
        );

        request.setDeviceScore(
                factorScore
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

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/risk-assessments"
                                )
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
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result
                                .getResponse()
                                .getContentAsString()
                );

        return UUID.fromString(
                response
                        .get(
                                "riskAssessmentId"
                        )
                        .asText()
        );
    }

    private void updateAssessmentTimestamp(
            UUID riskAssessmentId,
            LocalDateTime assessmentTimestamp) {

        jdbcTemplate.update(
                """
                UPDATE transaction.risk_assessment
                SET assessment_timestamp = ?
                WHERE risk_assessment_id = ?
                """,
                assessmentTimestamp,
                riskAssessmentId
        );
    }
}