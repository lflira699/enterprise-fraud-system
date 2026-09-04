package com.efs.modules.integration.config;

import org.junit.jupiter.api.Test;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.Sleeper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionGeneratedRetryBackOffPolicyTest {

    @Test
    void shouldApplyApprovedRetryIntervals() {

        List<Long> intervals =
                new ArrayList<>();

        Sleeper sleeper =
                interval ->
                        intervals.add(
                                interval
                        );

        DecisionGeneratedRetryBackOffPolicy policy =
                new DecisionGeneratedRetryBackOffPolicy(
                        sleeper
                );

        BackOffContext context =
                policy.start(
                        null
                );

        policy.backOff(
                context
        );

        policy.backOff(
                context
        );

        policy.backOff(
                context
        );

        assertEquals(
                List.of(
                        5_000L,
                        15_000L,
                        60_000L
                ),
                intervals
        );
    }

    @Test
    void shouldRejectMissingSleeper() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new DecisionGeneratedRetryBackOffPolicy(
                                        null
                                )
                );

        assertEquals(
                "Retry sleeper is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInvalidBackOffContext() {

        DecisionGeneratedRetryBackOffPolicy policy =
                new DecisionGeneratedRetryBackOffPolicy(
                        interval -> {
                        }
                );

        BackOffContext invalidContext =
                new BackOffContext() {
                };

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                policy.backOff(
                                        invalidContext
                                )
                );

        assertEquals(
                "DecisionGenerated retry backoff context is required",
                exception.getMessage()
        );
    }
}