package com.efs.modules.notification.service;

import com.efs.modules.administration.dto.UserAccountReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationDestinationResolverTest {

    private NotificationDestinationResolver resolver;

    @BeforeEach
    void setUp() {

        resolver =
                new NotificationDestinationResolver();
    }

    @Test
    void shouldResolveEmailDestinationExactlyFromAuthorizedUserReference() {

        UserAccountReference recipient =
                recipient(
                        "recipient@example.com"
                );

        String destination =
                resolver.resolve(
                        "EMAIL",
                        recipient
                );

        assertEquals(
                "recipient@example.com",
                destination
        );
    }

    @Test
    void shouldRejectNullRecipient() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "EMAIL",
                                null
                        )
        );
    }

    @Test
    void shouldRejectNullChannel() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                null,
                                recipient(
                                        "recipient@example.com"
                                )
                        )
        );
    }

    @Test
    void shouldRejectBlankChannel() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "   ",
                                recipient(
                                        "recipient@example.com"
                                )
                        )
        );
    }

    @Test
    void shouldRejectUnsupportedChannel() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "SMS",
                                recipient(
                                        "recipient@example.com"
                                )
                        )
        );
    }

    @Test
    void shouldRejectNullEmailDestination() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "EMAIL",
                                recipient(
                                        null
                                )
                        )
        );
    }

    @Test
    void shouldRejectBlankEmailDestination() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        resolver.resolve(
                                "EMAIL",
                                recipient(
                                        "   "
                                )
                        )
        );
    }

    private UserAccountReference recipient(
            String email) {

        return new UserAccountReference(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                email
        );
    }
}