package com.efs.modules.dashboard.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.alert.service.AlertServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseDashboardMetricsResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.dto.DashboardComponent;
import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.detection.service.ScenarioActivationServiceInterface;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
import com.efs.shared.exception.DashboardDataUnavailableException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class DashboardService
        implements DashboardServiceInterface {

    private static final String DASHBOARD_VIEW_PERMISSION =
            "dashboard.view";

    private static final String DASHBOARD_VIEW_EVENT_TYPE =
            "DASHBOARD_VIEW";

    private static final String DASHBOARD_VIEW_ENTITY_TYPE =
            "DASHBOARD";

    private static final String DASHBOARD_VIEW_ACTION =
            "VIEW";

    private static final String DASHBOARD_VIEW_SOURCE_COMPONENT =
            "DASHBOARD";

    private static final String DATA_UNAVAILABLE_REASON =
            "DASHBOARD_DATA_UNAVAILABLE";

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AlertServiceInterface
            alertService;

    private final CaseServiceInterface
            caseService;

    private final RiskAssessmentServiceInterface
            riskAssessmentService;

    private final ScenarioActivationServiceInterface
            scenarioActivationService;

    private final AuditEventServiceInterface
            auditEventService;

    public DashboardService(
            UserAccountLookupServiceInterface userAccountLookupService,
            AlertServiceInterface alertService,
            CaseServiceInterface caseService,
            RiskAssessmentServiceInterface riskAssessmentService,
            ScenarioActivationServiceInterface scenarioActivationService,
            AuditEventServiceInterface auditEventService) {

        this.userAccountLookupService =
                userAccountLookupService;

        this.alertService =
                alertService;

        this.caseService =
                caseService;

        this.riskAssessmentService =
                riskAssessmentService;

        this.scenarioActivationService =
                scenarioActivationService;

        this.auditEventService =
                auditEventService;
    }

    @Override
    public DashboardResponse getDashboard(
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireDashboardViewPermission(
                securityContext
        );

        UserAccountReference authorizedUser;

        try {

            authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            validateAuthorizedScope(
                    securityContext,
                    authorizedUser
            );

        } catch (RuntimeException exception) {

            recordDashboardFailure(
                    securityContext,
                    null,
                    null,
                    exception
            );

            throw exception;
        }

        UUID organizationId =
                authorizedUser.organizationId();

        UUID tenantId =
                authorizedUser.tenantId();

        Long criticalAlerts = null;
        Long openAlerts = null;
        Long openCases = null;
        Long closedCases = null;
        BigDecimal averageRiskScore = null;
        Long activatedDetectionScenarios = null;

        List<DashboardComponent>
                unavailableComponents =
                new ArrayList<>();

        try {

            AlertDashboardMetricsResponse alertMetrics =
                    alertService.getDashboardMetrics(
                            organizationId,
                            tenantId
                    );

            criticalAlerts =
                    alertMetrics.getCriticalAlerts();

            openAlerts =
                    alertMetrics.getOpenAlerts();

        } catch (RuntimeException exception) {

            unavailableComponents.add(
                    DashboardComponent.ALERT
            );
        }

        try {

            CaseDashboardMetricsResponse caseMetrics =
                    caseService.getDashboardMetrics(
                            organizationId,
                            tenantId
                    );

            openCases =
                    caseMetrics.getOpenCases();

            closedCases =
                    caseMetrics.getClosedCases();

        } catch (RuntimeException exception) {

            unavailableComponents.add(
                    DashboardComponent.CASE
            );
        }

        try {

            averageRiskScore =
                    riskAssessmentService
                            .getAverageLatestRiskScore(
                                    organizationId,
                                    tenantId
                            );

        } catch (RuntimeException exception) {

            unavailableComponents.add(
                    DashboardComponent.RISK
            );
        }

        try {

            activatedDetectionScenarios =
                    scenarioActivationService
                            .countActivatedDetectionScenarios(
                                    organizationId,
                                    tenantId
                            );

        } catch (RuntimeException exception) {

            unavailableComponents.add(
                    DashboardComponent.DETECTION
            );
        }

        if (
                unavailableComponents.size()
                        == DashboardComponent.values().length
        ) {

            recordDashboardFailure(
                    securityContext,
                    organizationId,
                    DATA_UNAVAILABLE_REASON,
                    null
            );

            throw new DashboardDataUnavailableException(
                    "Dashboard data is unavailable"
            );
        }

        DashboardDataStatus dataStatus =
                unavailableComponents.isEmpty()
                        ? DashboardDataStatus.COMPLETE
                        : DashboardDataStatus.PARTIAL;

        DashboardResponse response =
                new DashboardResponse(
                        LocalDateTime.now(),
                        dataStatus,
                        criticalAlerts,
                        openAlerts,
                        openCases,
                        closedCases,
                        averageRiskScore,
                        activatedDetectionScenarios,
                        unavailableComponents
                );

        recordDashboardSuccess(
                securityContext,
                organizationId,
                dataStatus,
                unavailableComponents
        );

        return response;
    }

    private void requireDashboardViewPermission(
            SecurityContext securityContext) {

        if (!securityContext.hasPermission(
                DASHBOARD_VIEW_PERMISSION
        )) {

            AuditEventRequest request =
                    baseAuditRequest(
                            securityContext,
                            null,
                            "REJECTED"
                    );

            Map<String, Object> details =
                    new LinkedHashMap<>();

            details.put(
                    "permissionCode",
                    DASHBOARD_VIEW_PERMISSION
            );

            details.put(
                    "reason",
                    "MISSING_PERMISSION"
            );

            request.setEventDetails(
                    details
            );

            auditEventService.createAuditEvent(
                    request
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + DASHBOARD_VIEW_PERMISSION
            );
        }
    }

    private void validateAuthorizedScope(
            SecurityContext securityContext,
            UserAccountReference authorizedUser) {

        if (!Objects.equals(
                securityContext.getTenantId(),
                authorizedUser.tenantId()
        )) {

            throw new IllegalStateException(
                    "Authenticated tenant scope does not match "
                            + "authorized user scope"
            );
        }
    }

    private void recordDashboardSuccess(
            SecurityContext securityContext,
            UUID organizationId,
            DashboardDataStatus dataStatus,
            List<DashboardComponent>
                    unavailableComponents) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        "SUCCESS"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                DASHBOARD_VIEW_PERMISSION
        );

        details.put(
                "dataStatus",
                dataStatus.name()
        );

        details.put(
                "unavailableComponents",
                unavailableComponents
                        .stream()
                        .map(Enum::name)
                        .toList()
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void recordDashboardFailure(
            SecurityContext securityContext,
            UUID organizationId,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        "FAILURE"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                DASHBOARD_VIEW_PERMISSION
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
                    exception.getClass()
                            .getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private AuditEventRequest baseAuditRequest(
            SecurityContext securityContext,
            UUID organizationId,
            String eventResult) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (organizationId != null) {
            request.setOrganizationId(
                    organizationId
            );
        }

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
                DASHBOARD_VIEW_EVENT_TYPE
        );

        request.setEntityType(
                DASHBOARD_VIEW_ENTITY_TYPE
        );

        request.setEntityId(
                null
        );

        request.setAction(
                DASHBOARD_VIEW_ACTION
        );

        request.setSourceComponent(
                DASHBOARD_VIEW_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        return request;
    }
}
