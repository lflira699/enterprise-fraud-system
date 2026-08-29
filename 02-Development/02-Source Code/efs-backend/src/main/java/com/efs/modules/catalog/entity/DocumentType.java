package com.efs.modules.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_type", schema = "catalog")
public class DocumentType {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "document_type_id", nullable = false)
    private UUID documentTypeId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(
            name = "document_type_code",
            nullable = false,
            length = 60
    )
    private String documentTypeCode;

    @Column(
            name = "document_type_name",
            nullable = false,
            length = 150
    )
    private String documentTypeName;

    @Column(name = "description")
    private String description;

    @Column(
            name = "display_order",
            nullable = false
    )
    private Short displayOrder;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public DocumentType() {
    }

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

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