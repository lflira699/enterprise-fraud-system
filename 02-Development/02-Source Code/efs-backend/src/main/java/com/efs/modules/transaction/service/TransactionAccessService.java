package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionAccessService
        implements TransactionAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "transaction.view";

    private static final String CREATE_PERMISSION =
            "transaction.create";

    private static final String UPDATE_PERMISSION =
            "transaction.update";

    private static final String DELETE_PERMISSION =
            "transaction.delete";

    private final TransactionServiceInterface
            transactionService;

    private final TransactionScopeAuthorizationServiceInterface
            scopeAuthorizationService;

    public TransactionAccessService(
            TransactionServiceInterface transactionService,
            TransactionScopeAuthorizationServiceInterface
                    scopeAuthorizationService) {

        this.transactionService =
                transactionService;

        this.scopeAuthorizationService =
                scopeAuthorizationService;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(
            TransactionRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                CREATE_PERMISSION
                        );

        scopeAuthorizationService
                .validateRequestedScope(
                        request,
                        actor
                );

        return transactionService
                .createTransaction(
                        request
                );
    }

    @Override
    public TransactionResponse getTransactionById(
            UUID transactionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        TransactionResponse transaction =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        scopeAuthorizationService
                .requireVisible(
                        transaction,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        return transaction;
    }

    @Override
    public TransactionResponse getTransactionByReference(
            String transactionReference,
            SecurityContext securityContext) {

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        TransactionResponse transaction =
                transactionService
                        .getTransactionByReference(
                                transactionReference
                        );

        scopeAuthorizationService
                .requireVisible(
                        transaction,
                        actor,
                        "Transaction not found: "
                                + transactionReference
                );

        return transaction;
    }

    @Override
    public List<TransactionResponse> getTransactionsByCustomerId(
            UUID customerId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        return transactionService
                .getTransactionsByCustomerId(
                        customerId
                )
                .stream()
                .filter(
                        transaction ->
                                scopeAuthorizationService
                                        .isVisible(
                                                transaction,
                                                actor
                                        )
                )
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(
            UUID transactionId,
            TransactionRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                UPDATE_PERMISSION
                        );

        TransactionResponse existing =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        scopeAuthorizationService
                .requireVisible(
                        existing,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        scopeAuthorizationService
                .validateRequestedScope(
                        request,
                        actor
                );

        scopeAuthorizationService
                .validateScopeUnchanged(
                        existing,
                        request
                );

        return transactionService
                .updateTransaction(
                        transactionId,
                        request
                );
    }

    @Override
    @Transactional
    public void deleteTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                DELETE_PERMISSION
                        );

        TransactionResponse existing =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        scopeAuthorizationService
                .requireVisible(
                        existing,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        transactionService
                .deleteTransaction(
                        transactionId
                );
    }
}