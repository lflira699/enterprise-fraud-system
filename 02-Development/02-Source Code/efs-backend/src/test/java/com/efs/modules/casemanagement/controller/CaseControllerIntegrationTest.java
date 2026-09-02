package com.efs.modules.casemanagement.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
                .andExpect(jsonPath("$.caseNumber").value("CASE-API-001"))
                .andExpect(jsonPath("$.organizationId").value(ORGANIZATION_ID.toString()))
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID.toString()))
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID.toString()))
                .andExpect(jsonPath("$.severity").value("MEDIUM"))
                .andExpect(jsonPath("$.priority").value("NORMAL"))
                .andExpect(jsonPath("$.currentStatus").value("OPEN"));
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
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.caseNumber").value("CASE-API-002"))
                .andExpect(jsonPath("$.currentStatus").value("OPEN"));
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
                .andExpect(jsonPath("$[0].customerId").value(CUSTOMER_ID.toString()))
                .andExpect(jsonPath("$[0].caseNumber").value("CASE-API-003"));
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
                .andExpect(jsonPath("$.content[0].currentStatus").value("OPEN"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
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
                        .andExpect(jsonPath("$.caseNumber").value("CASE-ALERT-API-001"))
                        .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID.toString()))
                        .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID.toString()))
                        .andExpect(jsonPath("$.category").value("TRANSACTION"))
                        .andExpect(jsonPath("$.severity").value("HIGH"))
                        .andExpect(jsonPath("$.priority").value("NORMAL"))
                        .andExpect(jsonPath("$.currentStatus").value("OPEN"))
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
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.assignedFrom").value(ASSIGNED_FROM.toString()))
                .andExpect(jsonPath("$.assignedTo").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.assignedTeam").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$.assignmentReason").value("Assigned for investigation"))
                .andExpect(jsonPath("$.assignedAt").exists());

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedUser").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.assignedTeam").value("FRAUD_INVESTIGATION"));
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
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].assignedTo").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$[0].assignedTeam").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$[0].assignmentReason").value("Initial assignment"));
    }

    @Test
    void shouldCreateCaseTaskThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-TASK-API-001"
                );

        String requestBody =
                """
                {
                    "taskName": "Review transaction evidence",
                    "taskDescription": "Review transaction and supporting evidence",
                    "assignedTo": "%s",
                    "priority": "HIGH",
                    "status": "OPEN",
                    "dueDate": "%s"
                }
                """.formatted(
                        ASSIGNED_TO,
                        LocalDateTime.now().plusDays(1)
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/tasks",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.taskName").value("Review transaction evidence"))
                .andExpect(jsonPath("$.taskDescription").value("Review transaction and supporting evidence"))
                .andExpect(jsonPath("$.assignedTo").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveCaseTasksThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-TASK-API-002"
                );

        UUID taskId =
                insertCaseTask(
                        caseId,
                        "Review device activity"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/tasks",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(taskId.toString()))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].taskName").value("Review device activity"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void shouldRetrieveCaseTaskByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-TASK-API-003"
                );

        UUID taskId =
                insertCaseTask(
                        caseId,
                        "Review transaction history"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/tasks/{taskId}",
                                caseId,
                                taskId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.taskName").value("Review transaction history"))
                .andExpect(jsonPath("$.assignedTo").value(ASSIGNED_TO.toString()));
    }

    @Test
    void shouldCreateCaseCommentThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-COMMENT-API-001"
                );

        String requestBody =
                """
                {
                    "commentType": "INVESTIGATION_NOTE",
                    "commentText": "Investigation started",
                    "visibility": "INTERNAL",
                    "createdBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/comments",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.commentType").value("INVESTIGATION_NOTE"))
                .andExpect(jsonPath("$.commentText").value("Investigation started"))
                .andExpect(jsonPath("$.visibility").value("INTERNAL"))
                .andExpect(jsonPath("$.createdBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveCaseCommentsThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-COMMENT-API-002"
                );

        UUID commentId =
                insertCaseComment(
                        caseId,
                        "First investigation note"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/comments",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(commentId.toString()))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].commentText").value("First investigation note"))
                .andExpect(jsonPath("$[0].commentType").value("INVESTIGATION_NOTE"))
                .andExpect(jsonPath("$[0].visibility").value("INTERNAL"));
    }

    @Test
    void shouldRetrieveCaseCommentByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-COMMENT-API-003"
                );

        UUID commentId =
                insertCaseComment(
                        caseId,
                        "Investigation observation"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/comments/{commentId}",
                                caseId,
                                commentId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.commentText").value("Investigation observation"))
                .andExpect(jsonPath("$.createdBy").value(ASSIGNED_TO.toString()));
    }

    @Test
    void shouldCreateCaseEvidenceThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-001"
                );

        String requestBody =
                """
                {
                    "transactionId": "%s",
                    "evidenceType": "TRANSACTION_SCREENSHOT",
                    "sourceSystem": "INTERNAL_CASE_TOOL",
                    "storageUri": "case-evidence://transaction/screenshot-001",
                    "checksumSha256": "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                    "uploadedBy": "%s"
                }
                """.formatted(
                        TRANSACTION_ID,
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/evidence",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID.toString()))
                .andExpect(jsonPath("$.evidenceType").value("TRANSACTION_SCREENSHOT"))
                .andExpect(jsonPath("$.sourceSystem").value("INTERNAL_CASE_TOOL"))
                .andExpect(jsonPath("$.storageUri").value("case-evidence://transaction/screenshot-001"))
                .andExpect(jsonPath("$.checksumSha256").value(
                        "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
                ))
                .andExpect(jsonPath("$.uploadedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.uploadedAt").exists());
    }

    @Test
    void shouldRetrieveCaseEvidenceThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-002"
                );

        UUID evidenceId =
                insertCaseEvidence(
                        caseId,
                        "TRANSACTION_SCREENSHOT"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evidenceId").value(evidenceId.toString()))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].transactionId").value(TRANSACTION_ID.toString()))
                .andExpect(jsonPath("$[0].evidenceType").value("TRANSACTION_SCREENSHOT"))
                .andExpect(jsonPath("$[0].sourceSystem").value("INTERNAL_CASE_TOOL"));
    }

    @Test
    void shouldRetrieveCaseEvidenceByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-003"
                );

        UUID evidenceId =
                insertCaseEvidence(
                        caseId,
                        "DEVICE_EVIDENCE"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence/{evidenceId}",
                                caseId,
                                evidenceId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidenceId").value(evidenceId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.evidenceType").value("DEVICE_EVIDENCE"))
                .andExpect(jsonPath("$.uploadedBy").value(ASSIGNED_TO.toString()));
    }

    @Test
    void shouldReturnNotFoundForSoftDeletedCaseEvidenceThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-004"
                );

        UUID evidenceId =
                insertCaseEvidence(
                        caseId,
                        "TRANSACTION_SCREENSHOT"
                );

        jdbcTemplate.update(
                """
                UPDATE case_management.case_evidence
                SET deleted_at = CURRENT_TIMESTAMP,
                    deleted_by = ?
                WHERE evidence_id = ?
                """,
                ASSIGNED_TO,
                evidenceId
        );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence/{evidenceId}",
                                caseId,
                                evidenceId)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExcludeSoftDeletedCaseEvidenceFromApiList() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-005"
                );

        UUID activeEvidenceId =
                insertCaseEvidence(
                        caseId,
                        "TRANSACTION_SCREENSHOT"
                );

        UUID deletedEvidenceId =
                insertCaseEvidence(
                        caseId,
                        "DEVICE_EVIDENCE"
                );

        jdbcTemplate.update(
                """
                UPDATE case_management.case_evidence
                SET deleted_at = CURRENT_TIMESTAMP,
                    deleted_by = ?
                WHERE evidence_id = ?
                """,
                ASSIGNED_TO,
                deletedEvidenceId
        );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].evidenceId")
                        .value(activeEvidenceId.toString()))
                .andExpect(jsonPath("$[0].caseId")
                        .value(caseId.toString()));
    }
    @Test
    void shouldReturnNotFoundWhenRetrievingEvidenceForUnknownCaseThroughApi()
            throws Exception {

        UUID unknownCaseId =
                UUID.randomUUID();

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence",
                                unknownCaseId)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyEvidenceListForCaseWithoutEvidenceThroughApi()
            throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-EVIDENCE-API-006"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/evidence",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldUpdateCaseStatusThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-STATUS-API-001"
                );

        String requestBody =
                """
                {
                    "currentStatus": "IN_PROGRESS",
                    "changeReason": "Investigation started",
                    "changedBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        patch("/api/v1/cases/{caseId}/status",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.caseNumber").value("CASE-STATUS-API-001"))
                .andExpect(jsonPath("$.currentStatus").value("IN_PROGRESS"));

        entityManager.flush();

        String persistedStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_status
                        FROM case_management.case
                        WHERE case_id = ?
                        """,
                        String.class,
                        caseId
                );

        assertEquals(
                "IN_PROGRESS",
                persistedStatus
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_status_history
                        WHERE case_id = ?
                          AND previous_status = ?
                          AND current_status = ?
                          AND changed_by = ?
                        """,
                        Integer.class,
                        caseId,
                        "OPEN",
                        "IN_PROGRESS",
                        ASSIGNED_TO
                );

        assertEquals(
                1,
                historyCount
        );
    }

    @Test
    void shouldRetrieveCaseStatusHistoryThroughApi()
            throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-STATUS-API-002"
                );

        String firstRequest =
                """
                {
                    "currentStatus": "IN_PROGRESS",
                    "changeReason": "Investigation started",
                    "changedBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        patch("/api/v1/cases/{caseId}/status",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstRequest)
                )
                .andExpect(status().isOk());

        String secondRequest =
                """
                {
                    "currentStatus": "PENDING_INFORMATION",
                    "changeReason": "Additional information required",
                    "changedBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        patch("/api/v1/cases/{caseId}/status",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondRequest)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/status-history",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].previousStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].currentStatus").value("PENDING_INFORMATION"))
                .andExpect(jsonPath("$[0].changeReason").value("Additional information required"))
                .andExpect(jsonPath("$[0].changedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$[0].changedAt").exists())
                .andExpect(jsonPath("$[1].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[1].previousStatus").value("OPEN"))
                .andExpect(jsonPath("$[1].currentStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].changeReason").value("Investigation started"))
                .andExpect(jsonPath("$[1].changedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$[1].changedAt").exists());
    }

    @Test
    void shouldCreateCaseResolutionThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-RESOLUTION-API-001"
                );

        String requestBody =
                """
                {
                    "resolutionType": "CONFIRMED_FRAUD",
                    "resolutionSummary": "Investigation completed with documented resolution",
                    "economicImpact": 1500.00,
                    "currencyCode": "GTQ",
                    "resolvedBy": "%s",
                    "approvedBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO,
                        ASSIGNED_FROM
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/resolutions",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resolutionId").exists())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.resolutionType").value("CONFIRMED_FRAUD"))
                .andExpect(jsonPath("$.resolutionSummary").value(
                        "Investigation completed with documented resolution"
                ))
                .andExpect(jsonPath("$.economicImpact").value(1500.00))
                .andExpect(jsonPath("$.currencyCode").value("GTQ"))
                .andExpect(jsonPath("$.resolvedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.approvedBy").value(ASSIGNED_FROM.toString()))
                .andExpect(jsonPath("$.resolvedAt").exists());
    }

    @Test
    void shouldRetrieveCaseResolutionsThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-RESOLUTION-API-002"
                );

        UUID resolutionId =
                insertCaseResolution(
                        caseId,
                        "CONFIRMED_FRAUD"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/resolutions",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resolutionId").value(resolutionId.toString()))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].resolutionType").value("CONFIRMED_FRAUD"))
                .andExpect(jsonPath("$[0].resolutionSummary").value(
                        "Investigation completed with documented resolution"
                ))
                .andExpect(jsonPath("$[0].economicImpact").value(1500.00))
                .andExpect(jsonPath("$[0].currencyCode").value("GTQ"))
                .andExpect(jsonPath("$[0].resolvedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$[0].approvedBy").value(ASSIGNED_FROM.toString()));
    }

    @Test
    void shouldRetrieveCaseResolutionByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-RESOLUTION-API-003"
                );

        UUID resolutionId =
                insertCaseResolution(
                        caseId,
                        "FALSE_POSITIVE"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/resolutions/{resolutionId}",
                                caseId,
                                resolutionId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolutionId").value(resolutionId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.resolutionType").value("FALSE_POSITIVE"))
                .andExpect(jsonPath("$.resolutionSummary").value(
                        "Investigation completed with documented resolution"
                ))
                .andExpect(jsonPath("$.economicImpact").value(1500.00))
                .andExpect(jsonPath("$.currencyCode").value("GTQ"))
                .andExpect(jsonPath("$.resolvedBy").value(ASSIGNED_TO.toString()))
                .andExpect(jsonPath("$.approvedBy").value(ASSIGNED_FROM.toString()))
                .andExpect(jsonPath("$.resolvedAt").exists());
    }

    @Test
    void shouldCreateCaseEscalationThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-ESCALATION-API-001"
                );

        String requestBody =
                """
                {
                    "escalationLevel": "LEVEL_2",
                    "fromTeam": "FRAUD_INVESTIGATION",
                    "toTeam": "FRAUD_REVIEW",
                    "escalationReason": "Additional review required",
                    "escalatedBy": "%s"
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/escalations",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.escalationId").exists())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.escalationLevel").value("LEVEL_2"))
                .andExpect(jsonPath("$.fromTeam").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$.toTeam").value("FRAUD_REVIEW"))
                .andExpect(jsonPath("$.escalationReason").value(
                        "Additional review required"
                ))
                .andExpect(jsonPath("$.escalatedBy").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$.escalatedAt").exists())
                .andExpect(jsonPath("$.resolvedAt").doesNotExist());
    }

    @Test
    void shouldRetrieveCaseEscalationsThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-ESCALATION-API-002"
                );

        UUID escalationId =
                insertCaseEscalation(
                        caseId,
                        "LEVEL_2"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/escalations",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].escalationId").value(
                        escalationId.toString()
                ))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].escalationLevel").value("LEVEL_2"))
                .andExpect(jsonPath("$[0].fromTeam").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$[0].toTeam").value("FRAUD_REVIEW"))
                .andExpect(jsonPath("$[0].escalationReason").value(
                        "Additional review required"
                ))
                .andExpect(jsonPath("$[0].escalatedBy").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$[0].escalatedAt").exists());
    }

    @Test
    void shouldRetrieveCaseEscalationByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-ESCALATION-API-003"
                );

        UUID escalationId =
                insertCaseEscalation(
                        caseId,
                        "LEVEL_1"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/escalations/{escalationId}",
                                caseId,
                                escalationId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.escalationId").value(
                        escalationId.toString()
                ))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.escalationLevel").value("LEVEL_1"))
                .andExpect(jsonPath("$.fromTeam").value("FRAUD_INVESTIGATION"))
                .andExpect(jsonPath("$.toTeam").value("FRAUD_REVIEW"))
                .andExpect(jsonPath("$.escalationReason").value(
                        "Additional review required"
                ))
                .andExpect(jsonPath("$.escalatedBy").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$.escalatedAt").exists());
    }

    @Test
    void shouldCreateCaseSlaThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-SLA-API-001"
                );

        String requestBody =
                """
                {
                    "slaType": "INVESTIGATION",
                    "targetMinutes": 1440,
                    "elapsedMinutes": 0,
                    "deadline": "%s",
                    "breached": false,
                    "breachReason": null
                }
                """.formatted(
                        LocalDateTime.now().plusDays(1)
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/slas",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slaId").exists())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.slaType").value("INVESTIGATION"))
                .andExpect(jsonPath("$.targetMinutes").value(1440))
                .andExpect(jsonPath("$.elapsedMinutes").value(0))
                .andExpect(jsonPath("$.deadline").exists())
                .andExpect(jsonPath("$.breached").value(false))
                .andExpect(jsonPath("$.breachReason").doesNotExist())
                .andExpect(jsonPath("$.calculatedAt").exists());
    }

    @Test
    void shouldRetrieveCaseSlasThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-SLA-API-002"
                );

        UUID slaId =
                insertCaseSla(
                        caseId,
                        "INVESTIGATION"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/slas",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slaId").value(slaId.toString()))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].slaType").value("INVESTIGATION"))
                .andExpect(jsonPath("$[0].targetMinutes").value(1440))
                .andExpect(jsonPath("$[0].elapsedMinutes").value(0))
                .andExpect(jsonPath("$[0].deadline").exists())
                .andExpect(jsonPath("$[0].breached").value(false))
                .andExpect(jsonPath("$[0].calculatedAt").exists());
    }

    @Test
    void shouldRetrieveCaseSlaByIdThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-SLA-API-003"
                );

        UUID slaId =
                insertCaseSla(
                        caseId,
                        "INITIAL_REVIEW"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/slas/{slaId}",
                                caseId,
                                slaId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slaId").value(slaId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.slaType").value("INITIAL_REVIEW"))
                .andExpect(jsonPath("$.targetMinutes").value(1440))
                .andExpect(jsonPath("$.elapsedMinutes").value(0))
                .andExpect(jsonPath("$.deadline").exists())
                .andExpect(jsonPath("$.breached").value(false))
                .andExpect(jsonPath("$.calculatedAt").exists());
    }

    @Test
    void shouldCreateCaseNotificationThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-NOTIFICATION-API-001"
                );

        String requestBody =
                """
                {
                    "notificationType": "CASE_ASSIGNED",
                    "recipientUserId": "%s",
                    "notificationStatus": "PENDING",
                    "notificationReference": "NOTIFICATION-REF-001",
                    "deliveryResult": null,
                    "processedAt": null
                }
                """.formatted(
                        ASSIGNED_TO
                );

        mockMvc.perform(
                        post("/api/v1/cases/{caseId}/notifications",
                                caseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseNotificationId").exists())
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.notificationType").value("CASE_ASSIGNED"))
                .andExpect(jsonPath("$.recipientUserId").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$.notificationStatus").value("PENDING"))
                .andExpect(jsonPath("$.notificationReference").value(
                        "NOTIFICATION-REF-001"
                ))
                .andExpect(jsonPath("$.deliveryResult").doesNotExist())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.processedAt").doesNotExist());
    }

    @Test
    void shouldRetrieveCaseNotificationsThroughApi() throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-NOTIFICATION-API-002"
                );

        UUID caseNotificationId =
                insertCaseNotification(
                        caseId,
                        "CASE_ASSIGNED"
                );

        mockMvc.perform(
                        get("/api/v1/cases/{caseId}/notifications",
                                caseId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caseNotificationId").value(
                        caseNotificationId.toString()
                ))
                .andExpect(jsonPath("$[0].caseId").value(caseId.toString()))
                .andExpect(jsonPath("$[0].notificationType").value(
                        "CASE_ASSIGNED"
                ))
                .andExpect(jsonPath("$[0].recipientUserId").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$[0].notificationStatus").value(
                        "PENDING"
                ))
                .andExpect(jsonPath("$[0].notificationReference").value(
                        "NOTIFICATION-REF-001"
                ))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    void shouldRetrieveCaseNotificationByIdThroughApi()
            throws Exception {

        UUID caseId =
                insertCase(
                        "CASE-NOTIFICATION-API-003"
                );

        UUID caseNotificationId =
                insertCaseNotification(
                        caseId,
                        "CASE_ESCALATED"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/cases/{caseId}/notifications/{caseNotificationId}",
                                caseId,
                                caseNotificationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseNotificationId").value(
                        caseNotificationId.toString()
                ))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.notificationType").value(
                        "CASE_ESCALATED"
                ))
                .andExpect(jsonPath("$.recipientUserId").value(
                        ASSIGNED_TO.toString()
                ))
                .andExpect(jsonPath("$.notificationStatus").value(
                        "PENDING"
                ))
                .andExpect(jsonPath("$.notificationReference").value(
                        "NOTIFICATION-REF-001"
                ))
                .andExpect(jsonPath("$.createdAt").exists());
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

    private UUID insertCaseTask(
            UUID caseId,
            String taskName) {

        UUID taskId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_task (
                    task_id,
                    case_id,
                    task_name,
                    task_description,
                    assigned_to,
                    priority,
                    status,
                    due_date,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                taskId,
                caseId,
                taskName,
                "Review transaction and supporting evidence",
                ASSIGNED_TO,
                "HIGH",
                "OPEN",
                LocalDateTime.now().plusDays(1)
        );

        return taskId;
    }

    private UUID insertCaseComment(
            UUID caseId,
            String commentText) {

        UUID commentId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_comment (
                    comment_id,
                    case_id,
                    comment_type,
                    comment_text,
                    visibility,
                    created_by,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                commentId,
                caseId,
                "INVESTIGATION_NOTE",
                commentText,
                "INTERNAL",
                ASSIGNED_TO
        );

        return commentId;
    }

    private UUID insertCaseEvidence(
            UUID caseId,
            String evidenceType) {

        UUID evidenceId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_evidence (
                    evidence_id,
                    case_id,
                    transaction_id,
                    evidence_type,
                    source_system,
                    storage_uri,
                    checksum_sha256,
                    uploaded_by,
                    uploaded_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                evidenceId,
                caseId,
                TRANSACTION_ID,
                evidenceType,
                "INTERNAL_CASE_TOOL",
                "case-evidence://transaction/screenshot-001",
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                ASSIGNED_TO
        );

        return evidenceId;
    }

    private UUID insertCaseResolution(
            UUID caseId,
            String resolutionType) {

        UUID resolutionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_resolution (
                    resolution_id,
                    case_id,
                    resolution_type,
                    resolution_summary,
                    economic_impact,
                    currency_code,
                    resolved_by,
                    resolved_at,
                    approved_by
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
                """,
                resolutionId,
                caseId,
                resolutionType,
                "Investigation completed with documented resolution",
                new BigDecimal("1500.00"),
                "GTQ",
                ASSIGNED_TO,
                ASSIGNED_FROM
        );

        return resolutionId;
    }

    private UUID insertCaseEscalation(
            UUID caseId,
            String escalationLevel) {

        UUID escalationId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_escalation (
                    escalation_id,
                    case_id,
                    escalation_level,
                    from_team,
                    to_team,
                    escalation_reason,
                    escalated_by,
                    escalated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                escalationId,
                caseId,
                escalationLevel,
                "FRAUD_INVESTIGATION",
                "FRAUD_REVIEW",
                "Additional review required",
                ASSIGNED_TO
        );

        return escalationId;
    }

    private UUID insertCaseSla(
            UUID caseId,
            String slaType) {

        UUID slaId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_sla (
                    sla_id,
                    case_id,
                    sla_type,
                    target_minutes,
                    elapsed_minutes,
                    deadline,
                    breached,
                    breach_reason,
                    calculated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                slaId,
                caseId,
                slaType,
                1440,
                0,
                LocalDateTime.now().plusDays(1),
                false,
                null
        );

        return slaId;
    }

    private UUID insertCaseNotification(
            UUID caseId,
            String notificationType) {

        UUID caseNotificationId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_notification (
                    case_notification_id,
                    case_id,
                    notification_type,
                    recipient_user_id,
                    notification_status,
                    notification_reference,
                    delivery_result,
                    created_at,
                    processed_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
                """,
                caseNotificationId,
                caseId,
                notificationType,
                ASSIGNED_TO,
                "PENDING",
                "NOTIFICATION-REF-001",
                null,
                null
        );

        return caseNotificationId;
    }
}
