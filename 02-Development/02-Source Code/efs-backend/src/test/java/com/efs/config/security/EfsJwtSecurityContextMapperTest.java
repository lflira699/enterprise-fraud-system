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

        String sid =
                "keycloak-session-alpha";

        UUID sessionId =
                UUID.fromString(
                        "366fb2ef-6341-801f-a04d-4cfcdb378eb1"
                );

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
                                "sid",
                                sid
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
                context.getRoles().isEmpty()
        );

        assertTrue(
                context.getPermissions().isEmpty()
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
    void shouldAllowOptionalTenantClaimWhenSidIsPresent() {

        UUID userId =
                UUID.randomUUID();

        String sid =
                "keycloak-session-alpha";

        UUID sessionId =
                UUID.fromString(
                        "366fb2ef-6341-801f-a04d-4cfcdb378eb1"
                );

        Jwt jwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
                                userId.toString()
                        )
                        .claim(
                                "sid",
                                sid
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

        assertEquals(
                sessionId,
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
    void shouldRejectMissingSid() {

        Jwt jwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
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

    @Test
    void shouldDeriveStableSessionIdForSameIssuerAndSid() {

        UUID userId =
                UUID.randomUUID();

        Jwt firstJwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
                                userId.toString()
                        )
                        .claim(
                                "sid",
                                "keycloak-session-alpha"
                        )
                        .build();

        Jwt secondJwt =
                baseJwtBuilder()
                        .claim(
                                "user_id",
                                userId.toString()
                        )
                        .claim(
                                "sid",
                                "keycloak-session-alpha"
                        )
                        .build();

        assertEquals(
                mapper.map(firstJwt).getSessionId(),
                mapper.map(secondJwt).getSessionId()
        );

        assertEquals(
                UUID.fromString(
                        "366fb2ef-6341-801f-a04d-4cfcdb378eb1"
                ),
                mapper.map(firstJwt).getSessionId()
        );
    }

    @Test
    void shouldDeriveDifferentSessionIdForDifferentSid() {

        UUID userId =
                UUID.randomUUID();

        UUID first =
                mapper.map(
                        baseJwtBuilder()
                                .claim(
                                        "user_id",
                                        userId.toString()
                                )
                                .claim(
                                        "sid",
                                        "keycloak-session-alpha"
                                )
                                .build()
                ).getSessionId();

        UUID second =
                mapper.map(
                        baseJwtBuilder()
                                .claim(
                                        "user_id",
                                        userId.toString()
                                )
                                .claim(
                                        "sid",
                                        "keycloak-session-beta"
                                )
                                .build()
                ).getSessionId();

        assertTrue(
                !first.equals(second)
        );
    }

    @Test
    void shouldDeriveDifferentSessionIdForDifferentIssuer() {

        UUID userId =
                UUID.randomUUID();

        UUID first =
                mapper.map(
                        baseJwtBuilder()
                                .claim(
                                        "user_id",
                                        userId.toString()
                                )
                                .claim(
                                        "sid",
                                        "keycloak-session-alpha"
                                )
                                .build()
                ).getSessionId();

        UUID second =
                mapper.map(
                        baseJwtBuilder()
                                .claim(
                                        "iss",
                                        "efs-test-issuer-2"
                                )
                                .claim(
                                        "user_id",
                                        userId.toString()
                                )
                                .claim(
                                        "sid",
                                        "keycloak-session-alpha"
                                )
                                .build()
                ).getSessionId();

        assertTrue(
                !first.equals(second)
        );
    }

    @Test
    void shouldProduceUuidVersion8WithIetfVariant() {

        UUID sessionId =
                mapper.map(
                        baseJwtBuilder()
                                .claim(
                                        "user_id",
                                        UUID.randomUUID().toString()
                                )
                                .claim(
                                        "sid",
                                        "keycloak-session-alpha"
                                )
                                .build()
                ).getSessionId();

        assertEquals(
                8,
                sessionId.version()
        );

        assertEquals(
                2,
                sessionId.variant()
        );
    }

    @Test
    void shouldRejectMissingIssuer() {

        Instant now =
                Instant.now();

        Jwt jwt =
                Jwt.withTokenValue(
                                "efs-test-token-without-issuer"
                        )
                        .header(
                                "alg",
                                "RS256"
                        )
                        .issuedAt(
                                now
                        )
                        .expiresAt(
                                now.plusSeconds(300)
                        )
                        .claim(
                                "user_id",
                                UUID.randomUUID().toString()
                        )
                        .claim(
                                "sid",
                                "keycloak-session-alpha"
                        )
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(jwt)
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