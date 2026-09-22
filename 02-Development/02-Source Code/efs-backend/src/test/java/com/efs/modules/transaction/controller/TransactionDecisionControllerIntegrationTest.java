package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionDecisionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private final UUID actorId =
            UUID.randomUUID();

    private UUID customerId;
    private UUID transactionId;
    private UUID organizationId;
    private UUID createdBy;
    private UUID riskAssessmentId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        customerId =
                UUID.randomUUID();

        transactionId =
                UUID.randomUUID();

        organizationId =
                UUID.randomUUID();

        createdBy =
                UUID.randomUUID();

        riskAssessmentId =
                UUID.randomUUID();

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
                "TD-CTRL-" + customerId.toString().substring(0, 8),
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );

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
                "EFS-TD-CTRL-" + organizationId,
                "EFS Transaction Decision Controller " + organizationId,
                "GT",
                "America/Guatemala",
                "ACTIVE"
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
                "TD-CTRL-" + transactionId,
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

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    organization_id,
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                riskAssessmentId,
                transactionId,
                organizationId,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("85.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                now,
                now,
                now,
                0
        );

        authorize(
                allPermissions(),
                organizationId,
                null
        );
    }

    @Test
    void shouldCreateTransactionDecision()
            throws Exception {

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("90.00"),
                        "Controller integration decision",
                        false
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.decisionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(
                                        riskAssessmentId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.decisionType")
                                .value("REVIEW")
                )
                .andExpect(
                        jsonPath("$.decisionSource")
                                .value("RISK_ENGINE")
                )
                .andExpect(
                        jsonPath("$.confidenceScore")
                                .value(90.0)
                )
                .andExpect(
                        jsonPath("$.decisionReason")
                                .value(
                                        "Controller integration decision"
                                )
                )
                .andExpect(
                        jsonPath("$.decisionTimestamp")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value(false)
                );
    }

    @Test
    void shouldRejectInvalidTransactionDecisionRequest()
            throws Exception {

        TransactionDecisionRequest request =
                buildRequest(
                        " ",
                        "RISK_ENGINE",
                        new BigDecimal("90.00"),
                        "Invalid decision",
                        false
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingDecisionForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "REVIEW",
                                                        "RISK_ENGINE",
                                                        new BigDecimal("90.00"),
                                                        "Unknown transaction",
                                                        false
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionDecisionById()
            throws Exception {

        JsonNode created =
                createDecision(
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        UUID decisionId =
                UUID.fromString(
                        created.get(
                                "decisionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/{decisionId}",
                                decisionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.decisionId")
                                .value(
                                        decisionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(
                                        riskAssessmentId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.decisionType")
                                .value("REVIEW")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownDecision()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/{decisionId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetDecisionsByTransactionId()
            throws Exception {

        JsonNode first =
                createDecision(
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        JsonNode second =
                createDecision(
                        "APPROVE",
                        "ANALYST",
                        true
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].decisionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "decisionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].decisionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "decisionId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetDecisionsByType()
            throws Exception {

        JsonNode expected =
                createDecision(
                        "APPROVE",
                        "ANALYST",
                        true
                );

        createDecision(
                "REVIEW",
                "RISK_ENGINE",
                false
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/type/{decisionType}",
                                "APPROVE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].decisionId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "decisionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].decisionType")
                                .value(
                                        hasItem("APPROVE")
                                )
                );
    }

    @Test
    void shouldGetDecisionsBySource()
            throws Exception {

        JsonNode expected =
                createDecision(
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        createDecision(
                "APPROVE",
                "ANALYST",
                true
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/source/{decisionSource}",
                                "RISK_ENGINE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].decisionId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "decisionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].decisionSource")
                                .value(
                                        hasItem("RISK_ENGINE")
                                )
                );
    }

    @Test
    void shouldGetDecisionsByFinalStatus()
            throws Exception {

        JsonNode expected =
                createDecision(
                        "APPROVE",
                        "ANALYST",
                        true
                );

        createDecision(
                "REVIEW",
                "RISK_ENGINE",
                false
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/final/{finalDecision}",
                                true
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].decisionId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "decisionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].finalDecision")
                                .value(
                                        hasItem(true)
                                )
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingForSoftDeletedTransaction()
            throws Exception {

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "REVIEW",
                                                        "RISK_ENGINE",
                                                        new BigDecimal("80.00"),
                                                        "Deleted transaction",
                                                        false
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideDecisionByIdForSoftDeletedTransaction()
            throws Exception {

        JsonNode created =
                createDecision(
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        UUID decisionId =
                UUID.fromString(
                        created.get("decisionId").asText()
                );

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/{decisionId}",
                                decisionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectTransactionListForSoftDeletedTransaction()
            throws Exception {

        createDecision("REVIEW", "RISK_ENGINE", false);

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromTypeFilter()
            throws Exception {

        createDecision("SOFT_DELETE_TYPE", "RISK_ENGINE", false);
        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/type/{decisionType}",
                                "SOFT_DELETE_TYPE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromSourceFilter()
            throws Exception {

        createDecision("REVIEW", "SOFT_DELETE_SOURCE", false);
        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/source/{decisionSource}",
                                "SOFT_DELETE_SOURCE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldExcludeSoftDeletedTransactionFromFinalFilter()
            throws Exception {

        createDecision("SOFT_DELETE_FINAL", "RISK_ENGINE", true);
        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/final/{finalDecision}",
                                true
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnNotFoundForUnknownRiskAssessment()
            throws Exception {

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Unknown risk assessment",
                        false
                );

        request.setRiskAssessmentId(UUID.randomUUID());

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForRiskAssessmentFromDifferentTransaction()
            throws Exception {

        UUID otherTransactionId = UUID.randomUUID();
        UUID otherRiskAssessmentId = UUID.randomUUID();

        insertAdditionalTransaction(otherTransactionId);
        insertRiskAssessment(otherRiskAssessmentId, otherTransactionId);

        TransactionDecisionRequest request =
                buildRequest(
                        "REVIEW",
                        "RISK_ENGINE",
                        new BigDecimal("80.00"),
                        "Cross transaction risk assessment",
                        false
                );

        request.setRiskAssessmentId(otherRiskAssessmentId);

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForSoftDeletedRiskAssessment()
            throws Exception {

        jdbcTemplate.update(
                """
                UPDATE transaction.risk_assessment
                SET deleted_at = CURRENT_TIMESTAMP
                WHERE risk_assessment_id = ?
                """,
                riskAssessmentId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "REVIEW",
                                                        "RISK_ENGINE",
                                                        new BigDecimal("80.00"),
                                                        "Deleted risk assessment",
                                                        false
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCreatingWithoutTransactionUpdate()
            throws Exception {

        authorize(
                Set.of("transaction.view"),
                organizationId,
                null
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "REVIEW",
                                                        "RISK_ENGINE",
                                                        new BigDecimal("90.00"),
                                                        "Forbidden create",
                                                        false
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenReadingWithoutTransactionView()
            throws Exception {

        JsonNode created =
                createDecision(
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        authorize(
                Set.of("transaction.update"),
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/{decisionId}",
                                UUID.fromString(
                                        created.get("decisionId").asText()
                                )
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHideCrossOrganizationDecisionById()
            throws Exception {

        UUID authorizedOrganization =
                organizationId;

        UUID otherOrganization =
                UUID.randomUUID();

        insertOrganization(
                otherOrganization
        );

        UUID otherTransaction =
                UUID.randomUUID();

        UUID otherRiskAssessment =
                UUID.randomUUID();

        insertAdditionalTransactionForOrganization(
                otherTransaction,
                otherOrganization
        );

        insertRiskAssessmentForOrganization(
                otherRiskAssessment,
                otherTransaction,
                otherOrganization
        );

        authorize(
                allPermissions(),
                otherOrganization,
                null
        );

        JsonNode hidden =
                createDecisionForTransaction(
                        otherTransaction,
                        otherRiskAssessment,
                        "REVIEW",
                        "RISK_ENGINE",
                        false
                );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/{decisionId}",
                                UUID.fromString(
                                        hidden.get("decisionId").asText()
                                )
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationDecisionList()
            throws Exception {

        UUID otherOrganization =
                UUID.randomUUID();

        insertOrganization(
                otherOrganization
        );

        UUID otherTransaction =
                UUID.randomUUID();

        insertAdditionalTransactionForOrganization(
                otherTransaction,
                otherOrganization
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/decisions",
                                otherTransaction
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationDecisionCreate()
            throws Exception {

        UUID otherOrganization =
                UUID.randomUUID();

        insertOrganization(
                otherOrganization
        );

        UUID otherTransaction =
                UUID.randomUUID();

        insertAdditionalTransactionForOrganization(
                otherTransaction,
                otherOrganization
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/decisions",
                                otherTransaction
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "REVIEW",
                                                        "RISK_ENGINE",
                                                        new BigDecimal("90.00"),
                                                        "Cross organization create",
                                                        false
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterCrossOrganizationDecisionsFromGlobalQueries()
            throws Exception {

        UUID authorizedOrganization =
                organizationId;

        createDecision(
                "REVIEW",
                "RISK_ENGINE",
                false
        );

        UUID otherOrganization =
                UUID.randomUUID();

        insertOrganization(
                otherOrganization
        );

        UUID otherTransaction =
                UUID.randomUUID();

        UUID otherRiskAssessment =
                UUID.randomUUID();

        insertAdditionalTransactionForOrganization(
                otherTransaction,
                otherOrganization
        );

        insertRiskAssessmentForOrganization(
                otherRiskAssessment,
                otherTransaction,
                otherOrganization
        );

        authorize(
                allPermissions(),
                otherOrganization,
                null
        );

        createDecisionForTransaction(
                otherTransaction,
                otherRiskAssessment,
                "REVIEW",
                "RISK_ENGINE",
                false
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/type/{decisionType}",
                                "REVIEW"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/source/{decisionSource}",
                                "RISK_ENGINE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/decisions/final/{finalDecision}",
                                false
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );
    }

    private void insertOrganization(
            UUID targetOrganizationId) {

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
                targetOrganizationId,
                "EFS-TD-SEC-" + targetOrganizationId,
                "EFS Transaction Decision Security "
                        + targetOrganizationId,
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertAdditionalTransactionForOrganization(
            UUID additionalTransactionId,
            UUID targetOrganizationId) {

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
                additionalTransactionId,
                "TD-SEC-SECOND-" + additionalTransactionId,
                customerId,
                targetOrganizationId,
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

    private void insertRiskAssessmentForOrganization(
            UUID assessmentId,
            UUID assessmentTransactionId,
            UUID targetOrganizationId) {

        LocalDateTime now =
                LocalDateTime.now();

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    organization_id,
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                assessmentId,
                assessmentTransactionId,
                targetOrganizationId,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("85.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                now,
                now,
                now,
                0
        );
    }

    private JsonNode createDecisionForTransaction(
            UUID targetTransactionId,
            UUID targetRiskAssessmentId,
            String decisionType,
            String decisionSource,
            Boolean finalDecision)
            throws Exception {

        TransactionDecisionRequest request =
                buildRequest(
                        decisionType,
                        decisionSource,
                        new BigDecimal("90.00"),
                        "Controller security decision",
                        finalDecision
                );

        request.setRiskAssessmentId(
                targetRiskAssessmentId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/decisions",
                                        targetTransactionId
                                )
                                        .contentType(MediaType.APPLICATION_JSON)
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
                result.getResponse()
                        .getContentAsString()
        );
    }

    private void authorize(
            Set<String> permissions,
            UUID targetOrganizationId,
            UUID tenantId) {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        actorId,
                        tenantId,
                        null,
                        Set.of(),
                        permissions,
                        Set.of()
                )
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(actorId)
        ).thenReturn(
                new UserAccountReference(
                        actorId,
                        targetOrganizationId,
                        tenantId,
                        "transaction-decision-controller@example.com"
                )
        );
    }

    private Set<String> allPermissions() {

        return Set.of(
                "transaction.view",
                "transaction.update"
        );
    }
    private void softDeleteTransaction(
            UUID targetTransactionId) {

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE transaction.transaction
                SET deleted_at = CURRENT_TIMESTAMP
                WHERE transaction_id = ?
                """,
                targetTransactionId
        );

        entityManager.clear();
    }

    private void insertAdditionalTransaction(
            UUID additionalTransactionId) {

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
                additionalTransactionId,
                "TD-CTRL-SECOND-" + additionalTransactionId,
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

    private void insertRiskAssessment(
            UUID assessmentId,
            UUID assessmentTransactionId) {

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    organization_id,
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                assessmentId,
                assessmentTransactionId,
                organizationId,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("85.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                now,
                now,
                now,
                0
        );
    }

    private JsonNode createDecision(
            String decisionType,
            String decisionSource,
            Boolean finalDecision)
            throws Exception {

        TransactionDecisionRequest request =
                buildRequest(
                        decisionType,
                        decisionSource,
                        new BigDecimal("90.00"),
                        "Controller integration decision",
                        finalDecision
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/decisions",
                                        transactionId
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
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private TransactionDecisionRequest buildRequest(
            String decisionType,
            String decisionSource,
            BigDecimal confidenceScore,
            String decisionReason,
            Boolean finalDecision) {

        TransactionDecisionRequest request =
                new TransactionDecisionRequest();

        request.setRiskAssessmentId(
                riskAssessmentId
        );

        request.setDecisionType(
                decisionType
        );

        request.setDecisionSource(
                decisionSource
        );

        request.setConfidenceScore(
                confidenceScore
        );

        request.setDecisionReason(
                decisionReason
        );

        request.setDecisionTimestamp(
                LocalDateTime.now()
        );

        request.setFinalDecision(
                finalDecision
        );

        return request;
    }
}
