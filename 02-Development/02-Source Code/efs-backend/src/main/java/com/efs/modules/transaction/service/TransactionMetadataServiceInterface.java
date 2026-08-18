package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionMetadataServiceInterface {

    TransactionMetadataResponse createMetadata(
            UUID transactionId,
            TransactionMetadataRequest request
    );

    TransactionMetadataResponse getMetadataById(
            UUID metadataId
    );

    List<TransactionMetadataResponse> getMetadataByTransactionId(
            UUID transactionId
    );

    List<TransactionMetadataResponse> getMetadataByType(
            String metadataType
    );
}