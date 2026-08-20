package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleConditionRequest;
import com.efs.modules.rules.dto.RuleConditionResponse;
import com.efs.modules.rules.entity.RuleCondition;
import org.springframework.stereotype.Component;

@Component
public class RuleConditionMapper {

    public RuleCondition toEntity(
            RuleConditionRequest request) {

        RuleCondition condition =
                new RuleCondition();

        condition.setConditionOrder(
                request.getConditionOrder()
        );

        condition.setAttributeName(
                request.getAttributeName()
        );

        condition.setComparisonOperator(
                request.getComparisonOperator()
        );

        condition.setComparisonValue(
                request.getComparisonValue()
        );

        condition.setLogicalOperator(
                request.getLogicalOperator()
        );

        condition.setIsRequired(
                request.getIsRequired()
        );

        return condition;
    }

    public RuleConditionResponse toResponse(
            RuleCondition condition) {

        RuleConditionResponse response =
                new RuleConditionResponse();

        response.setConditionId(
                condition.getConditionId()
        );

        response.setRuleVersionId(
                condition.getRuleVersionId()
        );

        response.setConditionOrder(
                condition.getConditionOrder()
        );

        response.setAttributeName(
                condition.getAttributeName()
        );

        response.setComparisonOperator(
                condition.getComparisonOperator()
        );

        response.setComparisonValue(
                condition.getComparisonValue()
        );

        response.setLogicalOperator(
                condition.getLogicalOperator()
        );

        response.setIsRequired(
                condition.getIsRequired()
        );

        return response;
    }
}