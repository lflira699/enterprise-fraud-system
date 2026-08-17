package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.entity.TransactionRuleResult;
import org.springframework.stereotype.Component;

@Component
public class TransactionRuleResultMapper {

    public TransactionRuleResult toEntity(
            TransactionRuleResultRequest request) {

        TransactionRuleResult ruleResult =
                new TransactionRuleResult();

        ruleResult.setRuleId(
                request.getRuleId()
        );

        ruleResult.setRuleVersion(
                request.getRuleVersion()
        );

        ruleResult.setExecutionOrder(
                request.getExecutionOrder()
        );

        ruleResult.setExecutionTimeMs(
                request.getExecutionTimeMs()
        );

        ruleResult.setEvaluationResult(
                request.getEvaluationResult()
        );

        ruleResult.setRiskPoints(
                request.getRiskPoints()
        );

        ruleResult.setRecommendedAction(
                request.getRecommendedAction()
        );

        ruleResult.setExplanation(
                request.getExplanation()
        );

        ruleResult.setExecutedAt(
                request.getExecutedAt()
        );

        return ruleResult;
    }

    public TransactionRuleResultResponse toResponse(
            TransactionRuleResult ruleResult) {

        TransactionRuleResultResponse response =
                new TransactionRuleResultResponse();

        response.setRuleResultId(
                ruleResult.getRuleResultId()
        );

        response.setTransactionId(
                ruleResult.getTransactionId()
        );

        response.setRuleId(
                ruleResult.getRuleId()
        );

        response.setRuleVersion(
                ruleResult.getRuleVersion()
        );

        response.setExecutionOrder(
                ruleResult.getExecutionOrder()
        );

        response.setExecutionTimeMs(
                ruleResult.getExecutionTimeMs()
        );

        response.setEvaluationResult(
                ruleResult.getEvaluationResult()
        );

        response.setRiskPoints(
                ruleResult.getRiskPoints()
        );

        response.setRecommendedAction(
                ruleResult.getRecommendedAction()
        );

        response.setExplanation(
                ruleResult.getExplanation()
        );

        response.setExecutedAt(
                ruleResult.getExecutedAt()
        );

        return response;
    }
}