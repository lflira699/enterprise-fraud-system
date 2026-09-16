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
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
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

    @BeforeEach
    void setUp() {

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

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
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
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(1),
                successAuditCount
        );
    }

    @Test
    void shouldReturnEmptyPageAndPersistSuccessAudit() {

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

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
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
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(1),
                successAuditCount
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenAuditViewPermissionIsMissing() {

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

        Integer rejectedAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
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
                        USER_A
                );

        assertEquals(
                Integer.valueOf(1),
                rejectedAuditCount
        );
    }

    @Test
    void shouldPersistRejectedAuditForInvalidCriteria() {

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

        Integer rejectedAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
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
                        ORGANIZATION_A
                );

        assertEquals(
                Integer.valueOf(1),
                rejectedAuditCount
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
                """,
                auditEventId,
                organizationId,
                userId,
                auditEventId
        );
    }
}