package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionRuleResultServiceInterface {

    TransactionRuleResultResponse createRuleResult(
            UUID transactionId,
            TransactionRuleResultRequest request
    );

    TransactionRuleResultResponse getRuleResultById(
            UUID ruleResultId
    );

    List<TransactionRuleResultResponse> getRuleResultsByTransactionId(
            UUID transactionId
    );

    List<TransactionRuleResultResponse> getRuleResultsByRuleId(
            UUID ruleId
    );

    List<TransactionRuleResultResponse> getRuleResultsByEvaluationResult(
            String evaluationResult
    );
}