package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.PreparedNotificationDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.event.NotificationRequestedEventProcessor;
import com.efs.modules.notification.event.NotificationRequestedProcessingResult;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryOrchestratorTest {

    @Mock
    private NotificationRequestedEventProcessor
            notificationRequestedEventProcessor;

    @Mock
    private NotificationDeliveryPreparationService
            notificationDeliveryPreparationService;

    @Mock
    private NotificationDeliveryTerminalizationService
            notificationDeliveryTerminalizationService;

    private NotificationDeliveryOrchestrator orchestrator;

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

        orchestrator =
                new NotificationDeliveryOrchestrator(
                        notificationRequestedEventProcessor,
                        notificationDeliveryPreparationService,
                        notificationDeliveryTerminalizationService
                );
    }

    @Test
    void shouldPrepareReadyNotification() {

        NotificationRequestedEventMessage message =
                createMessage();

        NotificationDeliveryPreflightResult expected =
                createPreflightResult();

        when(
                notificationRequestedEventProcessor
                        .process(
                                message
                        )
        ).thenReturn(
                NotificationRequestedProcessingResult
                        .ready(
                                notificationId
                        )
        );

        when(
                notificationDeliveryPreparationService
                        .prepare(
                                notificationId,
                                message
                        )
        ).thenReturn(
                expected
        );

        Optional<NotificationDeliveryPreflightResult> result =
                orchestrator.orchestrate(
                        message
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                expected,
                result.orElseThrow()
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).rejectPreflight(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldTerminalizeControlledPreparationRejection() {

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                notificationRequestedEventProcessor
                        .process(
                                message
                        )
        ).thenReturn(
                NotificationRequestedProcessingResult
                        .ready(
                                notificationId
                        )
        );

        when(
                notificationDeliveryPreparationService
                        .prepare(
                                notificationId,
                                message
                        )
        ).thenThrow(
                new NotificationDeliveryPreparationException(
                        "DELIVERY_CONFIGURATION_UNAVAILABLE"
                )
        );

        Optional<NotificationDeliveryPreflightResult> result =
                orchestrator.orchestrate(
                        message
                );

        assertTrue(
                result.isEmpty()
        );

        verify(
                notificationDeliveryTerminalizationService
        ).rejectPreflight(
                notificationId,
                message,
                "DELIVERY_CONFIGURATION_UNAVAILABLE"
        );
    }

    @Test
    void shouldStopWhenRegistrationAlreadyTerminalized() {

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                notificationRequestedEventProcessor
                        .process(
                                message
                        )
        ).thenReturn(
                NotificationRequestedProcessingResult
                        .terminal(
                                notificationId
                        )
        );

        Optional<NotificationDeliveryPreflightResult> result =
                orchestrator.orchestrate(
                        message
                );

        assertTrue(
                result.isEmpty()
        );

        verify(
                notificationDeliveryPreparationService,
                never()
        ).prepare(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).rejectPreflight(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldStopWhenRegistrationIsDuplicate() {

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                notificationRequestedEventProcessor
                        .process(
                                message
                        )
        ).thenReturn(
                NotificationRequestedProcessingResult
                        .duplicate()
        );

        Optional<NotificationDeliveryPreflightResult> result =
                orchestrator.orchestrate(
                        message
                );

        assertTrue(
                result.isEmpty()
        );

        verify(
                notificationDeliveryPreparationService,
                never()
        ).prepare(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).rejectPreflight(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldPropagateUnexpectedPreparationFailure() {

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                notificationRequestedEventProcessor
                        .process(
                                message
                        )
        ).thenReturn(
                NotificationRequestedProcessingResult
                        .ready(
                                notificationId
                        )
        );

        when(
                notificationDeliveryPreparationService
                        .prepare(
                                notificationId,
                                message
                        )
        ).thenThrow(
                new IllegalStateException(
                        "Unexpected preparation failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        orchestrator.orchestrate(
                                message
                        )
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).rejectPreflight(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRequireMessage() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        orchestrator.orchestrate(
                                null
                        )
        );

        verify(
                notificationRequestedEventProcessor,
                never()
        ).process(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldNotDeclareTransactionalBoundary()
            throws Exception {

        Method method =
                NotificationDeliveryOrchestrator.class
                        .getMethod(
                                "orchestrate",
                                NotificationRequestedEventMessage.class
                        );

        assertFalse(
                NotificationDeliveryOrchestrator.class
                        .isAnnotationPresent(
                                Transactional.class
                        )
        );

        assertFalse(
                method.isAnnotationPresent(
                        Transactional.class
                )
        );
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
                UUID.randomUUID(),
                List.of(
                        recipientUserId
                ),
                Map.of(
                        "caseNumber",
                        "CASE-1"
                )
        );
    }

    private NotificationDeliveryPreflightResult
            createPreflightResult() {

        return new NotificationDeliveryPreflightResult(
                notificationId,
                organizationId,
                tenantId,
                correlationId,
                "Case CASE-1 created",
                "Case CASE-1 has been created.",
                List.of(
                        new PreparedNotificationDelivery(
                                UUID.randomUUID(),
                                recipientUserId,
                                "EMAIL",
                                "user@example.com"
                        )
                )
        );
    }
}