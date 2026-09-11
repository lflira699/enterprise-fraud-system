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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryConfirmedResultsTerminalizationTest {

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
    void shouldTerminalizeAllConfirmedDeliveriesAsDelivered() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery first =
                createPendingDelivery();

        NotificationRecipientDelivery second =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        first,
                        second
                )
        );

        Map<UUID, ExternalNotificationDeliveryResult> results =
                new LinkedHashMap<>();

        results.put(
                first.getNotificationDeliveryId(),
                deliveredResult(
                        "REF-1"
                )
        );

        results.put(
                second.getNotificationDeliveryId(),
                deliveredResult(
                        "REF-2"
                )
        );

        service.completeConfirmedResults(
                notificationId,
                createMessage(),
                results
        );

        assertEquals(
                "DELIVERED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "DELIVERED",
                first.getDeliveryStatus()
        );

        assertEquals(
                "DELIVERED",
                second.getDeliveryStatus()
        );

        assertNotNull(
                notification.getProcessedAt()
        );

        verifySuccessAudit();

        ArgumentCaptor<DomainEventEnvelope> captor =
                captureCompletionEvent();

        assertEquals(
                "DELIVERED",
                captor.getValue()
                        .getPayload()
                        .get("notificationStatus")
        );

        assertEquals(
                2L,
                captor.getValue()
                        .getPayload()
                        .get("deliveredCount")
        );

        assertEquals(
                0L,
                captor.getValue()
                        .getPayload()
                        .get("failedCount")
        );
    }

    @Test
    void shouldTerminalizeMixedConfirmedResultsAsPartiallyDelivered() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery delivered =
                createPendingDelivery();

        NotificationRecipientDelivery failed =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        delivered,
                        failed
                )
        );

        Map<UUID, ExternalNotificationDeliveryResult> results =
                new LinkedHashMap<>();

        results.put(
                delivered.getNotificationDeliveryId(),
                deliveredResult(
                        "REF-1"
                )
        );

        results.put(
                failed.getNotificationDeliveryId(),
                failedResult(
                        "PROVIDER_REJECTED"
                )
        );

        service.completeConfirmedResults(
                notificationId,
                createMessage(),
                results
        );

        assertEquals(
                "PARTIALLY_DELIVERED",
                notification.getNotificationStatus()
        );

        assertEquals(
                "DELIVERED",
                delivered.getDeliveryStatus()
        );

        assertEquals(
                "FAILED",
                failed.getDeliveryStatus()
        );

        verifySuccessAudit();
    }

    @Test
    void shouldTerminalizeAllConfirmedFailuresAsFailedWithSuccessAudit() {

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

        service.completeConfirmedResults(
                notificationId,
                createMessage(),
                Map.of(
                        delivery.getNotificationDeliveryId(),
                        failedResult(
                                "PROVIDER_REJECTED"
                        )
                )
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
                "PROVIDER_REJECTED",
                delivery.getDeliveryResult()
        );

        verifySuccessAudit();
    }

    @Test
    void shouldRejectIncompleteConfirmedResults() {

        Notification notification =
                createProcessingNotification();

        NotificationRecipientDelivery first =
                createPendingDelivery();

        NotificationRecipientDelivery second =
                createPendingDelivery();

        arrange(
                notification,
                List.of(
                        first,
                        second
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.completeConfirmedResults(
                                notificationId,
                                createMessage(),
                                Map.of(
                                        first.getNotificationDeliveryId(),
                                        deliveredResult(
                                                "REF-1"
                                        )
                                )
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
    void shouldRejectResultForUnknownDelivery() {

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
                        service.completeConfirmedResults(
                                notificationId,
                                createMessage(),
                                Map.of(
                                        UUID.randomUUID(),
                                        deliveredResult(
                                                "REF-X"
                                        )
                                )
                        )
        );
    }

    @Test
    void shouldRejectNotificationThatIsNotProcessing() {

        Notification notification =
                createProcessingNotification();

        notification.setNotificationStatus(
                "PENDING"
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
                        service.completeConfirmedResults(
                                notificationId,
                                createMessage(),
                                Map.of()
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
    void shouldRequireConfirmedResults() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.completeConfirmedResults(
                                notificationId,
                                createMessage(),
                                null
                        )
        );

        verify(
                notificationRepository,
                never()
        ).findByNotificationIdForUpdate(
                org.mockito.ArgumentMatchers.any()
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

    private void verifySuccessAudit() {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        assertEquals(
                "SUCCESS",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "NOTIFICATION_DELIVERY",
                captor.getValue()
                        .getEventType()
        );

        assertEquals(
                "NOTIFICATION",
                captor.getValue()
                        .getEntityType()
        );

        assertEquals(
                notificationId,
                captor.getValue()
                        .getEntityId()
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