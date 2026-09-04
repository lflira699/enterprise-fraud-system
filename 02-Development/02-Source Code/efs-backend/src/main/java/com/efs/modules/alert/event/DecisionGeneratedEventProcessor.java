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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DecisionGeneratedEventProcessor {

    private static final String CONSUMER_NAME =
            "Alert Engine";

    private static final String EVENT_TYPE =
            "DecisionGenerated";

    private final ProcessedDomainEventRegistry
            processedDomainEventRegistry;

    private final TransactionDecisionRepository
            transactionDecisionRepository;

    private final RuleAlertActionResolver
            ruleAlertActionResolver;

    private final AlertRuleActionConsolidator
            alertRuleActionConsolidator;

    private final AlertServiceInterface
            alertService;

    public DecisionGeneratedEventProcessor(
            ProcessedDomainEventRegistry processedDomainEventRegistry,
            TransactionDecisionRepository transactionDecisionRepository,
            RuleAlertActionResolver ruleAlertActionResolver,
            AlertRuleActionConsolidator alertRuleActionConsolidator,
            AlertServiceInterface alertService) {

        this.processedDomainEventRegistry =
                processedDomainEventRegistry;

        this.transactionDecisionRepository =
                transactionDecisionRepository;

        this.ruleAlertActionResolver =
                ruleAlertActionResolver;

        this.alertRuleActionConsolidator =
                alertRuleActionConsolidator;

        this.alertService =
                alertService;
    }

    @Transactional
    public void process(
            DecisionGeneratedEventMessage message) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "DecisionGenerated event message is required"
            );
        }

        boolean registered =
                processedDomainEventRegistry.register(
                        message.messageId(),
                        CONSUMER_NAME,
                        EVENT_TYPE
                );

        if (!registered) {
            return;
        }

        TransactionDecision decision =
                transactionDecisionRepository
                        .findByDecisionId(
                                message.decisionId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction decision not found: "
                                                + message.decisionId()
                                )
                        );

        List<RuleAction> ruleActions =
                ruleAlertActionResolver
                        .resolveCreateAlertActionsByTransactionId(
                                decision.getTransactionId()
                        );

        Optional<RuleAlertActionParameters>
                consolidatedParameters =
                alertRuleActionConsolidator
                        .consolidate(
                                ruleActions
                        );

        if (consolidatedParameters.isEmpty()) {
            return;
        }

        RuleAlertActionParameters parameters =
                consolidatedParameters.get();

        AlertRequest request =
                new AlertRequest();

        request.setDecisionId(
                message.decisionId()
        );

        request.setTransactionId(
                decision.getTransactionId()
        );

        request.setRiskAssessmentId(
                decision.getRiskAssessmentId()
        );

        request.setAlertType(
                parameters.alertType()
        );

        request.setPriority(
                parameters.priority()
        );

        request.setCorrelationId(
                message.correlationId()
        );

        alertService.createAlert(
                request
        );
    }
}