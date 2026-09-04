package com.efs.modules.alert.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DecisionGeneratedEventListenerTest {

    private DecisionGeneratedEventParser
            eventParser;

    private DecisionGeneratedEventProcessor
            eventProcessor;

    private DecisionGeneratedEventListener
            listener;

    @BeforeEach
    void setUp() {

        eventParser =
                mock(
                        DecisionGeneratedEventParser.class
                );

        eventProcessor =
                mock(
                        DecisionGeneratedEventProcessor.class
                );

        listener =
                new DecisionGeneratedEventListener(
                        eventParser,
                        eventProcessor
                );
    }

    @Test
    void shouldParseAndProcessDecisionGeneratedEvent() {

        byte[] body =
                """
                {
                  "eventType": "DecisionGenerated"
                }
                """
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        when(
                eventParser.parse(
                        body
                )
        ).thenReturn(
                message
        );

        listener.consume(
                body
        );

        verify(
                eventParser
        ).parse(
                body
        );

        verify(
                eventProcessor
        ).process(
                message
        );
    }

    @Test
    void shouldPropagateParserFailure() {

        byte[] body =
                "invalid"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        IllegalArgumentException expectedException =
                new IllegalArgumentException(
                        "Invalid DecisionGenerated event"
                );

        when(
                eventParser.parse(
                        body
                )
        ).thenThrow(
                expectedException
        );

        IllegalArgumentException actualException =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                listener.consume(
                                        body
                                )
                );

        assertSame(
                expectedException,
                actualException
        );

        verify(
                eventProcessor,
                never()
        ).process(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldPropagateProcessorFailure() {

        byte[] body =
                """
                {
                  "eventType": "DecisionGenerated"
                }
                """
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        when(
                eventParser.parse(
                        body
                )
        ).thenReturn(
                message
        );

        IllegalStateException expectedException =
                new IllegalStateException(
                        "Processing failed"
                );

        org.mockito.Mockito
                .doThrow(
                        expectedException
                )
                .when(
                        eventProcessor
                )
                .process(
                        message
                );

        IllegalStateException actualException =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                listener.consume(
                                        body
                                )
                );

        assertSame(
                expectedException,
                actualException
        );

        assertEquals(
                "Processing failed",
                actualException.getMessage()
        );
    }
}