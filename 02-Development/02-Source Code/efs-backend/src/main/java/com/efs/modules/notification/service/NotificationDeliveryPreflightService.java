package com.efs.modules.notification.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.modules.integration.service.ExternalNotificationDeliveryAvailabilityServiceInterface;
import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.PreparedNotificationDelivery;
import com.efs.modules.notification.dto.RenderedNotificationContent;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class NotificationDeliveryPreflightService {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String DELIVERY_STATUS_PENDING =
            "PENDING";

    private static final String ACTIVE_TEMPLATE_STATUS =
            "ACTIVE";

    private static final String NO_AUTHORIZED_RECIPIENTS =
            "NO_AUTHORIZED_RECIPIENTS";

    private static final String DELIVERY_CONFIGURATION_UNAVAILABLE =
            "DELIVERY_CONFIGURATION_UNAVAILABLE";

    private final NotificationTemplateServiceInterface
            notificationTemplateService;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final NotificationTemplateRenderer
            notificationTemplateRenderer;

    private final NotificationDestinationResolver
            notificationDestinationResolver;

    private final ExternalNotificationDeliveryAvailabilityServiceInterface
            externalNotificationDeliveryAvailabilityService;

    public NotificationDeliveryPreflightService(
            NotificationTemplateServiceInterface notificationTemplateService,
            UserAccountLookupServiceInterface userAccountLookupService,
            NotificationTemplateRenderer notificationTemplateRenderer,
            NotificationDestinationResolver notificationDestinationResolver,
            ExternalNotificationDeliveryAvailabilityServiceInterface
                    externalNotificationDeliveryAvailabilityService) {

        this.notificationTemplateService =
                notificationTemplateService;

        this.userAccountLookupService =
                userAccountLookupService;

        this.notificationTemplateRenderer =
                notificationTemplateRenderer;

        this.notificationDestinationResolver =
                notificationDestinationResolver;

        this.externalNotificationDeliveryAvailabilityService =
                externalNotificationDeliveryAvailabilityService;
    }

    public NotificationDeliveryPreflightResult prepare(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries,
            NotificationRequestedEventMessage message) {

        requireInputs(
                notification,
                deliveries,
                message
        );

        requirePendingNotification(
                notification
        );

        requireMessageContext(
                notification,
                message
        );

        requirePendingDeliveries(
                notification,
                deliveries,
                message
        );

        NotificationTemplateResponse template =
                resolveTemplate(
                        notification,
                        message
                );

        List<UUID> recipientUserIds =
                deliveries
                        .stream()
                        .map(
                                NotificationRecipientDelivery
                                        ::getRecipientUserId
                        )
                        .toList();

        List<UserAccountReference> authorizedUsers =
                userAccountLookupService
                        .findAuthorizedUsers(
                                notification.getOrganizationId(),
                                notification.getTenantId(),
                                recipientUserIds
                        );

        if (authorizedUsers.isEmpty()) {
            throw controlledFailure(
                    NO_AUTHORIZED_RECIPIENTS
            );
        }

        Map<UUID, UserAccountReference>
                authorizedUsersById =
                indexAuthorizedUsers(
                        authorizedUsers
                );

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            if (!authorizedUsersById.containsKey(
                    delivery.getRecipientUserId()
            )) {

                throw controlledFailure(
                        DELIVERY_CONFIGURATION_UNAVAILABLE
                );
            }
        }

        RenderedNotificationContent renderedContent;

        try {

            renderedContent =
                    notificationTemplateRenderer
                            .render(
                                    template.getSubjectTemplate(),
                                    template.getBodyTemplate(),
                                    message.templateParameters()
                            );

        } catch (IllegalArgumentException exception) {

            throw controlledFailure(
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );
        }

        List<PreparedNotificationDelivery>
                preparedDeliveries =
                new ArrayList<>();

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            UserAccountReference recipient =
                    authorizedUsersById.get(
                            delivery.getRecipientUserId()
                    );

            String destination;

            try {

                destination =
                        notificationDestinationResolver
                                .resolve(
                                        delivery.getChannel(),
                                        recipient
                                );

            } catch (IllegalArgumentException exception) {

                throw controlledFailure(
                        DELIVERY_CONFIGURATION_UNAVAILABLE
                );
            }

            preparedDeliveries.add(
                    new PreparedNotificationDelivery(
                            delivery.getNotificationDeliveryId(),
                            delivery.getRecipientUserId(),
                            delivery.getChannel(),
                            destination
                    )
            );
        }

        boolean deliveryAvailable =
                externalNotificationDeliveryAvailabilityService
                        .isAvailable(
                                notification.getOrganizationId(),
                                notification.getTenantId(),
                                message.channel()
                        );

        if (!deliveryAvailable) {
            throw controlledFailure(
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );
        }

        return new NotificationDeliveryPreflightResult(
                notification.getNotificationId(),
                notification.getOrganizationId(),
                notification.getTenantId(),
                notification.getCorrelationId(),
                renderedContent.subject(),
                renderedContent.body(),
                preparedDeliveries
        );
    }

    private void requireInputs(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries,
            NotificationRequestedEventMessage message) {

        if (notification == null) {
            throw new IllegalArgumentException(
                    "Notification is required"
            );
        }

        if (deliveries == null) {
            throw new IllegalArgumentException(
                    "Notification deliveries are required"
            );
        }

        if (message == null) {
            throw new IllegalArgumentException(
                    "NotificationRequested event message is required"
            );
        }
    }

    private void requirePendingNotification(
            Notification notification) {

        if (!STATUS_PENDING.equals(
                notification.getNotificationStatus()
        )) {

            throw new IllegalStateException(
                    "Notification must be PENDING for delivery preparation"
            );
        }

        if (notification.getNotificationId() == null) {
            throw new IllegalStateException(
                    "Notification id is required for delivery preparation"
            );
        }

        if (notification.getNotificationTemplateId() == null) {

            throw controlledFailure(
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );
        }
    }

    private void requireMessageContext(
            Notification notification,
            NotificationRequestedEventMessage message) {

        boolean valid =
                Objects.equals(
                        notification.getOrganizationId(),
                        message.organizationId()
                )
                && Objects.equals(
                        notification.getTenantId(),
                        message.tenantId()
                )
                && Objects.equals(
                        notification.getNotificationType(),
                        message.notificationType()
                )
                && Objects.equals(
                        notification.getSourceComponent(),
                        message.sourceComponent()
                )
                && Objects.equals(
                        notification.getSourceEntityType(),
                        message.sourceEntityType()
                )
                && Objects.equals(
                        notification.getSourceEntityId(),
                        message.sourceEntityId()
                )
                && Objects.equals(
                        notification.getCorrelationId(),
                        message.correlationId()
                );

        if (!valid) {
            throw new IllegalStateException(
                    "NotificationRequested context does not match Notification"
            );
        }
    }

    private void requirePendingDeliveries(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries,
            NotificationRequestedEventMessage message) {

        if (deliveries.isEmpty()) {
            throw controlledFailure(
                    NO_AUTHORIZED_RECIPIENTS
            );
        }

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            if (delivery == null) {
                throw new IllegalStateException(
                        "Notification delivery must not be null"
                );
            }

            if (delivery.getNotificationDeliveryId() == null) {
                throw new IllegalStateException(
                        "Notification delivery id is required"
                );
            }

            if (!Objects.equals(
                    notification.getNotificationId(),
                    delivery.getNotificationId()
            )) {

                throw new IllegalStateException(
                        "Notification delivery belongs to a different Notification"
                );
            }

            if (!DELIVERY_STATUS_PENDING.equals(
                    delivery.getDeliveryStatus()
            )) {

                throw new IllegalStateException(
                        "Notification delivery must be PENDING"
                );
            }

            if (!Objects.equals(
                    message.channel(),
                    delivery.getChannel()
            )) {

                throw new IllegalStateException(
                        "Notification delivery channel does not match NotificationRequested"
                );
            }
        }
    }

    private NotificationTemplateResponse resolveTemplate(
            Notification notification,
            NotificationRequestedEventMessage message) {

        NotificationTemplateResponse template;

        try {

            template =
                    notificationTemplateService
                            .getNotificationTemplateById(
                                    notification
                                            .getNotificationTemplateId()
                            );

        } catch (ResourceNotFoundException exception) {

            throw controlledFailure(
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );
        }

        if (template == null
                || !Objects.equals(
                        notification.getNotificationTemplateId(),
                        template.getNotificationTemplateId()
                )
                || !Objects.equals(
                        notification.getOrganizationId(),
                        template.getOrganizationId()
                )
                || !Objects.equals(
                        notification.getTenantId(),
                        template.getTenantId()
                )
                || !Objects.equals(
                        message.templateCode(),
                        template.getTemplateCode()
                )
                || !Objects.equals(
                        message.channel(),
                        template.getChannel()
                )
                || !Objects.equals(
                        message.languageId(),
                        template.getLanguageId()
                )
                || !ACTIVE_TEMPLATE_STATUS.equals(
                        template.getStatus()
                )) {

            throw controlledFailure(
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );
        }

        return template;
    }

    private Map<UUID, UserAccountReference>
            indexAuthorizedUsers(
                    List<UserAccountReference> authorizedUsers) {

        Map<UUID, UserAccountReference> result =
                new LinkedHashMap<>();

        for (
                UserAccountReference recipient
                : authorizedUsers
        ) {

            if (recipient == null
                    || recipient.userId() == null) {

                throw new IllegalStateException(
                        "Authorized user reference is invalid"
                );
            }

            result.put(
                    recipient.userId(),
                    recipient
            );
        }

        return result;
    }

    private NotificationDeliveryPreparationException
            controlledFailure(
                    String reason) {

        return new NotificationDeliveryPreparationException(
                reason
        );
    }
}