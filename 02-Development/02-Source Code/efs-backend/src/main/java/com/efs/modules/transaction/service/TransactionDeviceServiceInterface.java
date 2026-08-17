package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionDeviceServiceInterface {

    TransactionDeviceResponse createDevice(
            UUID transactionId,
            TransactionDeviceRequest request
    );

    TransactionDeviceResponse getDeviceById(
            UUID deviceTransactionId
    );

    List<TransactionDeviceResponse> getDevicesByTransactionId(
            UUID transactionId
    );

    List<TransactionDeviceResponse> getDevicesByFingerprint(
            String deviceFingerprint
    );
}