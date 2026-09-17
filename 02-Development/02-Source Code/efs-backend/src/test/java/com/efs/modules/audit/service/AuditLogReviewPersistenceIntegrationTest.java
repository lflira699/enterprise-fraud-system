package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuditLogReviewPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_A =
            UUID.fromString(
                    "a043a043-a043-a043-a043-a043a043a043"
            );

    private static final UUID ORGANIZATION_B =
            UUID.fromString(
                    "b043b043-b043-b043-b043-b043b043b043"
            );

    private static final UUID USER_A =
            UUID.fromString(
                    "c043c043-c043-c043-c043-c043c043c043"
            );

    private static final UUID USER_B =
            UUID.fromString(
                    "d043d043-d043-d043-d043-d043d043d043"
            );

    private static final UUID SOURCE_EVENT_A =
            UUID.fromString(
                    "f043f043-f043-f043-f043-f043f043f043"
            );

    private static final UUID SOURCE_EVENT_B =
            UUID.fromString(
                    "10431043-1043-1043-1043-104310431043"
            );

    @Autowired
    private AuditLogReviewServiceInterface
            auditLogReviewService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Timestamp testStartedAt;

    @BeforeEach
    void setUp() {

        testStartedAt =
                jdbcTemplate.queryForObject(
                        "SELECT clock_timestamp()",
                        Timestamp.class
                );

        insertOrganization(
                ORGANIZATION_A,
                "EFS-UC043-ORG-A"
        );

        insertOrganization(
                ORGANIZATION_B,
                "EFS-UC043-ORG-B"
        );

        insertUser(
                USER_A,
                ORGANIZATION_A,
                "efs.uc043.user.a",
                "efs.uc043.user.a@example.com"
        );

        insertUser(
                USER_B,
                ORGANIZATION_B,
                "efs.uc043.user.b",
                "efs.uc043.user.b@example.com"
        );

        insertSourceAuditEvent(
                SOURCE_EVENT_A,
                ORGANIZATION_A,
                USER_A
        );

        insertSourceAuditEvent(
                SOURCE_EVENT_B,
                ORGANIZATION_B,
                USER_B
        );
    }

    @Test
    void shouldRestrictReviewToAuthorizedOrganizationAndPersistSuccessAudit() {

        Integer successAuditCountBefore =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND tenant_id IS NULL
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'SUCCESS'
                          AND event_details ->> 'permissionCode' =
                              'audit.view'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'entityType' =
                              'CASE'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'action' =
                              'REVIEW'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        PageResponse<AuditEventResponse> response =
                auditLogReviewService
                        .searchAuditEvents(
                                null,
                                null,
                                null,
                                "CASE",
                                null,
                                "REVIEW",
                                0,
                                25,
                                "eventTimestamp",
                                "DESC",
                                authorizedContext()
                        );

        assertEquals(
                1,
                response.getContent().size()
        );

        assertEquals(
                SOURCE_EVENT_A,
                response.getContent()
                        .get(0)
                        .getAuditEventId()
        );

        assertEquals(
                1L,
                response.getTotalElements()
        );

        Integer successAuditCountAfter =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND tenant_id IS NULL
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'SUCCESS'
                          AND event_details ->> 'permissionCode' =
                              'audit.view'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'entityType' =
                              'CASE'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'action' =
                              'REVIEW'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(successAuditCountBefore + 1),
                successAuditCountAfter
        );
    }

    @Test
    void shouldReturnEmptyPageAndPersistSuccessAudit() {

        Integer successAuditCountBefore =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'SUCCESS'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'entityType' =
                              'NON_EXISTENT_ENTITY_TYPE'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        PageResponse<AuditEventResponse> response =
                auditLogReviewService
                        .searchAuditEvents(
                                null,
                                null,
                                null,
                                "NON_EXISTENT_ENTITY_TYPE",
                                null,
                                null,
                                0,
                                25,
                                "eventTimestamp",
                                "DESC",
                                authorizedContext()
                        );

        assertTrue(
                response.getContent().isEmpty()
        );

        assertEquals(
                0L,
                response.getTotalElements()
        );

        Integer successAuditCountAfter =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'SUCCESS'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'entityType' =
                              'NON_EXISTENT_ENTITY_TYPE'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(successAuditCountBefore + 1),
                successAuditCountAfter
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenAuditViewPermissionIsMissing() {

        Integer rejectedAuditCountBefore =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'REJECTED'
                          AND organization_id IS NULL
                          AND tenant_id IS NULL
                          AND session_id IS NULL
                          AND event_details ->> 'reason' =
                              'MISSING_PERMISSION'
                          AND event_details ->> 'permissionCode' =
                              'audit.view'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt
                );

        SecurityContext securityContext =
                new SecurityContext(
                        USER_A,
                        null,
                        null,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        auditLogReviewService
                                .searchAuditEvents(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0,
                                        25,
                                        "eventTimestamp",
                                        "DESC",
                                        securityContext
                                )
        );

        Integer rejectedAuditCountAfter =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'REJECTED'
                          AND organization_id IS NULL
                          AND tenant_id IS NULL
                          AND session_id IS NULL
                          AND event_details ->> 'reason' =
                              'MISSING_PERMISSION'
                          AND event_details ->> 'permissionCode' =
                              'audit.view'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt
                );

        assertEquals(
                Integer.valueOf(rejectedAuditCountBefore + 1),
                rejectedAuditCountAfter
        );
    }

    @Test
    void shouldPersistRejectedAuditForInvalidCriteria() {

        Integer rejectedAuditCountBefore =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'INVALID_AUDIT_LOG_SEARCH_CRITERIA'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'size' =
                              '101'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        assertThrows(
                RequestValidationException.class,
                () ->
                        auditLogReviewService
                                .searchAuditEvents(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0,
                                        101,
                                        "eventTimestamp",
                                        "DESC",
                                        authorizedContext()
                                )
        );

        Integer rejectedAuditCountAfter =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_timestamp >= ?
                          AND organization_id = ?
                          AND event_type = 'AUDIT_LOG_REVIEW'
                          AND entity_type = 'AUDIT_EVENT'
                          AND entity_id IS NULL
                          AND action = 'REVIEW'
                          AND source_component = 'AUDIT'
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' =
                              'INVALID_AUDIT_LOG_SEARCH_CRITERIA'
                          AND event_details
                                  -> 'criteria'
                                  ->> 'size' =
                              '101'
                        """,
                        Integer.class,
                        USER_A,
                        testStartedAt,
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(rejectedAuditCountBefore + 1),
                rejectedAuditCountAfter
        );
    }

    private SecurityContext authorizedContext() {

        return new SecurityContext(
                USER_A,
                        null,
                        null,
                Set.of(),
                Set.of(
                        "audit.view"
                ),
                Set.of()
        );
    }

    private void insertOrganization(
            UUID organizationId,
            String organizationCode) {

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
                organizationId,
                organizationCode,
                organizationCode,
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser(
            UUID userId,
            UUID organizationId,
            String username,
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
                ON CONFLICT DO NOTHING
                """,
                userId,
                organizationId,
                username,
                username,
                email,
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertSourceAuditEvent(
            UUID auditEventId,
            UUID organizationId,
            UUID userId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
                    organization_id,
                    user_id,
                    event_type,
                    entity_type,
                    entity_id,
                    action,
                    source_component,
                    event_result,
                    event_details
                )
                VALUES (
                    ?,
                    CURRENT_TIMESTAMP,
                    ?,
                    ?,
                    'UC043_SOURCE_EVENT',
                    'CASE',
                    ?,
                    'REVIEW',
                    'CASE',
                    'SUCCESS',
                    CAST('{"source":"UC-043"}' AS jsonb)
                )
                ON CONFLICT DO NOTHING
                """,
                auditEventId,
                organizationId,
                userId,
                auditEventId
        );
    }
}