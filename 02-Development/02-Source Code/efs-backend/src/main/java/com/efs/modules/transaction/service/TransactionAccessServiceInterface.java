package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface TransactionAccessServiceInterface {

    TransactionResponse createTransaction(
            TransactionRequest request,
            SecurityContext securityContext
    );

    TransactionResponse getTransactionById(
            UUID transactionId,
            SecurityContext securityContext
    );

    TransactionResponse getTransactionByReference(
            String transactionReference,
            SecurityContext securityContext
    );

    List<TransactionResponse> getTransactionsByCustomerId(
            UUID customerId,
            SecurityContext securityContext
    );

    TransactionResponse updateTransaction(
            UUID transactionId,
            TransactionRequest request,
            SecurityContext securityContext
    );

    void deleteTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );
}