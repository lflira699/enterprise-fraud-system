package com.efs.e2e;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.entity.AuditEvent;
import com.efs.modules.audit.repository.AuditEventRepository;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import com.efs.modules.integration.service.ExternalNotificationDeliveryAvailabilityServiceInterface;
import com.efs.modules.integration.service.ExternalNotificationDeliveryServiceInterface;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventListener;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class NotificationDeliveryEndToEndIntegrationTest {

    private static final String NOTIFICATION_TYPE =
            "CASE_STATUS_CHANGED";

    private static final String TEMPLATE_CODE =
            "CASE_STATUS_CHANGED";

    private static final String CHANNEL =
            "EMAIL";

    private static final String RECIPIENT_EMAIL =
            "uc038@example.com";

    @Autowired
    private NotificationRequestedEventListener
            notificationRequestedEventListener;

    @Autowired
    private NotificationRepository
            notificationRepository;

    @Autowired
    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    @Autowired
    private AuditEventRepository
            auditEventRepository;

    @Autowired
    private OutboxEventRepository
            outboxEventRepository;

    @Autowired
    private JdbcTemplate
            jdbcTemplate;

    @Autowired
    private EntityManager
            entityManager;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @MockitoBean
    private NotificationTemplateServiceInterface
            notificationTemplateService;

    @MockitoBean
    private ExternalNotificationDeliveryAvailabilityServiceInterface
            externalNotificationDeliveryAvailabilityService;

    @MockitoBean
    private ExternalNotificationDeliveryServiceInterface
            externalNotificationDeliveryService;

    private UUID messageId;
    private UUID correlationId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID languageId;
    private UUID recipientUserId;
    private UUID sourceEntityId;
    private UUID notificationTemplateId;

    @BeforeEach
    void setUp() {

        messageId =
                UUID.randomUUID();

        correlationId =
                UUID.randomUUID();

        organizationId =
                UUID.randomUUID();

        tenantId =
                UUID.randomUUID();

        languageId =
                UUID.randomUUID();

        recipientUserId =
                UUID.randomUUID();

        sourceEntityId =
                UUID.randomUUID();

        notificationTemplateId =
                UUID.randomUUID();

        insertOrganization();
        insertTenant();
        insertRecipientUser();
        insertNotificationTemplate();

        configureExternalBoundaries();
    }

    @Test
    void shouldProcessNotificationRequestedEndToEndAndPersistDeliveryOutcome() {

        notificationRequestedEventListener.consume(
                eventBody()
        );

        entityManager.flush();
        entityManager.clear();

        assertEquals(
                1L,
                count(
                        """
                        SELECT COUNT(*)
                        FROM notification.notification
                        WHERE correlation_id = ?
                        """,
                        correlationId
                )
        );

        UUID notificationId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT notification_id
                        FROM notification.notification
                        WHERE correlation_id = ?
                        """,
                        UUID.class,
                        correlationId
                );

        assertNotNull(
                notificationId
        );

        Notification notification =
                notificationRepository
                        .findById(
                                notificationId
                        )
                        .orElseThrow();

        assertEquals(
                organizationId,
                notification.getOrganizationId()
        );

        assertEquals(
                tenantId,
                notification.getTenantId()
        );

        assertEquals(
                NOTIFICATION_TYPE,
                notification.getNotificationType()
        );

        assertEquals(
                notificationTemplateId,
                notification.getNotificationTemplateId()
        );

        assertEquals(
                "CASE",
                notification.getSourceComponent()
        );

        assertEquals(
                "CASE",
                notification.getSourceEntityType()
        );

        assertEquals(
                sourceEntityId,
                notification.getSourceEntityId()
        );

        assertEquals(
                correlationId,
                notification.getCorrelationId()
        );

        assertEquals(
                "DELIVERED",
                notification.getNotificationStatus()
        );

        assertNotNull(
                notification.getProcessedAt()
        );

        List<NotificationRecipientDelivery> deliveries =
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        );

        assertEquals(
                1,
                deliveries.size()
        );

        NotificationRecipientDelivery delivery =
                deliveries.get(
                        0
                );

        assertEquals(
                recipientUserId,
                delivery.getRecipientUserId()
        );

        assertEquals(
                CHANNEL,
                delivery.getChannel()
        );

        assertEquals(
                "DELIVERED",
                delivery.getDeliveryStatus()
        );

        assertNull(
                delivery.getDeliveryReference()
        );

        assertNull(
                delivery.getDeliveryResult()
        );

        assertNotNull(
                delivery.getProcessedAt()
        );

        ArgumentCaptor<ExternalNotificationDeliveryRequest>
                requestCaptor =
                ArgumentCaptor.forClass(
                        ExternalNotificationDeliveryRequest.class
                );

        verify(
                externalNotificationDeliveryService,
                times(1)
        ).deliver(
                requestCaptor.capture()
        );

        ExternalNotificationDeliveryRequest deliveryRequest =
                requestCaptor.getValue();

        assertEquals(
                delivery.getNotificationDeliveryId(),
                deliveryRequest.getNotificationDeliveryId()
        );

        assertEquals(
                correlationId,
                deliveryRequest.getCorrelationId()
        );

        assertEquals(
                organizationId,
                deliveryRequest.getOrganizationId()
        );

        assertEquals(
                tenantId,
                deliveryRequest.getTenantId()
        );

        assertEquals(
                CHANNEL,
                deliveryRequest.getChannel()
        );

        assertEquals(
                recipientUserId,
                deliveryRequest.getRecipientUserId()
        );

        assertEquals(
                RECIPIENT_EMAIL,
                deliveryRequest.getDestination()
        );

        assertEquals(
                "Case status CLOSED",
                deliveryRequest.getSubject()
        );

        assertEquals(
                "Case moved to CLOSED",
                deliveryRequest.getBody()
        );

        List<AuditEvent> notificationAudits =
                auditEventRepository
                        .findByCorrelationIdOrderByEventTimestampDesc(
                                correlationId
                        )
                        .stream()
                        .filter(
                                audit ->
                                        "NOTIFICATION_DELIVERY".equals(
                                                audit.getEventType()
                                        )
                        )
                        .toList();

        assertEquals(
                1,
                notificationAudits.size()
        );

        AuditEvent audit =
                notificationAudits.get(
                        0
                );

        assertEquals(
                "NOTIFICATION",
                audit.getEntityType()
        );

        assertEquals(
                notificationId,
                audit.getEntityId()
        );

        assertEquals(
                "DELIVER",
                audit.getAction()
        );

        assertEquals(
                "NOTIFICATION",
                audit.getSourceComponent()
        );

        assertEquals(
                correlationId,
                audit.getCorrelationId()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        List<OutboxEvent> completionEvents =
                outboxEventRepository
                        .findAll()
                        .stream()
                        .filter(
                                event ->
                                        "NotificationDeliveryCompleted"
                                                .equals(
                                                        event.getEventType()
                                                )
                                                && correlationId.equals(
                                                        event.getCorrelationId()
                                                )
                        )
                        .toList();

        assertEquals(
                1,
                completionEvents.size()
        );

        OutboxEvent outboxEvent =
                completionEvents.get(
                        0
                );

        assertEquals(
                "Notification",
                outboxEvent.getAggregateType()
        );

        assertEquals(
                notificationId,
                outboxEvent.getAggregateId()
        );

        assertEquals(
                "PENDING",
                outboxEvent.getStatus()
        );

        Map<?, ?> envelope =
                asMap(
                        outboxEvent.getPayload()
                );

        assertEquals(
                outboxEvent.getId().toString(),
                envelope.get(
                        "messageId"
                )
        );

        assertEquals(
                "NotificationDeliveryCompleted",
                envelope.get(
                        "eventType"
                )
        );

        assertEquals(
                "1.0",
                envelope.get(
                        "schemaVersion"
                )
        );

        assertEquals(
                "Notification Service",
                envelope.get(
                        "producer"
                )
        );

        assertEquals(
                correlationId.toString(),
                envelope.get(
                        "correlationId"
                )
        );

        assertEquals(
                messageId.toString(),
                envelope.get(
                        "causationId"
                )
        );

        assertEquals(
                tenantId.toString(),
                envelope.get(
                        "tenantId"
                )
        );

        Map<?, ?> metadata =
                asMap(
                        envelope.get(
                                "metadata"
                        )
                );

        assertTrue(
                metadata.isEmpty()
        );

        Map<?, ?> completionPayload =
                asMap(
                        envelope.get(
                                "payload"
                        )
                );

        assertEquals(
                notificationId.toString(),
                completionPayload.get(
                        "notificationId"
                )
        );

        assertEquals(
                NOTIFICATION_TYPE,
                completionPayload.get(
                        "notificationType"
                )
        );

        assertEquals(
                "CASE",
                completionPayload.get(
                        "sourceComponent"
                )
        );

        assertEquals(
                "CASE",
                completionPayload.get(
                        "sourceEntityType"
                )
        );

        assertEquals(
                sourceEntityId.toString(),
                completionPayload.get(
                        "sourceEntityId"
                )
        );

        assertEquals(
                "DELIVERED",
                completionPayload.get(
                        "notificationStatus"
                )
        );

        assertNotNull(
                completionPayload.get(
                        "processedAt"
                )
        );

        assertEquals(
                1L,
                number(
                        completionPayload.get(
                                "deliveredCount"
                        )
                )
        );

        assertEquals(
                0L,
                number(
                        completionPayload.get(
                                "failedCount"
                        )
                )
        );

        List<?> completionDeliveries =
                asList(
                        completionPayload.get(
                                "deliveries"
                        )
                );

        assertEquals(
                1,
                completionDeliveries.size()
        );

        Map<?, ?> completionDelivery =
                asMap(
                        completionDeliveries.get(
                                0
                        )
                );

        assertEquals(
                recipientUserId.toString(),
                completionDelivery.get(
                        "recipientUserId"
                )
        );

        assertEquals(
                CHANNEL,
                completionDelivery.get(
                        "channel"
                )
        );

        assertEquals(
                "DELIVERED",
                completionDelivery.get(
                        "deliveryStatus"
                )
        );

        assertNull(
                completionDelivery.get(
                        "deliveryReference"
                )
        );

        assertNull(
                completionDelivery.get(
                        "deliveryResult"
                )
        );

        assertNotNull(
                completionDelivery.get(
                        "processedAt"
                )
        );

        assertEquals(
                1L,
                processedDomainEventCount()
        );
    }

    @Test
    void shouldIgnoreDuplicateNotificationRequestedMessageEndToEnd() {

        byte[] body =
                eventBody();

        notificationRequestedEventListener.consume(
                body
        );

        notificationRequestedEventListener.consume(
                body
        );

        entityManager.flush();
        entityManager.clear();

        assertEquals(
                1L,
                count(
                        """
                        SELECT COUNT(*)
                        FROM notification.notification
                        WHERE correlation_id = ?
                        """,
                        correlationId
                )
        );

        assertEquals(
                1L,
                count(
                        """
                        SELECT COUNT(*)
                        FROM notification.notification_recipient_delivery d
                        JOIN notification.notification n
                          ON n.notification_id = d.notification_id
                        WHERE n.correlation_id = ?
                        """,
                        correlationId
                )
        );

        assertEquals(
                1L,
                count(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE event_type = 'NotificationDeliveryCompleted'
                          AND correlation_id = ?
                        """,
                        correlationId
                )
        );

        assertEquals(
                1L,
                count(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'NOTIFICATION_DELIVERY'
                          AND correlation_id = ?
                        """,
                        correlationId
                )
        );

        assertEquals(
                1L,
                processedDomainEventCount()
        );

        verify(
                externalNotificationDeliveryService,
                times(1)
        ).deliver(
                any(
                        ExternalNotificationDeliveryRequest.class
                )
        );
    }

    private void configureExternalBoundaries() {

        UserAccountReference recipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        RECIPIENT_EMAIL
                );

        when(
                userAccountLookupService
                        .findAuthorizedUsers(
                                organizationId,
                                tenantId,
                                List.of(
                                        recipientUserId
                                )
                        )
        ).thenReturn(
                List.of(
                        recipient
                )
        );

        NotificationTemplateResponse template =
                new NotificationTemplateResponse();

        template.setNotificationTemplateId(
                notificationTemplateId
        );

        template.setOrganizationId(
                organizationId
        );

        template.setTenantId(
                tenantId
        );

        template.setLanguageId(
                languageId
        );

        template.setTemplateCode(
                TEMPLATE_CODE
        );

        template.setTemplateName(
                "UC-038 Notification Delivery E2E"
        );

        template.setChannel(
                CHANNEL
        );

        template.setSubjectTemplate(
                "Case status {{status}}"
        );

        template.setBodyTemplate(
                "Case moved to {{status}}"
        );

        template.setStatus(
                "ACTIVE"
        );

        when(
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                organizationId,
                                tenantId,
                                TEMPLATE_CODE,
                                CHANNEL,
                                languageId
                        )
        ).thenReturn(
                template
        );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                notificationTemplateId
                        )
        ).thenReturn(
                template
        );

        when(
                externalNotificationDeliveryAvailabilityService
                        .isAvailable(
                                organizationId,
                                tenantId,
                                CHANNEL
                        )
        ).thenReturn(
                true
        );

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        result.setDelivered(
                true
        );

        result.setDeliveryReference(
                null
        );

        result.setDeliveryResult(
                null
        );

        when(
                externalNotificationDeliveryService
                        .deliver(
                                any(
                                        ExternalNotificationDeliveryRequest.class
                                )
                        )
        ).thenReturn(
                result
        );
    }

    private byte[] eventBody() {

        String body =
                """
                {
                  "messageId":"%s",
                  "eventType":"NotificationRequested",
                  "schemaVersion":"1.1",
                  "occurredAt":"2026-09-12T12:00:00",
                  "producer":"Case Management",
                  "correlationId":"%s",
                  "causationId":null,
                  "tenantId":"%s",
                  "payload":{
                    "notificationType":"%s",
                    "templateCode":"%s",
                    "channel":"%s",
                    "languageId":"%s",
                    "organizationId":"%s",
                    "tenantId":"%s",
                    "sourceComponent":"CASE",
                    "sourceEntityType":"CASE",
                    "sourceEntityId":"%s",
                    "recipientUserIds":[
                      "%s"
                    ],
                    "templateParameters":{
                      "status":"CLOSED"
                    }
                  },
                  "metadata":{}
                }
                """.formatted(
                        messageId,
                        correlationId,
                        tenantId,
                        NOTIFICATION_TYPE,
                        TEMPLATE_CODE,
                        CHANNEL,
                        languageId,
                        organizationId,
                        tenantId,
                        sourceEntityId,
                        recipientUserId
                );

        return body.getBytes(
                StandardCharsets.UTF_8
        );
    }

    private void insertOrganization() {

        String suffix =
                organizationId
                        .toString()
                        .substring(
                                0,
                                8
                        );

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
                "UC038-" + suffix,
                "UC-038 Notification Delivery E2E Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant() {

        String suffix =
                tenantId
                        .toString()
                        .substring(
                                0,
                                8
                        );

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
                "UC038-" + suffix,
                "UC-038 Notification Delivery E2E Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    private void insertRecipientUser() {

        String suffix =
                recipientUserId
                        .toString()
                        .substring(
                                0,
                                8
                        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    tenant_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                recipientUserId,
                organizationId,
                tenantId,
                "uc038." + suffix,
                "UC-038 Notification Recipient",
                RECIPIENT_EMAIL,
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertNotificationTemplate() {

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
                    status
                )
                VALUES (?, ?, ?, NULL, ?, ?, ?, ?, ?, ?)
                """,
                notificationTemplateId,
                organizationId,
                tenantId,
                TEMPLATE_CODE,
                "UC-038 Notification Delivery E2E",
                CHANNEL,
                "Case status {{status}}",
                "Case moved to {{status}}",
                "ACTIVE"
        );
    }

    private long processedDomainEventCount() {

        return count(
                """
                SELECT COUNT(*)
                FROM integration.processed_domain_event
                WHERE message_id = ?
                  AND consumer_name = 'Notification Service'
                  AND event_type = 'NotificationRequested'
                """,
                messageId
        );
    }

    private long count(
            String sql,
            Object... arguments) {

        Long value =
                jdbcTemplate.queryForObject(
                        sql,
                        Long.class,
                        arguments
                );

        assertNotNull(
                value
        );

        return value;
    }

    private Map<?, ?> asMap(
            Object value) {

        assertTrue(
                value instanceof Map<?, ?>
        );

        return (Map<?, ?>) value;
    }

    private List<?> asList(
            Object value) {

        assertTrue(
                value instanceof List<?>
        );

        return (List<?>) value;
    }

    private long number(
            Object value) {

        assertTrue(
                value instanceof Number
        );

        return ((Number) value)
                .longValue();
    }
}