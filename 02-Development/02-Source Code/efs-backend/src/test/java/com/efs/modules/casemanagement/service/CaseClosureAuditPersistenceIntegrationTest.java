package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseResolutionRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CaseClosureAuditPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a036a036-a036-a036-a036-a036a036a036"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "b036b036-b036-b036-b036-b036b036b036"
            );

    @Autowired
    private CaseServiceInterface caseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void setUpPersistentIdentity() {

        insertOrganization();
        insertUser();
    }

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldRegisterCaseClosePermission() {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.permission
                        WHERE permission_code = 'case.close'
                          AND resource = 'case'
                          AND action = 'close'
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

        assertThrows(
                AccessDeniedException.class,
                () -> caseService.createCaseResolution(
                        caseId,
                        resolutionRequest(),
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
                () -> caseService.createCaseResolution(
                        caseId,
                        resolutionRequest(),
                        securityContext(
                                Set.of("case.close")
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
    void shouldPersistRejectedAuditWhenCaseIsAlreadyClosed() {

        UUID caseId =
                insertCase(
                        "CLOSED"
                );

        assertThrows(
                ValidationException.class,
                () -> caseService.createCaseResolution(
                        caseId,
                        resolutionRequest(),
                        securityContext(
                                Set.of("case.close")
                        )
                )
        );

        Integer resolutionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_resolution
                        WHERE case_id = ?
                        """,
                        Integer.class,
                        caseId
                );

        assertEquals(
                Integer.valueOf(0),
                resolutionCount
        );

        assertRejectedAudit(
                caseId,
                "CASE_ALREADY_CLOSED"
        );
    }

    @Test
    @Transactional
    void shouldPersistSuccessfulCaseClosureAudit() {

        UUID caseId =
                insertCase(
                        "OPEN"
                );

        CaseResolutionResponse resolution =
                caseService.createCaseResolution(
                        caseId,
                        resolutionRequest(),
                        securityContext(
                                Set.of("case.close")
                        )
                );

        assertNotNull(
                resolution.getResolutionId()
        );

        assertEquals(
                caseId,
                resolution.getCaseId()
        );

        String currentStatus =
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
                "CLOSED",
                currentStatus
        );

        Integer closedAtCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case
                        WHERE case_id = ?
                          AND closed_at IS NOT NULL
                        """,
                        Integer.class,
                        caseId
                );

        assertEquals(
                Integer.valueOf(1),
                closedAtCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_status_history
                        WHERE case_id = ?
                          AND previous_status = 'OPEN'
                          AND current_status = 'CLOSED'
                          AND changed_by = ?
                        """,
                        Integer.class,
                        caseId,
                        USER_ID
                );

        assertEquals(
                Integer.valueOf(1),
                historyCount
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_CLOSURE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'CLOSE'
                          AND source_component = 'CASE'
                          AND event_result = 'SUCCESS'
                          AND event_details ->> 'permissionCode' =
                              'case.close'
                          AND event_details ->> 'resolutionId' = ?
                          AND event_details ->> 'resolutionType' =
                              'CONFIRMED_FRAUD'
                          AND event_details ->> 'resolvedBy' = ?
                          AND event_details ->> 'approvedBy' = ?
                          AND event_details ->> 'previousStatus' =
                              'OPEN'
                          AND event_details ->> 'currentStatus' =
                              'CLOSED'
                          AND event_details ->> 'closedAt' IS NOT NULL
                        """,
                        Integer.class,
                        USER_ID,
                        caseId,
                        resolution.getResolutionId().toString(),
                        USER_ID.toString(),
                        USER_ID.toString()
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
        );
    }

    private void assertRejectedAudit(
            UUID caseId,
            String reason) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_CLOSURE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'CLOSE'
                          AND source_component = 'CASE'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' = ?
                          AND event_details ->> 'permissionCode' =
                              'case.close'
                        """,
                        Integer.class,
                        USER_ID,
                        caseId,
                        reason
                );

        assertEquals(
                Integer.valueOf(1),
                count
        );
    }

    private CaseResolutionRequest resolutionRequest() {

        CaseResolutionRequest request =
                new CaseResolutionRequest();

        request.setResolutionType(
                "CONFIRMED_FRAUD"
        );

        request.setResolutionSummary(
                "Investigation completed with documented resolution"
        );

        request.setResolvedBy(
                USER_ID
        );

        request.setApprovedBy(
                USER_ID
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

        String caseNumber =
                "UC036-AUDIT-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

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
                caseNumber,
                ORGANIZATION_ID,
                "FRAUD_INVESTIGATION",
                "TRANSACTION",
                "MEDIUM",
                "NORMAL",
                currentStatus
        );

        return caseId;
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
                "EFS-CASE-CLOSURE-AUDIT-ORG",
                "EFS Case Closure Audit Organization",
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
                "efs.case.closure.audit",
                "EFS Case Closure Audit User",
                "efs.case.closure.audit@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    @Transactional
    void shouldPersistFailureAuditWhenResolutionPersistenceFails() {

        UUID caseId =
                insertCase(
                        "OPEN"
                );

        String failureMessage =
                "UC036 forced case closure failure";

        CaseResolutionRequest request =
                org.mockito.Mockito.mock(
                        CaseResolutionRequest.class
                );

        org.mockito.Mockito.when(
                request.getResolutionType()
        ).thenThrow(
                new IllegalStateException(
                        failureMessage
                )
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> caseService.createCaseResolution(
                                caseId,
                                request,
                                securityContext(
                                        Set.of("case.close")
                                )
                        )
                );

        assertEquals(
                failureMessage,
                exception.getMessage()
        );

        assertEquals(
                "OPEN",
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_status
                        FROM case_management.case
                        WHERE case_id = ?
                        """,
                        String.class,
                        caseId
                )
        );

        Integer resolutionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_resolution
                        WHERE case_id = ?
                        """,
                        Integer.class,
                        caseId
                );

        assertEquals(
                Integer.valueOf(0),
                resolutionCount
        );

        Integer failureAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_CLOSURE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'CLOSE'
                          AND source_component = 'CASE'
                          AND event_result = 'FAILURE'
                          AND event_details ->> 'reason' =
                              'CASE_CLOSURE_FAILED'
                          AND event_details ->> 'permissionCode' =
                              'case.close'
                          AND event_details ->> 'errorType' =
                              'java.lang.IllegalStateException'
                          AND event_details ->> 'errorMessage' = ?
                        """,
                        Integer.class,
                        USER_ID,
                        caseId,
                        failureMessage
                );

        assertEquals(
                Integer.valueOf(1),
                failureAuditCount
        );

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'CASE_CLOSURE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND event_result = 'SUCCESS'
                        """,
                        Integer.class,
                        caseId
                );

        assertEquals(
                Integer.valueOf(0),
                successAuditCount
        );
    }}
