package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleCondition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
public class RuleConditionEvaluator {

    private static final String EQUALS =
            "EQUALS";

    private static final String NOT_EQUALS =
            "NOT_EQUALS";

    private static final String GREATER_THAN =
            "GREATER_THAN";

    private static final String GREATER_THAN_OR_EQUAL =
            "GREATER_THAN_OR_EQUAL";

    private static final String LESS_THAN =
            "LESS_THAN";

    private static final String LESS_THAN_OR_EQUAL =
            "LESS_THAN_OR_EQUAL";

    private static final String BETWEEN =
            "BETWEEN";

    public boolean evaluate(
            RuleCondition condition,
            Map<String, Object> facts) {

        if (condition == null) {
            throw new IllegalArgumentException(
                    "Rule condition is required"
            );
        }

        if (facts == null) {
            throw new IllegalArgumentException(
                    "Evaluation facts are required"
            );
        }

        String attributeName =
                requireText(
                        condition.getAttributeName(),
                        "Rule condition attribute name is required"
                );

        String comparisonOperator =
                normalizeComparisonOperator(
                        requireText(
                                condition.getComparisonOperator(),
                                "Rule condition comparison operator is required"
                        )
                );

        Map<String, Object> comparisonValue =
                condition.getComparisonValue();

        if (comparisonValue == null) {
            throw new IllegalArgumentException(
                    "Rule condition comparison value is required"
            );
        }

        Object actualValue =
                resolveAttribute(
                        facts,
                        attributeName
                );

        if (actualValue == null) {
            return false;
        }

        return switch (comparisonOperator) {

            case EQUALS, "=" ->
                    evaluateEquals(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    );

            case NOT_EQUALS, "!=" ->
                    !evaluateEquals(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    );

            case GREATER_THAN, ">" ->
                    compareNumeric(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    ) > 0;

            case GREATER_THAN_OR_EQUAL,
                 "GREATER_THAN_OR_EQUALS",
                 ">=" ->
                    compareNumeric(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    ) >= 0;

            case LESS_THAN, "<" ->
                    compareNumeric(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    ) < 0;

            case LESS_THAN_OR_EQUAL,
                 "LESS_THAN_OR_EQUALS",
                 "<=" ->
                    compareNumeric(
                            actualValue,
                            requiredComparisonValue(
                                    comparisonValue,
                                    "value"
                            )
                    ) <= 0;

            case BETWEEN ->
                    evaluateBetween(
                            actualValue,
                            comparisonValue
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported rule comparison operator: "
                                    + comparisonOperator
                    );
        };
    }

    private boolean evaluateEquals(
            Object actualValue,
            Object expectedValue) {

        if (actualValue instanceof Number
                && expectedValue instanceof Number) {

            return toBigDecimal(actualValue)
                    .compareTo(
                            toBigDecimal(expectedValue)
                    ) == 0;
        }

        return Objects.equals(
                actualValue,
                expectedValue
        );
    }

    private boolean evaluateBetween(
            Object actualValue,
            Map<String, Object> comparisonValue) {

        Object minimum =
                requiredComparisonValue(
                        comparisonValue,
                        "minimum"
                );

        Object maximum =
                requiredComparisonValue(
                        comparisonValue,
                        "maximum"
                );

        BigDecimal minimumValue =
                toBigDecimal(minimum);

        BigDecimal maximumValue =
                toBigDecimal(maximum);

        if (minimumValue.compareTo(maximumValue) > 0) {
            throw new IllegalArgumentException(
                    "Rule condition BETWEEN minimum "
                            + "cannot be greater than maximum"
            );
        }

        BigDecimal actual =
                toBigDecimal(actualValue);

        return actual.compareTo(minimumValue) >= 0
                && actual.compareTo(maximumValue) <= 0;
    }

    private int compareNumeric(
            Object actualValue,
            Object expectedValue) {

        return toBigDecimal(actualValue)
                .compareTo(
                        toBigDecimal(expectedValue)
                );
    }

    private BigDecimal toBigDecimal(
            Object value) {

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        if (value instanceof Number number) {
            return new BigDecimal(
                    number.toString()
            );
        }

        if (value instanceof String text) {

            try {
                return new BigDecimal(
                        text
                );
            } catch (NumberFormatException exception) {

                throw new IllegalArgumentException(
                        "Rule condition value is not numeric: "
                                + text,
                        exception
                );
            }
        }

        throw new IllegalArgumentException(
                "Rule condition value is not numeric: "
                        + value
        );
    }

    private Object requiredComparisonValue(
            Map<String, Object> comparisonValue,
            String key) {

        if (!comparisonValue.containsKey(key)
                || comparisonValue.get(key) == null) {

            throw new IllegalArgumentException(
                    "Rule condition comparison value "
                            + "requires field: "
                            + key
            );
        }

        return comparisonValue.get(key);
    }

    private Object resolveAttribute(
            Map<String, Object> facts,
            String attributeName) {

        if (facts.containsKey(attributeName)) {
            return facts.get(attributeName);
        }

        Object current =
                facts;

        for (String segment :
                attributeName.split("\\.")) {

            if (!(current instanceof Map<?, ?> currentMap)) {
                return null;
            }

            if (!currentMap.containsKey(segment)) {
                return null;
            }

            current =
                    currentMap.get(segment);
        }

        return current;
    }

    private String normalizeComparisonOperator(
            String comparisonOperator) {

        return comparisonOperator
                .trim()
                .toUpperCase(
                        Locale.ROOT
                )
                .replace(
                        ' ',
                        '_'
                );
    }

    private String requireText(
            String value,
            String message) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return value;
    }
}