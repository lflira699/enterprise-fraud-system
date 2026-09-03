package com.efs.modules.rules.service;

import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleAlertActionResolverTest {

    private RuleActionRepository ruleActionRepository;
    private RuleAlertActionResolver resolver;

    @BeforeEach
    void setUp() {

        ruleActionRepository =
                mock(
                        RuleActionRepository.class
                );

        resolver =
                new RuleAlertActionResolver(
                        ruleActionRepository
                );
    }

    @Test
    void shouldResolveCreateAlertActionsForMatchedExecution() {

        UUID ruleVersionId =
                UUID.randomUUID();

        RuleExecution execution =
                createExecution(
                        true,
                        ruleVersionId
                );

        RuleAction firstCreateAlert =
                createAction(
                        "CREATE_ALERT",
                        (short) 1
                );

        RuleAction nonAlertAction =
                createAction(
                        "OTHER_ACTION",
                        (short) 2
                );

        RuleAction secondCreateAlert =
                createAction(
                        "CREATE_ALERT",
                        (short) 3
                );

        when(
                ruleActionRepository
                        .findByRuleVersionIdOrderByExecutionOrderAsc(
                                ruleVersionId
                        )
        ).thenReturn(
                List.of(
                        firstCreateAlert,
                        nonAlertAction,
                        secondCreateAlert
                )
        );

        List<RuleAction> result =
                resolver.resolveCreateAlertActions(
                        execution
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                firstCreateAlert,
                result.get(0)
        );

        assertEquals(
                secondCreateAlert,
                result.get(1)
        );

        verify(
                ruleActionRepository
        ).findByRuleVersionIdOrderByExecutionOrderAsc(
                ruleVersionId
        );
    }

    @Test
    void shouldReturnEmptyForNonMatchedExecution() {

        RuleExecution execution =
                createExecution(
                        false,
                        UUID.randomUUID()
                );

        List<RuleAction> result =
                resolver.resolveCreateAlertActions(
                        execution
                );

        assertTrue(
                result.isEmpty()
        );

        verify(
                ruleActionRepository,
                never()
        ).findByRuleVersionIdOrderByExecutionOrderAsc(
                org.mockito.ArgumentMatchers
                        .any()
        );
    }

    @Test
    void shouldReturnEmptyWhenRuleVersionIsMissing() {

        RuleExecution execution =
                createExecution(
                        true,
                        null
                );

        List<RuleAction> result =
                resolver.resolveCreateAlertActions(
                        execution
                );

        assertTrue(
                result.isEmpty()
        );

        verify(
                ruleActionRepository,
                never()
        ).findByRuleVersionIdOrderByExecutionOrderAsc(
                org.mockito.ArgumentMatchers
                        .any()
        );
    }

    @Test
    void shouldReturnEmptyWhenMatchedRuleHasNoCreateAlertAction() {

        UUID ruleVersionId =
                UUID.randomUUID();

        RuleExecution execution =
                createExecution(
                        true,
                        ruleVersionId
                );

        when(
                ruleActionRepository
                        .findByRuleVersionIdOrderByExecutionOrderAsc(
                                ruleVersionId
                        )
        ).thenReturn(
                List.of(
                        createAction(
                                "OTHER_ACTION",
                                (short) 1
                        )
                )
        );

        List<RuleAction> result =
                resolver.resolveCreateAlertActions(
                        execution
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldRejectMissingRuleExecution() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                resolver
                                        .resolveCreateAlertActions(
                                                null
                                        )
                );

        assertEquals(
                "Rule execution is required",
                exception.getMessage()
        );
    }

    private RuleExecution createExecution(
            boolean matched,
            UUID ruleVersionId) {

        RuleExecution execution =
                new RuleExecution();

        execution.setMatched(
                matched
        );

        execution.setRuleVersionId(
                ruleVersionId
        );

        return execution;
    }

    private RuleAction createAction(
            String actionType,
            short executionOrder) {

        RuleAction action =
                new RuleAction();

        action.setActionType(
                actionType
        );

        action.setExecutionOrder(
                executionOrder
        );

        return action;
    }
}