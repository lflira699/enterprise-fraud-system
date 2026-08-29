package com.efs.modules.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentTypeResponse {

    private UUID documentTypeId;
    private UUID organizationId;
    private String documentTypeCode;
    private String documentTypeName;
    private String description;
    private Short displayOrder;
    private String status;
    private LocalDateTime createdAt;

    public UUID getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(
            UUID documentTypeId) {

        this.documentTypeId =
                documentTypeId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }
}