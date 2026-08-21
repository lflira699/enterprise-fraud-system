package com.efs.modules.casemanagement.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CaseControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "55555555-aaaa-5555-aaaa-555555555555"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "66666666-bbbb-6666-bbbb-666666666666"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "77777777-cccc-7777-cccc-777777777777"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "88888888-dddd-8888-dddd-888888888888"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "99999999-eeee-9999-eeee-999999999999"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "aaaaaaaa-ffff-aaaa-ffff-aaaaaaaaaaaa"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "bbbbbbbb-aaaa-bbbb-aaaa-bbbbbbbbbbbb"
            );

    private static final UUID ASSIGNED_FROM =
            UUID.fromString(
                    "cccccccc-bbbb-cccc-bbbb-cccccccccccc"
            );

    private static final UUID ASSIGNED_TO =
            UUID.fromString(
                    "dddddddd-cccc-dddd-cccc-dddddddddddd"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                "EFS-CASE-CONTROLLER-ORG",
                "EFS Case Controller Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ASSIGNED_FROM,
                ORGANIZATION_ID,
                "efs.case.controller.from",
                "EFS Case Controller From",
                "efs.case.controller.from@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ASSIGNED_TO,
                ORGANIZATION_ID,
                "efs.case.controller.to",
                "EFS Case Controller To",
                "efs.case.controller.to@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
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
                "EFS-CASE-CONTROLLER-CUSTOMER",
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
                "EFS-CASE-CONTROLLER-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("1250.00"),
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
                new BigDecimal("89.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("91.00"),
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
                new BigDecimal("91.00"),
                "Escalate to investigation",
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
                    severity,
                    priority,
                    status,
                    risk_score,
                    generated_at,
                    created_at,
                    updated_at
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
                "HIGH",
                "NEW",
                new BigDecimal("89.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateCaseThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "caseNumber": "CASE-API-001",
                    "organizationId": "%s",
                    "transactionId": "%s",
                    "customerId": "%s",
                    "caseType": "FRAUD_INVESTIGATION",
                    "category": "TRANSACTION"
                }
                """.formatted(
                        ORGANIZATION_ID,
                        TRANSACTION_ID,
                        CUSTOMER_ID
                );

        mockMvc.perform(
                        post("/api/v1/cases")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.caseNumber")
                                .value("CASE-API-001")
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(ORGANIZATION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(CUSTOMER_ID.toString())
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$.priority")
                                .value("NORMAL")
                )
                .andExpect(
                        jsonPath("$.currentStatus")
                                .value("OPEN")
                );
    }

    @Test
    void shouldRetrieveCaseByNumberThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-API-002"
                );

        mockMvc.perform(
                        get("/api/v1/cases/number/{caseNumber}",
                                "CASE-API-002")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.caseId")
                                .value(caseId.toString())
                )
                .andExpect(
                        jsonPath("$.caseNumber")
                                .value("CASE-API-002")
                )
                .andExpect(
                        jsonPath("$.currentStatus")
                                .value("OPEN")
                );
    }

    @Test
    void shouldRetrieveCasesByCustomerThroughApi() throws Exception {

        insertCase(
                "CASE-API-003"
        );

        mockMvc.perform(
                        get("/api/v1/cases/customer/{customerId}",
                                CUSTOMER_ID)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].customerId")
                                .value(CUSTOMER_ID.toString())
                )
                .andExpect(
                        jsonPath("$[0].caseNumber")
                                .value("CASE-API-003")
                );
    }

    @Test
    void shouldFilterCasesByStatusThroughApi() throws Exception {

        insertCase(
                "CASE-API-004"
        );

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param("status", "OPEN")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].currentStatus")
                                .value("OPEN")
                );
    }

    @Test
    void shouldCreateCaseFromAlertThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "alertId": "%s",
                    "caseNumber": "CASE-ALERT-API-001",
                    "organizationId": "%s",
                    "caseType": "FRAUD_INVESTIGATION"
                }
                """.formatted(
                        ALERT_ID,
                        ORGANIZATION_ID
                );

        String responseBody =
                mockMvc.perform(
                                post("/api/v1/cases/from-alert")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(
                                jsonPath("$.caseNumber")
                                        .value("CASE-ALERT-API-001")
                        )
                        .andExpect(
                                jsonPath("$.customerId")
                                        .value(CUSTOMER_ID.toString())
                        )
                        .andExpect(
                                jsonPath("$.transactionId")
                                        .value(TRANSACTION_ID.toString())
                        )
                        .andExpect(
                                jsonPath("$.category")
                                        .value("TRANSACTION")
                        )
                        .andExpect(
                                jsonPath("$.severity")
                                        .value("HIGH")
                        )
                        .andExpect(
                                jsonPath("$.priority")
                                        .value("NORMAL")
                        )
                        .andExpect(
                                jsonPath("$.currentStatus")
                                        .value("OPEN")
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String caseId =
                com.jayway.jsonpath.JsonPath
                        .read(
                                responseBody,
                                "$.caseId"
                        );

        Integer linkCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_alert
                        WHERE case_id = ?::uuid
                          AND source_alert_id = ?
                        """,
                        Integer.class,
                        caseId,
                        ALERT_ID
                );

        assertEquals(
                1,
                linkCount
        );
    }

    @Test
    void shouldRejectDuplicateCaseCreationFromSameAlertThroughApi()
            throws Exception {

        String firstRequest =
                """
                {
                    "alertId": "%s",
                    "caseNumber": "CASE-ALERT-API-002",
                    "organizationId": "%s",
                    "caseType": "FRAUD_INVESTIGATION"
                }
                """.formatted(
                        ALERT_ID,
                        ORGANIZATION_ID
                );

        mockMvc.perform(
                        post("/api/v1/cases/from-alert")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstRequest)
                )
                .andExpect(status().isCreated());

        String secondRequest =
                """
                {
                    "alertId": "%s",
                    "caseNumber": "CASE-ALERT-API-003",
                    "organizationId": "%s",
                    "caseType": "FRAUD_INVESTIGATION"
                }
                """.formatted(
                        ALERT_ID,
                        ORGANIZATION_ID
                );

        mockMvc.perform(
                        post("/api/v1/cases/from-alert")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondRequest)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAssignCaseThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-ASSIGNMENT-API-001"
                );

        String requestBody =
                """
                {
                    "assignedFrom": "%s",
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "assignmentReason": "Assigned for investigation"
                }
                """.formatted(
                        ASSIGNED_FROM,
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/assignments",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.caseId")
                                .value(caseId.toString())
                )
                .andExpect(
                        jsonPath("$.assignedFrom")
                                .value(ASSIGNED_FROM.toString())
                )
                .andExpect(
                        jsonPath("$.assignedTo")
                                .value(ASSIGNED_TO.toString())
                )
                .andExpect(
                        jsonPath("$.assignedTeam")
                                .value("FRAUD_INVESTIGATION")
                )
                .andExpect(
                        jsonPath("$.assignmentReason")
                                .value("Assigned for investigation")
                )
                .andExpect(
                        jsonPath("$.assignedAt")
                                .exists()
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.assignedUser")
                                .value(ASSIGNED_TO.toString())
                )
                .andExpect(
                        jsonPath("$.assignedTeam")
                                .value("FRAUD_INVESTIGATION")
                );
    }

    @Test
    void shouldRetrieveCaseAssignmentHistoryThroughApi()
            throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-ASSIGNMENT-API-002"
                );

        String requestBody =
                """
                {
                    "assignedFrom": "%s",
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "assignmentReason": "Initial assignment"
                }
                """.formatted(
                        ASSIGNED_FROM,
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/assignments",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/assignments",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].caseId")
                                .value(caseId.toString())
                )
                .andExpect(
                        jsonPath("$[0].assignedTo")
                                .value(ASSIGNED_TO.toString())
                )
                .andExpect(
                        jsonPath("$[0].assignedTeam")
                                .value("FRAUD_INVESTIGATION")
                )
                .andExpect(
                        jsonPath("$[0].assignmentReason")
                                .value("Initial assignment")
                );
    }

    private UUID insertCase(
            String caseNumber) {

        UUID caseId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case (
                    case_id,
                    case_number,
                    organization_id,
                    transaction_id,
                    customer_id,
                    case_type,
                    category,
                    severity,
                    priority,
                    current_status,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                caseId,
                caseNumber,
                ORGANIZATION_ID,
                TRANSACTION_ID,
                CUSTOMER_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "MEDIUM",
                "NORMAL",
                "OPEN"
        );

        return caseId;
    }
}