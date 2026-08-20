package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface RuleHistoryServiceInterface {

    RuleHistoryResponse createRuleHistory(
            RuleHistoryRequest request
    );

    RuleHistoryResponse getRuleHistoryById(
            UUID historyId
    );

    List<RuleHistoryResponse> getRuleHistoriesByEntity(
            String entityType,
            UUID entityId
    );

    List<RuleHistoryResponse> getRuleHistoriesByChangedBy(
            UUID changedBy
    );

    List<RuleHistoryResponse> getRuleHistoriesByOperationType(
            String operationType
    );

    List<RuleHistoryResponse> getRuleHistoriesByCorrelationId(
            UUID correlationId
    );
}