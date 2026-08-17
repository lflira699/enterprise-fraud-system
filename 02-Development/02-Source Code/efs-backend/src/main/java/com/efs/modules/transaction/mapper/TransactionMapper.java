package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(request.getTransactionReference());
        transaction.setExternalReference(request.getExternalReference());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setOrganizationId(request.getOrganizationId());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionSubtype(request.getTransactionSubtype());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyCode(request.getCurrencyCode());
        transaction.setExchangeRate(request.getExchangeRate());
        transaction.setTransactionDatetime(request.getTransactionDatetime());
        transaction.setProcessingDatetime(request.getProcessingDatetime());
        transaction.setTransactionStatus(request.getTransactionStatus());
        transaction.setFinalDecision(request.getFinalDecision());
        transaction.setFraudScore(request.getFraudScore());
        transaction.setCorrelationId(request.getCorrelationId());
        transaction.setRequestId(request.getRequestId());
        transaction.setSessionId(request.getSessionId());
        transaction.setCreatedBy(request.getCreatedBy());
        transaction.setUpdatedBy(request.getUpdatedBy());
        transaction.setTenantId(request.getTenantId());

        return transaction;
    }

    public void updateEntity(
            TransactionRequest request,
            Transaction transaction) {

        transaction.setTransactionReference(request.getTransactionReference());
        transaction.setExternalReference(request.getExternalReference());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setOrganizationId(request.getOrganizationId());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionSubtype(request.getTransactionSubtype());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyCode(request.getCurrencyCode());
        transaction.setExchangeRate(request.getExchangeRate());
        transaction.setTransactionDatetime(request.getTransactionDatetime());
        transaction.setProcessingDatetime(request.getProcessingDatetime());
        transaction.setTransactionStatus(request.getTransactionStatus());
        transaction.setFinalDecision(request.getFinalDecision());
        transaction.setFraudScore(request.getFraudScore());
        transaction.setCorrelationId(request.getCorrelationId());
        transaction.setRequestId(request.getRequestId());
        transaction.setSessionId(request.getSessionId());
        transaction.setUpdatedBy(request.getUpdatedBy());
        transaction.setTenantId(request.getTenantId());
    }

    public TransactionResponse toResponse(Transaction transaction) {

        TransactionResponse response = new TransactionResponse();

        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setExternalReference(transaction.getExternalReference());
        response.setCustomerId(transaction.getCustomerId());
        response.setOrganizationId(transaction.getOrganizationId());
        response.setTransactionType(transaction.getTransactionType());
        response.setTransactionSubtype(transaction.getTransactionSubtype());
        response.setAmount(transaction.getAmount());
        response.setCurrencyCode(transaction.getCurrencyCode());
        response.setExchangeRate(transaction.getExchangeRate());
        response.setTransactionDatetime(transaction.getTransactionDatetime());
        response.setProcessingDatetime(transaction.getProcessingDatetime());
        response.setTransactionStatus(transaction.getTransactionStatus());
        response.setFinalDecision(transaction.getFinalDecision());
        response.setFraudScore(transaction.getFraudScore());
        response.setCorrelationId(transaction.getCorrelationId());
        response.setRequestId(transaction.getRequestId());
        response.setSessionId(transaction.getSessionId());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        response.setCreatedBy(transaction.getCreatedBy());
        response.setUpdatedBy(transaction.getUpdatedBy());
        response.setRecordVersion(transaction.getRecordVersion());
        response.setTenantId(transaction.getTenantId());
        response.setDeletedAt(transaction.getDeletedAt());

        return response;
    }
}