package com.efs.modules.integration.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DOMAIN_EVENTS_EXCHANGE =
            "efs.domain.events";

    public static final String SCENARIO_ACTIVATED_QUEUE =
            "risk-engine.scenario-activated";

    public static final String SCENARIO_ACTIVATED_DLQ =
            "risk-engine.scenario-activated.dlq";

    public static final String SCENARIO_ACTIVATED_ROUTING_KEY =
            "scenario.activated.v1";

    public static final String DECISION_GENERATED_QUEUE =
            "alert-engine.decision-generated";

    public static final String DECISION_GENERATED_DLQ =
            "alert-engine.decision-generated.dlq";

    public static final String DECISION_GENERATED_ROUTING_KEY =
            "decision.generated.v1";

    @Bean
    public DirectExchange domainEventsExchange() {
        return new DirectExchange(
                DOMAIN_EVENTS_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue scenarioActivatedDlq() {
        return QueueBuilder
                .durable(SCENARIO_ACTIVATED_DLQ)
                .build();
    }

    @Bean
    public Queue scenarioActivatedQueue() {
        return QueueBuilder
                .durable(SCENARIO_ACTIVATED_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        ""
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        SCENARIO_ACTIVATED_DLQ
                )
                .build();
    }

    @Bean
    public Binding scenarioActivatedBinding(
            Queue scenarioActivatedQueue,
            DirectExchange domainEventsExchange) {

        return BindingBuilder
                .bind(scenarioActivatedQueue)
                .to(domainEventsExchange)
                .with(SCENARIO_ACTIVATED_ROUTING_KEY);
    }

    @Bean
    public Queue decisionGeneratedDlq() {
        return QueueBuilder
                .durable(DECISION_GENERATED_DLQ)
                .build();
    }

    @Bean
    public Queue decisionGeneratedQueue() {
        return QueueBuilder
                .durable(DECISION_GENERATED_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        ""
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        DECISION_GENERATED_DLQ
                )
                .build();
    }

    @Bean
    public Binding decisionGeneratedBinding(
            Queue decisionGeneratedQueue,
            DirectExchange domainEventsExchange) {

        return BindingBuilder
                .bind(decisionGeneratedQueue)
                .to(domainEventsExchange)
                .with(DECISION_GENERATED_ROUTING_KEY);
    }
}