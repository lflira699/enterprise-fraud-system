package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleApprovalRequest;
import com.efs.modules.rules.dto.RuleApprovalResponse;
import com.efs.modules.rules.entity.RuleApproval;
import org.springframework.stereotype.Component;

@Component
public class RuleApprovalMapper {

    public RuleApproval toEntity(
            RuleApprovalRequest request) {

        RuleApproval approval =
                new RuleApproval();

        approval.setEntityType(
                request.getEntityType()
        );

        approval.setEntityId(
                request.getEntityId()
        );

        approval.setApprovalStatus(
                request.getApprovalStatus()
        );

        approval.setSubmittedBy(
                request.getSubmittedBy()
        );

        approval.setReviewedBy(
                request.getReviewedBy()
        );

        approval.setDecisionComment(
                request.getDecisionComment()
        );

        approval.setApprovalLevel(
                request.getApprovalLevel()
        );

        return approval;
    }

    public RuleApprovalResponse toResponse(
            RuleApproval approval) {

        RuleApprovalResponse response =
                new RuleApprovalResponse();

        response.setApprovalId(
                approval.getApprovalId()
        );

        response.setEntityType(
                approval.getEntityType()
        );

        response.setEntityId(
                approval.getEntityId()
        );

        response.setApprovalStatus(
                approval.getApprovalStatus()
        );

        response.setSubmittedBy(
                approval.getSubmittedBy()
        );

        response.setSubmittedAt(
                approval.getSubmittedAt()
        );

        response.setReviewedBy(
                approval.getReviewedBy()
        );

        response.setReviewedAt(
                approval.getReviewedAt()
        );

        response.setDecisionComment(
                approval.getDecisionComment()
        );

        response.setApprovalLevel(
                approval.getApprovalLevel()
        );

        response.setCreatedAt(
                approval.getCreatedAt()
        );

        return response;
    }
}