package com.efs.modules.notification.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryTerminalizationServiceTest {

    @Mock
    private NotificationRepository
            notificationRepository;

    @Mock
    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    @Mock
    private AuditEventServiceInterface
            auditEventService;

    @Mock
    private DomainEventOutboxService
            domainEventOutboxService;

    private NotificationDeliveryTerminalizationService service;

    private UUID notificationId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID correlationId;
    private UUID sourceEntityId;
    private UUID recipientUserId;

    @BeforeEach
    void setUp() {

        notificationId =
                UUID.randomUUID();

        organizationId =
                UUID.randomUUID();

        tenantId =
                UUID.randomUUID();

        correlationId =
                UUID.randomUUID();

        sourceEntityId =
                UUID.randomUUID();

        recipientUserId =
                UUID.randomUUID();

        service =
                new NotificationDeliveryTerminalizationService(
                        notificationRepository,
                        notificationRecipientDeliveryRepository,
                        auditEventService,
                        domainEventOutboxService
                );
    }

    @Test
    void shouldTerminalizeControlledPreflightRejection() {

        Notification notification =
                createPendingNotification();

        NotificationRecipientDelivery delivery =
                createPendingDelivery();

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
        ).thenReturn(
                Optional.of(
                        notification
                )
        );

        when(
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        )
        ).thenReturn(
                List.of(
                        delivery
                )
        );

        doAnswer(
                invocation -> {

                    Notification processing =
                            invocation.getArgument(0);

                    assertEquals(
                            "PROCESSING",
                            processing.getNotificationStatus()
                    );

                    assertNull(
                            processing.getProcessedAt()
                    );

                    return processing;
                }
        ).when(
                notificationRepository
        ).saveAndFlush(
                notification
        );

        service.rejectPreflight(
                notificationId,
                message,
                "DELIVERY_CONFIGURATION_UNAVAILABLE"
        );

        assertEquals(
                "FAILED",
                notification.getNotificationStatus()
        );

        assertNotNull(
                notification.getProcessedAt()
        );

        assertEquals(
                "FAILED",
                delivery.getDeliveryStatus()
        );

        assertNull(
                delivery.getDeliveryReference()
        );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                delivery.getDeliveryResult()
        );

        assertNotNull(
                delivery.getProcessedAt()
        );

        assertEquals(
                notification.getProcessedAt(),
                delivery.getProcessedAt()
        );

        verify(
                notificationRecipientDeliveryRepository
        ).saveAll(
                List.of(
                        delivery
                )
        );

        verify(
                notificationRepository
        ).save(
                notification
        );

        ArgumentCaptor<AuditEventRequest> auditCaptor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                auditCaptor.capture()
        );

        AuditEventRequest audit =
                auditCaptor.getValue();

        assertEquals(
                tenantId,
                audit.getTenantId()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY",
                audit.getEventType()
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
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                audit.getEventDetails()
                        .get("reason")
        );

        ArgumentCaptor<DomainEventEnvelope> eventCaptor =
                ArgumentCaptor.forClass(
                        DomainEventEnvelope.class
                );

        verify(
                domainEventOutboxService
        ).persist(
                org.mockito.ArgumentMatchers.eq(
                        "Notification"
                ),
                org.mockito.ArgumentMatchers.eq(
                        notificationId
                ),
                eventCaptor.capture()
        );

        DomainEventEnvelope envelope =
                eventCaptor.getValue();

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
                correlationId,
                envelope.getCorrelationId()
        );

        assertEquals(
                message.messageId(),
                envelope.getCausationId()
        );

        assertEquals(
                tenantId,
                envelope.getTenantId()
        );

        Map<String, Object> payload =
                envelope.getPayload();

        assertEquals(
                notificationId.toString(),
                payload.get(
                        "notificationId"
                )
        );

        assertEquals(
                "FAILED",
                payload.get(
                        "notificationStatus"
                )
        );

        assertEquals(
                0L,
                payload.get(
                        "deliveredCount"
                )
        );

        assertEquals(
                1L,
                payload.get(
                        "failedCount"
                )
        );
    }

    @Test
    void shouldAcceptNoAuthorizedRecipientsReason() {

        Notification notification =
                createPendingNotification();

        NotificationRecipientDelivery delivery =
                createPendingDelivery();

        when(
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
        ).thenReturn(
                Optional.of(
                        notification
                )
        );

        when(
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        )
        ).thenReturn(
                List.of(
                        delivery
                )
        );

        service.rejectPreflight(
                notificationId,
                createMessage(),
                "NO_AUTHORIZED_RECIPIENTS"
        );

        assertEquals(
                "FAILED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "FAILED",
                delivery.getDeliveryStatus()
        );

        assertEquals(
                "NO_AUTHORIZED_RECIPIENTS",
                delivery.getDeliveryResult()
        );
    }

    @Test
    void shouldRejectUnsupportedReasonBeforePersistence() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.rejectPreflight(
                                notificationId,
                                createMessage(),
                                "NOTIFICATION_DELIVERY_FAILED"
                        )
        );

        verify(
                notificationRepository,
                never()
        ).findByNotificationIdForUpdate(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectMissingNotification() {

        when(
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.rejectPreflight(
                                notificationId,
                                createMessage(),
                                "DELIVERY_CONFIGURATION_UNAVAILABLE"
                        )
        );

        verify(
                auditEventService,
                never()
        ).createAuditEvent(
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                domainEventOutboxService,
                never()
        ).persist(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectNotificationThatIsNotPending() {

        Notification notification =
                createPendingNotification();

        notification.setNotificationStatus(
                "PROCESSING"
        );

        when(
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
        ).thenReturn(
                Optional.of(
                        notification
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.rejectPreflight(
                                notificationId,
                                createMessage(),
                                "DELIVERY_CONFIGURATION_UNAVAILABLE"
                        )
        );

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).findByNotificationIdOrderByCreatedAtAsc(
                notificationId
        );
    }

    @Test
    void shouldRejectNonPendingRecipientDelivery() {

        Notification notification =
                createPendingNotification();

        NotificationRecipientDelivery delivery =
                createPendingDelivery();

        delivery.setDeliveryStatus(
                "DELIVERED"
        );

        when(
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
        ).thenReturn(
                Optional.of(
                        notification
                )
        );

        when(
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        )
        ).thenReturn(
                List.of(
                        delivery
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.rejectPreflight(
                                notificationId,
                                createMessage(),
                                "DELIVERY_CONFIGURATION_UNAVAILABLE"
                        )
        );

        assertEquals(
                "PENDING",
                notification.getNotificationStatus()
        );

        verify(
                notificationRepository,
                never()
        ).saveAndFlush(
                notification
        );
    }

    @Test
    void shouldDeclareTransactionalTerminalizationBoundary()
            throws Exception {

        Method method =
                NotificationDeliveryTerminalizationService.class
                        .getMethod(
                                "rejectPreflight",
                                UUID.class,
                                NotificationRequestedEventMessage.class,
                                String.class
                        );

        assertTrue(
                method.isAnnotationPresent(
                        Transactional.class
                )
        );
    }

    private Notification createPendingNotification() {

        Notification notification =
                new Notification();

        notification.setNotificationId(
                notificationId
        );

        notification.setOrganizationId(
                organizationId
        );

        notification.setTenantId(
                tenantId
        );

        notification.setNotificationType(
                "CASE_CREATED"
        );

        notification.setNotificationTemplateId(
                UUID.randomUUID()
        );

        notification.setSourceComponent(
                "CASE"
        );

        notification.setSourceEntityType(
                "CASE"
        );

        notification.setSourceEntityId(
                sourceEntityId
        );

        notification.setCorrelationId(
                correlationId
        );

        notification.setNotificationStatus(
                "PENDING"
        );

        return notification;
    }

    private NotificationRecipientDelivery
            createPendingDelivery() {

        NotificationRecipientDelivery delivery =
                new NotificationRecipientDelivery();

        delivery.setNotificationDeliveryId(
                UUID.randomUUID()
        );

        delivery.setNotificationId(
                notificationId
        );

        delivery.setRecipientUserId(
                recipientUserId
        );

        delivery.setChannel(
                "EMAIL"
        );

        delivery.setDeliveryStatus(
                "PENDING"
        );

        return delivery;
    }

    private NotificationRequestedEventMessage
            createMessage() {

        return new NotificationRequestedEventMessage(
                UUID.randomUUID(),
                correlationId,
                "CASE_CREATED",
                "CASE_CREATED",
                "EMAIL",
                UUID.randomUUID(),
                organizationId,
                tenantId,
                "CASE",
                "CASE",
                sourceEntityId,
                List.of(
                        recipientUserId
                ),
                Map.of(
                        "caseNumber",
                        "CASE-1"
                )
        );
    }
}