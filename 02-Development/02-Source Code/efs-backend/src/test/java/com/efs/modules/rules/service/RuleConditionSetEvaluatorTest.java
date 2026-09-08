package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleConditionSetEvaluatorTest {

    private RuleConditionSetEvaluator evaluator;

    @BeforeEach
    void setUp() {

        evaluator =
                new RuleConditionSetEvaluator(
                        new RuleConditionEvaluator()
                );
    }

    @Test
    void shouldEvaluateSingleCondition() {

        RuleCondition condition =
                condition(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        null
                );

        assertTrue(
                evaluator.evaluate(
                        List.of(condition),
                        Map.of(
                                "transaction.amount",
                                6000
                        )
                )
        );
    }

    @Test
    void shouldEvaluateAndWhenAllConditionsMatch() {

        RuleCondition amount =
                condition(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        null
                );

        RuleCondition country =
                condition(
                        (short) 2,
                        "transaction.country",
                        "EQUALS",
                        Map.of(
                                "value",
                                "GT"
                        ),
                        "AND"
                );

        assertTrue(
                evaluator.evaluate(
                        List.of(
                                amount,
                                country
                        ),
                        Map.of(
                                "transaction",
                                Map.of(
                                        "amount",
                                        7000,
                                        "country",
                                        "GT"
                                )
                        )
                )
        );
    }

    @Test
    void shouldReturnFalseWhenAndConditionDoesNotMatch() {

        RuleCondition amount =
                condition(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        null
                );

        RuleCondition country =
                condition(
                        (short) 2,
                        "transaction.country",
                        "EQUALS",
                        Map.of(
                                "value",
                                "GT"
                        ),
                        "AND"
                );

        assertFalse(
                evaluator.evaluate(
                        List.of(
                                amount,
                                country
                        ),
                        Map.of(
                                "transaction",
                                Map.of(
                                        "amount",
                                        7000,
                                        "country",
                                        "US"
                                )
                        )
                )
        );
    }

    @Test
    void shouldEvaluateOrWhenOneConditionMatches() {

        RuleCondition amount =
                condition(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                10000
                        ),
                        null
                );

        RuleCondition riskScore =
                condition(
                        (short) 2,
                        "device.risk_score",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                80
                        ),
                        "OR"
                );

        assertTrue(
                evaluator.evaluate(
                        List.of(
                                amount,
                                riskScore
                        ),
                        Map.of(
                                "transaction.amount",
                                5000,
                                "device.risk_score",
                                95
                        )
                )
        );
    }

    @Test
    void shouldEvaluateMixedOperatorsLeftToRight() {

        RuleCondition first =
                condition(
                        (short) 1,
                        "first",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        RuleCondition second =
                condition(
                        (short) 2,
                        "second",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "OR"
                );

        RuleCondition third =
                condition(
                        (short) 3,
                        "third",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "AND"
                );

        assertFalse(
                evaluator.evaluate(
                        List.of(
                                third,
                                first,
                                second
                        ),
                        Map.of(
                                "first",
                                true,
                                "second",
                                false,
                                "third",
                                false
                        )
                )
        );
    }

    @Test
    void shouldSortConditionsByConditionOrder() {

        RuleCondition first =
                condition(
                        (short) 1,
                        "first",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        RuleCondition second =
                condition(
                        (short) 2,
                        "second",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "OR"
                );

        RuleCondition third =
                condition(
                        (short) 3,
                        "third",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "AND"
                );

        assertFalse(
                evaluator.evaluate(
                        List.of(
                                third,
                                second,
                                first
                        ),
                        Map.of(
                                "first",
                                true,
                                "second",
                                false,
                                "third",
                                false
                        )
                )
        );
    }

    @Test
    void shouldRejectLogicalOperatorOnFirstCondition() {

        RuleCondition condition =
                condition(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        "AND"
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(condition),
                                Map.of(
                                        "transaction.amount",
                                        6000
                                )
                        )
        );
    }

    @Test
    void shouldRejectMissingLogicalOperatorAfterFirstCondition() {

        RuleCondition first =
                condition(
                        (short) 1,
                        "first",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        RuleCondition second =
                condition(
                        (short) 2,
                        "second",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(
                                        first,
                                        second
                                ),
                                Map.of(
                                        "first",
                                        true,
                                        "second",
                                        true
                                )
                        )
        );
    }

    @Test
    void shouldRejectUnsupportedLogicalOperator() {

        RuleCondition first =
                condition(
                        (short) 1,
                        "first",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        RuleCondition second =
                condition(
                        (short) 2,
                        "second",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "XOR"
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(
                                        first,
                                        second
                                ),
                                Map.of(
                                        "first",
                                        true,
                                        "second",
                                        true
                                )
                        )
        );
    }

    @Test
    void shouldRejectDuplicateConditionOrder() {

        RuleCondition first =
                condition(
                        (short) 1,
                        "first",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        null
                );

        RuleCondition duplicate =
                condition(
                        (short) 1,
                        "second",
                        "EQUALS",
                        Map.of(
                                "value",
                                true
                        ),
                        "AND"
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(
                                        first,
                                        duplicate
                                ),
                                Map.of(
                                        "first",
                                        true,
                                        "second",
                                        true
                                )
                        )
        );
    }

    @Test
    void shouldRejectConditionWithoutOrder() {

        RuleCondition condition =
                condition(
                        null,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        null
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(condition),
                                Map.of(
                                        "transaction.amount",
                                        6000
                                )
                        )
        );
    }

    @Test
    void shouldRejectEmptyConditionList() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                List.of(),
                                Map.of(
                                        "transaction.amount",
                                        6000
                                )
                        )
        );
    }

    private RuleCondition condition(
            Short order,
            String attributeName,
            String comparisonOperator,
            Map<String, Object> comparisonValue,
            String logicalOperator) {

        RuleCondition condition =
                new RuleCondition();

        condition.setConditionOrder(
                order
        );

        condition.setAttributeName(
                attributeName
        );

        condition.setComparisonOperator(
                comparisonOperator
        );

        condition.setComparisonValue(
                comparisonValue
        );

        condition.setLogicalOperator(
                logicalOperator
        );

        condition.setIsRequired(
                true
        );

        return condition;
    }
}