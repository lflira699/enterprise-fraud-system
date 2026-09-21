package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
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

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    public TransactionAccessService(
            TransactionServiceInterface transactionService,
            UserAccountLookupServiceInterface userAccountLookupService,
            TenantOrganizationLookupServiceInterface
                    tenantOrganizationLookupService) {

        this.transactionService =
                transactionService;

        this.userAccountLookupService =
                userAccountLookupService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;
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
                authorize(
                        securityContext,
                        CREATE_PERMISSION
                );

        validateRequestedScope(
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
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        TransactionResponse transaction =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        requireVisible(
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
                authorize(
                        securityContext,
                        VIEW_PERMISSION
                );

        TransactionResponse transaction =
                transactionService
                        .getTransactionByReference(
                                transactionReference
                        );

        requireVisible(
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
                authorize(
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
                                isVisible(
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
                authorize(
                        securityContext,
                        UPDATE_PERMISSION
                );

        TransactionResponse existing =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        requireVisible(
                existing,
                actor,
                "Transaction not found: "
                        + transactionId
        );

        validateRequestedScope(
                request,
                actor
        );

        validateScopeUnchanged(
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
                authorize(
                        securityContext,
                        DELETE_PERMISSION
                );

        TransactionResponse existing =
                transactionService
                        .getTransactionById(
                                transactionId
                        );

        requireVisible(
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

    private UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        if (!securityContext.hasPermission(
                requiredPermission
        )) {

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + requiredPermission
            );
        }

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext
                                        .getUserId()
                        );

        if (!Objects.equals(
                securityContext.getTenantId(),
                actor.tenantId()
        )) {

            throw new AccessDeniedException(
                    "Authenticated tenant scope mismatch"
            );
        }

        return actor;
    }

    private void validateRequestedScope(
            TransactionRequest request,
            UserAccountReference actor) {

        UUID requestedOrganizationId =
                request.getOrganizationId();

        if (
            requestedOrganizationId == null
                    ||
            !actor.organizationId()
                    .equals(
                            requestedOrganizationId
                    )
        ) {

            throw new AccessDeniedException(
                    "Transaction organization scope is not authorized"
            );
        }

        UUID actorTenantId =
                actor.tenantId();

        UUID requestedTenantId =
                request.getTenantId();

        if (actorTenantId != null) {

            if (!actorTenantId.equals(
                    requestedTenantId
            )) {

                throw new AccessDeniedException(
                        "Transaction tenant scope is not authorized"
                );
            }

            return;
        }

        if (requestedTenantId == null) {
            return;
        }

        UUID tenantOrganizationId =
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                requestedTenantId
                        );

        if (!actor.organizationId()
                .equals(
                        tenantOrganizationId
                )) {

            throw new AccessDeniedException(
                    "Transaction tenant belongs to another organization"
            );
        }
    }

    private void validateScopeUnchanged(
            TransactionResponse existing,
            TransactionRequest request) {

        if (!Objects.equals(
                existing.getOrganizationId(),
                request.getOrganizationId()
        )) {

            throw new AccessDeniedException(
                    "Transaction organization scope cannot be changed"
            );
        }

        if (!Objects.equals(
                existing.getTenantId(),
                request.getTenantId()
        )) {

            throw new AccessDeniedException(
                    "Transaction tenant scope cannot be changed"
            );
        }
    }

    private void requireVisible(
            TransactionResponse transaction,
            UserAccountReference actor,
            String notFoundMessage) {

        if (!isVisible(
                transaction,
                actor
        )) {

            throw new ResourceNotFoundException(
                    notFoundMessage
            );
        }
    }

    private boolean isVisible(
            TransactionResponse transaction,
            UserAccountReference actor) {

        if (
            transaction == null
                    ||
            transaction.getOrganizationId() == null
        ) {
            return false;
        }

        if (!actor.organizationId()
                .equals(
                        transaction.getOrganizationId()
                )) {
            return false;
        }

        UUID actorTenantId =
                actor.tenantId();

        if (actorTenantId == null) {
            return true;
        }

        return actorTenantId.equals(
                transaction.getTenantId()
        );
    }
}