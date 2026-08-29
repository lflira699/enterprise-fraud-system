package com.efs.modules.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class DocumentTypeRequest {

    @NotNull
    private UUID organizationId;

    @NotBlank
    @Size(max = 60)
    private String documentTypeCode;

    @NotBlank
    @Size(max = 150)
    private String documentTypeName;

    private String description;

    @NotNull
    private Short displayOrder;

    @NotBlank
    @Size(max = 20)
    private String status;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(
            String documentTypeCode) {

        this.documentTypeCode =
                documentTypeCode;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(
            String documentTypeName) {

        this.documentTypeName =
                documentTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }

    public Short getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(
            Short displayOrder) {

        this.displayOrder =
                displayOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status =
                status;
    }
}