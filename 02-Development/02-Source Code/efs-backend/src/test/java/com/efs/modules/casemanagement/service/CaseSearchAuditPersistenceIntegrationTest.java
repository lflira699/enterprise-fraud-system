package com.efs.modules.casemanagement.service;

import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CaseSearchAuditPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "c033c033-c033-c033-c033-c033c033c033"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "d033d033-d033-d033-d033-d033d033d033"
            );

    @Autowired
    private CaseServiceInterface caseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldPersistRejectedAuditWhenCaseViewPermissionIsMissing() {

        String auditMarker =
                "UC033-MISSING-"
                        + UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        assertThrows(
                AccessDeniedException.class,
                () -> caseService.searchCases(
                        null,
                        null,
                        null,
                        auditMarker,
                        0,
                        20,
                        "createdAt",
                        "DESC",
                        securityContext
                )
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'INVESTIGATION_SEARCH'
                          AND entity_type = 'CASE'
                          AND entity_id IS NULL
                          AND action = 'SEARCH'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'MISSING_PERMISSION'
                          AND event_details
                              -> 'criteria'
                              ->> 'assignedTeam' = ?
                        """,
                        Integer.class,
                        USER_ID,
                        auditMarker
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
        );
    }

    @Test
    void shouldPersistRejectedAuditForInvalidSearchCriteria() {

        String auditMarker =
                "UC033-INVALID-"
                        + UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of("case.view"),
                        Set.of()
                );

        assertThrows(
                RequestValidationException.class,
                () -> caseService.searchCases(
                        null,
                        null,
                        null,
                        auditMarker,
                        -1,
                        20,
                        "createdAt",
                        "DESC",
                        securityContext
                )
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'INVESTIGATION_SEARCH'
                          AND entity_type = 'CASE'
                          AND entity_id IS NULL
                          AND action = 'SEARCH'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'INVALID_SEARCH_CRITERIA'
                          AND event_details
                              -> 'criteria'
                              ->> 'assignedTeam' = ?
                        """,
                        Integer.class,
                        USER_ID,
                        auditMarker
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
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
                "EFS-CASE-SEARCH-AUDIT-TEST-ORG",
                "EFS Case Search Audit Test Organization",
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
                "efs.case.search.audit.test",
                "EFS Case Search Audit Test User",
                "efs.case.search.audit.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}