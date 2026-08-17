package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionServiceInterface {

    TransactionResponse createTransaction(
            TransactionRequest request
    );

    TransactionResponse getTransactionById(
            UUID transactionId
    );

    TransactionResponse getTransactionByReference(
            String transactionReference
    );

    List<TransactionResponse> getTransactionsByCustomerId(
            UUID customerId
    );

    TransactionResponse updateTransaction(
            UUID transactionId,
            TransactionRequest request
    );

    void deleteTransaction(
            UUID transactionId
    );
}