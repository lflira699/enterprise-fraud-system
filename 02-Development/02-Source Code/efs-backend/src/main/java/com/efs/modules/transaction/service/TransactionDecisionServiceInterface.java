package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionDecisionServiceInterface {

    TransactionDecisionResponse createDecision(
            UUID transactionId,
            TransactionDecisionRequest request
    );

    TransactionDecisionResponse getDecisionById(
            UUID decisionId
    );

    List<TransactionDecisionResponse> getDecisionsByTransactionId(
            UUID transactionId
    );

    List<TransactionDecisionResponse> getDecisionsByType(
            String decisionType
    );

    List<TransactionDecisionResponse> getDecisionsBySource(
            String decisionSource
    );

    List<TransactionDecisionResponse> getDecisionsByFinalStatus(
            Boolean finalDecision
    );
}