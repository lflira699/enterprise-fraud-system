package com.efs.config.security;

import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EfsJwtSecurityContextMapperTest {

    private final EfsJwtSecurityContextMapper mapper =
            new EfsJwtSecurityContextMapper();

    @Test
    void shouldMapApprovedEfsJwtClaims() {

        UUID userId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID sessionId =
                UUID.randomUUID();

        Jwt jwt =
                baseJwtBuilder()
                        .subject(
                                "external-keycloak-subject"
                        )
                        .claim(
                                "user_id",
                                userId.toString()
                        )
                        .claim(
                                "tenant_id",
                                tenantId.toString()
                        )
                        .claim(
                                "session_id",
                                sessionId.toString()
                        )
                        .claim(
                                "roles",
                                List.of(
                                        "RULE_ADMINISTRATOR"
                                )
                        )
                        .claim(
                                "permissions",
                                List.of(
                                        "RULE_HISTORY_READ"
                                )
                        )
                        .claim(
                                "scope",
                                "rules.read audit.read"
                        )
                        .build();

        SecurityContext context =
                mapper.map(
                        jwt
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

        assertTrue(
                context.hasScope(
                        "audit.read"
                )
        );
    }

    @Test
    void shouldAllowOptionalTenantAndSessionClaimsToBeAbsent() {

        UUID userId =
                UUID.randomUUID();

        Jwt jwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
                                userId.toString()
                        )
                        .build();

        SecurityContext context =
                mapper.map(
                        jwt
                );

        assertEquals(
                userId,
                context.getUserId()
        );

        assertNull(
                context.getTenantId()
        );

        assertNull(
                context.getSessionId()
        );

        assertTrue(
                context.getRoles().isEmpty()
        );

        assertTrue(
                context.getPermissions().isEmpty()
        );

        assertTrue(
                context.getScopes().isEmpty()
        );
    }

    @Test
    void shouldRejectMissingFunctionalUserId() {

        Jwt jwt =
                baseJwtBuilder()
                        .subject(
                                UUID.randomUUID().toString()
                        )
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(
                        jwt
                )
        );
    }

    @Test
    void shouldRejectInvalidFunctionalUserId() {

        Jwt jwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
                                "not-a-uuid"
                        )
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(
                        jwt
                )
        );
    }

    private Jwt.Builder baseJwtBuilder() {

        Instant now =
                Instant.now();

        return Jwt.withTokenValue(
                        "efs-test-token"
                )
                .header(
                        "alg",
                        "RS256"
                )
                .issuedAt(
                        now
                )
                .expiresAt(
                        now.plusSeconds(
                                300
                        )
                )
                .claim(
                        "iss",
                        "efs-test-issuer"
                )
                .claim(
                        "aud",
                        List.of(
                                "efs-backend"
                        )
                );
    }
}