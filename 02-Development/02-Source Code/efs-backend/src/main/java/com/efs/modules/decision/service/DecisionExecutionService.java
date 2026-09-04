package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionDecisionServiceInterface;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DecisionExecutionService
        implements DecisionExecutionServiceInterface {

    private static final String DECISION_SOURCE =
            "DECISION_ENGINE";

    private static final String EVENT_TYPE =
            "DecisionGenerated";

    private static final String EVENT_SCHEMA_VERSION =
            "1.0";

    private static final String EVENT_PRODUCER =
            "Decision Engine";

    private static final String AGGREGATE_TYPE =
            "TransactionDecision";

    private final DecisionEvaluationServiceInterface
            decisionEvaluationService;

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    private final TransactionServiceInterface
            transactionService;

    private final DomainEventOutboxService
            domainEventOutboxService;

    public DecisionExecutionService(
            DecisionEvaluationServiceInterface decisionEvaluationService,
            TransactionDecisionServiceInterface transactionDecisionService,
            TransactionServiceInterface transactionService,
            DomainEventOutboxService domainEventOutboxService) {

        this.decisionEvaluationService =
                decisionEvaluationService;

        this.transactionDecisionService =
                transactionDecisionService;

        this.transactionService =
                transactionService;

        this.domainEventOutboxService =
                domainEventOutboxService;
    }

    @Override
    @Transactional
    public TransactionDecisionResponse evaluateAndPersistDecision(
            DecisionEvaluationRequest request) {

        DecisionEvaluationResponse evaluation =
                decisionEvaluationService.evaluateDecision(
                        request
                );

        TransactionResponse transaction =
                transactionService.getTransactionById(
                        evaluation.getTransactionId()
                );

        if (transaction.getCorrelationId() == null) {
            throw new IllegalStateException(
                    "Transaction correlationId is required "
                            + "for DecisionGenerated event"
            );
        }

        TransactionDecisionRequest decisionRequest =
                new TransactionDecisionRequest();

        decisionRequest.setRiskAssessmentId(
                evaluation.getRiskAssessmentId()
        );

        decisionRequest.setDecisionType(
                evaluation.getDecisionType()
        );

        decisionRequest.setDecisionSource(
                DECISION_SOURCE
        );

        decisionRequest.setConfidenceScore(
                evaluation.getConfidenceScore()
        );

        decisionRequest.setDecisionReason(
                evaluation.getDecisionReason()
        );

        decisionRequest.setDecisionTimestamp(
                LocalDateTime.now()
        );

        decisionRequest.setFinalDecision(
                evaluation.getFinalDecision()
        );

        TransactionDecisionResponse decision =
                transactionDecisionService.createDecision(
                        evaluation.getTransactionId(),
                        decisionRequest
                );

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                EVENT_TYPE
        );

        envelope.setSchemaVersion(
                EVENT_SCHEMA_VERSION
        );

        envelope.setOccurredAt(
                decision.getDecisionTimestamp()
        );

        envelope.setProducer(
                EVENT_PRODUCER
        );

        envelope.setCorrelationId(
                transaction.getCorrelationId()
        );

        envelope.setTenantId(
                transaction.getTenantId()
        );

        envelope.setPayload(
                Map.of(
                        "decisionId",
                        decision.getDecisionId()
                                .toString()
                )
        );

        envelope.setMetadata(
                Map.of()
        );

        domainEventOutboxService.persist(
                AGGREGATE_TYPE,
                decision.getDecisionId(),
                envelope
        );

        return decision;
    }
}
