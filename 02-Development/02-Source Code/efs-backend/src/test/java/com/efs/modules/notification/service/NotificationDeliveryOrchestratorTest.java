package com.efs.modules.notification.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import com.efs.modules.integration.service.ExternalNotificationDeliveryServiceInterface;
import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.dto.PreparedNotificationDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.event.NotificationRequestedEventProcessor;
import com.efs.modules.notification.event.NotificationRequestedProcessingResult;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Mock
    private ExternalNotificationDeliveryServiceInterface
            externalNotificationDeliveryService;

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
                        notificationDeliveryTerminalizationService,
                        externalNotificationDeliveryService
                );
    }

    @Test
    void shouldDeliverAndTerminalizeReadyNotification() {

        NotificationRequestedEventMessage message =
                createMessage();

        NotificationDeliveryPreflightResult expected =
                createPreflightResult();

        PreparedNotificationDelivery delivery =
                expected.deliveries()
                        .get(
                                0
                        );

        ExternalNotificationDeliveryResult deliveryResult =
                createDeliveryResult(
                        true
                );

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

        when(
                externalNotificationDeliveryService
                        .deliver(
                                any(
                                        ExternalNotificationDeliveryRequest.class
                                )
                        )
        ).thenReturn(
                deliveryResult
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

        ArgumentCaptor<ExternalNotificationDeliveryRequest>
                requestCaptor =
                ArgumentCaptor.forClass(
                        ExternalNotificationDeliveryRequest.class
                );

        verify(
                externalNotificationDeliveryService
        ).deliver(
                requestCaptor.capture()
        );

        ExternalNotificationDeliveryRequest request =
                requestCaptor.getValue();

        assertEquals(
                delivery.notificationDeliveryId(),
                request.getNotificationDeliveryId()
        );

        assertEquals(
                correlationId,
                request.getCorrelationId()
        );

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                delivery.channel(),
                request.getChannel()
        );

        assertEquals(
                delivery.recipientUserId(),
                request.getRecipientUserId()
        );

        assertEquals(
                delivery.destination(),
                request.getDestination()
        );

        assertEquals(
                expected.subject(),
                request.getSubject()
        );

        assertEquals(
                expected.body(),
                request.getBody()
        );

        verify(
                notificationDeliveryTerminalizationService
        ).completeConfirmedResults(
                notificationId,
                message,
                Map.of(
                        delivery.notificationDeliveryId(),
                        deliveryResult
                )
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).completeUnexpectedFailure(
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void shouldContinueAfterConfirmedProviderFailure() {

        NotificationRequestedEventMessage message =
                createMessage();

        PreparedNotificationDelivery firstDelivery =
                new PreparedNotificationDelivery(
                        UUID.randomUUID(),
                        recipientUserId,
                        "EMAIL",
                        "first@example.com"
                );

        PreparedNotificationDelivery secondDelivery =
                new PreparedNotificationDelivery(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL",
                        "second@example.com"
                );

        NotificationDeliveryPreflightResult preflightResult =
                createPreflightResult(
                        List.of(
                                firstDelivery,
                                secondDelivery
                        )
                );

        ExternalNotificationDeliveryResult firstResult =
                createDeliveryResult(
                        false
                );

        ExternalNotificationDeliveryResult secondResult =
                createDeliveryResult(
                        true
                );

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
                preflightResult
        );

        when(
                externalNotificationDeliveryService
                        .deliver(
                                any(
                                        ExternalNotificationDeliveryRequest.class
                                )
                        )
        ).thenReturn(
                firstResult,
                secondResult
        );

        orchestrator.orchestrate(
                message
        );

        verify(
                externalNotificationDeliveryService,
                times(
                        2
                )
        ).deliver(
                any(
                        ExternalNotificationDeliveryRequest.class
                )
        );

        verify(
                notificationDeliveryTerminalizationService
        ).completeConfirmedResults(
                notificationId,
                message,
                Map.of(
                        firstDelivery.notificationDeliveryId(),
                        firstResult,
                        secondDelivery.notificationDeliveryId(),
                        secondResult
                )
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).completeUnexpectedFailure(
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void shouldStopAndTerminalizeUnexpectedDeliveryFailure() {

        NotificationRequestedEventMessage message =
                createMessage();

        PreparedNotificationDelivery firstDelivery =
                new PreparedNotificationDelivery(
                        UUID.randomUUID(),
                        recipientUserId,
                        "EMAIL",
                        "first@example.com"
                );

        PreparedNotificationDelivery failedDelivery =
                new PreparedNotificationDelivery(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL",
                        "second@example.com"
                );

        PreparedNotificationDelivery remainingDelivery =
                new PreparedNotificationDelivery(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL",
                        "third@example.com"
                );

        NotificationDeliveryPreflightResult preflightResult =
                createPreflightResult(
                        List.of(
                                firstDelivery,
                                failedDelivery,
                                remainingDelivery
                        )
                );

        ExternalNotificationDeliveryResult confirmedResult =
                createDeliveryResult(
                        true
                );

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
                preflightResult
        );

        when(
                externalNotificationDeliveryService
                        .deliver(
                                any(
                                        ExternalNotificationDeliveryRequest.class
                                )
                        )
        ).thenAnswer(
                invocation -> {

                    ExternalNotificationDeliveryRequest request =
                            invocation.getArgument(
                                    0
                            );

                    if (firstDelivery.notificationDeliveryId()
                            .equals(
                                    request.getNotificationDeliveryId()
                            )) {

                        return confirmedResult;
                    }

                    throw new IllegalStateException(
                            "Unexpected provider failure"
                    );
                }
        );

        Optional<NotificationDeliveryPreflightResult> result =
                orchestrator.orchestrate(
                        message
                );

        assertTrue(
                result.isPresent()
        );

        verify(
                externalNotificationDeliveryService,
                times(
                        2
                )
        ).deliver(
                any(
                        ExternalNotificationDeliveryRequest.class
                )
        );

        verify(
                notificationDeliveryTerminalizationService
        ).completeUnexpectedFailure(
                notificationId,
                message,
                Map.of(
                        firstDelivery.notificationDeliveryId(),
                        confirmedResult
                ),
                failedDelivery.notificationDeliveryId()
        );

        verify(
                notificationDeliveryTerminalizationService,
                never()
        ).completeConfirmedResults(
                any(),
                any(),
                any()
        );
    }

    @Test
    void shouldPropagateConfirmedResultsTerminalizationFailure() {

        NotificationRequestedEventMessage message =
                createMessage();

        NotificationDeliveryPreflightResult preflightResult =
                createPreflightResult();

        ExternalNotificationDeliveryResult deliveryResult =
                createDeliveryResult(
                        true
                );

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
                preflightResult
        );

        when(
                externalNotificationDeliveryService
                        .deliver(
                                any(
                                        ExternalNotificationDeliveryRequest.class
                                )
                        )
        ).thenReturn(
                deliveryResult
        );

        doThrow(
                new IllegalStateException(
                        "Terminalization failure"
                )
        ).when(
                notificationDeliveryTerminalizationService
        ).completeConfirmedResults(
                any(),
                any(),
                any()
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
        ).completeUnexpectedFailure(
                any(),
                any(),
                any(),
                any()
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

        verify(
                externalNotificationDeliveryService,
                never()
        ).deliver(
                any()
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
                any(),
                any()
        );

        verify(
                externalNotificationDeliveryService,
                never()
        ).deliver(
                any()
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
                any(),
                any()
        );

        verify(
                externalNotificationDeliveryService,
                never()
        ).deliver(
                any()
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
                any(),
                any(),
                any()
        );

        verify(
                externalNotificationDeliveryService,
                never()
        ).deliver(
                any()
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
                any()
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

        return createPreflightResult(
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

    private NotificationDeliveryPreflightResult
            createPreflightResult(
                    List<PreparedNotificationDelivery> deliveries) {

        return new NotificationDeliveryPreflightResult(
                notificationId,
                organizationId,
                tenantId,
                correlationId,
                "Case CASE-1 created",
                "Case CASE-1 has been created.",
                deliveries
        );
    }

    private ExternalNotificationDeliveryResult
            createDeliveryResult(
                    boolean delivered) {

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        result.setDelivered(
                delivered
        );

        return result;
    }
}