package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleActionRequest;
import com.efs.modules.rules.dto.RuleActionResponse;
import com.efs.modules.rules.entity.RuleAction;
import org.springframework.stereotype.Component;

@Component
public class RuleActionMapper {

    public RuleAction toEntity(
            RuleActionRequest request) {

        RuleAction action =
                new RuleAction();

        action.setActionType(
                request.getActionType()
        );

        action.setExecutionOrder(
                request.getExecutionOrder()
        );

        action.setParameterJson(
                request.getParameterJson()
        );

        action.setIsAsync(
                request.getIsAsync()
        );

        return action;
    }

    public RuleActionResponse toResponse(
            RuleAction action) {

        RuleActionResponse response =
                new RuleActionResponse();

        response.setActionId(
                action.getActionId()
        );

        response.setRuleVersionId(
                action.getRuleVersionId()
        );

        response.setActionType(
                action.getActionType()
        );

        response.setExecutionOrder(
                action.getExecutionOrder()
        );

        response.setParameterJson(
                action.getParameterJson()
        );

        response.setIsAsync(
                action.getIsAsync()
        );

        response.setCreatedAt(
                action.getCreatedAt()
        );

        return response;
    }
}