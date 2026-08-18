package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionStatusHistoryServiceInterface {

    TransactionStatusHistoryResponse createStatusHistory(
            UUID transactionId,
            TransactionStatusHistoryRequest request
    );

    TransactionStatusHistoryResponse getStatusHistoryById(
            UUID historyId
    );

    List<TransactionStatusHistoryResponse> getStatusHistoryByTransactionId(
            UUID transactionId
    );

    List<TransactionStatusHistoryResponse> getStatusHistoryByCurrentStatus(
            String currentStatus
    );

    List<TransactionStatusHistoryResponse> getStatusHistoryByChangedBy(
            UUID changedBy
    );
}