package com.efs.modules.transaction.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionChildAccessServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID CHILD_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    @Mock
    private TransactionScopeAuthorizationServiceInterface
            scopeAuthorizationService;

    private TransactionChildAccessService
            service;

    private SecurityContext
            securityContext;

    private UserAccountReference
            actor;

    @BeforeEach
    void setUp() {

        service =
                new TransactionChildAccessService(
                        scopeAuthorizationService
                );

        securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of(
                                "transaction.view",
                                "transaction.update"
                        ),
                        Set.of()
                );

        actor =
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        null,
                        "child-access@test.local"
                );
    }

    @Test
    void createShouldRequireUpdatePermissionAndVisibleParent() {

        TransactionAttachmentRequest request =
                new TransactionAttachmentRequest();

        TransactionAttachmentResponse expected =
                new TransactionAttachmentResponse();

        AtomicInteger creatorCalls =
                new AtomicInteger();

        when(
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.update"
                        )
        ).thenReturn(actor);

        TransactionAttachmentResponse result =
                service.create(
                        securityContext,
                        TRANSACTION_ID,
                        request,
                        (transactionId, childRequest) -> {

                            assertEquals(
                                    TRANSACTION_ID,
                                    transactionId
                            );

                            assertSame(
                                    request,
                                    childRequest
                            );

                            creatorCalls.incrementAndGet();

                            return expected;
                        }
                );

        assertSame(
                expected,
                result
        );

        assertEquals(
                1,
                creatorCalls.get()
        );

        verify(
                scopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Transaction not found: "
                        + TRANSACTION_ID
        );
    }

    @Test
    void getByIdShouldRequireViewAndValidateParentVisibility() {

        TransactionAttachmentResponse expected =
                new TransactionAttachmentResponse();

        expected.setTransactionId(
                TRANSACTION_ID
        );

        when(
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.view"
                        )
        ).thenReturn(actor);

        TransactionAttachmentResponse result =
                service.getById(
                        securityContext,
                        CHILD_ID,
                        ignored -> expected,
                        TransactionAttachmentResponse
                                ::getTransactionId,
                        "Attachment hidden"
                );

        assertSame(
                expected,
                result
        );

        verify(
                scopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Attachment hidden"
        );
    }

    @Test
    void getByTransactionIdShouldValidateParentBeforeDelegate() {

        List<TransactionAttachmentResponse> expected =
                List.of(
                        new TransactionAttachmentResponse()
                );

        AtomicInteger getterCalls =
                new AtomicInteger();

        when(
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.view"
                        )
        ).thenReturn(actor);

        List<TransactionAttachmentResponse> result =
                service.getByTransactionId(
                        securityContext,
                        TRANSACTION_ID,
                        transactionId -> {

                            assertEquals(
                                    TRANSACTION_ID,
                                    transactionId
                            );

                            getterCalls.incrementAndGet();

                            return expected;
                        }
                );

        assertSame(
                expected,
                result
        );

        assertEquals(
                1,
                getterCalls.get()
        );

        verify(
                scopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Transaction not found: "
                        + TRANSACTION_ID
        );
    }

    @Test
    void filterVisibleShouldFilterAndCacheParentVisibility() {

        UUID visibleTransactionId =
                TRANSACTION_ID;

        UUID hiddenTransactionId =
                UUID.fromString(
                        "55555555-5555-5555-5555-555555555555"
                );

        TransactionAttachmentResponse visibleOne =
                response(
                        visibleTransactionId
                );

        TransactionAttachmentResponse visibleTwo =
                response(
                        visibleTransactionId
                );

        TransactionAttachmentResponse hidden =
                response(
                        hiddenTransactionId
                );

        when(
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.view"
                        )
        ).thenReturn(actor);

        when(
                scopeAuthorizationService
                        .isTransactionVisible(
                                visibleTransactionId,
                                actor
                        )
        ).thenReturn(true);

        when(
                scopeAuthorizationService
                        .isTransactionVisible(
                                hiddenTransactionId,
                                actor
                        )
        ).thenReturn(false);

        List<TransactionAttachmentResponse> result =
                service.filterVisible(
                        securityContext,
                        () -> List.of(
                                visibleOne,
                                hidden,
                                visibleTwo
                        ),
                        TransactionAttachmentResponse
                                ::getTransactionId
                );

        assertEquals(
                List.of(
                        visibleOne,
                        visibleTwo
                ),
                result
        );

        verify(
                scopeAuthorizationService
        ).isTransactionVisible(
                visibleTransactionId,
                actor
        );

        verify(
                scopeAuthorizationService
        ).isTransactionVisible(
                hiddenTransactionId,
                actor
        );
    }

    @Test
    void authorizationFailureShouldPreventChildDelegate() {

        AtomicInteger getterCalls =
                new AtomicInteger();

        when(
                scopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.view"
                        )
        ).thenThrow(
                new AccessDeniedException(
                        "denied"
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getByTransactionId(
                                securityContext,
                                TRANSACTION_ID,
                                ignored -> {

                                    getterCalls.incrementAndGet();

                                    return List.of();
                                }
                        )
        );

        assertEquals(
                0,
                getterCalls.get()
        );
    }

    private TransactionAttachmentResponse response(
            UUID transactionId) {

        TransactionAttachmentResponse response =
                new TransactionAttachmentResponse();

        response.setTransactionId(
                transactionId
        );

        return response;
    }
}