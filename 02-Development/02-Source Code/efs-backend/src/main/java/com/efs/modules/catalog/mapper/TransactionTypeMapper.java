package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.TransactionTypeRequest;
import com.efs.modules.catalog.dto.TransactionTypeResponse;
import com.efs.modules.catalog.entity.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapper {

    public TransactionType toEntity(
            TransactionTypeRequest request) {

        TransactionType transactionType =
                new TransactionType();

        transactionType.setTransactionTypeCode(
                request.getTransactionTypeCode()
        );

        transactionType.setTransactionTypeName(
                request.getTransactionTypeName()
        );

        transactionType.setDescription(
                request.getDescription()
        );

        transactionType.setDisplayOrder(
                request.getDisplayOrder()
        );

        transactionType.setStatus(
                request.getStatus()
        );

        return transactionType;
    }

    public TransactionTypeResponse toResponse(
            TransactionType transactionType) {

        TransactionTypeResponse response =
                new TransactionTypeResponse();

        response.setTransactionTypeId(
                transactionType.getTransactionTypeId()
        );

        response.setTransactionTypeCode(
                transactionType.getTransactionTypeCode()
        );

        response.setTransactionTypeName(
                transactionType.getTransactionTypeName()
        );

        response.setDescription(
                transactionType.getDescription()
        );

        response.setDisplayOrder(
                transactionType.getDisplayOrder()
        );

        response.setStatus(
                transactionType.getStatus()
        );

        response.setCreatedAt(
                transactionType.getCreatedAt()
        );

        return response;
    }
}