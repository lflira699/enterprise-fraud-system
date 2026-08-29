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
@Table(name = "transaction_type", schema = "catalog")
public class TransactionType {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "transaction_type_id", nullable = false)
    private UUID transactionTypeId;

    @Column(
            name = "transaction_type_code",
            nullable = false,
            length = 60
    )
    private String transactionTypeCode;

    @Column(
            name = "transaction_type_name",
            nullable = false,
            length = 150
    )
    private String transactionTypeName;

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

    public TransactionType() {
    }

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(
            UUID transactionTypeId) {

        this.transactionTypeId =
                transactionTypeId;
    }

    public String getTransactionTypeCode() {
        return transactionTypeCode;
    }

    public void setTransactionTypeCode(
            String transactionTypeCode) {

        this.transactionTypeCode =
                transactionTypeCode;
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(
            String transactionTypeName) {

        this.transactionTypeName =
                transactionTypeName;
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