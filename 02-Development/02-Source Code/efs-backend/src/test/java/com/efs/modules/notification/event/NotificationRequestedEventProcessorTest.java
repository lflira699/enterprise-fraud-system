package com.efs.modules.notification.event;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.integration.service.ProcessedDomainEventRegistry;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class NotificationRequestedEventProcessorTest {

    private static final UUID MESSAGE_ID =
            UUID.fromString(
                    "20120120-1201-4201-8201-201201201201"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "20220220-2202-4202-8202-202202202202"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "20320320-3203-4203-8203-203203203203"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "20420420-4204-4204-8204-204204204204"
            );

    private static final UUID LANGUAGE_ID =
            UUID.fromString(
                    "20520520-5205-4205-8205-205205205205"
            );

    private static final UUID SOURCE_ENTITY_ID =
            UUID.fromString(
                    "20620620-6206-4206-8206-206206206206"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "20720720-7207-4207-8207-207207207207"
            );

    private static final UUID NOTIFICATION_ID =
            UUID.fromString(
                    "20820820-8208-4208-8208-208208208208"
            );

    private static final UUID TEMPLATE_ID =
            UUID.fromString(
                    "20920920-9209-4209-8209-209209209209"
            );

    private static final UUID OUTBOX_ID =
            UUID.fromString(
                    "21021020-1210-4210-8210-210210210210"
            );

    private ProcessedDomainEventRegistry
            processedDomainEventRegistry;

    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private NotificationTemplateServiceInterface
            notificationTemplateService;

    private NotificationRepository
            notificationRepository;

    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    private AuditEventServiceInterface
            auditEventService;

    private DomainEventOutboxService
            domainEventOutboxService;

    private NotificationRequestedEventProcessor
            processor;

    private List<NotificationRecipientDelivery>
            savedDeliveries;

    @BeforeEach
    void setUp() {

        processedDomainEventRegistry =
                mock(
                        ProcessedDomainEventRegistry.class
                );

        userAccountLookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        notificationTemplateService =
                mock(
                        NotificationTemplateServiceInterface.class
                );

        notificationRepository =
                mock(
                        NotificationRepository.class
                );

        notificationRecipientDeliveryRepository =
                mock(
                        NotificationRecipientDeliveryRepository.class
                );

        auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        domainEventOutboxService =
                mock(
                        DomainEventOutboxService.class
                );

        savedDeliveries =
                new ArrayList<>();

        when(
                notificationRepository.saveAndFlush(
                        any(Notification.class)
                )
        ).thenAnswer(invocation -> {

            Notification notification =
                    invocation.getArgument(0);

            if (notification.getNotificationId() == null) {
                notification.setNotificationId(
                        NOTIFICATION_ID
                );
            }

            return notification;
        });

        when(
                notificationRecipientDeliveryRepository
                        .saveAll(
                                anyList()
                        )
        ).thenAnswer(invocation -> {

            List<NotificationRecipientDelivery> deliveries =
                    invocation.getArgument(0);

            savedDeliveries.clear();
            savedDeliveries.addAll(
                    deliveries
            );

            return deliveries;
        });

        when(
                domainEventOutboxService.persist(
                        anyString(),
                        any(UUID.class),
                        any(DomainEventEnvelope.class)
                )
        ).thenReturn(
                OUTBOX_ID
        );

        processor =
                new NotificationRequestedEventProcessor(
                        processedDomainEventRegistry,
                        userAccountLookupService,
                        notificationTemplateService,
                        notificationRepository,
                        notificationRecipientDeliveryRepository,
                        auditEventService,
                        domainEventOutboxService
                );
    }

    @Test
    void shouldIgnoreDuplicateEvent() {

        when(
                processedDomainEventRegistry.register(
                        MESSAGE_ID,
                        "Notification Service",
                        "NotificationRequested"
                )
        ).thenReturn(
                false
        );

        processor.process(
                message()
        );

        verifyNoInteractions(
                userAccountLookupService,
                notificationTemplateService,
                notificationRepository,
                notificationRecipientDeliveryRepository,
                auditEventService,
                domainEventOutboxService
        );
    }

    @Test
    void shouldFailWhenNoAuthorizedRecipientsRemain() {

        registerEvent();

        when(
                userAccountLookupService.findAuthorizedUsers(
                        ORGANIZATION_ID,
                        TENANT_ID,
                        List.of(USER_ID)
                )
        ).thenReturn(
                List.of()
        );

        processor.process(
                message()
        );

        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(
                        Notification.class
                );

        verify(
                notificationRepository,
                atLeastOnce()
        ).saveAndFlush(
                notificationCaptor.capture()
        );

        Notification notification =
                notificationCaptor
                        .getValue();

        assertEquals(
                "FAILED",
                notification.getNotificationStatus()
        );

        assertNotNull(
                notification.getProcessedAt()
        );

        verify(
                notificationTemplateService,
                never()
        ).getNotificationTemplateByScope(
                any(),
                any(),
                anyString(),
                anyString(),
                any()
        );

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).saveAll(
                anyList()
        );

        verifyRejectedAudit();

        DomainEventEnvelope envelope =
                captureCompletionEvent();

        assertEquals(
                "FAILED",
                envelope
                        .getPayload()
                        .get("notificationStatus")
        );

        assertEquals(
                0L,
                envelope
                        .getPayload()
                        .get("deliveredCount")
        );

        assertEquals(
                0L,
                envelope
                        .getPayload()
                        .get("failedCount")
        );

        assertTrue(
                ((List<?>)
                        envelope
                                .getPayload()
                                .get("deliveries"))
                        .isEmpty()
        );
    }

    @Test
    void shouldFailWhenNotificationTemplateIsMissing() {

        registerEvent();
        authorizeRecipient();

        when(
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "CASE_STATUS_CHANGED",
                                "EMAIL",
                                LANGUAGE_ID
                        )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Notification template not found for requested scope"
                )
        );

        processor.process(
                message()
        );

        verifyRejectedAudit();

        DomainEventEnvelope envelope =
                captureCompletionEvent();

        assertEquals(
                "NotificationDeliveryCompleted",
                envelope.getEventType()
        );

        assertEquals(
                "1.0",
                envelope.getSchemaVersion()
        );

        assertEquals(
                "Notification Service",
                envelope.getProducer()
        );

        assertEquals(
                MESSAGE_ID,
                envelope.getCausationId()
        );

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).saveAll(
                anyList()
        );
    }

    @Test
    void shouldFailWhenNotificationTemplateIsNotActive() {

        registerEvent();
        authorizeRecipient();

        when(
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "CASE_STATUS_CHANGED",
                                "EMAIL",
                                LANGUAGE_ID
                        )
        ).thenReturn(
                template(
                        "INACTIVE"
                )
        );

        processor.process(
                message()
        );

        verifyRejectedAudit();
        captureCompletionEvent();

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).saveAll(
                anyList()
        );
    }

    @Test
    void shouldRegisterPendingNotificationAndRecipientDeliveries() {

        registerEvent();
        authorizeRecipient();

        when(
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "CASE_STATUS_CHANGED",
                                "EMAIL",
                                LANGUAGE_ID
                        )
        ).thenReturn(
                template(
                        "ACTIVE"
                )
        );

        processor.process(
                message()
        );

        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(
                        Notification.class
                );

        verify(
                notificationRepository,
                atLeastOnce()
        ).saveAndFlush(
                notificationCaptor.capture()
        );

        Notification notification =
                notificationCaptor.getValue();

        assertEquals(
                "PENDING",
                notification.getNotificationStatus()
        );

        assertEquals(
                TEMPLATE_ID,
                notification.getNotificationTemplateId()
        );

        assertEquals(
                1,
                savedDeliveries.size()
        );

        NotificationRecipientDelivery delivery =
                savedDeliveries.get(0);

        assertEquals(
                NOTIFICATION_ID,
                delivery.getNotificationId()
        );

        assertEquals(
                USER_ID,
                delivery.getRecipientUserId()
        );

        assertEquals(
                "EMAIL",
                delivery.getChannel()
        );

        assertEquals(
                "PENDING",
                delivery.getDeliveryStatus()
        );

        verifyNoInteractions(
                auditEventService
        );

        verify(
                domainEventOutboxService,
                never()
        ).persist(
                anyString(),
                any(UUID.class),
                any(DomainEventEnvelope.class)
        );
    }

    private void registerEvent() {

        when(
                processedDomainEventRegistry.register(
                        MESSAGE_ID,
                        "Notification Service",
                        "NotificationRequested"
                )
        ).thenReturn(
                true
        );
    }

    private void authorizeRecipient() {

        when(
                userAccountLookupService.findAuthorizedUsers(
                        ORGANIZATION_ID,
                        TENANT_ID,
                        List.of(USER_ID)
                )
        ).thenReturn(
                List.of(
                        new UserAccountReference(
                                USER_ID,
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "recipient@example.com"
                        )
                )
        );
    }

    private NotificationTemplateResponse template(
            String status) {

        NotificationTemplateResponse template =
                new NotificationTemplateResponse();

        template.setNotificationTemplateId(
                TEMPLATE_ID
        );

        template.setOrganizationId(
                ORGANIZATION_ID
        );

        template.setTenantId(
                TENANT_ID
        );

        template.setLanguageId(
                LANGUAGE_ID
        );

        template.setTemplateCode(
                "CASE_STATUS_CHANGED"
        );

        template.setChannel(
                "EMAIL"
        );

        template.setStatus(
                status
        );

        return template;
    }

    private NotificationRequestedEventMessage message() {

        return new NotificationRequestedEventMessage(
                MESSAGE_ID,
                CORRELATION_ID,
                "CASE_STATUS_CHANGED",
                "CASE_STATUS_CHANGED",
                "EMAIL",
                LANGUAGE_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                "CASE",
                "CASE",
                SOURCE_ENTITY_ID,
                List.of(
                        USER_ID
                ),
                Map.of(
                        "status",
                        "CLOSED"
                )
        );
    }

    private void verifyRejectedAudit() {

        verify(
                auditEventService
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );
    }

    private DomainEventEnvelope captureCompletionEvent() {

        ArgumentCaptor<DomainEventEnvelope> envelopeCaptor =
                ArgumentCaptor.forClass(
                        DomainEventEnvelope.class
                );

        verify(
                domainEventOutboxService
        ).persist(
                eq("Notification"),
                eq(NOTIFICATION_ID),
                envelopeCaptor.capture()
        );

        return envelopeCaptor.getValue();
    }
}