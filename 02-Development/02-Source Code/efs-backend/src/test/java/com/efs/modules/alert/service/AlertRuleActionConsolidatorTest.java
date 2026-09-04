package com.efs.modules.alert.service;

import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.service.RuleAlertActionParameterResolver;
import com.efs.modules.rules.service.RuleAlertActionParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertRuleActionConsolidatorTest {

    private AlertRuleActionConsolidator consolidator;

    @BeforeEach
    void setUp() {

        RuleAlertActionParameterResolver parameterResolver =
                new RuleAlertActionParameterResolver();

        consolidator =
                new AlertRuleActionConsolidator(
                        parameterResolver
                );
    }

    @Test
    void shouldReturnEmptyWhenNoRuleActionsExist() {

        Optional<RuleAlertActionParameters> result =
                consolidator.consolidate(
                        List.of()
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldConsolidateSingleCreateAlertAction() {

        RuleAction action =
                createAction(
                        "FRAUD",
                        "HIGH"
                );

        Optional<RuleAlertActionParameters> result =
                consolidator.consolidate(
                        List.of(
                                action
                        )
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "FRAUD",
                result.get().alertType()
        );

        assertEquals(
                "HIGH",
                result.get().priority()
        );
    }

    @Test
    void shouldConsolidateEquivalentCreateAlertActions() {

        RuleAction firstAction =
                createAction(
                        "FRAUD",
                        "HIGH"
                );

        RuleAction secondAction =
                createAction(
                        "FRAUD",
                        "HIGH"
                );

        Optional<RuleAlertActionParameters> result =
                consolidator.consolidate(
                        List.of(
                                firstAction,
                                secondAction
                        )
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                new RuleAlertActionParameters(
                        "FRAUD",
                        "HIGH"
                ),
                result.get()
        );
    }

    @Test
    void shouldRejectConflictingAlertType() {

        RuleAction firstAction =
                createAction(
                        "FRAUD",
                        "HIGH"
                );

        RuleAction secondAction =
                createAction(
                        "ACCOUNT_TAKEOVER",
                        "HIGH"
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                consolidator.consolidate(
                                        List.of(
                                                firstAction,
                                                secondAction
                                        )
                                )
                );

        assertEquals(
                "CREATE_ALERT actions contain conflicting alert parameters",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectConflictingPriority() {

        RuleAction firstAction =
                createAction(
                        "FRAUD",
                        "HIGH"
                );

        RuleAction secondAction =
                createAction(
                        "FRAUD",
                        "MEDIUM"
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                consolidator.consolidate(
                                        List.of(
                                                firstAction,
                                                secondAction
                                        )
                                )
                );

        assertEquals(
                "CREATE_ALERT actions contain conflicting alert parameters",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMissingRuleActions() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                consolidator.consolidate(
                                        null
                                )
                );

        assertEquals(
                "Rule actions are required",
                exception.getMessage()
        );
    }

    private RuleAction createAction(
            String alertType,
            String priority) {

        RuleAction action =
                new RuleAction();

        action.setActionType(
                "CREATE_ALERT"
        );

        action.setParameterJson(
                Map.of(
                        "alertType",
                        alertType,
                        "priority",
                        priority
                )
        );

        return action;
    }
}