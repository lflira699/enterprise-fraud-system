package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleGroupRequest;
import com.efs.modules.rules.dto.RuleGroupResponse;

import java.util.List;
import java.util.UUID;

public interface RuleGroupServiceInterface {

    RuleGroupResponse createRuleGroup(
            RuleGroupRequest request
    );

    RuleGroupResponse getRuleGroupById(
            UUID ruleGroupId
    );

    RuleGroupResponse getRuleGroupByCode(
            String groupCode
    );

    List<RuleGroupResponse> getRuleGroupsByStatus(
            String status
    );

    List<RuleGroupResponse> getRuleGroupsByCategory(
            String category
    );
}