package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class RuleApprovalRequest {

    @NotBlank
    @Size(max = 30)
    private String entityType;

    @NotNull
    private UUID entityId;

    @NotBlank
    @Size(max = 30)
    private String approvalStatus;

    @NotNull
    private UUID submittedBy;

    private UUID reviewedBy;

    private String decisionComment;

    @NotNull
    private Short approvalLevel;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UUID submittedBy) {
        this.submittedBy = submittedBy;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UUID reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getDecisionComment() {
        return decisionComment;
    }

    public void setDecisionComment(String decisionComment) {
        this.decisionComment = decisionComment;
    }

    public Short getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Short approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
}