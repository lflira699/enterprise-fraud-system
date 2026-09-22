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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AlertAccessService
        implements AlertAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "alert.view";

    private static final String CREATE_PERMISSION =
            "alert.create";

    private static final String UPDATE_PERMISSION =
            "alert.update";

    private static final String ASSIGN_PERMISSION =
            "alert.assign";

    private static final String CLOSE_PERMISSION =
            "alert.close";

    private final AlertServiceInterface
            alertService;

    private final AlertScopeAuthorizationServiceInterface
            alertScopeAuthorizationService;

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    private final TransactionScopeAuthorizationServiceInterface
            transactionScopeAuthorizationService;

    public AlertAccessService(
            AlertServiceInterface alertService,
            AlertScopeAuthorizationServiceInterface
                    alertScopeAuthorizationService,
            TransactionDecisionServiceInterface
                    transactionDecisionService,
            TransactionScopeAuthorizationServiceInterface
                    transactionScopeAuthorizationService) {

        this.alertService =
                alertService;

        this.alertScopeAuthorizationService =
                alertScopeAuthorizationService;

        this.transactionDecisionService =
                transactionDecisionService;

        this.transactionScopeAuthorizationService =
                transactionScopeAuthorizationService;
    }

    @Override
    @Transactional
    public AlertResponse createAlert(
            AlertRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                CREATE_PERMISSION
                        );

        TransactionDecisionResponse decision =
                transactionDecisionService
                        .getDecisionById(
                                request.getDecisionId()
                        );

        UUID transactionId =
                decision.getTransactionId();

        /*
         * AlertService owns the domain validation that a decision
         * must reference a transaction.  If it is null, preserve
         * that trusted domain behavior rather than duplicating it.
         */
        if (transactionId != null) {

            transactionScopeAuthorizationService
                    .requireVisibleTransaction(
                            transactionId,
                            actor,
                            "Transaction not found: "
                                    + transactionId
                    );
        }

        return alertService
                .createAlert(
                        request
                );
    }

    @Override
    public AlertResponse getAlertById(
            UUID alertId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        requireVisible(
                alertId,
                actor
        );

        return alertService
                .getAlertById(
                        alertId
                );
    }

    @Override
    @Transactional
    public AlertResponse updateAlertStatus(
            UUID alertId,
            AlertStatusUpdateRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                UPDATE_PERMISSION
                        );

        requireVisible(
                alertId,
                actor
        );

        return alertService
                .updateAlertStatus(
                        alertId,
                        request
                );
    }

    @Override
    @Transactional
    public AlertResponse assignAlert(
            UUID alertId,
            AlertAssignmentRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                ASSIGN_PERMISSION
                        );

        requireVisible(
                alertId,
                actor
        );

        return alertService
                .assignAlert(
                        alertId,
                        request
                );
    }

    @Override
    @Transactional
    public AlertResponse closeAlert(
            UUID alertId,
            AlertClosureRequest request,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                CLOSE_PERMISSION
                        );

        requireVisible(
                alertId,
                actor
        );

        return alertService
                .closeAlert(
                        alertId,
                        request
                );
    }

    @Override
    public List<AlertHistoryResponse> getAlertHistory(
            UUID alertId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        requireVisible(
                alertId,
                actor
        );

        return alertService
                .getAlertHistory(
                        alertId
                );
    }

    @Override
    public List<AlertResponse> getAlertsByTransactionId(
            UUID transactionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        transactionScopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        return alertService
                .getAlertsByTransactionId(
                        transactionId
                );
    }

    @Override
    public List<AlertResponse> getAlertsByDecisionId(
            UUID decisionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        TransactionDecisionResponse decision =
                transactionDecisionService
                        .getDecisionById(
                                decisionId
                        );

        UUID transactionId =
                decision.getTransactionId();

        if (transactionId == null) {

            throw new ResourceNotFoundException(
                    "Alert decision transaction not found: "
                            + decisionId
            );
        }

        transactionScopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        "Transaction not found: "
                                + transactionId
                );

        return alertService
                .getAlertsByDecisionId(
                        decisionId
                );
    }

    @Override
    public PageResponse<AlertResponse> searchAlerts(
            String status,
            String priority,
            String riskLevel,
            UUID assignedTo,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            UUID customerId,
            String scenarioCode,
            UUID caseId,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        UserAccountReference actor =
                alertScopeAuthorizationService
                        .authorize(
                                securityContext,
                                VIEW_PERMISSION
                        );

        return alertService
                .searchAlertsScoped(
                        status,
                        priority,
                        riskLevel,
                        assignedTo,
                        createdFrom,
                        createdTo,
                        customerId,
                        scenarioCode,
                        caseId,
                        page,
                        size,
                        sort,
                        direction,
                        actor.organizationId(),
                        actor.tenantId()
                );
    }
    private void requireVisible(
            UUID alertId,
            UserAccountReference actor) {

        alertScopeAuthorizationService
                .requireVisibleAlert(
                        alertId,
                        actor,
                        "Alert not found: "
                                + alertId
                );
    }
}