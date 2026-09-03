package com.efs.modules.alert.controller;

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
class AlertControllerIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "71717171-7171-7171-7171-717171717171"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "72727272-7272-7272-7272-727272727272"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "73737373-7373-7373-7373-737373737373"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "74747474-7474-7474-7474-747474747474"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "75757575-7575-7575-7575-757575757575"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "76767676-7676-7676-7676-767676767676"
            );

    private static final UUID CHANGED_BY =
            UUID.fromString(
                    "78787878-7878-7878-7878-787878787878"
            );

    private static final UUID ASSIGNED_USER_ID =
            UUID.fromString(
                    "79797979-7979-7979-7979-797979797979"
            );

    private static final UUID REASSIGNED_USER_ID =
            UUID.fromString(
                    "7a7a7a7a-7a7a-7a7a-7a7a-7a7a7a7a7a7a"
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
                "EFS-ALERT-CONTROLLER-ORG",
                "EFS Alert Controller Organization",
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
                ASSIGNED_USER_ID,
                ORGANIZATION_ID,
                "efs.alert.controller.user",
                "EFS Alert Controller User",
                "efs.alert.controller@example.com",
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
                REASSIGNED_USER_ID,
                ORGANIZATION_ID,
                "efs.alert.controller.reassigned",
                "EFS Alert Controller Reassigned User",
                "efs.alert.controller.reassigned@example.com",
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
                "EFS-ALERT-CONTROLLER-TEST-CUSTOMER",
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
                "EFS-ALERT-CONTROLLER-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("750.00"),
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
    }

    @Test
    void shouldCreateAlertThroughApi()
            throws Exception {

        String requestBody =
                """
                {
                    "decisionId": "%s",
                    "alertType": "FRAUD",
                    "category": "TRANSACTION",
                    "priority": "HIGH",
                    "riskScore": 91.00
                }
                """.formatted(
                        DECISION_ID
                );

        mockMvc.perform(
                        post("/api/v1/alerts")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        CUSTOMER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        TRANSACTION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.riskAssessmentId")
                                .value(
                                        RISK_ASSESSMENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.decisionId")
                                .value(
                                        DECISION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("NEW")
                );
    }

    @Test
    void shouldRetrieveAlertsByDecisionThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/decision/{decisionId}",
                                DECISION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].alertId")
                                .value(
                                        alertId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].decisionId")
                                .value(
                                        DECISION_ID.toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByStatusThroughApi()
            throws Exception {

        insertAlert(
                "NEW"
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "status",
                                        "NEW"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].status"
                        )
                                .value("NEW")
                )
                .andExpect(
                        jsonPath("$.page")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(25)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.hasNext")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.hasPrevious")
                                .value(false)
                );
    }

    @Test
    void shouldUpdateAlertStatusThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        String requestBody =
                """
                {
                    "status": "IN_PROGRESS",
                    "changedBy": "%s",
                    "changeReason": "Investigation started"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/status",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_PROGRESS")
                );
    }

    @Test
    void shouldRetrieveAlertHistoryThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        String requestBody =
                """
                {
                    "status": "IN_PROGRESS",
                    "changedBy": "%s",
                    "changeReason": "Investigation started"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/status",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/{alertId}/history",
                                alertId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].actionType")
                                .value("STATUS_CHANGE")
                )
                .andExpect(
                        jsonPath("$[0].previousStatus")
                                .value("NEW")
                )
                .andExpect(
                        jsonPath("$[0].newStatus")
                                .value("IN_PROGRESS")
                );
    }

    @Test
    void shouldAssignAlertThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        String requestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Assigned for investigation"
                }
                """.formatted(
                        ASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.assignedTo")
                                .value(
                                        ASSIGNED_USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.assignedTeam")
                                .value(
                                        "FRAUD_INVESTIGATION"
                                )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/{alertId}/history",
                                alertId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].actionType")
                                .value("ASSIGNMENT")
                )
                .andExpect(
                        jsonPath("$[0].changedBy")
                                .value(
                                        CHANGED_BY.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].changeReason")
                                .value(
                                        "Assigned for investigation"
                                )
                );
    }

    @Test
    void shouldReassignAlertThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        String firstRequestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Initial assignment"
                }
                """.formatted(
                        ASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(firstRequestBody)
                )
                .andExpect(
                        status().isOk()
                );

        String secondRequestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Reassigned for investigation"
                }
                """.formatted(
                        REASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(secondRequestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.assignedTo")
                                .value(
                                        REASSIGNED_USER_ID.toString()
                                )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/{alertId}/history",
                                alertId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].actionType")
                                .value("ASSIGNMENT")
                )
                .andExpect(
                        jsonPath("$[1].actionType")
                                .value("ASSIGNMENT")
                );
    }

    @Test
    void shouldReturnNotFoundWhenAssigningUnknownAlertThroughApi()
            throws Exception {

        UUID unknownAlertId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Assignment attempt for unknown alert"
                }
                """.formatted(
                        ASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                unknownAlertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectAssignmentForClosedAlertThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "IN_PROGRESS"
                );

        String closureRequestBody =
                """
                {
                    "investigationResult": "Investigation completed",
                    "closureReason": "Closed before assignment attempt",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(closureRequestBody)
                )
                .andExpect(
                        status().isOk()
                );

        Integer versionBeforeAssignment =
                getRecordVersion(
                        alertId
                );

        Integer historyBeforeAssignment =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        alertId
                );

        String assignmentRequestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Must not assign closed alert"
                }
                """.formatted(
                        ASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(assignmentRequestBody)
                )
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "BUSINESS_VALIDATION_ERROR"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Alert is not available for assignment"
                                )
                );

        UUID persistedAssignedTo =
                jdbcTemplate.queryForObject(
                        """
                        SELECT assigned_to
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        UUID.class,
                        alertId
                );

        Integer historyAfterAssignment =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        alertId
                );

        assertEquals(
                null,
                persistedAssignedTo
        );

        assertEquals(
                versionBeforeAssignment,
                getRecordVersion(
                        alertId
                )
        );

        assertEquals(
                historyBeforeAssignment,
                historyAfterAssignment
        );
    }

    @Test
    void shouldCloseAlertThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "IN_PROGRESS"
                );

        String requestBody =
                """
                {
                    "investigationResult": "No confirmed fraud after investigation",
                    "closureReason": "False positive",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("CLOSED")
                )
                .andExpect(
                        jsonPath("$.closureReason")
                                .value("False positive")
                )
                .andExpect(
                        jsonPath("$.closedAt")
                                .exists()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/alerts/{alertId}/history",
                                alertId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].actionType")
                                .value("CLOSURE")
                )
                .andExpect(
                        jsonPath("$[0].previousStatus")
                                .value("IN_PROGRESS")
                )
                .andExpect(
                        jsonPath("$[0].newStatus")
                                .value("CLOSED")
                )
                .andExpect(
                        jsonPath("$[0].changedBy")
                                .value(
                                        CHANGED_BY.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundWhenClosingUnknownAlertThroughApi()
            throws Exception {

        UUID unknownAlertId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                    "investigationResult": "Investigation completed",
                    "closureReason": "Closure attempt for unknown alert",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                unknownAlertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectIncompleteAlertClosureThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "IN_PROGRESS"
                );

        String requestBody =
                """
                {
                    "investigationResult": "",
                    "closureReason": "",
                    "closedBy": null
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                );

        String persistedStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT status
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        String.class,
                        alertId
                );

        assertEquals(
                "IN_PROGRESS",
                persistedStatus
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        alertId
                );

        assertEquals(
                0,
                historyCount
        );
    }

    @Test
    void shouldRejectClosingAlreadyClosedAlertThroughApi()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "IN_PROGRESS"
                );

        String firstRequestBody =
                """
                {
                    "investigationResult": "Investigation completed",
                    "closureReason": "False positive",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(firstRequestBody)
                )
                .andExpect(
                        status().isOk()
                );

        LocalDateTime originalClosedAt =
                jdbcTemplate.queryForObject(
                        """
                        SELECT closed_at
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        LocalDateTime.class,
                        alertId
                );

        String originalClosureReason =
                jdbcTemplate.queryForObject(
                        """
                        SELECT closure_reason
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        String.class,
                        alertId
                );

        Integer originalRecordVersion =
                getRecordVersion(
                        alertId
                );

        Integer historyCountBeforeRetry =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        alertId
                );

        String secondRequestBody =
                """
                {
                    "investigationResult": "Second closure attempt",
                    "closureReason": "Must not replace original closure",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(secondRequestBody)
                )
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "BUSINESS_VALIDATION_ERROR"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Alert is already closed"
                                )
                );

        String persistedStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT status
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        String.class,
                        alertId
                );

        LocalDateTime persistedClosedAt =
                jdbcTemplate.queryForObject(
                        """
                        SELECT closed_at
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        LocalDateTime.class,
                        alertId
                );

        String persistedClosureReason =
                jdbcTemplate.queryForObject(
                        """
                        SELECT closure_reason
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        String.class,
                        alertId
                );

        Integer historyCountAfterRetry =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        alertId
                );

        assertEquals(
                "CLOSED",
                persistedStatus
        );

        assertEquals(
                originalClosedAt,
                persistedClosedAt
        );

        assertEquals(
                originalClosureReason,
                persistedClosureReason
        );

        assertEquals(
                originalRecordVersion,
                getRecordVersion(
                        alertId
                )
        );

        assertEquals(
                historyCountBeforeRetry,
                historyCountAfterRetry
        );

        assertEquals(
                1,
                historyCountAfterRetry
        );
    }

    @Test
    void shouldIncrementRecordVersionWhenUpdatingStatus()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        assertEquals(
                1,
                getRecordVersion(
                        alertId
                )
        );

        String requestBody =
                """
                {
                    "status": "IN_PROGRESS",
                    "changedBy": "%s",
                    "changeReason": "Version test status update"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/status",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.recordVersion")
                                .value(2)
                );

        assertEquals(
                2,
                getRecordVersion(
                        alertId
                )
        );
    }

    @Test
    void shouldIncrementRecordVersionWhenAssigningAlert()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "NEW"
                );

        assertEquals(
                1,
                getRecordVersion(
                        alertId
                )
        );

        String requestBody =
                """
                {
                    "assignedTo": "%s",
                    "assignedTeam": "FRAUD_INVESTIGATION",
                    "changedBy": "%s",
                    "changeReason": "Version test assignment"
                }
                """.formatted(
                        ASSIGNED_USER_ID,
                        CHANGED_BY
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/alerts/{alertId}/assignment",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.recordVersion")
                                .value(2)
                );

        assertEquals(
                2,
                getRecordVersion(
                        alertId
                )
        );
    }

    @Test
    void shouldIncrementRecordVersionWhenClosingAlert()
            throws Exception {

        UUID alertId =
                insertAlert(
                        "IN_PROGRESS"
                );

        assertEquals(
                1,
                getRecordVersion(
                        alertId
                )
        );

        String requestBody =
                """
                {
                    "investigationResult": "No confirmed fraud after investigation",
                    "closureReason": "Version test closure",
                    "closedBy": "%s"
                }
                """.formatted(
                        CHANGED_BY
                );

        mockMvc.perform(
                        post(
                                "/api/v1/alerts/{alertId}/close",
                                alertId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.recordVersion")
                                .value(2)
                );

        assertEquals(
                2,
                getRecordVersion(
                        alertId
                )
        );
    }

    private UUID insertAlert(
            String status) {

        UUID alertId =
                UUID.randomUUID();

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
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                alertId,
                CUSTOMER_ID,
                TRANSACTION_ID,
                DECISION_ID,
                RISK_ASSESSMENT_ID,
                "FRAUD",
                "TRANSACTION",
                "HIGH",
                status,
                new BigDecimal("91.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return alertId;
    }

    private Integer getRecordVersion(
            UUID alertId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT record_version
                FROM alert.alert
                WHERE alert_id = ?
                """,
                Integer.class,
                alertId
        );
    }
}
