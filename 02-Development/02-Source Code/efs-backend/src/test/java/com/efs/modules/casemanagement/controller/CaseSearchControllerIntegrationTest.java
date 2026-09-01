package com.efs.modules.casemanagement.controller;

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
class CaseSearchControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "71717171-7171-7171-7171-717171717171"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "72727272-7272-7272-7272-727272727272"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "73737373-7373-7373-7373-737373737373"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "74747474-7474-7474-7474-747474747474"
            );

    private static final UUID ASSIGNED_USER =
            UUID.fromString(
                    "75757575-7575-7575-7575-757575757575"
            );

    private static final UUID OTHER_ASSIGNED_USER =
            UUID.fromString(
                    "76767676-7676-7676-7676-767676767676"
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
                "EFS-CASE-SEARCH-ORG",
                "EFS Case Search Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        insertUser(
                ASSIGNED_USER,
                "efs.case.search.assigned",
                "EFS Case Search Assigned",
                "efs.case.search.assigned@example.com"
        );

        insertUser(
                OTHER_ASSIGNED_USER,
                "efs.case.search.other",
                "EFS Case Search Other",
                "efs.case.search.other@example.com"
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
                "EFS-CASE-SEARCH-CUSTOMER",
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
                "EFS-CASE-SEARCH-TRANSACTION",
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
    }

    @Test
    void shouldUseDefaultPaginationAndSort()
            throws Exception {

        insertCase(
                "CASE-SEARCH-DEFAULT-001",
                "OPEN",
                "NORMAL",
                null,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        9,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-DEFAULT-002",
                "OPEN",
                "HIGH",
                null,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/cases")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].caseNumber"
                        )
                                .value(
                                        "CASE-SEARCH-DEFAULT-002"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[1].caseNumber"
                        )
                                .value(
                                        "CASE-SEARCH-DEFAULT-001"
                                )
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
                );
    }

    @Test
    void shouldPaginateAndSortCasesByCreatedAt()
            throws Exception {

        insertCase(
                "CASE-SEARCH-PAGE-001",
                "OPEN",
                "NORMAL",
                null,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        8,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-PAGE-002",
                "OPEN",
                "NORMAL",
                null,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        9,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-PAGE-003",
                "OPEN",
                "NORMAL",
                null,
                null,
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "page",
                                        "1"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                                .param(
                                        "sort",
                                        "createdAt"
                                )
                                .param(
                                        "direction",
                                        "ASC"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].caseNumber"
                        )
                                .value(
                                        "CASE-SEARCH-PAGE-002"
                                )
                )
                .andExpect(
                        jsonPath("$.page")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.hasNext")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.hasPrevious")
                                .value(true)
                );
    }

    @Test
    void shouldCombineCanonicalCaseFilters()
            throws Exception {

        UUID matchingCaseId =
                insertCase(
                        "CASE-SEARCH-FILTER-001",
                        "OPEN",
                        "HIGH",
                        ASSIGNED_USER,
                        "FRAUD_INVESTIGATION",
                        LocalDateTime.of(
                                2026,
                                9,
                                1,
                                11,
                                0
                        )
                );

        insertCase(
                "CASE-SEARCH-FILTER-002",
                "OPEN",
                "NORMAL",
                ASSIGNED_USER,
                "FRAUD_INVESTIGATION",
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        10,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-FILTER-003",
                "OPEN",
                "HIGH",
                OTHER_ASSIGNED_USER,
                "FRAUD_INVESTIGATION",
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        9,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-FILTER-004",
                "OPEN",
                "HIGH",
                ASSIGNED_USER,
                "OTHER_TEAM",
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        8,
                        0
                )
        );

        insertCase(
                "CASE-SEARCH-FILTER-005",
                "CLOSED",
                "HIGH",
                ASSIGNED_USER,
                "FRAUD_INVESTIGATION",
                LocalDateTime.of(
                        2026,
                        9,
                        1,
                        7,
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "status",
                                        "OPEN"
                                )
                                .param(
                                        "priority",
                                        "HIGH"
                                )
                                .param(
                                        "assignedUser",
                                        ASSIGNED_USER.toString()
                                )
                                .param(
                                        "assignedTeam",
                                        "FRAUD_INVESTIGATION"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].caseId"
                        )
                                .value(
                                        matchingCaseId.toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].caseNumber"
                        )
                                .value(
                                        "CASE-SEARCH-FILTER-001"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].currentStatus"
                        )
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].priority"
                        )
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].assignedUser"
                        )
                                .value(
                                        ASSIGNED_USER.toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].assignedTeam"
                        )
                                .value(
                                        "FRAUD_INVESTIGATION"
                                )
                );
    }

    @Test
    void shouldRejectNegativePage()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "page",
                                        "-1"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "size",
                                        "101"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectUnsupportedSortField()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "sort",
                                        "priority"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectUnsupportedSortDirection()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/cases")
                                .param(
                                        "direction",
                                        "SIDEWAYS"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    private void insertUser(
            UUID userId,
            String username,
            String fullName,
            String email) {

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
                userId,
                ORGANIZATION_ID,
                username,
                fullName,
                email,
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private UUID insertCase(
            String caseNumber,
            String currentStatus,
            String priority,
            UUID assignedUser,
            String assignedTeam,
            LocalDateTime createdAt) {

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
                    assigned_team,
                    assigned_user,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                caseId,
                caseNumber,
                ORGANIZATION_ID,
                TRANSACTION_ID,
                CUSTOMER_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "MEDIUM",
                priority,
                currentStatus,
                assignedTeam,
                assignedUser,
                createdAt,
                createdAt
        );

        return caseId;
    }
}