package com.efs.modules.notification.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import com.efs.modules.integration.service.ExternalNotificationDeliveryServiceInterface;
import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.PreparedNotificationDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.event.NotificationRequestedEventProcessor;
import com.efs.modules.notification.event.NotificationRequestedProcessingResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationDeliveryOrchestrator {

    private final NotificationRequestedEventProcessor
            notificationRequestedEventProcessor;

    private final NotificationDeliveryPreparationService
            notificationDeliveryPreparationService;

    private final NotificationDeliveryTerminalizationService
            notificationDeliveryTerminalizationService;

    private final ExternalNotificationDeliveryServiceInterface
            externalNotificationDeliveryService;

    public NotificationDeliveryOrchestrator(
            NotificationRequestedEventProcessor
                    notificationRequestedEventProcessor,
            NotificationDeliveryPreparationService
                    notificationDeliveryPreparationService,
            NotificationDeliveryTerminalizationService
                    notificationDeliveryTerminalizationService,
            ExternalNotificationDeliveryServiceInterface
                    externalNotificationDeliveryService) {

        this.notificationRequestedEventProcessor =
                notificationRequestedEventProcessor;

        this.notificationDeliveryPreparationService =
                notificationDeliveryPreparationService;

        this.notificationDeliveryTerminalizationService =
                notificationDeliveryTerminalizationService;

        this.externalNotificationDeliveryService =
                externalNotificationDeliveryService;
    }

    public Optional<NotificationDeliveryPreflightResult> orchestrate(
            NotificationRequestedEventMessage message) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "NotificationRequested event message is required"
            );
        }

        NotificationRequestedProcessingResult processingResult =
                notificationRequestedEventProcessor
                        .process(
                                message
                        );

        if (processingResult.status()
                == NotificationRequestedProcessingResult
                        .Status.DUPLICATE) {

            return Optional.empty();
        }

        if (processingResult.status()
                == NotificationRequestedProcessingResult
                        .Status.TERMINAL) {

            return Optional.empty();
        }

        NotificationDeliveryPreflightResult preflightResult;

        try {

            preflightResult =
                    notificationDeliveryPreparationService
                            .prepare(
                                    processingResult.notificationId(),
                                    message
                            );

        } catch (
                NotificationDeliveryPreparationException exception
        ) {

            notificationDeliveryTerminalizationService
                    .rejectPreflight(
                            processingResult.notificationId(),
                            message,
                            exception.getReason()
                    );

            return Optional.empty();
        }

        Map<UUID, ExternalNotificationDeliveryResult>
                confirmedResults =
                new LinkedHashMap<>();

        for (PreparedNotificationDelivery delivery
                : preflightResult.deliveries()) {

            ExternalNotificationDeliveryResult deliveryResult;

            try {

                deliveryResult =
                        externalNotificationDeliveryService
                                .deliver(
                                        createDeliveryRequest(
                                                preflightResult,
                                                delivery
                                        )
                                );

                if (deliveryResult == null) {
                    throw new IllegalStateException(
                            "External notification delivery result is required"
                    );
                }

            } catch (RuntimeException exception) {

                notificationDeliveryTerminalizationService
                        .completeUnexpectedFailure(
                                preflightResult.notificationId(),
                                message,
                                confirmedResults,
                                delivery.notificationDeliveryId()
                        );

                return Optional.of(
                        preflightResult
                );
            }

            confirmedResults.put(
                    delivery.notificationDeliveryId(),
                    deliveryResult
            );
        }

        notificationDeliveryTerminalizationService
                .completeConfirmedResults(
                        preflightResult.notificationId(),
                        message,
                        confirmedResults
                );

        return Optional.of(
                preflightResult
        );
    }

    private ExternalNotificationDeliveryRequest
            createDeliveryRequest(
                    NotificationDeliveryPreflightResult preflightResult,
                    PreparedNotificationDelivery delivery) {

        ExternalNotificationDeliveryRequest request =
                new ExternalNotificationDeliveryRequest();

        request.setNotificationDeliveryId(
                delivery.notificationDeliveryId()
        );

        request.setCorrelationId(
                preflightResult.correlationId()
        );

        request.setOrganizationId(
                preflightResult.organizationId()
        );

        request.setTenantId(
                preflightResult.tenantId()
        );

        request.setChannel(
                delivery.channel()
        );

        request.setRecipientUserId(
                delivery.recipientUserId()
        );

        request.setDestination(
                delivery.destination()
        );

        request.setSubject(
                preflightResult.subject()
        );

        request.setBody(
                preflightResult.body()
        );

        return request;
    }
}