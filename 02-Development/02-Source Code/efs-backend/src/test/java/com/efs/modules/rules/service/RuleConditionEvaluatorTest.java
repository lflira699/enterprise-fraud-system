package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleConditionEvaluatorTest {

    private RuleConditionEvaluator evaluator;

    @BeforeEach
    void setUp() {

        evaluator =
                new RuleConditionEvaluator();
    }

    @Test
    void shouldEvaluateEqualsForNestedStringAttribute() {

        RuleCondition condition =
                condition(
                        "transaction.country",
                        "EQUALS",
                        Map.of(
                                "value",
                                "GT"
                        )
                );

        Map<String, Object> facts =
                Map.of(
                        "transaction",
                        Map.of(
                                "country",
                                "GT"
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        facts
                )
        );
    }

    @Test
    void shouldReturnFalseWhenEqualsDoesNotMatch() {

        RuleCondition condition =
                condition(
                        "transaction.country",
                        "EQUALS",
                        Map.of(
                                "value",
                                "GT"
                        )
                );

        Map<String, Object> facts =
                Map.of(
                        "transaction",
                        Map.of(
                                "country",
                                "US"
                        )
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        facts
                )
        );
    }

    @Test
    void shouldEvaluateGreaterThanAcrossNumericTypes() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        )
                );

        Map<String, Object> facts =
                Map.of(
                        "transaction",
                        Map.of(
                                "amount",
                                new BigDecimal("5000.01")
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        facts
                )
        );
    }

    @Test
    void shouldReturnFalseWhenGreaterThanIsEqual() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        )
                );

        Map<String, Object> facts =
                Map.of(
                        "transaction.amount",
                        5000L
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        facts
                )
        );
    }

    @Test
    void shouldEvaluateBetweenInclusively() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "BETWEEN",
                        Map.of(
                                "minimum",
                                1000,
                                "maximum",
                                5000
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                1000
                        )
                )
        );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5000
                        )
                )
        );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                2500
                        )
                )
        );
    }

    @Test
    void shouldReturnFalseWhenBetweenValueIsOutsideRange() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "BETWEEN",
                        Map.of(
                                "minimum",
                                1000,
                                "maximum",
                                5000
                        )
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5001
                        )
                )
        );
    }

    @Test
    void shouldReturnFalseWhenAttributeIsMissing() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction",
                                Map.of(
                                        "country",
                                        "GT"
                                )
                        )
                )
        );
    }

    @Test
    void shouldRejectUnsupportedComparisonOperator() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "UNSUPPORTED",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                condition,
                                Map.of(
                                        "transaction.amount",
                                        6000
                                )
                        )
        );
    }

    @Test
    void shouldRejectInvalidBetweenRange() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "BETWEEN",
                        Map.of(
                                "minimum",
                                5000,
                                "maximum",
                                1000
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                condition,
                                Map.of(
                                        "transaction.amount",
                                        2500
                                )
                        )
        );
    }

    @Test
    void shouldRejectMissingComparisonValue() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "unexpected",
                                5000
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                condition,
                                Map.of(
                                        "transaction.amount",
                                        6000
                                )
                        )
        );
    }

    @Test
    void shouldEvaluateEqualsSymbol() {

        RuleCondition condition =
                condition(
                        "transaction.country",
                        "=",
                        Map.of(
                                "value",
                                "GT"
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.country",
                                "GT"
                        )
                )
        );
    }

    @Test
    void shouldEvaluateNotEquals() {

        RuleCondition condition =
                condition(
                        "transaction.country",
                        "NOT_EQUALS",
                        Map.of(
                                "value",
                                "US"
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.country",
                                "GT"
                        )
                )
        );
    }

    @Test
    void shouldEvaluateNotEqualsSymbol() {

        RuleCondition condition =
                condition(
                        "transaction.country",
                        "!=",
                        Map.of(
                                "value",
                                "GT"
                        )
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.country",
                                "GT"
                        )
                )
        );
    }

    @Test
    void shouldEvaluateGreaterThanOrEqualAtBoundary() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "GREATER_THAN_OR_EQUAL",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5000L
                        )
                )
        );
    }

    @Test
    void shouldEvaluateGreaterThanOrEqualSymbol() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        ">=",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5001
                        )
                )
        );
    }

    @Test
    void shouldEvaluateLessThan() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "LESS_THAN",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                4999
                        )
                )
        );
    }

    @Test
    void shouldEvaluateLessThanSymbol() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "<",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertFalse(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5000
                        )
                )
        );
    }

    @Test
    void shouldEvaluateLessThanOrEqualAtBoundary() {

        RuleCondition condition =
                condition(
                        "transaction.amount",
                        "<=",
                        Map.of(
                                "value",
                                5000
                        )
                );

        assertTrue(
                evaluator.evaluate(
                        condition,
                        Map.of(
                                "transaction.amount",
                                5000
                        )
                )
        );
    }
    private RuleCondition condition(
            String attributeName,
            String comparisonOperator,
            Map<String, Object> comparisonValue) {

        RuleCondition condition =
                new RuleCondition();

        condition.setAttributeName(
                attributeName
        );

        condition.setComparisonOperator(
                comparisonOperator
        );

        condition.setComparisonValue(
                comparisonValue
        );

        condition.setIsRequired(
                true
        );

        return condition;
    }
}
