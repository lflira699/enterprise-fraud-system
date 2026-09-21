package com.efs.modules.transaction.validator;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TransactionLocationValueValidator {

    private static final BigDecimal MIN_LATITUDE =
            new BigDecimal("-90");

    private static final BigDecimal MAX_LATITUDE =
            new BigDecimal("90");

    private static final BigDecimal MIN_LONGITUDE =
            new BigDecimal("-180");

    private static final BigDecimal MAX_LONGITUDE =
            new BigDecimal("180");

    private static final Pattern COUNTRY_CODE_PATTERN =
            Pattern.compile("[A-Za-z]{2}");

    private static final Pattern IPV4_COMPONENT_PATTERN =
            Pattern.compile("[0-9]{1,3}");

    private static final Pattern IPV6_LITERAL_PATTERN =
            Pattern.compile("[0-9A-Fa-f:.]+");

    private TransactionLocationValueValidator() {
    }

    public static InetAddress parseLiteralIpAddress(
            String ipAddress) {

        if (ipAddress == null || ipAddress.isBlank()) {
            throw invalidIpAddress(ipAddress, null);
        }

        String candidate =
                ipAddress.trim();

        if (candidate.contains(":")) {
            return parseIpv6Literal(
                    candidate,
                    ipAddress
            );
        }

        return parseIpv4Literal(
                candidate,
                ipAddress
        );
    }

    public static boolean isLiteralIpAddress(
            String ipAddress) {

        try {
            parseLiteralIpAddress(ipAddress);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static String normalizeCountryCode(
            String countryCode) {

        if (countryCode == null) {
            return null;
        }

        if (!COUNTRY_CODE_PATTERN
                .matcher(countryCode)
                .matches()) {

            throw new IllegalArgumentException(
                    "Invalid country code: "
                            + countryCode
            );
        }

        return countryCode.toUpperCase(
                Locale.ROOT
        );
    }

    public static BigDecimal validateLatitude(
            BigDecimal latitude) {

        return validateRange(
                latitude,
                MIN_LATITUDE,
                MAX_LATITUDE,
                "Latitude"
        );
    }

    public static BigDecimal validateLongitude(
            BigDecimal longitude) {

        return validateRange(
                longitude,
                MIN_LONGITUDE,
                MAX_LONGITUDE,
                "Longitude"
        );
    }

    private static InetAddress parseIpv4Literal(
            String candidate,
            String original) {

        String[] components =
                candidate.split("\\.", -1);

        if (components.length != 4) {
            throw invalidIpAddress(original, null);
        }

        byte[] address =
                new byte[4];

        for (int index = 0;
             index < components.length;
             index++) {

            String component =
                    components[index];

            if (!IPV4_COMPONENT_PATTERN
                    .matcher(component)
                    .matches()) {

                throw invalidIpAddress(
                        original,
                        null
                );
            }

            int octet =
                    Integer.parseInt(component);

            if (octet > 255) {
                throw invalidIpAddress(
                        original,
                        null
                );
            }

            address[index] =
                    (byte) octet;
        }

        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException exception) {
            throw invalidIpAddress(
                    original,
                    exception
            );
        }
    }

    private static InetAddress parseIpv6Literal(
            String candidate,
            String original) {

        if (!IPV6_LITERAL_PATTERN
                .matcher(candidate)
                .matches()) {

            throw invalidIpAddress(
                    original,
                    null
            );
        }

        try {
            return InetAddress.getByName(candidate);
        } catch (UnknownHostException exception) {
            throw invalidIpAddress(
                    original,
                    exception
            );
        }
    }

    private static BigDecimal validateRange(
            BigDecimal value,
            BigDecimal minimum,
            BigDecimal maximum,
            String fieldName) {

        if (value == null) {
            return null;
        }

        if (value.compareTo(minimum) < 0
                || value.compareTo(maximum) > 0) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be between "
                            + minimum
                            + " and "
                            + maximum
            );
        }

        return value;
    }

    private static IllegalArgumentException invalidIpAddress(
            String ipAddress,
            Exception cause) {

        String message =
                "Invalid IP address: " + ipAddress;

        if (cause == null) {
            return new IllegalArgumentException(message);
        }

        return new IllegalArgumentException(
                message,
                cause
        );
    }
}
