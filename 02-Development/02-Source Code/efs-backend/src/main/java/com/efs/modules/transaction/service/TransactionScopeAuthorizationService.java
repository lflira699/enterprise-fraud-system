package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionScopeAuthorizationService
        implements TransactionScopeAuthorizationServiceInterface {

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private final TransactionRepository
            transactionRepository;

    public TransactionScopeAuthorizationService(
            UserAccountLookupServiceInterface userAccountLookupService,
            TenantOrganizationLookupServiceInterface
                    tenantOrganizationLookupService,
            TransactionRepository transactionRepository) {

        this.userAccountLookupService =
                userAccountLookupService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;

        this.transactionRepository =
                transactionRepository;
    }

    @Override
    public UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Objects.requireNonNull(
                requiredPermission,
                "requiredPermission is required"
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

    @Override
    public void validateRequestedScope(
            TransactionRequest request,
            UserAccountReference actor) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

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

    @Override
    public void validateScopeUnchanged(
            TransactionResponse existing,
            TransactionRequest request) {

        Objects.requireNonNull(
                existing,
                "existing transaction is required"
        );

        Objects.requireNonNull(
                request,
                "request is required"
        );

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

    @Override
    public void requireVisible(
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

    @Override
    public boolean isVisible(
            TransactionResponse transaction,
            UserAccountReference actor) {

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

        if (
            transaction == null
                    ||
            transaction.getOrganizationId() == null
        ) {
            return false;
        }

        return isScopeVisible(
                transaction.getOrganizationId(),
                transaction.getTenantId(),
                actor
        );
    }

    @Override
    public void requireVisibleTransaction(
            UUID transactionId,
            UserAccountReference actor,
            String notFoundMessage) {

        if (!isTransactionVisible(
                transactionId,
                actor
        )) {

            throw new ResourceNotFoundException(
                    notFoundMessage
            );
        }
    }

    @Override
    public boolean isTransactionVisible(
            UUID transactionId,
            UserAccountReference actor) {

        Objects.requireNonNull(
                transactionId,
                "transactionId is required"
        );

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

        return transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        transactionId
                )
                .map(
                        transaction ->
                                isVisible(
                                        transaction,
                                        actor
                                )
                )
                .orElse(false);
    }

    private boolean isVisible(
            Transaction transaction,
            UserAccountReference actor) {

        if (
            transaction == null
                    ||
            transaction.getOrganizationId() == null
        ) {
            return false;
        }

        return isScopeVisible(
                transaction.getOrganizationId(),
                transaction.getTenantId(),
                actor
        );
    }

    private boolean isScopeVisible(
            UUID organizationId,
            UUID tenantId,
            UserAccountReference actor) {

        if (!actor.organizationId()
                .equals(
                        organizationId
                )) {
            return false;
        }

        UUID actorTenantId =
                actor.tenantId();

        if (actorTenantId == null) {
            return true;
        }

        return actorTenantId.equals(
                tenantId
        );
    }
}