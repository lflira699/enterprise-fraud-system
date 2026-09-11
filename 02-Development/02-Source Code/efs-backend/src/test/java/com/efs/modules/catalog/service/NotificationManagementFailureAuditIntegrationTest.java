package com.efs.modules.catalog.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateUpdateRequest;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class NotificationManagementFailureAuditIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "e037e037-e037-e037-e037-e037e037e037"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "f037f037-f037-f037-f037-f037f037f037"
            );

    @Autowired
    private NotificationTemplateServiceInterface
            notificationTemplateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private AuditEntityChangeServiceInterface
            auditEntityChangeService;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldRollbackNotificationUpdateAndPersistFailureAudit() {

        UUID notificationTemplateId =
                insertNotificationTemplate();

        String failureMessage =
                "UC037 forced audit entity change failure";

        doThrow(
                new IllegalStateException(
                        failureMessage
                )
        ).when(
                auditEntityChangeService
        ).createAuditEntityChange(
                any(
                        AuditEntityChangeRequest.class
                )
        );

        NotificationTemplateUpdateRequest request =
                new NotificationTemplateUpdateRequest();

        request.setTemplateName(
                "Updated Notification"
        );

        request.setStatus(
                "INACTIVE"
        );

        SecurityContext securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of("notification.manage"),
                        Set.of()
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                notificationTemplateService
                                        .updateNotificationTemplate(
                                                notificationTemplateId,
                                                request,
                                                securityContext
                                        )
                );

        assertEquals(
                failureMessage,
                exception.getMessage()
        );

        assertEquals(
                "Original Notification",
                jdbcTemplate.queryForObject(
                        """
                        SELECT template_name
                        FROM catalog.notification_template
                        WHERE notification_template_id = ?
                        """,
                        String.class,
                        notificationTemplateId
                )
        );

        assertEquals(
                "ACTIVE",
                jdbcTemplate.queryForObject(
                        """
                        SELECT status
                        FROM catalog.notification_template
                        WHERE notification_template_id = ?
                        """,
                        String.class,
                        notificationTemplateId
                )
        );

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type =
                              'NOTIFICATION_TEMPLATE'
                          AND entity_id = ?
                          AND event_type =
                              'NOTIFICATION_MANAGEMENT'
                          AND event_result = 'SUCCESS'
                        """,
                        Integer.class,
                        notificationTemplateId
                );

        assertEquals(
                Integer.valueOf(0),
                successAuditCount
        );

        Integer failureAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type =
                              'NOTIFICATION_MANAGEMENT'
                          AND entity_type =
                              'NOTIFICATION_TEMPLATE'
                          AND entity_id = ?
                          AND action = 'UPDATE'
                          AND source_component = 'CATALOG'
                          AND event_result = 'FAILURE'
                          AND event_details ->> 'reason' =
                              'NOTIFICATION_MANAGEMENT_FAILED'
                          AND event_details
                                  ->> 'permissionCode' =
                              'notification.manage'
                          AND event_details ->> 'errorType' =
                              'java.lang.IllegalStateException'
                          AND event_details ->> 'errorMessage' = ?
                        """,
                        Integer.class,
                        USER_ID,
                        notificationTemplateId,
                        failureMessage
                );

        assertEquals(
                Integer.valueOf(1),
                failureAuditCount
        );
    }

    private UUID insertNotificationTemplate() {

        UUID notificationTemplateId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO catalog.notification_template (
                    notification_template_id,
                    organization_id,
                    template_code,
                    template_name,
                    channel,
                    subject_template,
                    body_template,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                notificationTemplateId,
                ORGANIZATION_ID,
                "UC037_FAILURE_"
                        + notificationTemplateId
                                .toString()
                                .substring(0, 8),
                "Original Notification",
                "EMAIL",
                "Original Subject",
                "Original Body",
                "ACTIVE"
        );

        return notificationTemplateId;
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
                "EFS-UC037-FAILURE",
                "EFS UC037 Failure Test Organization",
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
                "efs.uc037.failure",
                "EFS UC037 Failure Test User",
                "efs.uc037.failure@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}