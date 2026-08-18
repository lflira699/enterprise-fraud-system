package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionHistoryServiceInterface {

    TransactionHistoryResponse createHistory(
            UUID transactionId,
            TransactionHistoryRequest request
    );

    TransactionHistoryResponse getHistoryById(
            UUID historyId
    );

    TransactionHistoryResponse getHistoryByTransactionIdAndVersionNumber(
            UUID transactionId,
            Integer versionNumber
    );

    List<TransactionHistoryResponse> getHistoryByTransactionId(
            UUID transactionId
    );

    List<TransactionHistoryResponse> getHistoryByChangedBy(
            UUID changedBy
    );
}