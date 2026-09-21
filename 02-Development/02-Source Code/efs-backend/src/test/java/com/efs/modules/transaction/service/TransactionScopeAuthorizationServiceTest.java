package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionScopeAuthorizationServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    @Mock
    private TransactionRepository
            transactionRepository;

    private TransactionScopeAuthorizationService
            service;

    @BeforeEach
    void setUp() {

        service =
                new TransactionScopeAuthorizationService(
                        userAccountLookupService,
                        tenantOrganizationLookupService,
                        transactionRepository
                );
    }

    @Test
    void authorizeShouldReturnResolvedActor() {

        UserAccountReference actor =
                actor(
                        TENANT_ID
                );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(actor);

        UserAccountReference result =
                service.authorize(
                        context(
                                Set.of("transaction.view"),
                                TENANT_ID
                        ),
                        "transaction.view"
                );

        assertSame(
                actor,
                result
        );
    }

    @Test
    void missingPermissionShouldRejectBeforeActorLookup() {

        assertThrows(
                AccessDeniedException.class,
                () -> service.authorize(
                        context(
                                Set.of(),
                                TENANT_ID
                        ),
                        "transaction.view"
                )
        );

        verifyNoInteractions(
                userAccountLookupService,
                tenantOrganizationLookupService,
                transactionRepository
        );
    }

    @Test
    void authenticatedTenantMismatchShouldReject() {

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                actor(
                        TENANT_ID
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.authorize(
                        context(
                                Set.of("transaction.view"),
                                OTHER_TENANT_ID
                        ),
                        "transaction.view"
                )
        );
    }

    @Test
    void organizationActorShouldSeeTenantTransaction() {

        Transaction transaction =
                mock(
                        Transaction.class
                );

        when(
                transaction.getOrganizationId()
        ).thenReturn(
                ORGANIZATION_ID
        );

        when(
                transaction.getTenantId()
        ).thenReturn(
                TENANT_ID
        );

        when(
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                Optional.of(
                        transaction
                )
        );

        assertTrue(
                service.isTransactionVisible(
                        TRANSACTION_ID,
                        actor(null)
                )
        );
    }

    @Test
    void tenantActorShouldNotSeeDifferentTenantTransaction() {

        Transaction transaction =
                mock(
                        Transaction.class
                );

        when(
                transaction.getOrganizationId()
        ).thenReturn(
                ORGANIZATION_ID
        );

        when(
                transaction.getTenantId()
        ).thenReturn(
                OTHER_TENANT_ID
        );

        when(
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                Optional.of(
                        transaction
                )
        );

        assertFalse(
                service.isTransactionVisible(
                        TRANSACTION_ID,
                        actor(TENANT_ID)
                )
        );
    }

    @Test
    void missingOrSoftDeletedParentShouldBeHidden() {

        when(
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertFalse(
                service.isTransactionVisible(
                        TRANSACTION_ID,
                        actor(null)
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.requireVisibleTransaction(
                        TRANSACTION_ID,
                        actor(null),
                        "Transaction not found: "
                                + TRANSACTION_ID
                )
        );
    }

    private SecurityContext context(
            Set<String> permissions,
            UUID tenantId) {

        return new SecurityContext(
                USER_ID,
                tenantId,
                null,
                Set.of(),
                permissions,
                Set.of()
        );
    }

    private UserAccountReference actor(
            UUID tenantId) {

        return new UserAccountReference(
                USER_ID,
                ORGANIZATION_ID,
                tenantId,
                "transaction-scope@test.local"
        );
    }
}