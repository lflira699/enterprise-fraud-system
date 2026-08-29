package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class NotificationTemplateServiceIntegrationTest {

    @Autowired
    private NotificationTemplateServiceInterface notificationTemplateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveNotificationTemplateById() {

        UUID organizationId = createOrganization();
        UUID tenantId = createTenant(organizationId);
        UUID languageId = createLanguage();

        NotificationTemplateRequest request =
                new NotificationTemplateRequest();

        request.setOrganizationId(organizationId);
        request.setTenantId(tenantId);
        request.setLanguageId(languageId);
        request.setTemplateCode("CASE_CREATED");
        request.setTemplateName("Case Created");
        request.setChannel("EMAIL");
        request.setSubjectTemplate("Case {{caseNumber}} created");
        request.setBodyTemplate(
                "Case {{caseNumber}} has been created."
        );
        request.setStatus("ACTIVE");

        NotificationTemplateResponse created =
                notificationTemplateService
                        .createNotificationTemplate(request);

        assertNotNull(created);
        assertNotNull(
                created.getNotificationTemplateId()
        );

        assertEquals(
                organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                tenantId,
                created.getTenantId()
        );

        assertEquals(
                languageId,
                created.getLanguageId()
        );

        assertEquals(
                "CASE_CREATED",
                created.getTemplateCode()
        );

        assertEquals(
                "Case Created",
                created.getTemplateName()
        );

        assertEquals(
                "EMAIL",
                created.getChannel()
        );

        assertEquals(
                "Case {{caseNumber}} created",
                created.getSubjectTemplate()
        );

        assertEquals(
                "Case {{caseNumber}} has been created.",
                created.getBodyTemplate()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        assertNotNull(
                created.getUpdatedAt()
        );

        NotificationTemplateResponse retrieved =
                notificationTemplateService
                        .getNotificationTemplateById(
                                created.getNotificationTemplateId()
                        );

        assertEquals(
                created.getNotificationTemplateId(),
                retrieved.getNotificationTemplateId()
        );
    }

    @Test
    void shouldRetrieveNotificationTemplateByScope() {

        UUID organizationId = createOrganization();
        UUID tenantId = createTenant(organizationId);
        UUID languageId = createLanguage();

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

        NotificationTemplateResponse result =
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                organizationId,
                                tenantId,
                                "CASE_ASSIGNED",
                                "EMAIL",
                                languageId
                        );

        assertEquals(
                notificationTemplateId,
                result.getNotificationTemplateId()
        );

        assertEquals(
                organizationId,
                result.getOrganizationId()
        );

        assertEquals(
                tenantId,
                result.getTenantId()
        );

        assertEquals(
                languageId,
                result.getLanguageId()
        );
    }

    @Test
    void shouldKeepTemplatesIsolatedByScope() {

        UUID organizationId =
                createOrganization();

        UUID tenantIdOne =
                createTenant(organizationId);

        UUID tenantIdTwo =
                createTenant(organizationId);

        UUID languageId =
                createLanguage();

        UUID templateIdOne =
                UUID.randomUUID();

        UUID templateIdTwo =
                UUID.randomUUID();

        insertNotificationTemplate(
                templateIdOne,
                organizationId,
                tenantIdOne,
                languageId,
                "CASE_CREATED",
                "Case Created Tenant One",
                "EMAIL",
                null,
                "Tenant one body",
                "ACTIVE"
        );

        insertNotificationTemplate(
                templateIdTwo,
                organizationId,
                tenantIdTwo,
                languageId,
                "CASE_CREATED",
                "Case Created Tenant Two",
                "EMAIL",
                null,
                "Tenant two body",
                "ACTIVE"
        );

        NotificationTemplateResponse resultOne =
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                organizationId,
                                tenantIdOne,
                                "CASE_CREATED",
                                "EMAIL",
                                languageId
                        );

        NotificationTemplateResponse resultTwo =
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                organizationId,
                                tenantIdTwo,
                                "CASE_CREATED",
                                "EMAIL",
                                languageId
                        );

        assertEquals(
                templateIdOne,
                resultOne.getNotificationTemplateId()
        );

        assertEquals(
                templateIdTwo,
                resultTwo.getNotificationTemplateId()
        );
    }

    @Test
    void shouldReturnNotificationTemplatesByScope() {

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

        List<NotificationTemplateResponse> results =
                notificationTemplateService
                        .getNotificationTemplatesByScope(
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Case Assigned",
                results.get(0).getTemplateName()
        );

        assertEquals(
                "Case Closed",
                results.get(1).getTemplateName()
        );
    }

    @Test
    void shouldReturnNotificationTemplatesByScopeAndStatus() {

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

        List<NotificationTemplateResponse> results =
                notificationTemplateService
                        .getNotificationTemplatesByScopeAndStatus(
                                organizationId,
                                tenantId,
                                "ACTIVE"
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                "CASE_CREATED",
                results.get(0).getTemplateCode()
        );
    }

    @Test
    void shouldReturnNotificationTemplatesByCode() {

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

        List<NotificationTemplateResponse> results =
                notificationTemplateService
                        .getNotificationTemplatesByCode(
                                "CASE_CREATED"
                        );

        assertEquals(
                2,
                results.size()
        );
    }

    @Test
    void shouldRejectUnknownNotificationTemplateId() {

        UUID notificationTemplateId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        notificationTemplateService
                                .getNotificationTemplateById(
                                        notificationTemplateId
                                )
        );
    }

    @Test
    void shouldRejectUnknownNotificationTemplateScope() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        notificationTemplateService
                                .getNotificationTemplateByScope(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        "UNKNOWN",
                                        "EMAIL",
                                        UUID.randomUUID()
                                )
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
                "Notification Template Test Organization",
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
                "Notification Template Test Tenant",
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
                "Notification Template Test Language",
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