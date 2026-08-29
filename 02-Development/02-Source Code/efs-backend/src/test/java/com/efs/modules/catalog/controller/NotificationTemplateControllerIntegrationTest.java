package com.efs.modules.catalog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationTemplateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateNotificationTemplate()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        String requestBody =
                """
                {
                  "organizationId": "%s",
                  "tenantId": "%s",
                  "languageId": "%s",
                  "templateCode": "CASE_CREATED",
                  "templateName": "Case Created",
                  "channel": "EMAIL",
                  "subjectTemplate": "Case created",
                  "bodyTemplate": "Case has been created.",
                  "status": "ACTIVE"
                }
                """.formatted(
                        organizationId,
                        tenantId,
                        languageId
                );

        mockMvc.perform(
                        post("/api/v1/notification-templates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.notificationTemplateId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(organizationId.toString())
                )
                .andExpect(
                        jsonPath("$.tenantId")
                                .value(tenantId.toString())
                )
                .andExpect(
                        jsonPath("$.languageId")
                                .value(languageId.toString())
                )
                .andExpect(
                        jsonPath("$.templateCode")
                                .value("CASE_CREATED")
                )
                .andExpect(
                        jsonPath("$.channel")
                                .value("EMAIL")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectInvalidNotificationTemplate()
            throws Exception {

        String requestBody =
                """
                {
                  "templateCode": "",
                  "templateName": "",
                  "channel": "",
                  "bodyTemplate": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/notification-templates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetNotificationTemplateById()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        UUID notificationTemplateId =
                UUID.randomUUID();

        insertNotificationTemplate(
                notificationTemplateId,
                organizationId,
                tenantId,
                languageId,
                "CASE_ASSIGNED",
                "Case Assigned",
                "EMAIL",
                "Case assigned",
                "A case has been assigned.",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.notificationTemplateId")
                                .value(notificationTemplateId.toString())
                )
                .andExpect(
                        jsonPath("$.templateCode")
                                .value("CASE_ASSIGNED")
                );
    }

    @Test
    void shouldGetNotificationTemplateByScope()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        UUID notificationTemplateId =
                UUID.randomUUID();

        insertNotificationTemplate(
                notificationTemplateId,
                organizationId,
                tenantId,
                languageId,
                "CASE_CREATED",
                "Case Created",
                "EMAIL",
                null,
                "Case created body",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/notification-templates/scope")
                                .param(
                                        "organizationId",
                                        organizationId.toString()
                                )
                                .param(
                                        "tenantId",
                                        tenantId.toString()
                                )
                                .param(
                                        "templateCode",
                                        "CASE_CREATED"
                                )
                                .param(
                                        "channel",
                                        "EMAIL"
                                )
                                .param(
                                        "languageId",
                                        languageId.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.notificationTemplateId")
                                .value(notificationTemplateId.toString())
                );
    }

    @Test
    void shouldGetNotificationTemplatesByScope()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_CLOSED",
                "Case Closed",
                "EMAIL",
                null,
                "Case closed body",
                "ACTIVE"
        );

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_ASSIGNED",
                "Case Assigned",
                "EMAIL",
                null,
                "Case assigned body",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/notification-templates")
                                .param(
                                        "organizationId",
                                        organizationId.toString()
                                )
                                .param(
                                        "tenantId",
                                        tenantId.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].templateName")
                                .value("Case Assigned")
                )
                .andExpect(
                        jsonPath("$[1].templateName")
                                .value("Case Closed")
                );
    }

    @Test
    void shouldGetNotificationTemplatesByScopeAndStatus()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_CREATED",
                "Case Created",
                "EMAIL",
                null,
                "Active body",
                "ACTIVE"
        );

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_CLOSED",
                "Case Closed",
                "EMAIL",
                null,
                "Inactive body",
                "INACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/notification-templates")
                                .param(
                                        "organizationId",
                                        organizationId.toString()
                                )
                                .param(
                                        "tenantId",
                                        tenantId.toString()
                                )
                                .param(
                                        "status",
                                        "ACTIVE"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].templateCode")
                                .value("CASE_CREATED")
                );
    }

    @Test
    void shouldGetNotificationTemplatesByCode()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID tenantId =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_CREATED",
                "Case Created Email",
                "EMAIL",
                null,
                "Email body",
                "ACTIVE"
        );

        insertNotificationTemplate(
                UUID.randomUUID(),
                organizationId,
                tenantId,
                languageId,
                "CASE_CREATED",
                "Case Created SMS",
                "SMS",
                null,
                "SMS body",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/notification-templates/code/{templateCode}",
                                "CASE_CREATED"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownNotificationTemplateId()
            throws Exception {

        UUID notificationTemplateId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/notification-templates/{notificationTemplateId}",
                                notificationTemplateId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScope()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/notification-templates/scope")
                                .param(
                                        "organizationId",
                                        UUID.randomUUID().toString()
                                )
                                .param(
                                        "tenantId",
                                        UUID.randomUUID().toString()
                                )
                                .param(
                                        "templateCode",
                                        "UNKNOWN"
                                )
                                .param(
                                        "channel",
                                        "EMAIL"
                                )
                                .param(
                                        "languageId",
                                        UUID.randomUUID().toString()
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private UUID createOrganization() {

        UUID organizationId =
                UUID.randomUUID();

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
                """,
                organizationId,
                "ORG-" + organizationId,
                "Notification Template Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        return organizationId;
    }

    private UUID createTenant(
            UUID organizationId) {

        UUID tenantId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                tenantId,
                organizationId,
                "TENANT-" + tenantId,
                "Notification Template Controller Test Tenant",
                "ACTIVE",
                "TEST"
        );

        return tenantId;
    }

    private UUID createLanguage() {

        UUID languageId =
                UUID.randomUUID();

        String suffix =
                languageId.toString()
                        .replace("-", "")
                        .substring(0, 3)
                        .toUpperCase();

        String languageCode =
                suffix.substring(0, 2);

        String alpha3Code =
                suffix;

        jdbcTemplate.update(
                """
                INSERT INTO catalog.language (
                    language_id,
                    language_code,
                    alpha3_code,
                    language_name,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                languageId,
                languageCode,
                alpha3Code,
                "Notification Template Controller Test Language",
                "ACTIVE"
        );

        return languageId;
    }

    private void insertNotificationTemplate(
            UUID notificationTemplateId,
            UUID organizationId,
            UUID tenantId,
            UUID languageId,
            String templateCode,
            String templateName,
            String channel,
            String subjectTemplate,
            String bodyTemplate,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.notification_template (
                    notification_template_id,
                    organization_id,
                    tenant_id,
                    language_id,
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
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    clock_timestamp(),
                    clock_timestamp()
                )
                """,
                notificationTemplateId,
                organizationId,
                tenantId,
                languageId,
                templateCode,
                templateName,
                channel,
                subjectTemplate,
                bodyTemplate,
                status
        );
    }
}