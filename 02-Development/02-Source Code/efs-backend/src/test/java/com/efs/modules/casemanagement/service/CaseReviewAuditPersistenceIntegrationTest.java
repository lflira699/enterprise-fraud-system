package com.efs.modules.casemanagement.service;

import com.efs.shared.exception.ResourceNotFoundException;
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
class CaseReviewAuditPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "c034c034-c034-c034-c034-c034c034c034"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "d034d034-d034-d034-d034-d034d034d034"
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

        UUID caseId =
                UUID.randomUUID();

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
                () -> caseService.getCaseById(
                        caseId,
                        securityContext
                )
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_REVIEW'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'REVIEW'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'MISSING_PERMISSION'
                          AND event_details ->> 'permissionCode' =
                              'case.view'
                        """,
                        Integer.class,
                        USER_ID,
                        caseId
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenCaseDoesNotExist() {

        UUID caseId =
                UUID.randomUUID();

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
                ResourceNotFoundException.class,
                () -> caseService.getCaseById(
                        caseId,
                        securityContext
                )
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_REVIEW'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'REVIEW'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'CASE_NOT_FOUND'
                          AND event_details ->> 'permissionCode' =
                              'case.view'
                        """,
                        Integer.class,
                        USER_ID,
                        caseId
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
                "EFS-CASE-REVIEW-AUDIT-TEST-ORG",
                "EFS Case Review Audit Test Organization",
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
                "efs.case.review.audit.test",
                "EFS Case Review Audit Test User",
                "efs.case.review.audit.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}