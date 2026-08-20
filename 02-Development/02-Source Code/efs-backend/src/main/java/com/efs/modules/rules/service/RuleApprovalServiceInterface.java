package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleApprovalRequest;
import com.efs.modules.rules.dto.RuleApprovalResponse;

import java.util.List;
import java.util.UUID;

public interface RuleApprovalServiceInterface {

    RuleApprovalResponse createRuleApproval(
            RuleApprovalRequest request
    );

    RuleApprovalResponse getRuleApprovalById(
            UUID approvalId
    );

    List<RuleApprovalResponse> getRuleApprovalsByEntity(
            String entityType,
            UUID entityId
    );

    List<RuleApprovalResponse> getRuleApprovalsByStatus(
            String approvalStatus
    );

    List<RuleApprovalResponse> getRuleApprovalsBySubmittedBy(
            UUID submittedBy
    );

    List<RuleApprovalResponse> getRuleApprovalsByReviewedBy(
            UUID reviewedBy
    );
}