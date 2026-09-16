package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "configuration_change_request",
        schema = "administration"
)
public class ConfigurationChangeRequest {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "change_request_id",
            nullable = false
    )
    private UUID changeRequestId;

    @Column(
            name = "organization_id",
            nullable = false
    )
    private UUID organizationId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(
            name = "requested_by",
            nullable = false
    )
    private UUID requestedBy;

    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

    @Column(
            name = "justification",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String justification;

    @Column(
            name = "affected_environment",
            nullable = false,
            length = 50
    )
    private String affectedEnvironment;

    @Column(
            name = "risk_assessment",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String riskAssessment;

    @Column(
            name = "expected_result",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String expectedResult;

    @Column(
            name = "rollback_plan",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String rollbackPlan;

    @Column(
            name = "requested_at",
            nullable = false
    )
    private LocalDateTime requestedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private UUID rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(
            name = "rejection_reason",
            columnDefinition = "TEXT"
    )
    private String rejectionReason;

    @Column(name = "applied_by")
    private UUID appliedBy;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(
            name = "failure_reason",
            columnDefinition = "TEXT"
    )
    private String failureReason;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public ConfigurationChangeRequest() {
    }

    public UUID getChangeRequestId() {
        return changeRequestId;
    }

    public void setChangeRequestId(
            UUID changeRequestId) {
        this.changeRequestId =
                changeRequestId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {
        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {
        this.tenantId =
                tenantId;
    }

    public UUID getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(
            UUID requestedBy) {
        this.requestedBy =
                requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status =
                status;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(
            String justification) {
        this.justification =
                justification;
    }

    public String getAffectedEnvironment() {
        return affectedEnvironment;
    }

    public void setAffectedEnvironment(
            String affectedEnvironment) {
        this.affectedEnvironment =
                affectedEnvironment;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(
            String riskAssessment) {
        this.riskAssessment =
                riskAssessment;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(
            String expectedResult) {
        this.expectedResult =
                expectedResult;
    }

    public String getRollbackPlan() {
        return rollbackPlan;
    }

    public void setRollbackPlan(
            String rollbackPlan) {
        this.rollbackPlan =
                rollbackPlan;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            LocalDateTime requestedAt) {
        this.requestedAt =
                requestedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(
            UUID approvedBy) {
        this.approvedBy =
                approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(
            LocalDateTime approvedAt) {
        this.approvedAt =
                approvedAt;
    }

    public UUID getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(
            UUID rejectedBy) {
        this.rejectedBy =
                rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(
            LocalDateTime rejectedAt) {
        this.rejectedAt =
                rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(
            String rejectionReason) {
        this.rejectionReason =
                rejectionReason;
    }

    public UUID getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(
            UUID appliedBy) {
        this.appliedBy =
                appliedBy;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(
            LocalDateTime appliedAt) {
        this.appliedAt =
                appliedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(
            String failureReason) {
        this.failureReason =
                failureReason;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt =
                updatedAt;
    }
}