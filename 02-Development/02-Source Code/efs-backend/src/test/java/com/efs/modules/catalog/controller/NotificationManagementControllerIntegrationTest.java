package com.efs.modules.catalog.controller;

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
class NotificationManagementControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "c037c037-c037-c037-c037-c037c037c037"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "d037d037-d037-d037-d037-d037d037d037"
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
                        Set.of("notification.manage")
                )
        );
    }

    @Test
    void shouldUpdateNotificationTemplateThroughApi()
            throws Exception {

        UUID notificationTemplateId =
                insertNotificationTemplate(
                        "Original Notification",
                        "Original Subject",
                        "Original Body",
                        "ACTIVE"
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "templateName": "Updated Notification",
                                            "subjectTemplate": "Updated Subject",
                                            "status": "INACTIVE"
                                        }
                                        """
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.notificationTemplateId")
                                .value(
                                        notificationTemplateId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.templateCode")
                                .value("CASE_ASSIGNED")
                )
                .andExpect(
                        jsonPath("$.templateName")
                                .value("Updated Notification")
                )
                .andExpect(
                        jsonPath("$.channel")
                                .value("EMAIL")
                )
                .andExpect(
                        jsonPath("$.subjectTemplate")
                                .value("Updated Subject")
                )
                .andExpect(
                        jsonPath("$.bodyTemplate")
                                .value("Original Body")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("INACTIVE")
                );

        assertEquals(
                "Original Body",
                getColumn(
                        notificationTemplateId,
                        "body_template"
                )
        );

        assertEquals(
                "CASE_ASSIGNED",
                getColumn(
                        notificationTemplateId,
                        "template_code"
                )
        );

        assertEquals(
                "EMAIL",
                getColumn(
                        notificationTemplateId,
                        "channel"
                )
        );

        UUID auditEventId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT audit_event_id
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type =
                              'NOTIFICATION_MANAGEMENT'
                          AND entity_type =
                              'NOTIFICATION_TEMPLATE'
                          AND entity_id = ?
                          AND action = 'UPDATE'
                          AND source_component = 'CATALOG'
                          AND event_result = 'SUCCESS'
                        """,
                        UUID.class,
                        USER_ID,
                        notificationTemplateId
                );

        assertEquals(
                "Original Notification",
                jdbcTemplate.queryForObject(
                        """
                        SELECT previous_value
                                   ->> 'templateName'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type =
                              'NOTIFICATION_TEMPLATE'
                          AND entity_id = ?
                          AND operation = 'UPDATE'
                        """,
                        String.class,
                        auditEventId,
                        notificationTemplateId
                )
        );

        assertEquals(
                "Updated Notification",
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_value
                                   ->> 'templateName'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type =
                              'NOTIFICATION_TEMPLATE'
                          AND entity_id = ?
                          AND operation = 'UPDATE'
                        """,
                        String.class,
                        auditEventId,
                        notificationTemplateId
                )
        );

        assertEquals(
                Integer.valueOf(1),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                        """,
                        Integer.class,
                        auditEventId
                )
        );
    }

    @Test
    void shouldRejectNotificationManagementWithoutPermission()
            throws Exception {

        UUID notificationTemplateId =
                insertNotificationTemplate(
                        "Original Notification",
                        "Original Subject",
                        "Original Body",
                        "ACTIVE"
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
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "templateName": "Unauthorized Update"
                                        }
                                        """
                                )
                )
                .andExpect(status().isForbidden());

        assertEquals(
                "Original Notification",
                getColumn(
                        notificationTemplateId,
                        "template_name"
                )
        );
    }

    @Test
    void shouldReturnNotFoundForUnknownNotificationTemplate()
            throws Exception {

        UUID notificationTemplateId =
                UUID.randomUUID();

        mockMvc.perform(
                        patch(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "templateName": "Updated Notification"
                                        }
                                        """
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectEmptyNotificationTemplateUpdate()
            throws Exception {

        UUID notificationTemplateId =
                insertNotificationTemplate(
                        "Original Notification",
                        "Original Subject",
                        "Original Body",
                        "ACTIVE"
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        assertEquals(
                "Original Notification",
                getColumn(
                        notificationTemplateId,
                        "template_name"
                )
        );
    }

    @Test
    void shouldRejectBlankRequiredNotificationTemplateField()
            throws Exception {

        UUID notificationTemplateId =
                insertNotificationTemplate(
                        "Original Notification",
                        "Original Subject",
                        "Original Body",
                        "ACTIVE"
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                            "bodyTemplate": "   "
                                        }
                                        """
                                )
                )
                .andExpect(status().isBadRequest());

        assertEquals(
                "Original Body",
                getColumn(
                        notificationTemplateId,
                        "body_template"
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

    private UUID insertNotificationTemplate(
            String templateName,
            String subjectTemplate,
            String bodyTemplate,
            String status) {

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
                "CASE_ASSIGNED",
                templateName,
                "EMAIL",
                subjectTemplate,
                bodyTemplate,
                status
        );

        return notificationTemplateId;
    }

    private String getColumn(
            UUID notificationTemplateId,
            String column) {

        return jdbcTemplate.queryForObject(
                """
                SELECT %s
                FROM catalog.notification_template
                WHERE notification_template_id = ?
                """.formatted(column),
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
                "EFS-UC037-CONTROLLER",
                "EFS UC037 Controller Test Organization",
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
                "efs.uc037.controller",
                "EFS UC037 Controller Test User",
                "efs.uc037.controller@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}