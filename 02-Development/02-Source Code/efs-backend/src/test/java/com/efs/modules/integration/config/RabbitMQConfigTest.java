package com.efs.modules.integration.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMQConfigTest {

    private final RabbitMQConfig config =
            new RabbitMQConfig();

    @Test
    void shouldConfigureDomainEventsExchange() {

        DirectExchange exchange =
                config.domainEventsExchange();

        assertEquals(
                "efs.domain.events",
                exchange.getName()
        );

        assertTrue(
                exchange.isDurable()
        );
    }

    @Test
    void shouldConfigureDecisionGeneratedDlq() {

        Queue dlq =
                config.decisionGeneratedDlq();

        assertEquals(
                "alert-engine.decision-generated.dlq",
                dlq.getName()
        );

        assertTrue(
                dlq.isDurable()
        );
    }

    @Test
    void shouldConfigureDecisionGeneratedQueueWithDlq() {

        Queue queue =
                config.decisionGeneratedQueue();

        assertEquals(
                "alert-engine.decision-generated",
                queue.getName()
        );

        assertTrue(
                queue.isDurable()
        );

        assertEquals(
                "",
                queue.getArguments()
                        .get(
                                "x-dead-letter-exchange"
                        )
        );

        assertEquals(
                "alert-engine.decision-generated.dlq",
                queue.getArguments()
                        .get(
                                "x-dead-letter-routing-key"
                        )
        );
    }

    @Test
    void shouldBindDecisionGeneratedQueueToApprovedRoutingKey() {

        Queue queue =
                config.decisionGeneratedQueue();

        DirectExchange exchange =
                config.domainEventsExchange();

        Binding binding =
                config.decisionGeneratedBinding(
                        queue,
                        exchange
                );

        assertEquals(
                "alert-engine.decision-generated",
                binding.getDestination()
        );

        assertEquals(
                "efs.domain.events",
                binding.getExchange()
        );

        assertEquals(
                "decision.generated.v1",
                binding.getRoutingKey()
        );
    }
}