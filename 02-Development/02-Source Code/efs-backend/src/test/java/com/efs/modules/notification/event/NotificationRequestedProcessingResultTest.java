package com.efs.modules.notification.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationRequestedProcessingResultTest {

    @Test
    void shouldCreateReadyResult() {

        UUID notificationId =
                UUID.randomUUID();

        NotificationRequestedProcessingResult result =
                NotificationRequestedProcessingResult
                        .ready(
                                notificationId
                        );

        assertEquals(
                NotificationRequestedProcessingResult.Status.READY,
                result.status()
        );

        assertEquals(
                notificationId,
                result.notificationId()
        );
    }

    @Test
    void shouldCreateTerminalResult() {

        UUID notificationId =
                UUID.randomUUID();

        NotificationRequestedProcessingResult result =
                NotificationRequestedProcessingResult
                        .terminal(
                                notificationId
                        );

        assertEquals(
                NotificationRequestedProcessingResult.Status.TERMINAL,
                result.status()
        );

        assertEquals(
                notificationId,
                result.notificationId()
        );
    }

    @Test
    void shouldCreateDuplicateResultWithoutNotificationId() {

        NotificationRequestedProcessingResult result =
                NotificationRequestedProcessingResult
                        .duplicate();

        assertEquals(
                NotificationRequestedProcessingResult.Status.DUPLICATE,
                result.status()
        );

        assertNull(
                result.notificationId()
        );
    }

    @Test
    void shouldRejectNullStatus() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new NotificationRequestedProcessingResult(
                                null,
                                null
                        )
        );
    }

    @Test
    void shouldRejectReadyWithoutNotificationId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new NotificationRequestedProcessingResult(
                                NotificationRequestedProcessingResult.Status.READY,
                                null
                        )
        );
    }

    @Test
    void shouldRejectDuplicateWithNotificationId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new NotificationRequestedProcessingResult(
                                NotificationRequestedProcessingResult.Status.DUPLICATE,
                                UUID.randomUUID()
                        )
        );
    }
}