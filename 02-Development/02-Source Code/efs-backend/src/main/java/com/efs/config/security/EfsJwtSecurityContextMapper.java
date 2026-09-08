package com.efs.config.security;

import com.efs.shared.security.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class EfsJwtSecurityContextMapper {

    static final String CLAIM_USER_ID =
            "user_id";

    static final String CLAIM_TENANT_ID =
            "tenant_id";

    static final String CLAIM_SESSION_ID =
            "session_id";

    static final String CLAIM_ROLES =
            "roles";

    static final String CLAIM_PERMISSIONS =
            "permissions";

    static final String CLAIM_SCOPE =
            "scope";

    public SecurityContext map(
            Jwt jwt) {

        Objects.requireNonNull(
                jwt,
                "jwt is required"
        );

        return new SecurityContext(
                requiredUuidClaim(
                        jwt,
                        CLAIM_USER_ID
                ),
                optionalUuidClaim(
                        jwt,
                        CLAIM_TENANT_ID
                ),
                optionalUuidClaim(
                        jwt,
                        CLAIM_SESSION_ID
                ),
                stringSetClaim(
                        jwt,
                        CLAIM_ROLES
                ),
                stringSetClaim(
                        jwt,
                        CLAIM_PERMISSIONS
                ),
                stringSetClaim(
                        jwt,
                        CLAIM_SCOPE
                )
        );
    }

    private UUID requiredUuidClaim(
            Jwt jwt,
            String claimName) {

        Object value =
                jwt.getClaim(
                        claimName
                );

        if (value == null) {
            throw new IllegalArgumentException(
                    "JWT claim '"
                            + claimName
                            + "' is required"
            );
        }

        return parseUuid(
                claimName,
                value
        );
    }

    private UUID optionalUuidClaim(
            Jwt jwt,
            String claimName) {

        Object value =
                jwt.getClaim(
                        claimName
                );

        if (value == null) {
            return null;
        }

        return parseUuid(
                claimName,
                value
        );
    }

    private UUID parseUuid(
            String claimName,
            Object value) {

        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException(
                    "JWT claim '"
                            + claimName
                            + "' must be a UUID string"
            );
        }

        try {
            return UUID.fromString(
                    stringValue
            );
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "JWT claim '"
                            + claimName
                            + "' must contain a valid UUID",
                    exception
            );
        }
    }

    private Set<String> stringSetClaim(
            Jwt jwt,
            String claimName) {

        Object value =
                jwt.getClaim(
                        claimName
                );

        if (value == null) {
            return Set.of();
        }

        if (value instanceof String stringValue) {
            return parseDelimitedValues(
                    stringValue
            );
        }

        if (value instanceof Collection<?> collection) {

            LinkedHashSet<String> values =
                    new LinkedHashSet<>();

            for (Object item : collection) {

                if (!(item instanceof String stringItem)) {
                    throw new IllegalArgumentException(
                            "JWT claim '"
                                    + claimName
                                    + "' must contain only strings"
                    );
                }

                String normalized =
                        stringItem.trim();

                if (!normalized.isEmpty()) {
                    values.add(
                            normalized
                    );
                }
            }

            return Set.copyOf(
                    values
            );
        }

        throw new IllegalArgumentException(
                "JWT claim '"
                        + claimName
                        + "' must be a string or collection of strings"
        );
    }

    private Set<String> parseDelimitedValues(
            String value) {

        if (value.isBlank()) {
            return Set.of();
        }

        LinkedHashSet<String> values =
                new LinkedHashSet<>();

        for (String item : value.trim().split("\\s+")) {

            if (!item.isBlank()) {
                values.add(
                        item
                );
            }
        }

        return Set.copyOf(
                values
        );
    }
}