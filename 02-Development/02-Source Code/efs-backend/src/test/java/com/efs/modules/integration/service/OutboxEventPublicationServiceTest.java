package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxEventPublicationServiceTest {

    private OutboxEventLifecycleService lifecycleService;
    private OutboxEventRabbitPublisher rabbitPublisher;
    private OutboxEventPublicationService publicationService;

    @BeforeEach
    void setUp() {

        lifecycleService =
                mock(
                        OutboxEventLifecycleService.class
                );

        rabbitPublisher =
                mock(
                        OutboxEventRabbitPublisher.class
                );

        publicationService =
                new OutboxEventPublicationService(
                        lifecycleService,
                        rabbitPublisher
                );
    }

    @Test
    void shouldPublishClaimedEventAndMarkPublishedAfterAck() {

        UUID eventId =
                UUID.randomUUID();

        OutboxEvent event =
                createEvent(eventId);

        CompletableFuture<Void> brokerResult =
                new CompletableFuture<>();

        when(
                lifecycleService
                        .claimForPublication(eventId)
        ).thenReturn(
                Optional.of(event)
        );

        when(
                rabbitPublisher.publish(event)
        ).thenReturn(
                brokerResult
        );

        CompletableFuture<Void> result =
                publicationService.publish(
                        eventId
                );

        assertFalse(
                result.isDone()
        );

        verify(
                lifecycleService,
                never()
        ).markPublished(eventId);

        verify(
                lifecycleService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.eq(
                        eventId
                ),
                org.mockito.ArgumentMatchers
                        .anyString()
        );

        brokerResult.complete(null);

        result.join();

        assertTrue(
                result.isDone()
        );

        verify(
                lifecycleService
        ).markPublished(eventId);

        verify(
                lifecycleService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers.eq(
                        eventId
                ),
                org.mockito.ArgumentMatchers
                        .anyString()
        );
    }

    @Test
    void shouldMarkFailedWhenRabbitPublicationFails() {

        UUID eventId =
                UUID.randomUUID();

        OutboxEvent event =
                createEvent(eventId);

        when(
                lifecycleService
                        .claimForPublication(eventId)
        ).thenReturn(
                Optional.of(event)
        );

        when(
                rabbitPublisher.publish(event)
        ).thenReturn(
                CompletableFuture.failedFuture(
                        new IllegalStateException(
                                "broker-nack"
                        )
                )
        );

        CompletableFuture<Void> result =
                publicationService.publish(
                        eventId
                );

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertEquals(
                "broker-nack",
                exception
                        .getCause()
                        .getMessage()
        );

        verify(
                lifecycleService,
                never()
        ).markPublished(eventId);

        verify(
                lifecycleService
        ).markFailed(
                eventId,
                "broker-nack"
        );
    }

    @Test
    void shouldNotPublishWhenEventCannotBeClaimed() {

        UUID eventId =
                UUID.randomUUID();

        when(
                lifecycleService
                        .claimForPublication(eventId)
        ).thenReturn(
                Optional.empty()
        );

        CompletableFuture<Void> result =
                publicationService.publish(
                        eventId
                );

        result.join();

        assertTrue(
                result.isDone()
        );

        verify(
                rabbitPublisher,
                never()
        ).publish(
                org.mockito.ArgumentMatchers
                        .any()
        );

        verify(
                lifecycleService,
                never()
        ).markPublished(
                org.mockito.ArgumentMatchers
                        .any()
        );

        verify(
                lifecycleService,
                never()
        ).markFailed(
                org.mockito.ArgumentMatchers
                        .any(),
                org.mockito.ArgumentMatchers
                        .anyString()
        );
    }

    @Test
    void shouldReturnFailedFutureWhenClaimFails() {

        UUID eventId =
                UUID.randomUUID();

        when(
                lifecycleService
                        .claimForPublication(eventId)
        ).thenThrow(
                new IllegalStateException(
                        "claim-failure"
                )
        );

        CompletableFuture<Void> result =
                publicationService.publish(
                        eventId
                );

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertEquals(
                "claim-failure",
                exception
                        .getCause()
                        .getMessage()
        );

        verify(
                rabbitPublisher,
                never()
        ).publish(
                org.mockito.ArgumentMatchers
                        .any()
        );
    }

    @Test
    void shouldUseExceptionTypeWhenFailureHasNoMessage() {

        UUID eventId =
                UUID.randomUUID();

        OutboxEvent event =
                createEvent(eventId);

        when(
                lifecycleService
                        .claimForPublication(eventId)
        ).thenReturn(
                Optional.of(event)
        );

        when(
                rabbitPublisher.publish(event)
        ).thenReturn(
                CompletableFuture.failedFuture(
                        new IllegalStateException()
                )
        );

        CompletableFuture<Void> result =
                publicationService.publish(
                        eventId
                );

        assertThrows(
                CompletionException.class,
                result::join
        );

        verify(
                lifecycleService
        ).markFailed(
                eventId,
                "IllegalStateException"
        );
    }

    private OutboxEvent createEvent(
            UUID eventId) {

        OutboxEvent event =
                new OutboxEvent();

        event.setId(
                eventId
        );

        event.setEventType(
                "DecisionGenerated"
        );

        event.setStatus(
                "PROCESSING"
        );

        event.setAttemptCount(
                0
        );

        return event;
    }
}