package com.efs.modules.casemanagement.controller;

import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CaseUpdateControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "c035c035-c035-c035-c035-c035c035c035"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "d035d035-d035-d035-d035-d035d035d035"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext(
                        Set.of("case.update")
                )
        );
    }

    @Test
    void shouldUpdatePriorityAndDueDateThroughApi()
            throws Exception {

        UUID caseId =
                insertCase(
                        "UC035-API-001",
                        "OPEN",
                        null
                );

        LocalDateTime dueDate =
                LocalDateTime.of(
                        2030,
                        1,
                        15,
                        12,
                        30
                );

        String requestBody =
                """
                {
                    "priority": "HIGH",
                    "dueDate": "2030-01-15T12:30:00"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.caseId")
                                .value(caseId.toString())
                )
                .andExpect(
                        jsonPath("$.caseNumber")
                                .value("UC035-API-001")
                )
                .andExpect(
                        jsonPath("$.priority")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.currentStatus")
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath("$.dueDate")
                                .exists()
                );

        assertEquals(
                "HIGH",
                jdbcTemplate.queryForObject(
                        """
                        SELECT priority
                        FROM case_management.case
                        WHERE case_id = ?
                        """,
                        String.class,
                        caseId
                )
        );

        assertEquals(
                dueDate,
                jdbcTemplate.queryForObject(
                        """
                        SELECT due_date
                        FROM case_management.case
                        WHERE case_id = ?
                        """,
                        LocalDateTime.class,
                        caseId
                )
        );

        UUID auditEventId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT audit_event_id
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_UPDATE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'UPDATE'
                          AND source_component = 'CASE'
                          AND event_result = 'SUCCESS'
                        """,
                        UUID.class,
                        USER_ID,
                        caseId
                );

        assertEquals(
                "NORMAL",
                jdbcTemplate.queryForObject(
                        """
                        SELECT previous_value ->> 'priority'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND operation = 'UPDATE'
                        """,
                        String.class,
                        auditEventId,
                        caseId
                )
        );

        assertEquals(
                "HIGH",
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_value ->> 'priority'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND operation = 'UPDATE'
                        """,
                        String.class,
                        auditEventId,
                        caseId
                )
        );
    }

    @Test
    void shouldPreserveDueDateWhenOnlyPriorityIsUpdated()
            throws Exception {

        LocalDateTime originalDueDate =
                LocalDateTime.of(
                        2031,
                        5,
                        20,
                        9,
                        45
                );

        UUID caseId =
                insertCase(
                        "UC035-API-002",
                        "OPEN",
                        originalDueDate
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "priority": "HIGH"
                                        }
                                        """
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.priority")
                                .value("HIGH")
                );

        assertEquals(
                originalDueDate,
                jdbcTemplate.queryForObject(
                        """
                        SELECT due_date
                        FROM case_management.case
                        WHERE case_id = ?
                        """,
                        LocalDateTime.class,
                        caseId
                )
        );
    }

    @Test
    void shouldRejectCaseUpdateWithoutPermission()
            throws Exception {

        UUID caseId =
                insertCase(
                        "UC035-API-003",
                        "OPEN",
                        null
                );

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext(
                        Set.of()
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "priority": "HIGH"
                                        }
                                        """
                                )
                )
                .andExpect(status().isForbidden());

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );
    }

    @Test
    void shouldRejectUpdateForClosedCase()
            throws Exception {

        UUID caseId =
                insertCase(
                        "UC035-API-004",
                        "CLOSED",
                        null
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "priority": "HIGH"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status()
                                .isUnprocessableEntity()
                );

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownCase()
            throws Exception {

        UUID caseId =
                UUID.randomUUID();

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "priority": "HIGH"
                                        }
                                        """
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectEmptyCaseUpdate()
            throws Exception {

        UUID caseId =
                insertCase(
                        "UC035-API-005",
                        "OPEN",
                        null
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );
    }

    @Test
    void shouldRejectBlankPriority()
            throws Exception {

        UUID caseId =
                insertCase(
                        "UC035-API-006",
                        "OPEN",
                        null
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/cases/{caseId}",
                                caseId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "priority": "   "
                                        }
                                        """
                                )
                )
                .andExpect(status().isBadRequest());

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );
    }

    private SecurityContext securityContext(
            Set<String> permissions) {

        return new SecurityContext(
                USER_ID,
                null,
                null,
                Set.of(),
                permissions,
                Set.of()
        );
    }

    private UUID insertCase(
            String caseNumber,
            String currentStatus,
            LocalDateTime dueDate) {

        UUID caseId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case (
                    case_id,
                    case_number,
                    organization_id,
                    case_type,
                    category,
                    severity,
                    priority,
                    current_status,
                    created_at,
                    updated_at,
                    due_date
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    ?
                )
                """,
                caseId,
                caseNumber,
                ORGANIZATION_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "MEDIUM",
                "NORMAL",
                currentStatus,
                dueDate
        );

        return caseId;
    }

    private String getPriority(
            UUID caseId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT priority
                FROM case_management.case
                WHERE case_id = ?
                """,
                String.class,
                caseId
        );
    }

    private void insertOrganization() {

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
                ON CONFLICT DO NOTHING
                """,
                ORGANIZATION_ID,
                "EFS-UC035-CONTROLLER",
                "EFS UC035 Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

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
                ON CONFLICT DO NOTHING
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.uc035.controller",
                "EFS UC035 Controller Test User",
                "efs.uc035.controller@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}
