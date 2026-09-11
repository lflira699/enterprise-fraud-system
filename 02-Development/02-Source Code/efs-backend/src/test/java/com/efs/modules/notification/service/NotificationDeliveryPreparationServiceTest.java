package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.PreparedNotificationDelivery;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryPreparationServiceTest {

    @Mock
    private NotificationRepository
            notificationRepository;

    @Mock
    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    @Mock
    private NotificationDeliveryPreflightService
            notificationDeliveryPreflightService;

    private NotificationDeliveryPreparationService service;

    private UUID notificationId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID correlationId;
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

        recipientUserId =
                UUID.randomUUID();

        service =
                new NotificationDeliveryPreparationService(
                        notificationRepository,
                        notificationRecipientDeliveryRepository,
                        notificationDeliveryPreflightService
                );
    }

    @Test
    void shouldPreparePendingNotificationAndTransitionToProcessing() {

        Notification notification =
                createPendingNotification();

        NotificationRecipientDelivery delivery =
                createPendingDelivery();

        NotificationRequestedEventMessage message =
                createMessage();

        NotificationDeliveryPreflightResult expected =
                new NotificationDeliveryPreflightResult(
                        notificationId,
                        organizationId,
                        tenantId,
                        correlationId,
                        "Subject",
                        "Body",
                        List.of(
                                new PreparedNotificationDelivery(
                                        delivery.getNotificationDeliveryId(),
                                        recipientUserId,
                                        "EMAIL",
                                        "user@example.com"
                                )
                        )
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

        when(
                notificationDeliveryPreflightService
                        .prepare(
                                notification,
                                List.of(
                                        delivery
                                ),
                                message
                        )
        ).thenReturn(
                expected
        );

        NotificationDeliveryPreflightResult actual =
                service.prepare(
                        notificationId,
                        message
                );

        assertEquals(
                expected,
                actual
        );

        assertEquals(
                "PROCESSING",
                notification.getNotificationStatus()
        );

        verify(
                notificationRepository
        ).save(
                notification
        );
    }

    @Test
    void shouldRejectMissingNotification() {

        NotificationRequestedEventMessage message =
                createMessage();

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
                        service.prepare(
                                notificationId,
                                message
                        )
        );

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).findByNotificationIdOrderByCreatedAtAsc(
                notificationId
        );

        verify(
                notificationDeliveryPreflightService,
                never()
        ).prepare(
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
                        service.prepare(
                                notificationId,
                                createMessage()
                        )
        );

        verify(
                notificationRecipientDeliveryRepository,
                never()
        ).findByNotificationIdOrderByCreatedAtAsc(
                notificationId
        );

        verify(
                notificationRepository,
                never()
        ).save(
                notification
        );
    }

    @Test
    void shouldNotTransitionWhenPreflightFails() {

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

        when(
                notificationDeliveryPreflightService
                        .prepare(
                                notification,
                                List.of(
                                        delivery
                                ),
                                message
                        )
        ).thenThrow(
                new NotificationDeliveryPreparationException(
                        "DELIVERY_CONFIGURATION_UNAVAILABLE"
                )
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notificationId,
                                        message
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );

        assertEquals(
                "PENDING",
                notification.getNotificationStatus()
        );

        verify(
                notificationRepository,
                never()
        ).save(
                notification
        );
    }

    @Test
    void shouldRequireNotificationId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.prepare(
                                null,
                                createMessage()
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
    void shouldRequireMessage() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.prepare(
                                notificationId,
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

    @Test
    void shouldDeclareTransactionalPreparationBoundary()
            throws Exception {

        Method method =
                NotificationDeliveryPreparationService.class
                        .getMethod(
                                "prepare",
                                UUID.class,
                                NotificationRequestedEventMessage.class
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
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                )
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
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                ),
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