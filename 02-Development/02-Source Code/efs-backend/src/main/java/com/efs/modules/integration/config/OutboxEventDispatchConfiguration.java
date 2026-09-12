package com.efs.modules.integration.config;

import com.efs.modules.integration.repository.OutboxEventRepository;
import com.efs.modules.integration.service.OutboxEventDispatchScheduler;
import com.efs.modules.integration.service.OutboxEventPublicationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(
        proxyBeanMethods = false
)
@EnableScheduling
@ConditionalOnProperty(
        prefix = "efs.integration.outbox.dispatch",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OutboxEventDispatchConfiguration {

    @Bean
    public OutboxEventDispatchScheduler
            outboxEventDispatchScheduler(
                    OutboxEventRepository outboxEventRepository,
                    OutboxEventPublicationService
                            outboxEventPublicationService) {

        return new OutboxEventDispatchScheduler(
                outboxEventRepository,
                outboxEventPublicationService
        );
    }
}