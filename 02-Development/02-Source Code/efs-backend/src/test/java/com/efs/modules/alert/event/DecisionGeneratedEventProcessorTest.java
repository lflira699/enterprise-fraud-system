package com.efs.modules.alert.event;

import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.service.AlertRuleActionConsolidator;
import com.efs.modules.alert.service.AlertServiceInterface;
import com.efs.modules.integration.service.ProcessedDomainEventRegistry;
import com.efs.modules.rules.entity.RuleAction;
import com.efs.modules.rules.service.RuleAlertActionParameters;
import com.efs.modules.rules.service.RuleAlertActionResolver;
import com.efs.modules.transaction.entity.TransactionDecision;
import com.efs.modules.transaction.repository.TransactionDecisionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DecisionGeneratedEventProcessorTest {

    private ProcessedDomainEventRegistry
            processedDomainEventRegistry;

    private TransactionDecisionRepository
            transactionDecisionRepository;

    private RuleAlertActionResolver
            ruleAlertActionResolver;

    private AlertRuleActionConsolidator
            alertRuleActionConsolidator;

    private AlertServiceInterface
            alertService;

    private DecisionGeneratedEventProcessor
            processor;

    @BeforeEach
    void setUp() {

        processedDomainEventRegistry =
                mock(
                        ProcessedDomainEventRegistry.class
                );

        transactionDecisionRepository =
                mock(
                        TransactionDecisionRepository.class
                );

        ruleAlertActionResolver =
                mock(
                        RuleAlertActionResolver.class
                );

        alertRuleActionConsolidator =
                mock(
                        AlertRuleActionConsolidator.class
                );

        alertService =
                mock(
                        AlertServiceInterface.class
                );

        processor =
                new DecisionGeneratedEventProcessor(
                        processedDomainEventRegistry,
                        transactionDecisionRepository,
                        ruleAlertActionResolver,
                        alertRuleActionConsolidator,
                        alertService
                );
    }

    @Test
    void shouldCreateSingleAlertForDecisionGeneratedEvent() {

        UUID messageId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID decisionId =
                UUID.randomUUID();

        UUID transactionId =
                UUID.randomUUID();

        UUID riskAssessmentId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        correlationId,
                        decisionId
                );

        TransactionDecision decision =
                createDecision(
                        decisionId,
                        transactionId,
                        riskAssessmentId
                );

        RuleAction action =
                new RuleAction();

        when(
                processedDomainEventRegistry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        ).thenReturn(
                true
        );

        when(
                transactionDecisionRepository
                        .findByDecisionId(
                                decisionId
                        )
        ).thenReturn(
                Optional.of(
                        decision
                )
        );

        when(
                ruleAlertActionResolver
                        .resolveCreateAlertActionsByTransactionId(
                                transactionId
                        )
        ).thenReturn(
                List.of(
                        action
                )
        );

        when(
                alertRuleActionConsolidator
                        .consolidate(
                                List.of(
                                        action
                                )
                        )
        ).thenReturn(
                Optional.of(
                        new RuleAlertActionParameters(
                                "FRAUD",
                                "HIGH"
                        )
                )
        );

        processor.process(
                message
        );

        ArgumentCaptor<AlertRequest> requestCaptor =
                ArgumentCaptor.forClass(
                        AlertRequest.class
                );

        verify(
                alertService
        ).createAlert(
                requestCaptor.capture()
        );

        AlertRequest request =
                requestCaptor.getValue();

        assertEquals(
                decisionId,
                request.getDecisionId()
        );

        assertEquals(
                transactionId,
                request.getTransactionId()
        );

        assertEquals(
                riskAssessmentId,
                request.getRiskAssessmentId()
        );

        assertEquals(
                "FRAUD",
                request.getAlertType()
        );

        assertEquals(
                "HIGH",
                request.getPriority()
        );

        assertEquals(
                correlationId,
                request.getCorrelationId()
        );

        assertNull(
                request.getRuleId()
        );
    }

    @Test
    void shouldIgnoreAlreadyProcessedEvent() {

        UUID messageId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        when(
                processedDomainEventRegistry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        ).thenReturn(
                false
        );

        processor.process(
                message
        );

        verifyNoInteractions(
                transactionDecisionRepository,
                ruleAlertActionResolver,
                alertRuleActionConsolidator,
                alertService
        );
    }

    @Test
    void shouldCompleteWithoutAlertWhenNoCreateAlertActionExists() {

        UUID messageId =
                UUID.randomUUID();

        UUID decisionId =
                UUID.randomUUID();

        UUID transactionId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        UUID.randomUUID(),
                        decisionId
                );

        TransactionDecision decision =
                createDecision(
                        decisionId,
                        transactionId,
                        UUID.randomUUID()
                );

        when(
                processedDomainEventRegistry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        ).thenReturn(
                true
        );

        when(
                transactionDecisionRepository
                        .findByDecisionId(
                                decisionId
                        )
        ).thenReturn(
                Optional.of(
                        decision
                )
        );

        when(
                ruleAlertActionResolver
                        .resolveCreateAlertActionsByTransactionId(
                                transactionId
                        )
        ).thenReturn(
                List.of()
        );

        when(
                alertRuleActionConsolidator
                        .consolidate(
                                List.of()
                        )
        ).thenReturn(
                Optional.empty()
        );

        processor.process(
                message
        );

        verify(
                alertService,
                never()
        ).createAlert(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldRejectMissingEventMessage() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                processor.process(
                                        null
                                )
                );

        assertEquals(
                "DecisionGenerated event message is required",
                exception.getMessage()
        );

        verifyNoInteractions(
                processedDomainEventRegistry,
                transactionDecisionRepository,
                ruleAlertActionResolver,
                alertRuleActionConsolidator,
                alertService
        );
    }

    @Test
    void shouldFailWhenTransactionDecisionDoesNotExist() {

        UUID messageId =
                UUID.randomUUID();

        UUID decisionId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        UUID.randomUUID(),
                        decisionId
                );

        when(
                processedDomainEventRegistry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        ).thenReturn(
                true
        );

        when(
                transactionDecisionRepository
                        .findByDecisionId(
                                decisionId
                        )
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                processor.process(
                                        message
                                )
                );

        assertEquals(
                "Transaction decision not found: "
                        + decisionId,
                exception.getMessage()
        );

        verifyNoInteractions(
                ruleAlertActionResolver,
                alertRuleActionConsolidator,
                alertService
        );
    }

    @Test
    void shouldNotCreateAlertWhenConsolidationFails() {

        UUID messageId =
                UUID.randomUUID();

        UUID decisionId =
                UUID.randomUUID();

        UUID transactionId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        UUID.randomUUID(),
                        decisionId
                );

        TransactionDecision decision =
                createDecision(
                        decisionId,
                        transactionId,
                        UUID.randomUUID()
                );

        RuleAction firstAction =
                new RuleAction();

        RuleAction secondAction =
                new RuleAction();

        List<RuleAction> actions =
                List.of(
                        firstAction,
                        secondAction
                );

        when(
                processedDomainEventRegistry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        ).thenReturn(
                true
        );

        when(
                transactionDecisionRepository
                        .findByDecisionId(
                                decisionId
                        )
        ).thenReturn(
                Optional.of(
                        decision
                )
        );

        when(
                ruleAlertActionResolver
                        .resolveCreateAlertActionsByTransactionId(
                                transactionId
                        )
        ).thenReturn(
                actions
        );

        when(
                alertRuleActionConsolidator
                        .consolidate(
                                actions
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "CREATE_ALERT actions contain conflicting alert parameters"
                )
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                processor.process(
                                        message
                                )
                );

        assertEquals(
                "CREATE_ALERT actions contain conflicting alert parameters",
                exception.getMessage()
        );

        verify(
                alertService,
                never()
        ).createAlert(
                org.mockito.ArgumentMatchers.any()
        );
    }

    private TransactionDecision createDecision(
            UUID decisionId,
            UUID transactionId,
            UUID riskAssessmentId) {

        TransactionDecision decision =
                new TransactionDecision();

        decision.setDecisionId(
                decisionId
        );

        decision.setTransactionId(
                transactionId
        );

        decision.setRiskAssessmentId(
                riskAssessmentId
        );

        return decision;
    }
}