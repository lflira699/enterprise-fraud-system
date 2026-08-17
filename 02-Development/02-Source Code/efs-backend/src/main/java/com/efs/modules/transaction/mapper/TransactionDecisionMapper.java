package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.entity.TransactionDecision;
import org.springframework.stereotype.Component;

@Component
public class TransactionDecisionMapper {

    public TransactionDecision toEntity(
            TransactionDecisionRequest request) {

        TransactionDecision decision =
                new TransactionDecision();

        decision.setDecisionType(
                request.getDecisionType()
        );

        decision.setDecisionSource(
                request.getDecisionSource()
        );

        decision.setConfidenceScore(
                request.getConfidenceScore()
        );

        decision.setDecisionReason(
                request.getDecisionReason()
        );

        decision.setApprovedBy(
                request.getApprovedBy()
        );

        decision.setDecisionTimestamp(
                request.getDecisionTimestamp()
        );

        decision.setFinalDecision(
                request.getFinalDecision()
        );

        return decision;
    }

    public TransactionDecisionResponse toResponse(
            TransactionDecision decision) {

        TransactionDecisionResponse response =
                new TransactionDecisionResponse();

        response.setDecisionId(
                decision.getDecisionId()
        );

        response.setTransactionId(
                decision.getTransactionId()
        );

        response.setDecisionType(
                decision.getDecisionType()
        );

        response.setDecisionSource(
                decision.getDecisionSource()
        );

        response.setConfidenceScore(
                decision.getConfidenceScore()
        );

        response.setDecisionReason(
                decision.getDecisionReason()
        );

        response.setApprovedBy(
                decision.getApprovedBy()
        );

        response.setDecisionTimestamp(
                decision.getDecisionTimestamp()
        );

        response.setFinalDecision(
                decision.getFinalDecision()
        );

        return response;
    }
}