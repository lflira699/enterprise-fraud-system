package com.efs.config.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;

public final class EfsJwtAudienceValidator
        implements OAuth2TokenValidator<Jwt> {

    private final String requiredAudience;

    public EfsJwtAudienceValidator(
            String requiredAudience) {

        this.requiredAudience =
                Objects.requireNonNull(
                        requiredAudience,
                        "requiredAudience is required"
                );

        if (requiredAudience.isBlank()) {
            throw new IllegalArgumentException(
                    "requiredAudience must not be blank"
            );
        }
    }

    @Override
    public OAuth2TokenValidatorResult validate(
            Jwt jwt) {

        Objects.requireNonNull(
                jwt,
                "jwt is required"
        );

        if (jwt.getAudience().contains(
                requiredAudience
        )) {

            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error =
                new OAuth2Error(
                        "invalid_token",
                        "JWT does not contain the required audience",
                        null
                );

        return OAuth2TokenValidatorResult.failure(
                error
        );
    }
}