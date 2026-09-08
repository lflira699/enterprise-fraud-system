package com.efs.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EfsJwtAudienceValidatorTest {

    @Test
    void shouldAcceptRequiredAudience() {

        EfsJwtAudienceValidator validator =
                new EfsJwtAudienceValidator(
                        "efs-backend"
                );

        OAuth2TokenValidatorResult result =
                validator.validate(
                        buildJwt(
                                List.of(
                                        "efs-backend",
                                        "another-service"
                                )
                        )
                );

        assertFalse(
                result.hasErrors()
        );
    }

    @Test
    void shouldRejectMissingRequiredAudience() {

        EfsJwtAudienceValidator validator =
                new EfsJwtAudienceValidator(
                        "efs-backend"
                );

        OAuth2TokenValidatorResult result =
                validator.validate(
                        buildJwt(
                                List.of(
                                        "another-service"
                                )
                        )
                );

        assertTrue(
                result.hasErrors()
        );
    }

    @Test
    void shouldRejectBlankRequiredAudienceConfiguration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EfsJwtAudienceValidator(
                        " "
                )
        );
    }

    private Jwt buildJwt(
            List<String> audience) {

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
                        "https://issuer.example"
                )
                .claim(
                        "aud",
                        audience
                )
                .build();
    }
}