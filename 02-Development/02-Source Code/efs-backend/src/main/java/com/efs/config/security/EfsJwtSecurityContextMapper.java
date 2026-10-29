package com.efs.config.security;

import com.efs.shared.security.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    static final String CLAIM_SID =
            "sid";

    private static final String CLAIM_ISSUER =
            "iss";

    private static final String SESSION_BINDING_DOMAIN =
            "EFS-IDP-SESSION-BINDING";

    private static final int SESSION_BINDING_VERSION =
            1;



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
                deriveSessionId(jwt),
                Set.of(),
                Set.of(),
                stringSetClaim(
                        jwt,
                        CLAIM_SCOPE
                )
        );
    }

    private UUID deriveSessionId(
            Jwt jwt) {

        String issuer =
                requiredStringClaim(
                        jwt,
                        CLAIM_ISSUER
                );

        String sid =
                requiredStringClaim(
                        jwt,
                        CLAIM_SID
                );

        MessageDigest digest =
                sessionBindingDigest();

        updateLengthDelimited(
                digest,
                SESSION_BINDING_DOMAIN
        );

        digest.update(
                ByteBuffer.allocate(Integer.BYTES)
                        .putInt(SESSION_BINDING_VERSION)
                        .array()
        );

        updateLengthDelimited(
                digest,
                issuer
        );

        updateLengthDelimited(
                digest,
                sid
        );

        byte[] uuidBytes =
                java.util.Arrays.copyOf(
                        digest.digest(),
                        16
                );

        uuidBytes[6] =
                (byte) (
                        (uuidBytes[6] & 0x0f)
                                | 0x80
                );

        uuidBytes[8] =
                (byte) (
                        (uuidBytes[8] & 0x3f)
                                | 0x80
                );

        ByteBuffer buffer =
                ByteBuffer.wrap(
                        uuidBytes
                );

        return new UUID(
                buffer.getLong(),
                buffer.getLong()
        );
    }

    private String requiredStringClaim(
            Jwt jwt,
            String claimName) {

        Object value =
                jwt.getClaim(
                        claimName
                );

        if (!(value instanceof String stringValue)
                || stringValue.isEmpty()) {

            throw new IllegalArgumentException(
                    "JWT claim '"
                            + claimName
                            + "' must contain a non-empty string"
            );
        }

        return stringValue;
    }

    private MessageDigest sessionBindingDigest() {

        try {
            return MessageDigest.getInstance(
                    "SHA-256"
            );
        }
        catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available",
                    exception
            );
        }
    }

    private void updateLengthDelimited(
            MessageDigest digest,
            String value) {

        byte[] bytes =
                value.getBytes(
                        StandardCharsets.UTF_8
                );

        digest.update(
                ByteBuffer.allocate(Integer.BYTES)
                        .putInt(bytes.length)
                        .array()
        );

        digest.update(
                bytes
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