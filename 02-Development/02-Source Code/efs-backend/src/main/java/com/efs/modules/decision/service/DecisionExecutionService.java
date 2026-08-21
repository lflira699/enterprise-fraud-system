package com.efs.modules.decision.service;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;
import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.service.TransactionDecisionServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DecisionExecutionService
        implements DecisionExecutionServiceInterface {

    private static final String DECISION_SOURCE =
            "DECISION_ENGINE";

    private final DecisionEvaluationServiceInterface
            decisionEvaluationService;

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    public DecisionExecutionService(
            DecisionEvaluationServiceInterface decisionEvaluationService,
            TransactionDecisionServiceInterface transactionDecisionService) {

        this.decisionEvaluationService =
                decisionEvaluationService;

        this.transactionDecisionService =
                transactionDecisionService;
    }

    @Override
    @Transactional
    public TransactionDecisionResponse evaluateAndPersistDecision(
            DecisionEvaluationRequest request) {

        DecisionEvaluationResponse evaluation =
                decisionEvaluationService.evaluateDecision(
                        request
                );

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

        return transactionDecisionService.createDecision(
                evaluation.getTransactionId(),
                decisionRequest
        );
    }
}