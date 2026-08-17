package com.efs.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class CustomerRelationshipRequest {

    private UUID relatedCustomerId;

    @NotBlank
    @Size(max = 50)
    private String relationshipType;

    @NotBlank
    @Size(max = 30)
    private String relationshipStatus;

    @Size(max = 500)
    private String relationshipDescription;

    private LocalDate effectiveDate;

    private LocalDate expirationDate;

    private UUID createdBy;

    private UUID updatedBy;

    public UUID getRelatedCustomerId() {
        return relatedCustomerId;
    }

    public void setRelatedCustomerId(UUID relatedCustomerId) {
        this.relatedCustomerId = relatedCustomerId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getRelationshipDescription() {
        return relationshipDescription;
    }

    public void setRelationshipDescription(String relationshipDescription) {
        this.relationshipDescription = relationshipDescription;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
}