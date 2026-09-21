package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.shared.security.SecurityContext;

import java.util.UUID;

public interface TransactionScopeAuthorizationServiceInterface {

    UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission
    );

    void validateRequestedScope(
            TransactionRequest request,
            UserAccountReference actor
    );

    void validateScopeUnchanged(
            TransactionResponse existing,
            TransactionRequest request
    );

    void requireVisible(
            TransactionResponse transaction,
            UserAccountReference actor,
            String notFoundMessage
    );

    boolean isVisible(
            TransactionResponse transaction,
            UserAccountReference actor
    );

    void requireVisibleTransaction(
            UUID transactionId,
            UserAccountReference actor,
            String notFoundMessage
    );

    boolean isTransactionVisible(
            UUID transactionId,
            UserAccountReference actor
    );
}