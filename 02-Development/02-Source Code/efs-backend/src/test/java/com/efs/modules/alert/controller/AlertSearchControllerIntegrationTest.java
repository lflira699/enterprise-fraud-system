package com.efs.modules.alert.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlertSearchControllerIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "81818181-8181-8181-8181-818181818181"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "82828282-8282-8282-8282-828282828282"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "83838383-8383-8383-8383-838383838383"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "84848484-8484-8484-8484-848484848484"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "85858585-8585-8585-8585-858585858585"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "86868686-8686-8686-8686-868686868686"
            );

    private static final UUID ASSIGNED_USER_ID =
            UUID.fromString(
                    "87878787-8787-8787-8787-878787878787"
            );

    private static final UUID SCENARIO_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID CASE_ID =
            UUID.fromString(
                    "89898989-8989-8989-8989-898989898989"
            );

    private static final UUID CASE_ALERT_ID =
            UUID.fromString(
                    "90909090-9090-9090-9090-909090909090"
            );

    private static final String SCENARIO_CODE =
            "EFS-ALERT-SEARCH-SCENARIO";

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
                "EFS-ALERT-SEARCH-ORG",
                "EFS Alert Search Organization",
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
                "efs.alert.search.user",
                "EFS Alert Search User",
                "efs.alert.search@example.com",
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
                "EFS-ALERT-SEARCH-CUSTOMER",
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
                "EFS-ALERT-SEARCH-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("1000.00"),
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
    void shouldReturnApprovedPaginationDefaults()
            throws Exception {

        UUID olderAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(2)
                );

        UUID newerAlertId =
                insertAlert(
                        "IN_PROGRESS",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(1)
                );

        mockMvc.perform(
                        get("/api/v1/alerts")
                )
                .andExpect(status().isOk())
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
                                .value(2)
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
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        newerAlertId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[1].alertId")
                                .value(
                                        olderAlertId.toString()
                                )
                );
    }

    @Test
    void shouldPaginateAndSortAlerts()
            throws Exception {

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(3)
        );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(2)
        );

        UUID thirdAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(1)
                );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "page",
                                        "1"
                                )
                                .param(
                                        "size",
                                        "2"
                                )
                                .param(
                                        "sort",
                                        "generatedAt"
                                )
                                .param(
                                        "direction",
                                        "ASC"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.page")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.hasNext")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.hasPrevious")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        thirdAlertId.toString()
                                )
                );
    }

    @Test
    void shouldCombineStatusPriorityAndCustomerFilters()
            throws Exception {

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(3)
                );

        insertAlert(
                "NEW",
                "LOW",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(2)
        );

        insertAlert(
                "IN_PROGRESS",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "status",
                                        "NEW"
                                )
                                .param(
                                        "priority",
                                        "HIGH"
                                )
                                .param(
                                        "customerId",
                                        CUSTOMER_ID.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("NEW")
                )
                .andExpect(
                        jsonPath("$.content[0].priority")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.content[0].customerId")
                                .value(
                                        CUSTOMER_ID.toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByRiskLevel()
            throws Exception {

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(2)
                );

        insertAlert(
                "NEW",
                "HIGH",
                null,
                null,
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "riskLevel",
                                        "ALTO"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].riskAssessmentId"
                        )
                                .value(
                                        RISK_ASSESSMENT_ID
                                                .toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByAssignedTo()
            throws Exception {

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        ASSIGNED_USER_ID,
                        LocalDateTime.now()
                                .minusMinutes(2)
                );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "assignedTo",
                                        ASSIGNED_USER_ID
                                                .toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].assignedTo")
                                .value(
                                        ASSIGNED_USER_ID
                                                .toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByCreatedRange()
            throws Exception {

        LocalDateTime rangeStart =
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                );

        LocalDateTime rangeEnd =
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        12,
                        0
                );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        9,
                        0
                )
        );

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.of(
                                2026,
                                9,
                                1,
                                11,
                                0
                        )
                );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        13,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "createdFrom",
                                        rangeStart.toString()
                                )
                                .param(
                                        "createdTo",
                                        rangeEnd.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByScenarioCode()
            throws Exception {

        insertScenario();

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(2)
                );

        jdbcTemplate.update(
                """
                UPDATE alert.alert
                SET scenario_id = ?
                WHERE alert_id = ?
                """,
                SCENARIO_ID,
                matchingAlertId
        );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "scenarioCode",
                                        SCENARIO_CODE
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].scenarioId")
                                .value(
                                        SCENARIO_ID.toString()
                                )
                );
    }

    @Test
    void shouldFilterAlertsByCaseId()
            throws Exception {

        UUID matchingAlertId =
                insertAlert(
                        "NEW",
                        "HIGH",
                        RISK_ASSESSMENT_ID,
                        null,
                        LocalDateTime.now()
                                .minusMinutes(2)
                );

        insertAlert(
                "NEW",
                "HIGH",
                RISK_ASSESSMENT_ID,
                null,
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        insertCase();

        insertCaseAlert(
                matchingAlertId
        );

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "caseId",
                                        CASE_ID.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].alertId")
                                .value(
                                        matchingAlertId.toString()
                                )
                );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "size",
                                        "101"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectUnsupportedSortField()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "sort",
                                        "status"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectInvalidCreatedRange()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "createdFrom",
                                        "2026-09-01T12:00:00"
                                )
                                .param(
                                        "createdTo",
                                        "2026-09-01T10:00:00"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectUnsupportedStatus()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/alerts")
                                .param(
                                        "status",
                                        "NOT_A_VALID_STATUS"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    private UUID insertAlert(
            String status,
            String priority,
            UUID riskAssessmentId,
            UUID assignedTo,
            LocalDateTime timestamp) {

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
                    assigned_to,
                    generated_at,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                alertId,
                CUSTOMER_ID,
                TRANSACTION_ID,
                DECISION_ID,
                riskAssessmentId,
                "FRAUD",
                "TRANSACTION",
                priority,
                status,
                new BigDecimal("91.00"),
                assignedTo,
                timestamp,
                timestamp,
                timestamp
        );

        return alertId;
    }

    private void insertScenario() {

        LocalDateTime now =
                LocalDateTime.now();

        jdbcTemplate.update(
                """
                INSERT INTO detection.scenario (
                    scenario_id,
                    scenario_code,
                    scenario_name,
                    objective,
                    category,
                    status,
                    version,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                SCENARIO_ID,
                SCENARIO_CODE,
                "EFS Alert Search Scenario",
                "Validate alert search by scenario code",
                "FRAUD",
                "ACTIVE",
                1,
                now,
                now
        );
    }

    private void insertCase() {

        LocalDateTime now =
                LocalDateTime.now();

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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CASE_ID,
                "EFS-ALERT-SEARCH-CASE",
                ORGANIZATION_ID,
                TRANSACTION_ID,
                CUSTOMER_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "HIGH",
                "HIGH",
                "OPEN",
                now,
                now
        );
    }

    private void insertCaseAlert(
            UUID sourceAlertId) {

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case_alert (
                    alert_id,
                    case_id,
                    transaction_id,
                    alert_type,
                    alert_source,
                    risk_score,
                    severity,
                    generated_at,
                    source_alert_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CASE_ALERT_ID,
                CASE_ID,
                TRANSACTION_ID,
                "FRAUD",
                "ALERT",
                new BigDecimal("91.00"),
                "HIGH",
                LocalDateTime.now(),
                sourceAlertId
        );
    }
}