package com.efs.modules.notification.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.modules.integration.service.ExternalNotificationDeliveryAvailabilityServiceInterface;
import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.RenderedNotificationContent;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryPreflightServiceTest {

    @Mock
    private NotificationTemplateServiceInterface
            notificationTemplateService;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private NotificationTemplateRenderer
            notificationTemplateRenderer;

    @Mock
    private NotificationDestinationResolver
            notificationDestinationResolver;

    @Mock
    private ExternalNotificationDeliveryAvailabilityServiceInterface
            externalNotificationDeliveryAvailabilityService;

    private NotificationDeliveryPreflightService service;

    private UUID organizationId;
    private UUID tenantId;
    private UUID languageId;
    private UUID notificationId;
    private UUID templateId;
    private UUID recipientUserId;

    @BeforeEach
    void setUp() {

        organizationId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        languageId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        templateId = UUID.randomUUID();
        recipientUserId = UUID.randomUUID();

        service =
                new NotificationDeliveryPreflightService(
                        notificationTemplateService,
                        userAccountLookupService,
                        notificationTemplateRenderer,
                        notificationDestinationResolver,
                        externalNotificationDeliveryAvailabilityService
                );
    }

    @Test
    void shouldPrepareNotificationDelivery() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        NotificationRequestedEventMessage message =
                createMessage(
                        List.of(
                                recipientUserId
                        )
                );

        NotificationTemplateResponse template =
                createTemplate(
                        "ACTIVE"
                );

        UserAccountReference recipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        "user@example.com"
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                template
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

        when(
                notificationTemplateRenderer
                        .render(
                                "Case {{caseNumber}} created",
                                "Case {{caseNumber}} has been created.",
                                Map.of(
                                        "caseNumber",
                                        "CASE-1"
                                )
                        )
        ).thenReturn(
                new RenderedNotificationContent(
                        "Case CASE-1 created",
                        "Case CASE-1 has been created."
                )
        );

        when(
                notificationDestinationResolver
                        .resolve(
                                "EMAIL",
                                recipient
                        )
        ).thenReturn(
                "user@example.com"
        );

        when(
                externalNotificationDeliveryAvailabilityService
                        .isAvailable(
                                organizationId,
                                tenantId,
                                "EMAIL"
                        )
        ).thenReturn(
                true
        );

        NotificationDeliveryPreflightResult result =
                service.prepare(
                        notification,
                        List.of(
                                delivery
                        ),
                        message
                );

        assertEquals(
                notificationId,
                result.notificationId()
        );

        assertEquals(
                "Case CASE-1 created",
                result.subject()
        );

        assertEquals(
                "Case CASE-1 has been created.",
                result.body()
        );

        assertEquals(
                1,
                result.deliveries().size()
        );

        assertEquals(
                "user@example.com",
                result.deliveries()
                        .get(0)
                        .destination()
        );
    }

    @Test
    void shouldRejectInactiveTemplate() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                createTemplate(
                        "INACTIVE"
                )
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                delivery
                                        ),
                                        createMessage(
                                                List.of(
                                                        recipientUserId
                                                )
                                        )
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );

        verify(
                userAccountLookupService,
                never()
        ).findAuthorizedUsers(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectWhenNoAuthorizedRecipientsRemain() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                createTemplate(
                        "ACTIVE"
                )
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
                List.of()
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                delivery
                                        ),
                                        createMessage(
                                                List.of(
                                                        recipientUserId
                                                )
                                        )
                                )
                );

        assertEquals(
                "NO_AUTHORIZED_RECIPIENTS",
                exception.getReason()
        );
    }

    @Test
    void shouldRejectWhenRecipientIsNoLongerAuthorized() {

        UUID secondRecipient =
                UUID.randomUUID();

        Notification notification =
                createNotification();

        NotificationRecipientDelivery firstDelivery =
                createDelivery(
                        recipientUserId
                );

        NotificationRecipientDelivery secondDelivery =
                createDelivery(
                        secondRecipient
                );

        UserAccountReference firstRecipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        "first@example.com"
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                createTemplate(
                        "ACTIVE"
                )
        );

        when(
                userAccountLookupService
                        .findAuthorizedUsers(
                                organizationId,
                                tenantId,
                                List.of(
                                        recipientUserId,
                                        secondRecipient
                                )
                        )
        ).thenReturn(
                List.of(
                        firstRecipient
                )
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                firstDelivery,
                                                secondDelivery
                                        ),
                                        createMessage(
                                                List.of(
                                                        recipientUserId,
                                                        secondRecipient
                                                )
                                        )
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );
    }

    @Test
    void shouldRejectTemplateRenderingFailure() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        UserAccountReference recipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        "user@example.com"
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                createTemplate(
                        "ACTIVE"
                )
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

        when(
                notificationTemplateRenderer
                        .render(
                                org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.any()
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "Missing parameter"
                )
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                delivery
                                        ),
                                        createMessage(
                                                List.of(
                                                        recipientUserId
                                                )
                                        )
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );
    }

    @Test
    void shouldRejectDestinationResolutionFailure() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        UserAccountReference recipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        null
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                createTemplate(
                        "ACTIVE"
                )
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

        when(
                notificationTemplateRenderer
                        .render(
                                org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.any()
                        )
        ).thenReturn(
                new RenderedNotificationContent(
                        "Subject",
                        "Body"
                )
        );

        when(
                notificationDestinationResolver
                        .resolve(
                                "EMAIL",
                                recipient
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "Recipient email is required"
                )
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                delivery
                                        ),
                                        createMessage(
                                                List.of(
                                                        recipientUserId
                                                )
                                        )
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );
    }

    @Test
    void shouldRejectWhenExternalDeliveryIsUnavailable() {

        Notification notification =
                createNotification();

        NotificationRecipientDelivery delivery =
                createDelivery(
                        recipientUserId
                );

        NotificationRequestedEventMessage message =
                createMessage(
                        List.of(
                                recipientUserId
                        )
                );

        NotificationTemplateResponse template =
                createTemplate(
                        "ACTIVE"
                );

        UserAccountReference recipient =
                new UserAccountReference(
                        recipientUserId,
                        organizationId,
                        tenantId,
                        "user@example.com"
                );

        when(
                notificationTemplateService
                        .getNotificationTemplateById(
                                templateId
                        )
        ).thenReturn(
                template
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

        when(
                notificationTemplateRenderer
                        .render(
                                "Case {{caseNumber}} created",
                                "Case {{caseNumber}} has been created.",
                                Map.of(
                                        "caseNumber",
                                        "CASE-1"
                                )
                        )
        ).thenReturn(
                new RenderedNotificationContent(
                        "Case CASE-1 created",
                        "Case CASE-1 has been created."
                )
        );

        when(
                notificationDestinationResolver
                        .resolve(
                                "EMAIL",
                                recipient
                        )
        ).thenReturn(
                "user@example.com"
        );

        when(
                externalNotificationDeliveryAvailabilityService
                        .isAvailable(
                                organizationId,
                                tenantId,
                                "EMAIL"
                        )
        ).thenReturn(
                false
        );

        NotificationDeliveryPreparationException exception =
                assertThrows(
                        NotificationDeliveryPreparationException.class,
                        () ->
                                service.prepare(
                                        notification,
                                        List.of(
                                                delivery
                                        ),
                                        message
                                )
                );

        assertEquals(
                "DELIVERY_CONFIGURATION_UNAVAILABLE",
                exception.getReason()
        );

        verify(
                externalNotificationDeliveryAvailabilityService
        ).isAvailable(
                organizationId,
                tenantId,
                "EMAIL"
        );
    }
    private Notification createNotification() {

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
                templateId
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
                UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                )
        );

        notification.setNotificationStatus(
                "PENDING"
        );

        return notification;
    }

    private NotificationRecipientDelivery createDelivery(
            UUID userId) {

        NotificationRecipientDelivery delivery =
                new NotificationRecipientDelivery();

        delivery.setNotificationDeliveryId(
                UUID.randomUUID()
        );

        delivery.setNotificationId(
                notificationId
        );

        delivery.setRecipientUserId(
                userId
        );

        delivery.setChannel(
                "EMAIL"
        );

        delivery.setDeliveryStatus(
                "PENDING"
        );

        return delivery;
    }

    private NotificationRequestedEventMessage createMessage(
            List<UUID> recipients) {

        return new NotificationRequestedEventMessage(
                UUID.randomUUID(),
                UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                ),
                "CASE_CREATED",
                "CASE_CREATED",
                "EMAIL",
                languageId,
                organizationId,
                tenantId,
                "CASE",
                "CASE",
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                ),
                recipients,
                Map.of(
                        "caseNumber",
                        "CASE-1"
                )
        );
    }

    private NotificationTemplateResponse createTemplate(
            String status) {

        NotificationTemplateResponse template =
                new NotificationTemplateResponse();

        template.setNotificationTemplateId(
                templateId
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
                "CASE_CREATED"
        );

        template.setChannel(
                "EMAIL"
        );

        template.setSubjectTemplate(
                "Case {{caseNumber}} created"
        );

        template.setBodyTemplate(
                "Case {{caseNumber}} has been created."
        );

        template.setStatus(
                status
        );

        return template;
    }
}