package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TransactionTypeRequest;
import com.efs.modules.catalog.dto.TransactionTypeResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionTypeServiceInterface {

    TransactionTypeResponse createTransactionType(
            TransactionTypeRequest request
    );

    TransactionTypeResponse getTransactionTypeById(
            UUID transactionTypeId
    );

    TransactionTypeResponse getTransactionTypeByCode(
            String transactionTypeCode
    );

    List<TransactionTypeResponse> getTransactionTypesByStatus(
            String status
    );

    List<TransactionTypeResponse> getAllTransactionTypes();
}