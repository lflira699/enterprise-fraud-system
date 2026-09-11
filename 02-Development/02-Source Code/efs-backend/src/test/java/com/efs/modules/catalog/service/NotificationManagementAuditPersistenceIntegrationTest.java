package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.NotificationTemplateUpdateRequest;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
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
class NotificationManagementAuditPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a037a037-a037-a037-a037-a037a037a037"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "b037b037-b037-b037-b037-b037b037b037"
            );

    @Autowired
    private NotificationTemplateServiceInterface
            notificationTemplateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldRegisterNotificationManagePermission() {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.permission
                        WHERE permission_code =
                              'notification.manage'
                          AND resource = 'notification'
                          AND action = 'manage'
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

        UUID notificationTemplateId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        notificationTemplateService
                                .updateNotificationTemplate(
                                        notificationTemplateId,
                                        updateNameRequest(),
                                        securityContext(
                                                Set.of()
                                        )
                                )
        );

        assertRejectedAudit(
                notificationTemplateId,
                "MISSING_PERMISSION"
        );
    }

    @Test
    void shouldPersistRejectedAuditWhenTemplateDoesNotExist() {

        UUID notificationTemplateId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        notificationTemplateService
                                .updateNotificationTemplate(
                                        notificationTemplateId,
                                        updateNameRequest(),
                                        securityContext(
                                                Set.of(
                                                        "notification.manage"
                                                )
                                        )
                                )
        );

        assertRejectedAudit(
                notificationTemplateId,
                "NOTIFICATION_TEMPLATE_NOT_FOUND"
        );
    }

    @Test
    @Transactional
    void shouldPersistRejectedAuditForEmptyUpdate() {

        UUID notificationTemplateId =
                insertNotificationTemplate();

        NotificationTemplateUpdateRequest request =
                new NotificationTemplateUpdateRequest();

        assertThrows(
                RequestValidationException.class,
                () ->
                        notificationTemplateService
                                .updateNotificationTemplate(
                                        notificationTemplateId,
                                        request,
                                        securityContext(
                                                Set.of(
                                                        "notification.manage"
                                                )
                                        )
                                )
        );

        assertEquals(
                "Original Notification",
                getTemplateName(
                        notificationTemplateId
                )
        );

        assertRejectedAudit(
                notificationTemplateId,
                "INVALID_UPDATE_REQUEST"
        );
    }

    @Test
    @Transactional
    void shouldPersistRejectedAuditForBlankBodyTemplate() {

        UUID notificationTemplateId =
                insertNotificationTemplate();

        NotificationTemplateUpdateRequest request =
                new NotificationTemplateUpdateRequest();

        request.setBodyTemplate(
                "   "
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        notificationTemplateService
                                .updateNotificationTemplate(
                                        notificationTemplateId,
                                        request,
                                        securityContext(
                                                Set.of(
                                                        "notification.manage"
                                                )
                                        )
                                )
        );

        assertEquals(
                "Original Body",
                getBodyTemplate(
                        notificationTemplateId
                )
        );

        assertRejectedAudit(
                notificationTemplateId,
                "INVALID_UPDATE_REQUEST"
        );
    }

    private void assertRejectedAudit(
            UUID notificationTemplateId,
            String reason) {

        Integer auditCount =
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
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason' = ?
                          AND event_details
                                  ->> 'permissionCode' =
                              'notification.manage'
                        """,
                        Integer.class,
                        USER_ID,
                        notificationTemplateId,
                        reason
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
        );
    }

    private NotificationTemplateUpdateRequest
            updateNameRequest() {

        NotificationTemplateUpdateRequest request =
                new NotificationTemplateUpdateRequest();

        request.setTemplateName(
                "Updated Notification"
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
                "UC037_AUDIT_"
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

    private String getTemplateName(
            UUID notificationTemplateId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT template_name
                FROM catalog.notification_template
                WHERE notification_template_id = ?
                """,
                String.class,
                notificationTemplateId
        );
    }

    private String getBodyTemplate(
            UUID notificationTemplateId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT body_template
                FROM catalog.notification_template
                WHERE notification_template_id = ?
                """,
                String.class,
                notificationTemplateId
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
                "EFS-UC037-AUDIT",
                "EFS UC037 Audit Test Organization",
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
                "efs.uc037.audit",
                "EFS UC037 Audit Test User",
                "efs.uc037.audit@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}