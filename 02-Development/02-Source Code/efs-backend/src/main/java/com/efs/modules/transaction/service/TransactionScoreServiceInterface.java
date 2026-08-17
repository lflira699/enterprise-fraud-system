package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionScoreServiceInterface {

    TransactionScoreResponse createScore(
            UUID transactionId,
            TransactionScoreRequest request
    );

    TransactionScoreResponse getScoreById(
            UUID scoreId
    );

    List<TransactionScoreResponse> getScoresByTransactionId(
            UUID transactionId
    );

    List<TransactionScoreResponse> getScoresByType(
            String scoreType
    );

    List<TransactionScoreResponse> getScoresByScoringModel(
            String scoringModel
    );
}