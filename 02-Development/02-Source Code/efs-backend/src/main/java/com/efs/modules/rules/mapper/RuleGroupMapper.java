package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleGroupRequest;
import com.efs.modules.rules.dto.RuleGroupResponse;
import com.efs.modules.rules.entity.RuleGroup;
import org.springframework.stereotype.Component;

@Component
public class RuleGroupMapper {

    public RuleGroup toEntity(
            RuleGroupRequest request) {

        RuleGroup ruleGroup =
                new RuleGroup();

        ruleGroup.setGroupCode(
                request.getGroupCode()
        );

        ruleGroup.setGroupName(
                request.getGroupName()
        );

        ruleGroup.setDescription(
                request.getDescription()
        );

        ruleGroup.setCategory(
                request.getCategory()
        );

        ruleGroup.setStatus(
                request.getStatus()
        );

        ruleGroup.setExecutionOrder(
                request.getExecutionOrder()
        );

        return ruleGroup;
    }

    public RuleGroupResponse toResponse(
            RuleGroup ruleGroup) {

        RuleGroupResponse response =
                new RuleGroupResponse();

        response.setRuleGroupId(
                ruleGroup.getRuleGroupId()
        );

        response.setGroupCode(
                ruleGroup.getGroupCode()
        );

        response.setGroupName(
                ruleGroup.getGroupName()
        );

        response.setDescription(
                ruleGroup.getDescription()
        );

        response.setCategory(
                ruleGroup.getCategory()
        );

        response.setStatus(
                ruleGroup.getStatus()
        );

        response.setExecutionOrder(
                ruleGroup.getExecutionOrder()
        );

        response.setCreatedAt(
                ruleGroup.getCreatedAt()
        );

        response.setUpdatedAt(
                ruleGroup.getUpdatedAt()
        );

        return response;
    }
}