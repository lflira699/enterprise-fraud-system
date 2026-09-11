package com.efs.modules.notification.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class NotificationRequestedEventParser {

    private static final String EVENT_TYPE =
            "NotificationRequested";

    private static final String SCHEMA_VERSION =
            "1.1";

    private final ObjectMapper objectMapper;

    public NotificationRequestedEventParser(
            ObjectMapper objectMapper) {

        this.objectMapper =
                objectMapper;
    }

    public NotificationRequestedEventMessage parse(
            byte[] body) {

        if (body == null
                || body.length == 0) {

            throw new IllegalArgumentException(
                    "NotificationRequested event body is required"
            );
        }

        Map<String, Object> envelope =
                readEnvelope(
                        body
                );

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
                    "NotificationRequested payload is required"
            );
        }

        String notificationType =
                requireText(
                        payload.get(
                                "notificationType"
                        ),
                        "payload.notificationType"
                );

        String templateCode =
                requireText(
                        payload.get(
                                "templateCode"
                        ),
                        "payload.templateCode"
                );

        String channel =
                requireText(
                        payload.get(
                                "channel"
                        ),
                        "payload.channel"
                );

        UUID languageId =
                requireUuid(
                        payload.get(
                                "languageId"
                        ),
                        "payload.languageId"
                );

        UUID organizationId =
                requireUuid(
                        payload.get(
                                "organizationId"
                        ),
                        "payload.organizationId"
                );

        UUID tenantId =
                requireUuid(
                        payload.get(
                                "tenantId"
                        ),
                        "payload.tenantId"
                );

        String sourceComponent =
                requireText(
                        payload.get(
                                "sourceComponent"
                        ),
                        "payload.sourceComponent"
                );

        String sourceEntityType =
                requireText(
                        payload.get(
                                "sourceEntityType"
                        ),
                        "payload.sourceEntityType"
                );

        UUID sourceEntityId =
                requireUuid(
                        payload.get(
                                "sourceEntityId"
                        ),
                        "payload.sourceEntityId"
                );

        List<UUID> recipientUserIds =
                requireUuidList(
                        payload.get(
                                "recipientUserIds"
                        ),
                        "payload.recipientUserIds"
                );

        Map<String, Object> templateParameters =
                requireMap(
                        payload.get(
                                "templateParameters"
                        ),
                        "payload.templateParameters"
                );

        return new NotificationRequestedEventMessage(
                messageId,
                correlationId,
                notificationType,
                templateCode,
                channel,
                languageId,
                organizationId,
                tenantId,
                sourceComponent,
                sourceEntityType,
                sourceEntityId,
                recipientUserIds,
                templateParameters
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
                    "Invalid NotificationRequested event JSON",
                    exception
            );
        }
    }

    private void requireValue(
            Map<String, Object> envelope,
            String field,
            String expectedValue) {

        Object actualValue =
                envelope.get(
                        field
                );

        if (!expectedValue.equals(
                actualValue)) {

            throw new IllegalArgumentException(
                    "Invalid NotificationRequested "
                            + field
            );
        }
    }

    private String requireText(
            Object value,
            String field) {

        if (!(value instanceof String text)
                || text.isBlank()) {

            throw new IllegalArgumentException(
                    "NotificationRequested "
                            + field
                            + " is required"
            );
        }

        return text;
    }

    private UUID requireUuid(
            Object value,
            String field) {

        String text =
                requireText(
                        value,
                        field
                );

        try {

            return UUID.fromString(
                    text
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "NotificationRequested "
                            + field
                            + " must be a UUID",
                    exception
            );
        }
    }

    private List<UUID> requireUuidList(
            Object value,
            String field) {

        if (!(value instanceof List<?> values)) {

            throw new IllegalArgumentException(
                    "NotificationRequested "
                            + field
                            + " is required"
            );
        }

        List<UUID> result =
                new ArrayList<>();

        for (Object item : values) {

            result.add(
                    requireUuid(
                            item,
                            field
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private Map<String, Object> requireMap(
            Object value,
            String field) {

        if (!(value instanceof Map<?, ?> values)) {

            throw new IllegalArgumentException(
                    "NotificationRequested "
                            + field
                            + " is required"
            );
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        values.forEach(
                (key, mapValue) -> {

                    if (!(key instanceof String textKey)) {

                        throw new IllegalArgumentException(
                                "NotificationRequested "
                                        + field
                                        + " keys must be strings"
                        );
                    }

                    result.put(
                            textKey,
                            mapValue
                    );
                }
        );

        return Map.copyOf(
                result
        );
    }
}