package com.efs.modules.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransactionTypeRequest {

    @NotBlank
    @Size(max = 60)
    private String transactionTypeCode;

    @NotBlank
    @Size(max = 150)
    private String transactionTypeName;

    private String description;

    @NotNull
    private Short displayOrder;

    @NotBlank
    @Size(max = 20)
    private String status;

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
}