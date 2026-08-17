package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionParticipantServiceInterface {

    TransactionParticipantResponse createParticipant(
            UUID transactionId,
            TransactionParticipantRequest request
    );

    TransactionParticipantResponse getParticipantById(
            UUID participantId
    );

    List<TransactionParticipantResponse> getParticipantsByTransactionId(
            UUID transactionId
    );

    List<TransactionParticipantResponse> getParticipantsByCustomerId(
            UUID customerId
    );
}