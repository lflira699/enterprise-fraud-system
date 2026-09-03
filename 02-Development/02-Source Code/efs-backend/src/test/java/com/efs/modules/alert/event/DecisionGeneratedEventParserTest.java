package com.efs.modules.alert.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionGeneratedEventParserTest {

    private DecisionGeneratedEventParser parser;

    @BeforeEach
    void setUp() {

        parser =
                new DecisionGeneratedEventParser(
                        new ObjectMapper()
                );
    }

    @Test
    void shouldParseApprovedDecisionGeneratedEvent() {

        UUID messageId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID decisionId =
                UUID.randomUUID();

        byte[] body =
                eventJson(
                        messageId.toString(),
                        correlationId.toString(),
                        decisionId.toString(),
                        "DecisionGenerated",
                        "1.0",
                        "Decision Engine"
                ).getBytes();

        DecisionGeneratedEventMessage result =
                parser.parse(body);

        assertEquals(
                messageId,
                result.messageId()
        );

        assertEquals(
                correlationId,
                result.correlationId()
        );

        assertEquals(
                decisionId,
                result.decisionId()
        );
    }

    @Test
    void shouldRejectMissingBody() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        new byte[0]
                )
        );
    }

    @Test
    void shouldRejectMalformedJson() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        "{not-json"
                                .getBytes()
                )
        );
    }

    @Test
    void shouldRejectWrongEventType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                "RiskCalculated",
                                "1.0",
                                "Decision Engine"
                        ).getBytes()
                )
        );
    }

    @Test
    void shouldRejectUnsupportedSchemaVersion() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                "DecisionGenerated",
                                "2.0",
                                "Decision Engine"
                        ).getBytes()
                )
        );
    }

    @Test
    void shouldRejectWrongProducer() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                "DecisionGenerated",
                                "1.0",
                                "Other Engine"
                        ).getBytes()
                )
        );
    }

    @Test
    void shouldRejectInvalidMessageId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                "not-a-uuid",
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                "DecisionGenerated",
                                "1.0",
                                "Decision Engine"
                        ).getBytes()
                )
        );
    }

    @Test
    void shouldRejectInvalidCorrelationId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                UUID.randomUUID()
                                        .toString(),
                                "not-a-uuid",
                                UUID.randomUUID()
                                        .toString(),
                                "DecisionGenerated",
                                "1.0",
                                "Decision Engine"
                        ).getBytes()
                )
        );
    }

    @Test
    void shouldRejectInvalidDecisionId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(
                        eventJson(
                                UUID.randomUUID()
                                        .toString(),
                                UUID.randomUUID()
                                        .toString(),
                                "not-a-uuid",
                                "DecisionGenerated",
                                "1.0",
                                "Decision Engine"
                        ).getBytes()
                )
        );
    }

    private String eventJson(
            String messageId,
            String correlationId,
            String decisionId,
            String eventType,
            String schemaVersion,
            String producer) {

        return """
                {
                  "messageId": "%s",
                  "eventType": "%s",
                  "schemaVersion": "%s",
                  "occurredAt": "2026-09-03T15:00:00",
                  "producer": "%s",
                  "correlationId": "%s",
                  "causationId": null,
                  "tenantId": null,
                  "payload": {
                    "decisionId": "%s"
                  },
                  "metadata": {}
                }
                """.formatted(
                messageId,
                eventType,
                schemaVersion,
                producer,
                correlationId,
                decisionId
        );
    }
}