package com.efs.modules.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionTypeResponse {

    private UUID transactionTypeId;
    private String transactionTypeCode;
    private String transactionTypeName;
    private String description;
    private Short displayOrder;
    private String status;
    private LocalDateTime createdAt;

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