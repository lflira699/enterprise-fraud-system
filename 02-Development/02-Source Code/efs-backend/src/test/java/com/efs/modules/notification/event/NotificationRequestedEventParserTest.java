package com.efs.modules.notification.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationRequestedEventParserTest {

    private static final UUID MESSAGE_ID =
            UUID.fromString(
                    "16116116-1161-4161-8161-161161161161"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "16216216-2162-4162-8162-162162162162"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "16316316-3163-4163-8163-163163163163"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "16416416-4164-4164-8164-164164164164"
            );

    private static final UUID SOURCE_ENTITY_ID =
            UUID.fromString(
                    "16516516-5165-4165-8165-165165165165"
            );

    private static final UUID RECIPIENT_USER_ID =
            UUID.fromString(
                    "16616616-6166-4166-8166-166166166166"
            );

    private static final UUID LANGUAGE_ID =
            UUID.fromString(
                    "16716716-7167-4167-8167-167167167167"
            );

    private final NotificationRequestedEventParser parser =
            new NotificationRequestedEventParser(
                    new ObjectMapper()
            );

    @Test
    void shouldParseApprovedNotificationRequestedContract() {

        NotificationRequestedEventMessage message =
                parser.parse(
                        validEvent()
                                .getBytes(
                                        StandardCharsets.UTF_8
                                )
                );

        assertEquals(
                MESSAGE_ID,
                message.messageId()
        );

        assertEquals(
                CORRELATION_ID,
                message.correlationId()
        );

        assertEquals(
                "CASE_STATUS_CHANGED",
                message.notificationType()
        );

        assertEquals(
                "CASE_STATUS_CHANGED",
                message.templateCode()
        );

        assertEquals(
                "EMAIL",
                message.channel()
        );

        assertEquals(
                LANGUAGE_ID,
                message.languageId()
        );

        assertEquals(
                ORGANIZATION_ID,
                message.organizationId()
        );

        assertEquals(
                TENANT_ID,
                message.tenantId()
        );

        assertEquals(
                "CASE",
                message.sourceComponent()
        );

        assertEquals(
                "CASE",
                message.sourceEntityType()
        );

        assertEquals(
                SOURCE_ENTITY_ID,
                message.sourceEntityId()
        );

        assertEquals(
                1,
                message.recipientUserIds().size()
        );

        assertEquals(
                RECIPIENT_USER_ID,
                message.recipientUserIds().get(0)
        );

        assertEquals(
                "CLOSED",
                message.templateParameters()
                        .get("status")
        );
    }

    @Test
    void shouldRejectInvalidEventType() {

        String body =
                validEvent()
                        .replace(
                                "\"NotificationRequested\"",
                                "\"DecisionGenerated\""
                        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        parser.parse(
                                body.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
        );
    }

    @Test
    void shouldRejectInvalidSchemaVersion() {

        String body =
                validEvent()
                        .replace(
                                "\"schemaVersion\":\"1.1\"",
                                "\"schemaVersion\":\"1.0\""
                        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        parser.parse(
                                body.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
        );
    }

    @Test
    void shouldRejectMissingChannel() {

        String body =
                validEvent()
                        .replace(
                                "\"channel\":\"EMAIL\",",
                                ""
                        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        parser.parse(
                                body.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
        );
    }

    @Test
    void shouldRejectMissingLanguageId() {

        String body =
                validEvent()
                        .replace(
                                "\"languageId\":\""
                                        + LANGUAGE_ID
                                        + "\",",
                                ""
                        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        parser.parse(
                                body.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
        );
    }

    @Test
    void shouldRejectInvalidRecipientUserId() {

        String body =
                validEvent()
                        .replace(
                                RECIPIENT_USER_ID.toString(),
                                "not-a-uuid"
                        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        parser.parse(
                                body.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
        );
    }

    @Test
    void shouldAcceptEmptyRecipientListForControlledProcessing() {

        String body =
                validEvent()
                        .replace(
                                "\"" + RECIPIENT_USER_ID + "\"",
                                ""
                        );

        NotificationRequestedEventMessage message =
                parser.parse(
                        body.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        assertEquals(
                0,
                message.recipientUserIds().size()
        );
    }

    private String validEvent() {

        return """
                {
                  "messageId":"%s",
                  "eventType":"NotificationRequested",
                  "schemaVersion":"1.1",
                  "occurredAt":"2026-09-11T12:00:00",
                  "producer":"CASE",
                  "correlationId":"%s",
                  "payload":{
                    "notificationType":"CASE_STATUS_CHANGED",
                    "templateCode":"CASE_STATUS_CHANGED",
                    "channel":"EMAIL",
                    "languageId":"%s",
                    "organizationId":"%s",
                    "tenantId":"%s",
                    "sourceComponent":"CASE",
                    "sourceEntityType":"CASE",
                    "sourceEntityId":"%s",
                    "recipientUserIds":[
                      "%s"
                    ],
                    "templateParameters":{
                      "status":"CLOSED"
                    }
                  }
                }
                """.formatted(
                MESSAGE_ID,
                CORRELATION_ID,
                LANGUAGE_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                SOURCE_ENTITY_ID,
                RECIPIENT_USER_ID
        );
    }
}