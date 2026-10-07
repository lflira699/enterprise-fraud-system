package com.efs.modules.alert.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private static final String ALERT_REVIEW_EVENT_TYPE =
            "ALERT_REVIEW";

    private static final String ALERT_REVIEW_ENTITY_TYPE =
            "ALERT";

    private static final String ALERT_REVIEW_ACTION =
            "REVIEW";

    private static final String ALERT_REVIEW_SOURCE_COMPONENT =
            "ALERT";

    private final AlertServiceInterface
            alertService;

    private final AlertScopeAuthorizationServiceInterface
            alertScopeAuthorizationService;

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    private final TransactionScopeAuthorizationServiceInterface
            transactionScopeAuthorizationService;

    private final AuditEventServiceInterface
            auditEventService;

    public AlertAccessService(
            AlertServiceInterface alertService,
            AlertScopeAuthorizationServiceInterface
                    alertScopeAuthorizationService,
            TransactionDecisionServiceInterface
                    transactionDecisionService,
            TransactionScopeAuthorizationServiceInterface
                    transactionScopeAuthorizationService,
            AuditEventServiceInterface auditEventService) {

        this.alertService =
                alertService;

        this.alertScopeAuthorizationService =
                alertScopeAuthorizationService;

        this.transactionDecisionService =
                transactionDecisionService;

        this.transactionScopeAuthorizationService =
                transactionScopeAuthorizationService;

        this.auditEventService =
                auditEventService;
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

        UserAccountReference actor;

        try {
            actor =
                    alertScopeAuthorizationService
                            .authorize(
                                    securityContext,
                                    VIEW_PERMISSION
                            );
        } catch (AccessDeniedException exception) {
            recordAlertReviewAudit(
                    securityContext,
                    alertId,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null
            );

            throw exception;
        }

        AlertResponse response;

        try {
            requireVisible(
                    alertId,
                    actor
            );

            response =
                    alertService
                            .getAlertById(
                                    alertId
                            );
        } catch (ResourceNotFoundException exception) {
            recordAlertReviewAudit(
                    securityContext,
                    alertId,
                    "REJECTED",
                    "ALERT_NOT_FOUND",
                    null
            );

            throw exception;
        } catch (RuntimeException exception) {
            recordAlertReviewAudit(
                    securityContext,
                    alertId,
                    "FAILURE",
                    "ALERT_REVIEW_FAILED",
                    exception
            );

            throw exception;
        }

        recordAlertReviewAudit(
                securityContext,
                alertId,
                "SUCCESS",
                null,
                null
        );

        return response;
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
    private void recordAlertReviewAudit(
            SecurityContext securityContext,
            UUID alertId,
            String eventResult,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                ALERT_REVIEW_EVENT_TYPE
        );

        request.setEntityType(
                ALERT_REVIEW_ENTITY_TYPE
        );

        request.setEntityId(
                alertId
        );

        request.setAction(
                ALERT_REVIEW_ACTION
        );

        request.setSourceComponent(
                ALERT_REVIEW_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                VIEW_PERMISSION
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass().getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
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