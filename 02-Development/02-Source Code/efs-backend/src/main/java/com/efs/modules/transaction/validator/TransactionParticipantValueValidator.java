package com.efs.modules.transaction.validator;

import java.util.Locale;
import java.util.regex.Pattern;

public final class TransactionParticipantValueValidator {

    private static final Pattern COUNTRY_CODE_PATTERN =
            Pattern.compile("[A-Za-z]{2}");

    private TransactionParticipantValueValidator() {
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
}
