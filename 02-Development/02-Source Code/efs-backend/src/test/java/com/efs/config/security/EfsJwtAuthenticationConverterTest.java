package com.efs.config.security;

import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EfsJwtAuthenticationConverterTest {

    private final EfsJwtAuthenticationConverter converter =
            new EfsJwtAuthenticationConverter(
                    new EfsJwtSecurityContextMapper()
            );

    @Test
    void shouldCreateAuthenticatedSpringSecurityPrincipal() {

        UUID userId =
                UUID.randomUUID();

        Jwt jwt =
                buildJwt(
                        userId
                );

        Authentication authentication =
                converter.convert(
                        jwt
                );

        assertTrue(
                authentication.isAuthenticated()
        );

        SecurityContext context =
                assertInstanceOf(
                        SecurityContext.class,
                        authentication.getPrincipal()
                );

        assertEquals(
                userId,
                context.getUserId()
        );
    }

    @Test
    void shouldNotUseExternalSubjectAsFunctionalUserId() {

        UUID userId =
                UUID.randomUUID();

        Jwt jwt =
                buildJwt(
                        userId
                );

        SecurityContext context =
                (SecurityContext) converter
                        .convert(
                                jwt
                        )
                        .getPrincipal();

        assertEquals(
                userId,
                context.getUserId()
        );
    }

    private Jwt buildJwt(
            UUID userId) {

        Instant now =
                Instant.now();

        return Jwt.withTokenValue(
                        "efs-test-token"
                )
                .header(
                        "alg",
                        "RS256"
                )
                .subject(
                        "external-keycloak-subject"
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
                )
                .claim(
                        "user_id",
                        userId.toString()
                )
                .build();
    }
}