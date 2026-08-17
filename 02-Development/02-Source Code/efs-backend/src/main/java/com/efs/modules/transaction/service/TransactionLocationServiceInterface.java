package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionLocationServiceInterface {

    TransactionLocationResponse createLocation(
            UUID transactionId,
            TransactionLocationRequest request
    );

    TransactionLocationResponse getLocationById(
            UUID locationId
    );

    List<TransactionLocationResponse> getLocationsByTransactionId(
            UUID transactionId
    );

    List<TransactionLocationResponse> getLocationsByIpAddress(
            String ipAddress
    );

    List<TransactionLocationResponse> getLocationsByCountryCode(
            String countryCode
    );

    List<TransactionLocationResponse> getLocationsByAsn(
            Long asn
    );
}