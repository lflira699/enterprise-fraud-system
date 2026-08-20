package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.entity.RuleExecution;
import org.springframework.stereotype.Component;

@Component
public class RuleExecutionMapper {

    public RuleExecution toEntity(
            RuleExecutionRequest request) {

        RuleExecution execution =
                new RuleExecution();

        execution.setRuleId(
                request.getRuleId()
        );

        execution.setRuleVersionId(
                request.getRuleVersionId()
        );

        execution.setPolicyId(
                request.getPolicyId()
        );

        execution.setTransactionId(
                request.getTransactionId()
        );

        execution.setExecutionStatus(
                request.getExecutionStatus()
        );

        execution.setMatched(
                request.getMatched()
        );

        execution.setExecutionTimeMs(
                request.getExecutionTimeMs()
        );

        execution.setErrorCode(
                request.getErrorCode()
        );

        execution.setEngineInstance(
                request.getEngineInstance()
        );

        return execution;
    }

    public RuleExecutionResponse toResponse(
            RuleExecution execution) {

        RuleExecutionResponse response =
                new RuleExecutionResponse();

        response.setExecutionId(
                execution.getExecutionId()
        );

        response.setRuleId(
                execution.getRuleId()
        );

        response.setRuleVersionId(
                execution.getRuleVersionId()
        );

        response.setPolicyId(
                execution.getPolicyId()
        );

        response.setTransactionId(
                execution.getTransactionId()
        );

        response.setExecutionStatus(
                execution.getExecutionStatus()
        );

        response.setMatched(
                execution.getMatched()
        );

        response.setExecutionTimeMs(
                execution.getExecutionTimeMs()
        );

        response.setErrorCode(
                execution.getErrorCode()
        );

        response.setExecutedAt(
                execution.getExecutedAt()
        );

        response.setEngineInstance(
                execution.getEngineInstance()
        );

        return response;
    }
}