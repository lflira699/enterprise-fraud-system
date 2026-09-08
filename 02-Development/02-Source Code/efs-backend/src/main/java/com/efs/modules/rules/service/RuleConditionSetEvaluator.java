package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleCondition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class RuleConditionSetEvaluator {

    private static final String AND =
            "AND";

    private static final String OR =
            "OR";

    private final RuleConditionEvaluator conditionEvaluator;

    public RuleConditionSetEvaluator(
            RuleConditionEvaluator conditionEvaluator) {

        this.conditionEvaluator =
                conditionEvaluator;
    }

    public boolean evaluate(
            List<RuleCondition> conditions,
            Map<String, Object> facts) {

        if (conditions == null
                || conditions.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one rule condition is required"
            );
        }

        if (facts == null) {
            throw new IllegalArgumentException(
                    "Evaluation facts are required"
            );
        }

        List<RuleCondition> orderedConditions =
                validateAndOrder(
                        conditions
                );

        RuleCondition firstCondition =
                orderedConditions.get(0);

        if (hasText(
                firstCondition.getLogicalOperator()
        )) {

            throw new IllegalArgumentException(
                    "First rule condition must not "
                            + "define a logical operator"
            );
        }

        boolean result =
                conditionEvaluator.evaluate(
                        firstCondition,
                        facts
                );

        for (int index = 1;
             index < orderedConditions.size();
             index++) {

            RuleCondition condition =
                    orderedConditions.get(index);

            String logicalOperator =
                    normalizeLogicalOperator(
                            condition.getLogicalOperator()
                    );

            boolean conditionResult =
                    conditionEvaluator.evaluate(
                            condition,
                            facts
                    );

            result =
                    switch (logicalOperator) {

                        case AND ->
                                result
                                        && conditionResult;

                        case OR ->
                                result
                                        || conditionResult;

                        default ->
                                throw new IllegalArgumentException(
                                        "Unsupported rule logical operator: "
                                                + logicalOperator
                                );
                    };
        }

        return result;
    }

    private List<RuleCondition> validateAndOrder(
            List<RuleCondition> conditions) {

        List<RuleCondition> orderedConditions =
                new ArrayList<>(
                        conditions
                );

        Set<Short> conditionOrders =
                new HashSet<>();

        for (RuleCondition condition :
                orderedConditions) {

            if (condition == null) {
                throw new IllegalArgumentException(
                        "Rule condition is required"
                );
            }

            Short conditionOrder =
                    condition.getConditionOrder();

            if (conditionOrder == null) {
                throw new IllegalArgumentException(
                        "Rule condition order is required"
                );
            }

            if (!conditionOrders.add(
                    conditionOrder
            )) {

                throw new IllegalArgumentException(
                        "Duplicate rule condition order: "
                                + conditionOrder
                );
            }
        }

        orderedConditions.sort(
                Comparator.comparing(
                        RuleCondition::getConditionOrder
                )
        );

        return orderedConditions;
    }

    private String normalizeLogicalOperator(
            String logicalOperator) {

        if (!hasText(logicalOperator)) {

            throw new IllegalArgumentException(
                    "Logical operator is required "
                            + "after the first rule condition"
            );
        }

        return logicalOperator
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }
}