package com.efs.modules.alert.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DecisionGeneratedEventParser {

    private static final String EVENT_TYPE =
            "DecisionGenerated";

    private static final String SCHEMA_VERSION =
            "1.0";

    private static final String PRODUCER =
            "Decision Engine";

    private final ObjectMapper objectMapper;

    public DecisionGeneratedEventParser(
            ObjectMapper objectMapper) {

        this.objectMapper =
                objectMapper;
    }

    public DecisionGeneratedEventMessage parse(
            byte[] body) {

        if (body == null
                || body.length == 0) {

            throw new IllegalArgumentException(
                    "DecisionGenerated event body is required"
            );
        }

        Map<String, Object> envelope =
                readEnvelope(body);

        requireValue(
                envelope,
                "eventType",
                EVENT_TYPE
        );

        requireValue(
                envelope,
                "schemaVersion",
                SCHEMA_VERSION
        );

        requireValue(
                envelope,
                "producer",
                PRODUCER
        );

        UUID messageId =
                requireUuid(
                        envelope.get(
                                "messageId"
                        ),
                        "messageId"
                );

        UUID correlationId =
                requireUuid(
                        envelope.get(
                                "correlationId"
                        ),
                        "correlationId"
                );

        Object payloadValue =
                envelope.get(
                        "payload"
                );

        if (!(payloadValue
                instanceof Map<?, ?> payload)) {

            throw new IllegalArgumentException(
                    "DecisionGenerated payload is required"
            );
        }

        UUID decisionId =
                requireUuid(
                        payload.get(
                                "decisionId"
                        ),
                        "payload.decisionId"
                );

        return new DecisionGeneratedEventMessage(
                messageId,
                correlationId,
                decisionId
        );
    }

    private Map<String, Object> readEnvelope(
            byte[] body) {

        try {

            return objectMapper.readValue(
                    body,
                    new TypeReference<>() {
                    }
            );

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid DecisionGenerated event JSON",
                    exception
            );
        }
    }

    private void requireValue(
            Map<String, Object> envelope,
            String field,
            String expectedValue) {

        Object actualValue =
                envelope.get(field);

        if (!expectedValue.equals(
                actualValue)) {

            throw new IllegalArgumentException(
                    "Invalid DecisionGenerated "
                            + field
            );
        }
    }

    private UUID requireUuid(
            Object value,
            String field) {

        if (!(value instanceof String text)
                || text.isBlank()) {

            throw new IllegalArgumentException(
                    "DecisionGenerated "
                            + field
                            + " is required"
            );
        }

        try {

            return UUID.fromString(
                    text
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "DecisionGenerated "
                            + field
                            + " must be a UUID",
                    exception
            );
        }
    }
}