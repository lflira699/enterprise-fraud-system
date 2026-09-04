package com.efs.modules.alert.event;

import com.efs.modules.integration.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DecisionGeneratedEventListener {

    private final DecisionGeneratedEventParser
            eventParser;

    private final DecisionGeneratedEventProcessor
            eventProcessor;

    public DecisionGeneratedEventListener(
            DecisionGeneratedEventParser eventParser,
            DecisionGeneratedEventProcessor eventProcessor) {

        this.eventParser =
                eventParser;

        this.eventProcessor =
                eventProcessor;
    }

    @RabbitListener(
            queues =
                    RabbitMQConfig.DECISION_GENERATED_QUEUE,
            containerFactory =
                    RabbitMQConfig.DECISION_GENERATED_LISTENER_CONTAINER_FACTORY
    )
    public void consume(
            byte[] body) {

        DecisionGeneratedEventMessage message =
                eventParser.parse(
                        body
                );

        eventProcessor.process(
                message
        );
    }
}