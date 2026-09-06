package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CaseEvidenceUpdateRequest {

    @Size(max = 60)
    private String evidenceType;

    @Size(max = 50)
    private String evidenceCategory;

    @Size(max = 200)
    private String evidenceName;

    private String evidenceDescription;

    @Size(max = 30)
    private String validationStatus;

    @Size(max = 20)
    private String confidentialityLevel;

    @NotNull
    private UUID updatedBy;

    public String getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(
            String evidenceType) {

        this.evidenceType =
                evidenceType;
    }

    public String getEvidenceCategory() {
        return evidenceCategory;
    }

    public void setEvidenceCategory(
            String evidenceCategory) {

        this.evidenceCategory =
                evidenceCategory;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(
            String evidenceName) {

        this.evidenceName =
                evidenceName;
    }

    public String getEvidenceDescription() {
        return evidenceDescription;
    }

    public void setEvidenceDescription(
            String evidenceDescription) {

        this.evidenceDescription =
                evidenceDescription;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(
            String validationStatus) {

        this.validationStatus =
                validationStatus;
    }

    public String getConfidentialityLevel() {
        return confidentialityLevel;
    }

    public void setConfidentialityLevel(
            String confidentialityLevel) {

        this.confidentialityLevel =
                confidentialityLevel;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(
            UUID updatedBy) {

        this.updatedBy =
                updatedBy;
    }
}
