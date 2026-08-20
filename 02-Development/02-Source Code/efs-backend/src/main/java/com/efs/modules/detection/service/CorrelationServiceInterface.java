package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.CorrelationRequest;
import com.efs.modules.detection.dto.CorrelationResponse;

import java.util.List;
import java.util.UUID;

public interface CorrelationServiceInterface {

    CorrelationResponse createCorrelation(
            CorrelationRequest request
    );

    CorrelationResponse getCorrelationById(
            UUID correlationId
    );

    List<CorrelationResponse> getCorrelationsByCustomer(
            UUID customerId
    );

    List<CorrelationResponse> getCorrelationsByTransaction(
            UUID transactionId
    );

    List<CorrelationResponse> getCorrelationsByKey(
            String correlationKey
    );

    List<CorrelationResponse> getCorrelationsByType(
            String correlationType
    );

    List<CorrelationResponse> getCorrelationsByStatus(
            String correlationStatus
    );
}