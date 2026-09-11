package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.event.NotificationRequestedEventProcessor;
import com.efs.modules.notification.event.NotificationRequestedProcessingResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationDeliveryOrchestrator {

    private final NotificationRequestedEventProcessor
            notificationRequestedEventProcessor;

    private final NotificationDeliveryPreparationService
            notificationDeliveryPreparationService;

    private final NotificationDeliveryTerminalizationService
            notificationDeliveryTerminalizationService;

    public NotificationDeliveryOrchestrator(
            NotificationRequestedEventProcessor
                    notificationRequestedEventProcessor,
            NotificationDeliveryPreparationService
                    notificationDeliveryPreparationService,
            NotificationDeliveryTerminalizationService
                    notificationDeliveryTerminalizationService) {

        this.notificationRequestedEventProcessor =
                notificationRequestedEventProcessor;

        this.notificationDeliveryPreparationService =
                notificationDeliveryPreparationService;

        this.notificationDeliveryTerminalizationService =
                notificationDeliveryTerminalizationService;
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

        try {

            NotificationDeliveryPreflightResult preflightResult =
                    notificationDeliveryPreparationService
                            .prepare(
                                    processingResult.notificationId(),
                                    message
                            );

            return Optional.of(
                    preflightResult
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
    }
}