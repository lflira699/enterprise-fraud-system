package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionEventServiceInterface {

    TransactionEventResponse createEvent(
            UUID transactionId,
            TransactionEventRequest request
    );

    TransactionEventResponse getEventById(
            UUID eventId
    );

    List<TransactionEventResponse> getEventsByTransactionId(
            UUID transactionId
    );

    List<TransactionEventResponse> getEventsByType(
            String eventType
    );

    List<TransactionEventResponse> getEventsByComponentName(
            String componentName
    );

    List<TransactionEventResponse> getEventsByCorrelationId(
            UUID correlationId
    );
}