package com.efs.modules.alert.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.service.TransactionDecisionServiceInterface;
import com.efs.modules.transaction.service.TransactionScopeAuthorizationServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AlertAccessServiceTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "a1111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a2222222-2222-2222-2222-222222222222"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "a3333333-3333-3333-3333-333333333333"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "a4444444-4444-4444-4444-444444444444"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "a5555555-5555-5555-5555-555555555555"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "a6666666-6666-6666-6666-666666666666"
            );

    private AlertServiceInterface
            alertService;

    private AlertScopeAuthorizationServiceInterface
            alertScopeAuthorizationService;

    private TransactionDecisionServiceInterface
            transactionDecisionService;

    private TransactionScopeAuthorizationServiceInterface
            transactionScopeAuthorizationService;

    private AlertAccessService
            accessService;

    private SecurityContext
            securityContext;

    private UserAccountReference
            actor;

    @BeforeEach
    void setUp() {

        alertService =
                mock(
                        AlertServiceInterface.class
                );

        alertScopeAuthorizationService =
                mock(
                        AlertScopeAuthorizationServiceInterface.class
                );

        transactionDecisionService =
                mock(
                        TransactionDecisionServiceInterface.class
                );

        transactionScopeAuthorizationService =
                mock(
                        TransactionScopeAuthorizationServiceInterface.class
                );

        accessService =
                new AlertAccessService(
                        alertService,
                        alertScopeAuthorizationService,
                        transactionDecisionService,
                        transactionScopeAuthorizationService
                );

        securityContext =
                new SecurityContext(
                        USER_ID,
                        TENANT_ID,
                        null,
                        Set.of(),
                        Set.of(
                                "alert.view",
                                "alert.create",
                                "alert.update",
                                "alert.assign",
                                "alert.close"
                        ),
                        Set.of()
                );

        actor =
                new UserAccountReference(
                        USER_ID,
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "alert-access@example.com"
                );
    }

    @Test
    void shouldSecureCreateAgainstDecisionTransactionScope() {

        AlertRequest request =
                mock(
                        AlertRequest.class
                );

        TransactionDecisionResponse decision =
                mock(
                        TransactionDecisionResponse.class
                );

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                request.getDecisionId()
        ).thenReturn(
                DECISION_ID
        );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.create"
                        )
        ).thenReturn(
                actor
        );

        when(
                transactionDecisionService
                        .getDecisionById(
                                DECISION_ID
                        )
        ).thenReturn(
                decision
        );

        when(
                decision.getTransactionId()
        ).thenReturn(
                TRANSACTION_ID
        );

        when(
                alertService.createAlert(
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.createAlert(
                        request,
                        securityContext
                )
        );

        verify(
                transactionScopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Transaction not found: "
                        + TRANSACTION_ID
        );
    }

    @Test
    void shouldPreserveDomainValidationWhenCreateDecisionHasNoTransaction() {

        AlertRequest request =
                mock(
                        AlertRequest.class
                );

        TransactionDecisionResponse decision =
                mock(
                        TransactionDecisionResponse.class
                );

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                request.getDecisionId()
        ).thenReturn(
                DECISION_ID
        );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.create"
                        )
        ).thenReturn(
                actor
        );

        when(
                transactionDecisionService
                        .getDecisionById(
                                DECISION_ID
                        )
        ).thenReturn(
                decision
        );

        when(
                decision.getTransactionId()
        ).thenReturn(
                null
        );

        when(
                alertService.createAlert(
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.createAlert(
                        request,
                        securityContext
                )
        );

        verify(
                transactionScopeAuthorizationService,
                never()
        ).requireVisibleTransaction(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldSecureGetByIdWithViewPermissionAndVisibility() {

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.getAlertById(
                        ALERT_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getAlertById(
                        ALERT_ID,
                        securityContext
                )
        );

        verify(
                alertScopeAuthorizationService
        ).requireVisibleAlert(
                ALERT_ID,
                actor,
                "Alert not found: "
                        + ALERT_ID
        );
    }

    @Test
    void shouldSecureStatusUpdateWithUpdatePermission() {

        AlertStatusUpdateRequest request =
                mock(
                        AlertStatusUpdateRequest.class
                );

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.update"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.updateAlertStatus(
                        ALERT_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.updateAlertStatus(
                        ALERT_ID,
                        request,
                        securityContext
                )
        );

        verify(
                alertScopeAuthorizationService
        ).requireVisibleAlert(
                ALERT_ID,
                actor,
                "Alert not found: "
                        + ALERT_ID
        );
    }

    @Test
    void shouldSecureAssignmentWithAssignPermission() {

        AlertAssignmentRequest request =
                mock(
                        AlertAssignmentRequest.class
                );

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.assign"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.assignAlert(
                        ALERT_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.assignAlert(
                        ALERT_ID,
                        request,
                        securityContext
                )
        );
    }

    @Test
    void shouldSecureClosureWithClosePermission() {

        AlertClosureRequest request =
                mock(
                        AlertClosureRequest.class
                );

        AlertResponse response =
                mock(
                        AlertResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.close"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.closeAlert(
                        ALERT_ID,
                        request
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.closeAlert(
                        ALERT_ID,
                        request,
                        securityContext
                )
        );
    }

    @Test
    void shouldSecureHistoryWithViewPermission() {

        List<AlertHistoryResponse> response =
                List.of(
                        mock(
                                AlertHistoryResponse.class
                        )
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.getAlertHistory(
                        ALERT_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getAlertHistory(
                        ALERT_ID,
                        securityContext
                )
        );
    }

    @Test
    void shouldSecureTransactionListAgainstTransactionScope() {

        List<AlertResponse> response =
                List.of(
                        mock(
                                AlertResponse.class
                        )
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.getAlertsByTransactionId(
                        TRANSACTION_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getAlertsByTransactionId(
                        TRANSACTION_ID,
                        securityContext
                )
        );

        verify(
                transactionScopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Transaction not found: "
                        + TRANSACTION_ID
        );
    }

    @Test
    void shouldSecureDecisionListAgainstDecisionTransactionScope() {

        TransactionDecisionResponse decision =
                mock(
                        TransactionDecisionResponse.class
                );

        List<AlertResponse> response =
                List.of(
                        mock(
                                AlertResponse.class
                        )
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                transactionDecisionService
                        .getDecisionById(
                                DECISION_ID
                        )
        ).thenReturn(
                decision
        );

        when(
                decision.getTransactionId()
        ).thenReturn(
                TRANSACTION_ID
        );

        when(
                alertService.getAlertsByDecisionId(
                        DECISION_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.getAlertsByDecisionId(
                        DECISION_ID,
                        securityContext
                )
        );

        verify(
                transactionScopeAuthorizationService
        ).requireVisibleTransaction(
                TRANSACTION_ID,
                actor,
                "Transaction not found: "
                        + TRANSACTION_ID
        );
    }

    @Test
    void shouldPreventTrustedServiceCallWhenAlertIsHidden() {

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        doThrow(
                new ResourceNotFoundException(
                        "Alert not found: "
                                + ALERT_ID
                )
        ).when(
                alertScopeAuthorizationService
        ).requireVisibleAlert(
                ALERT_ID,
                actor,
                "Alert not found: "
                        + ALERT_ID
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        accessService.getAlertById(
                                ALERT_ID,
                                securityContext
                        )
        );

        verify(
                alertService,
                never()
        ).getAlertById(
                ALERT_ID
        );
    }

    @Test
    void shouldFailClosedWhenDecisionHasNoTransactionForRead() {

        TransactionDecisionResponse decision =
                mock(
                        TransactionDecisionResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                transactionDecisionService
                        .getDecisionById(
                                DECISION_ID
                        )
        ).thenReturn(
                decision
        );

        when(
                decision.getTransactionId()
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        accessService.getAlertsByDecisionId(
                                DECISION_ID,
                                securityContext
                        )
        );

        verifyNoInteractions(
                alertService
        );
    }
    @Test
    void shouldScopeSearchToAuthorizedOrganizationAndTenant() {

        @SuppressWarnings("unchecked")
        PageResponse<AlertResponse> response =
                mock(
                        PageResponse.class
                );

        when(
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "alert.view"
                        )
        ).thenReturn(
                actor
        );

        when(
                alertService.searchAlertsScoped(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        25,
                        "generatedAt",
                        "DESC",
                        ORGANIZATION_ID,
                        TENANT_ID
                )
        ).thenReturn(
                response
        );

        assertSame(
                response,
                accessService.searchAlerts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        25,
                        "generatedAt",
                        "DESC",
                        securityContext
                )
        );

        verify(
                alertService
        ).searchAlertsScoped(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                25,
                "generatedAt",
                "DESC",
                ORGANIZATION_ID,
                TENANT_ID
        );
    }
}