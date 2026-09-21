package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionAccessServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID OTHER_ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    @Mock
    private TransactionServiceInterface
            transactionService;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private TransactionAccessService
            service;

    @BeforeEach
    void setUp() {

        service =
                new TransactionAccessService(
                        transactionService,
                        userAccountLookupService,
                        tenantOrganizationLookupService
                );
    }

    @Test
    void missingViewPermissionShouldRejectBeforeLookup() {

        assertThrows(
                AccessDeniedException.class,
                () -> service.getTransactionById(
                        TRANSACTION_ID,
                        context(
                                Set.of(),
                                TENANT_ID
                        )
                )
        );

        verifyNoInteractions(
                transactionService,
                userAccountLookupService,
                tenantOrganizationLookupService
        );
    }

    @Test
    void missingCreatePermissionShouldRejectBeforeDelegate() {

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createTransaction(
                        request,
                        context(
                                Set.of(),
                                TENANT_ID
                        )
                )
        );

        verifyNoInteractions(
                transactionService,
                userAccountLookupService,
                tenantOrganizationLookupService
        );
    }

    @Test
    void missingUpdatePermissionShouldRejectBeforeDelegate() {

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateTransaction(
                        TRANSACTION_ID,
                        request,
                        context(
                                Set.of(),
                                TENANT_ID
                        )
                )
        );

        verifyNoInteractions(
                transactionService,
                userAccountLookupService,
                tenantOrganizationLookupService
        );
    }

    @Test
    void missingDeletePermissionShouldRejectBeforeDelegate() {

        assertThrows(
                AccessDeniedException.class,
                () -> service.deleteTransaction(
                        TRANSACTION_ID,
                        context(
                                Set.of(),
                                TENANT_ID
                        )
                )
        );

        verifyNoInteractions(
                transactionService,
                userAccountLookupService,
                tenantOrganizationLookupService
        );
    }

    @Test
    void authenticatedTenantMismatchShouldFailClosed() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.getTransactionById(
                        TRANSACTION_ID,
                        context(
                                Set.of("transaction.view"),
                                OTHER_TENANT_ID
                        )
                )
        );

        verify(
                transactionService,
                never()
        ).getTransactionById(
                TRANSACTION_ID
        );
    }

    @Test
    void organizationLevelUserShouldViewSameOrganizationTransaction() {

        authorize(
                ORGANIZATION_ID,
                null
        );

        TransactionResponse transaction =
                transaction(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction
        );

        TransactionResponse result =
                service.getTransactionById(
                        TRANSACTION_ID,
                        context(
                                Set.of("transaction.view"),
                                null
                        )
                );

        assertSame(
                transaction,
                result
        );
    }

    @Test
    void tenantUserShouldHideTransactionFromDifferentTenant() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTransactionById(
                        TRANSACTION_ID,
                        context(
                                Set.of("transaction.view"),
                                TENANT_ID
                        )
                )
        );
    }

    @Test
    void organizationUserShouldHideTransactionFromDifferentOrganization() {

        authorize(
                ORGANIZATION_ID,
                null
        );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction(
                        OTHER_ORGANIZATION_ID,
                        null
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTransactionById(
                        TRANSACTION_ID,
                        context(
                                Set.of("transaction.view"),
                                null
                        )
                )
        );
    }

    @Test
    void organizationUserShouldCreateForOwnedTenant() {

        authorize(
                ORGANIZATION_ID,
                null
        );

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                TENANT_ID
                        )
        ).thenReturn(
                ORGANIZATION_ID
        );

        TransactionResponse expected =
                transaction(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                transactionService
                        .createTransaction(
                                request
                        )
        ).thenReturn(
                expected
        );

        TransactionResponse result =
                service.createTransaction(
                        request,
                        context(
                                Set.of("transaction.create"),
                                null
                        )
                );

        assertSame(
                expected,
                result
        );
    }

    @Test
    void createShouldRejectDifferentOrganization() {

        authorize(
                ORGANIZATION_ID,
                null
        );

        TransactionRequest request =
                request(
                        OTHER_ORGANIZATION_ID,
                        null
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createTransaction(
                        request,
                        context(
                                Set.of("transaction.create"),
                                null
                        )
                )
        );

        verify(
                transactionService,
                never()
        ).createTransaction(
                request
        );
    }

    @Test
    void tenantUserShouldRejectCreateForDifferentTenant() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createTransaction(
                        request,
                        context(
                                Set.of("transaction.create"),
                                TENANT_ID
                        )
                )
        );

        verifyNoInteractions(
                tenantOrganizationLookupService
        );

        verify(
                transactionService,
                never()
        ).createTransaction(
                request
        );
    }

    @Test
    void tenantUserShouldRejectUpdateScopeMove() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction(
                        ORGANIZATION_ID,
                        TENANT_ID
                )
        );

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateTransaction(
                        TRANSACTION_ID,
                        request,
                        context(
                                Set.of("transaction.update"),
                                TENANT_ID
                        )
                )
        );

        verify(
                transactionService,
                never()
        ).updateTransaction(
                TRANSACTION_ID,
                request
        );
    }

    @Test
    void organizationUserShouldRejectTenantScopeMoveWithinOrganization() {

        authorize(
                ORGANIZATION_ID,
                null
        );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction(
                        ORGANIZATION_ID,
                        TENANT_ID
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                OTHER_TENANT_ID
                        )
        ).thenReturn(
                ORGANIZATION_ID
        );

        TransactionRequest request =
                request(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateTransaction(
                        TRANSACTION_ID,
                        request,
                        context(
                                Set.of("transaction.update"),
                                null
                        )
                )
        );

        verify(
                transactionService,
                never()
        ).updateTransaction(
                TRANSACTION_ID,
                request
        );
    }

    @Test
    void deleteShouldHideTransactionOutsideScope() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        when(
                transactionService
                        .getTransactionById(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                transaction(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteTransaction(
                        TRANSACTION_ID,
                        context(
                                Set.of("transaction.delete"),
                                TENANT_ID
                        )
                )
        );

        verify(
                transactionService,
                never()
        ).deleteTransaction(
                TRANSACTION_ID
        );
    }

    @Test
    void customerListShouldReturnOnlyVisibleTransactions() {

        authorize(
                ORGANIZATION_ID,
                TENANT_ID
        );

        TransactionResponse visible =
                transaction(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        TransactionResponse wrongTenant =
                transaction(
                        ORGANIZATION_ID,
                        OTHER_TENANT_ID
                );

        TransactionResponse wrongOrganization =
                transaction(
                        OTHER_ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                transactionService
                        .getTransactionsByCustomerId(
                                CUSTOMER_ID
                        )
        ).thenReturn(
                List.of(
                        visible,
                        wrongTenant,
                        wrongOrganization
                )
        );

        List<TransactionResponse> result =
                service.getTransactionsByCustomerId(
                        CUSTOMER_ID,
                        context(
                                Set.of("transaction.view"),
                                TENANT_ID
                        )
                );

        assertEquals(
                List.of(visible),
                result
        );
    }

    private void authorize(
            UUID organizationId,
            UUID tenantId) {

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                USER_ID
                        )
        ).thenReturn(
                new UserAccountReference(
                        USER_ID,
                        organizationId,
                        tenantId,
                        "transaction-access@example.com"
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

    private TransactionResponse transaction(
            UUID organizationId,
            UUID tenantId) {

        TransactionResponse response =
                new TransactionResponse();

        response.setTransactionId(
                TRANSACTION_ID
        );

        response.setOrganizationId(
                organizationId
        );

        response.setTenantId(
                tenantId
        );

        return response;
    }

    private TransactionRequest request(
            UUID organizationId,
            UUID tenantId) {

        TransactionRequest request =
                new TransactionRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        return request;
    }
}