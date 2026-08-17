package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionPaymentMethodServiceInterface {

    TransactionPaymentMethodResponse createPaymentMethod(
            UUID transactionId,
            TransactionPaymentMethodRequest request
    );

    TransactionPaymentMethodResponse getPaymentMethodById(
            UUID paymentMethodId
    );

    List<TransactionPaymentMethodResponse> getPaymentMethodsByTransactionId(
            UUID transactionId
    );
}