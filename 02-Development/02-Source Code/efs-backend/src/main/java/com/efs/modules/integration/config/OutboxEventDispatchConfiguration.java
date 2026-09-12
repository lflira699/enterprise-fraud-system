package com.efs.modules.integration.config;

import com.efs.modules.integration.repository.OutboxEventRepository;
import com.efs.modules.integration.service.OutboxEventDispatchScheduler;
import com.efs.modules.integration.service.OutboxEventLifecycleService;
import com.efs.modules.integration.service.OutboxEventPublicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(
        proxyBeanMethods = false
)
@EnableScheduling
public class OutboxEventDispatchConfiguration {

    private final long staleProcessingThresholdMs;
    private final long publisherConfirmTimeoutMs;

    public OutboxEventDispatchConfiguration(
            @Value(
                    "${efs.integration.outbox.stale-processing-threshold-ms:30000}"
            )
            long staleProcessingThresholdMs,
            @Value(
                    "${efs.integration.outbox.publisher-confirm-timeout-ms:5000}"
            )
            long publisherConfirmTimeoutMs) {

        OutboxEventDispatchScheduler
                .validateRuntimeConfiguration(
                        staleProcessingThresholdMs,
                        publisherConfirmTimeoutMs
                );

        this.staleProcessingThresholdMs =
                staleProcessingThresholdMs;

        this.publisherConfirmTimeoutMs =
                publisherConfirmTimeoutMs;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "efs.integration.outbox.dispatch",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = false
    )
    public OutboxEventDispatchScheduler
            outboxEventDispatchScheduler(
                    OutboxEventRepository outboxEventRepository,
                    OutboxEventLifecycleService
                            outboxEventLifecycleService,
                    OutboxEventPublicationService
                            outboxEventPublicationService) {

        return new OutboxEventDispatchScheduler(
                outboxEventRepository,
                outboxEventLifecycleService,
                outboxEventPublicationService,
                staleProcessingThresholdMs,
                publisherConfirmTimeoutMs
        );
    }
}