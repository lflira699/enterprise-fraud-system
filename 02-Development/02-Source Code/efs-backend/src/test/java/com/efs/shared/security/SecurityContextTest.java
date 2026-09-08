package com.efs.shared.security;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityContextTest {

    @Test
    void shouldPreserveAuthenticatedIdentityContext() {

        UUID userId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID sessionId =
                UUID.randomUUID();

        SecurityContext context =
                new SecurityContext(
                        userId,
                        tenantId,
                        sessionId,
                        Set.of(
                                "RULE_ADMINISTRATOR"
                        ),
                        Set.of(
                                "RULE_HISTORY_READ"
                        ),
                        Set.of(
                                "rules.read"
                        )
                );

        assertEquals(
                userId,
                context.getUserId()
        );

        assertEquals(
                tenantId,
                context.getTenantId()
        );

        assertEquals(
                sessionId,
                context.getSessionId()
        );

        assertTrue(
                context.hasRole(
                        "RULE_ADMINISTRATOR"
                )
        );

        assertTrue(
                context.hasPermission(
                        "RULE_HISTORY_READ"
                )
        );

        assertTrue(
                context.hasScope(
                        "rules.read"
                )
        );

        assertFalse(
                context.hasPermission(
                        "RULE_PUBLISH"
                )
        );
    }

    @Test
    void shouldRequireUserIdentifier() {

        assertThrows(
                NullPointerException.class,
                () -> new SecurityContext(
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of(),
                        Set.of(),
                        Set.of()
                )
        );
    }

    @Test
    void shouldProtectContextCollectionsFromModification() {

        SecurityContext context =
                new SecurityContext(
                        UUID.randomUUID(),
                        null,
                        null,
                        Set.of(
                                "RULE_ADMINISTRATOR"
                        ),
                        Set.of(
                                "RULE_HISTORY_READ"
                        ),
                        Set.of(
                                "rules.read"
                        )
                );

        assertThrows(
                UnsupportedOperationException.class,
                () -> context
                        .getPermissions()
                        .add(
                                "RULE_PUBLISH"
                        )
        );
    }
}
