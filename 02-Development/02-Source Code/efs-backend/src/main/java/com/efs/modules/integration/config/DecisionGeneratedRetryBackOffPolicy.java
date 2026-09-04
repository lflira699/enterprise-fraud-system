package com.efs.modules.integration.config;

import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.backoff.ThreadWaitSleeper;

final class DecisionGeneratedRetryBackOffPolicy
        implements BackOffPolicy {

    private static final long[] BACK_OFF_INTERVALS = {
            5_000L,
            15_000L,
            60_000L
    };

    private final Sleeper sleeper;

    DecisionGeneratedRetryBackOffPolicy() {

        this(
                new ThreadWaitSleeper()
        );
    }

    DecisionGeneratedRetryBackOffPolicy(
            Sleeper sleeper) {

        if (sleeper == null) {
            throw new IllegalArgumentException(
                    "Retry sleeper is required"
            );
        }

        this.sleeper =
                sleeper;
    }

    @Override
    public BackOffContext start(
            RetryContext context) {

        return new SequenceBackOffContext();
    }

    @Override
    public void backOff(
            BackOffContext backOffContext)
            throws BackOffInterruptedException {

        if (!(backOffContext
                instanceof SequenceBackOffContext context)) {

            throw new IllegalArgumentException(
                    "DecisionGenerated retry backoff context is required"
            );
        }

        long interval =
                context.nextInterval();

        try {

            sleeper.sleep(
                    interval
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            throw new BackOffInterruptedException(
                    "DecisionGenerated retry backoff interrupted",
                    exception
            );
        }
    }

    private static final class SequenceBackOffContext
            implements BackOffContext {

        private int index;

        private long nextInterval() {

            if (index
                    >= BACK_OFF_INTERVALS.length) {

                return BACK_OFF_INTERVALS[
                        BACK_OFF_INTERVALS.length - 1
                        ];
            }

            return BACK_OFF_INTERVALS[
                    index++
                    ];
        }
    }
}