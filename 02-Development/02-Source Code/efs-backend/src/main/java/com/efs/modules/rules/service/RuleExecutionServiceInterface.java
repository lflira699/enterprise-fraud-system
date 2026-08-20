package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;

import java.util.List;
import java.util.UUID;

public interface RuleExecutionServiceInterface {

    RuleExecutionResponse createRuleExecution(
            RuleExecutionRequest request
    );

    RuleExecutionResponse getRuleExecutionById(
            UUID executionId
    );

    List<RuleExecutionResponse> getRuleExecutionsByRuleId(
            UUID ruleId
    );

    List<RuleExecutionResponse> getRuleExecutionsByRuleVersionId(
            UUID ruleVersionId
    );

    List<RuleExecutionResponse> getRuleExecutionsByPolicyId(
            UUID policyId
    );

    List<RuleExecutionResponse> getRuleExecutionsByTransactionId(
            UUID transactionId
    );

    List<RuleExecutionResponse> getRuleExecutionsByStatus(
            String executionStatus
    );
}