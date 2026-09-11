package com.efs.modules.notification.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryUnexpectedFailureTerminalizationTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    @Mock
    private AuditEventServiceInterface auditEventService;

    @Mock
    private DomainEventOutboxService domainEventOutboxService;

    private NotificationDeliveryTerminalizationService service;

    private UUID notificationId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID correlationId;
    private UUID sourceEntityId;

    @BeforeEach
    void setUp() {

        notificationId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        correlationId = UUID.randomUUID();
        sourceEntityId = UUID.randomUUID();

        service =
                new NotificationDeliveryTerminalizationService(
                        notificationRepository,
                        notificationRecipientDeliveryRepository,
                        auditEventService,
                        domainEventOutboxService
                );
    }

    @Test
    void shouldPreserveConfirmedSuccessAndFailRemainingDeliveries() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery confirmed =
                createPendingDelivery();

        NotificationRecipientDelivery failed =
                createPendingDelivery();

        NotificationRecipientDelivery unprocessed =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        confirmed,
                        failed,
                        unprocessed
                )
        );

        Map<UUID, ExternalNotificationDeliveryResult>
                confirmedResults =
                new LinkedHashMap<>();

        confirmedResults.put(
                confirmed.getNotificationDeliveryId(),
                deliveredResult(
                        "REF-1"
                )
        );

        service.completeUnexpectedFailure(
                notificationId,
                createMessage(),
                confirmedResults,
                failed.getNotificationDeliveryId()
        );

        assertEquals(
                "PARTIALLY_DELIVERED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "DELIVERED",
                confirmed.getDeliveryStatus()
        );

        assertEquals(
                "REF-1",
                confirmed.getDeliveryReference()
        );

        assertEquals(
                "DELIVERED",
                confirmed.getDeliveryResult()
        );

        assertEquals(
                "FAILED",
                failed.getDeliveryStatus()
        );

        assertNull(
                failed.getDeliveryReference()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY_FAILED",
                failed.getDeliveryResult()
        );

        assertEquals(
                "FAILED",
                unprocessed.getDeliveryStatus()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY_FAILED",
                unprocessed.getDeliveryResult()
        );

        verifyFailureAudit();

        ArgumentCaptor<DomainEventEnvelope> eventCaptor =
                captureCompletionEvent();

        assertEquals(
                "PARTIALLY_DELIVERED",
                eventCaptor.getValue()
                        .getPayload()
                        .get("notificationStatus")
        );

        assertEquals(
                1L,
                eventCaptor.getValue()
                        .getPayload()
                        .get("deliveredCount")
        );

        assertEquals(
                2L,
                eventCaptor.getValue()
                        .getPayload()
                        .get("failedCount")
        );
    }

    @Test
    void shouldFailAllWhenFirstDeliveryFailsUnexpectedly() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery failed =
                createPendingDelivery();

        NotificationRecipientDelivery unprocessed =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        failed,
                        unprocessed
                )
        );

        service.completeUnexpectedFailure(
                notificationId,
                createMessage(),
                Map.of(),
                failed.getNotificationDeliveryId()
        );

        assertEquals(
                "FAILED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "FAILED",
                failed.getDeliveryStatus()
        );

        assertEquals(
                "FAILED",
                unprocessed.getDeliveryStatus()
        );

        verifyFailureAudit();
    }

    @Test
    void shouldPreserveConfirmedProviderFailure() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery confirmedFailed =
                createPendingDelivery();

        NotificationRecipientDelivery unexpectedFailed =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        confirmedFailed,
                        unexpectedFailed
                )
        );

        service.completeUnexpectedFailure(
                notificationId,
                createMessage(),
                Map.of(
                        confirmedFailed.getNotificationDeliveryId(),
                        failedResult(
                                "PROVIDER_REJECTED"
                        )
                ),
                unexpectedFailed.getNotificationDeliveryId()
        );

        assertEquals(
                "FAILED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "PROVIDER_REJECTED",
                confirmedFailed.getDeliveryResult()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY_FAILED",
                unexpectedFailed.getDeliveryResult()
        );

        verifyFailureAudit();
    }

    @Test
    void shouldRejectFailedDeliveryThatDoesNotBelongToNotification() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery delivery =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        delivery
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.completeUnexpectedFailure(
                                notificationId,
                                createMessage(),
                                Map.of(),
                                UUID.randomUUID()
                        )
        );

        verify(
                auditEventService,
                never()
        ).createAuditEvent(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectFailedDeliveryAlreadyConfirmed() {

        UUID deliveryId =
                UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.completeUnexpectedFailure(
                                notificationId,
                                createMessage(),
                                Map.of(
                                        deliveryId,
                                        deliveredResult(
                                                "REF-1"
                                        )
                                ),
                                deliveryId
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
    void shouldRejectConfirmedResultForUnknownDelivery() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery failed =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        failed
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.completeUnexpectedFailure(
                                notificationId,
                                createMessage(),
                                Map.of(
                                        UUID.randomUUID(),
                                        deliveredResult(
                                                "REF-X"
                                        )
                                ),
                                failed.getNotificationDeliveryId()
                        )
        );
    }

    private void arrange(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries) {

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
                deliveries
        );
    }

    private void verifyFailureAudit() {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY_FAILED",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "NOTIFICATION_DELIVERY",
                audit.getEventType()
        );

        assertEquals(
                notificationId,
                audit.getEntityId()
        );
    }

    private ArgumentCaptor<DomainEventEnvelope>
            captureCompletionEvent() {

        ArgumentCaptor<DomainEventEnvelope> captor =
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
                captor.capture()
        );

        return captor;
    }

    private Notification createProcessingNotification() {

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
                "PROCESSING"
        );

        return notification;
    }

    private NotificationRecipientDelivery createPendingDelivery() {

        NotificationRecipientDelivery delivery =
                new NotificationRecipientDelivery();

        delivery.setNotificationDeliveryId(
                UUID.randomUUID()
        );

        delivery.setNotificationId(
                notificationId
        );

        delivery.setRecipientUserId(
                UUID.randomUUID()
        );

        delivery.setChannel(
                "EMAIL"
        );

        delivery.setDeliveryStatus(
                "PENDING"
        );

        return delivery;
    }

    private ExternalNotificationDeliveryResult deliveredResult(
            String reference) {

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        result.setDelivered(
                true
        );

        result.setDeliveryReference(
                reference
        );

        result.setDeliveryResult(
                "DELIVERED"
        );

        return result;
    }

    private ExternalNotificationDeliveryResult failedResult(
            String resultCode) {

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        result.setDelivered(
                false
        );

        result.setDeliveryReference(
                null
        );

        result.setDeliveryResult(
                resultCode
        );

        return result;
    }

    private NotificationRequestedEventMessage createMessage() {

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
                List.of(),
                Map.of()
        );
    }
}