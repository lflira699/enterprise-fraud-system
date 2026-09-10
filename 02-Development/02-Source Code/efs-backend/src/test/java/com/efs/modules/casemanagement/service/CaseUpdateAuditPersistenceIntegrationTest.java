package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseUpdateRequest;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CaseUpdateAuditPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a035a035-a035-a035-a035-a035a035a035"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "b035b035-b035-b035-b035-b035b035b035"
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
    void shouldRegisterCaseUpdatePermission() {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.permission
                        WHERE permission_code = 'case.update'
                          AND resource = 'case'
                          AND action = 'update'
                        """,
                        Integer.class
                );

        assertEquals(
                Integer.valueOf(1),
                count
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenPermissionIsMissing() {

        UUID caseId =
                UUID.randomUUID();

        CaseUpdateRequest request =
                updatePriorityRequest();

        assertThrows(
                AccessDeniedException.class,
                () -> caseService.updateCase(
                        caseId,
                        request,
                        securityContext(
                                Set.of()
                        )
                )
        );

        assertRejectedAudit(
                caseId,
                "MISSING_PERMISSION"
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenCaseDoesNotExist() {

        UUID caseId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> caseService.updateCase(
                        caseId,
                        updatePriorityRequest(),
                        securityContext(
                                Set.of("case.update")
                        )
                )
        );

        assertRejectedAudit(
                caseId,
                "CASE_NOT_FOUND"
        );
    }

    @Test
    @Transactional
    void shouldPersistRejectedAuditWhenCaseIsClosed() {

        UUID caseId =
                insertCase(
                        "CLOSED"
                );

        assertThrows(
                ValidationException.class,
                () -> caseService.updateCase(
                        caseId,
                        updatePriorityRequest(),
                        securityContext(
                                Set.of("case.update")
                        )
                )
        );

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );

        assertRejectedAudit(
                caseId,
                "CASE_CLOSED"
        );
    }

    @Test
    @Transactional
    void shouldPersistRejectedAuditForEmptyUpdate() {

        UUID caseId =
                insertCase(
                        "OPEN"
                );

        CaseUpdateRequest request =
                new CaseUpdateRequest();

        assertThrows(
                RequestValidationException.class,
                () -> caseService.updateCase(
                        caseId,
                        request,
                        securityContext(
                                Set.of("case.update")
                        )
                )
        );

        assertEquals(
                "NORMAL",
                getPriority(
                        caseId
                )
        );

        assertRejectedAudit(
                caseId,
                "INVALID_UPDATE_REQUEST"
        );
    }

    private void assertRejectedAudit(
            UUID caseId,
            String reason) {

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_UPDATE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'UPDATE'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' = ?
                          AND event_details ->> 'permissionCode' =
                              'case.update'
                        """,
                        Integer.class,
                        USER_ID,
                        caseId,
                        reason
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
        );
    }

    private CaseUpdateRequest updatePriorityRequest() {

        CaseUpdateRequest request =
                new CaseUpdateRequest();

        request.setPriority(
                "HIGH"
        );

        return request;
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
            String currentStatus) {

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
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                caseId,
                "UC035-AUDIT-"
                        + caseId.toString()
                                .substring(0, 8),
                ORGANIZATION_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "MEDIUM",
                "NORMAL",
                currentStatus
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
                "EFS-UC035-AUDIT",
                "EFS UC035 Audit Test Organization",
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
                "efs.uc035.audit",
                "EFS UC035 Audit Test User",
                "efs.uc035.audit@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}
