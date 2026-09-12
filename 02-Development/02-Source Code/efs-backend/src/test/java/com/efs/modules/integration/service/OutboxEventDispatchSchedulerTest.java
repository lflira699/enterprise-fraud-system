package com.efs.modules.integration.service;

import com.efs.modules.integration.config.OutboxEventDispatchConfiguration;
import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxEventDispatchSchedulerTest {

    private OutboxEventRepository
            outboxEventRepository;

    private OutboxEventPublicationService
            outboxEventPublicationService;

    private OutboxEventDispatchScheduler
            scheduler;

    @BeforeEach
    void setUp() {

        outboxEventRepository =
                mock(
                        OutboxEventRepository.class
                );

        outboxEventPublicationService =
                mock(
                        OutboxEventPublicationService.class
                );

        scheduler =
                new OutboxEventDispatchScheduler(
                        outboxEventRepository,
                        outboxEventPublicationService
                );
    }

    @Test
    void shouldDispatchPendingAndEligibleFailedEvents() {

        UUID pendingId =
                UUID.randomUUID();

        UUID failedId =
                UUID.randomUUID();

        OutboxEvent pending =
                event(
                        pendingId
                );

        OutboxEvent failed =
                event(
                        failedId
                );

        when(
                outboxEventRepository
                        .findByStatusOrderByOccurredAtAsc(
                                "PENDING"
                        )
        ).thenReturn(
                List.of(
                        pending
                )
        );

        when(
                outboxEventRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                                eq("FAILED"),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(
                List.of(
                        failed
                )
        );

        when(
                outboxEventPublicationService
                        .publish(
                                pendingId
                        )
        ).thenReturn(
                CompletableFuture.completedFuture(
                        null
                )
        );

        when(
                outboxEventPublicationService
                        .publish(
                                failedId
                        )
        ).thenReturn(
                CompletableFuture.completedFuture(
                        null
                )
        );

        scheduler.dispatch();

        verify(
                outboxEventPublicationService
        ).publish(
                pendingId
        );

        verify(
                outboxEventPublicationService
        ).publish(
                failedId
        );
    }

    @Test
    void shouldContinueAfterIndividualPublicationFailure() {

        UUID failedPublicationId =
                UUID.randomUUID();

        UUID followingId =
                UUID.randomUUID();

        when(
                outboxEventRepository
                        .findByStatusOrderByOccurredAtAsc(
                                "PENDING"
                        )
        ).thenReturn(
                List.of(
                        event(
                                failedPublicationId
                        ),
                        event(
                                followingId
                        )
                )
        );

        when(
                outboxEventRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                                eq("FAILED"),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(
                List.of()
        );

        when(
                outboxEventPublicationService
                        .publish(
                                failedPublicationId
                        )
        ).thenReturn(
                CompletableFuture.failedFuture(
                        new IllegalStateException(
                                "forced publication failure"
                        )
                )
        );

        when(
                outboxEventPublicationService
                        .publish(
                                followingId
                        )
        ).thenReturn(
                CompletableFuture.completedFuture(
                        null
                )
        );

        assertDoesNotThrow(
                scheduler::dispatch
        );

        verify(
                outboxEventPublicationService
        ).publish(
                failedPublicationId
        );

        verify(
                outboxEventPublicationService
        ).publish(
                followingId
        );
    }

    @Test
    void shouldUseApprovedDispatchIntervalProperty()
            throws Exception {

        Method method =
                OutboxEventDispatchScheduler.class
                        .getMethod(
                                "dispatch"
                        );

        Scheduled scheduled =
                method.getAnnotation(
                        Scheduled.class
                );

        assertNotNull(
                scheduled
        );

        assertEquals(
                "${efs.integration.outbox.dispatch.interval-ms:1000}",
                scheduled.fixedDelayString()
        );
    }

    @Test
    void shouldKeepRuntimeDispatchFailClosedByDefault() {

        ConditionalOnProperty condition =
                OutboxEventDispatchConfiguration.class
                        .getAnnotation(
                                ConditionalOnProperty.class
                        );

        assertNotNull(
                condition
        );

        assertEquals(
                "efs.integration.outbox.dispatch",
                condition.prefix()
        );

        assertArrayEquals(
                new String[]{
                        "enabled"
                },
                condition.name()
        );

        assertEquals(
                "true",
                condition.havingValue()
        );

        assertFalse(
                condition.matchIfMissing()
        );

        assertNotNull(
                OutboxEventDispatchConfiguration.class
                        .getAnnotation(
                                EnableScheduling.class
                        )
        );
    }

    private OutboxEvent event(
            UUID eventId) {

        OutboxEvent event =
                new OutboxEvent();

        event.setId(
                eventId
        );

        return event;
    }
}