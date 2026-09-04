package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleAction;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleAlertActionParameterResolverTest {

    private final RuleAlertActionParameterResolver resolver =
            new RuleAlertActionParameterResolver();

    @Test
    void shouldResolveApprovedCreateAlertParameters() {

        RuleAction action =
                createAction(
                        Map.of(
                                "alertType",
                                "FRAUD",
                                "priority",
                                "HIGH"
                        )
                );

        RuleAlertActionParameters result =
                resolver.resolve(
                        action
                );

        assertEquals(
                "FRAUD",
                result.alertType()
        );

        assertEquals(
                "HIGH",
                result.priority()
        );
    }

    @Test
    void shouldRejectMissingRuleAction() {

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(null)
        );
    }

    @Test
    void shouldRejectNonCreateAlertAction() {

        RuleAction action =
                new RuleAction();

        action.setActionType(
                "OTHER_ACTION"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectMissingParameterJson() {

        RuleAction action =
                createAction(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectMissingAlertType() {

        RuleAction action =
                createAction(
                        Map.of(
                                "priority",
                                "HIGH"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectMissingPriority() {

        RuleAction action =
                createAction(
                        Map.of(
                                "alertType",
                                "FRAUD"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectNonStringParameters() {

        RuleAction action =
                createAction(
                        Map.of(
                                "alertType",
                                100,
                                "priority",
                                "HIGH"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectAlertTypeAboveMaximumLength() {

        RuleAction action =
                createAction(
                        Map.of(
                                "alertType",
                                "A".repeat(41),
                                "priority",
                                "HIGH"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    @Test
    void shouldRejectPriorityAboveMaximumLength() {

        RuleAction action =
                createAction(
                        Map.of(
                                "alertType",
                                "FRAUD",
                                "priority",
                                "P".repeat(21)
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(action)
        );
    }

    private RuleAction createAction(
            Map<String, Object> parameters) {

        RuleAction action =
                new RuleAction();

        action.setActionType(
                "CREATE_ALERT"
        );

        action.setParameterJson(
                parameters
        );

        return action;
    }
}