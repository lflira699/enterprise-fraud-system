package com.efs.modules.dashboard.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.alert.service.AlertServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseDashboardMetricsResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.dto.DashboardComponent;
import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardEffectiveFilters;
import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardFilterOptionsResponse;
import com.efs.modules.dashboard.dto.DashboardFilterSource;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.detection.service.ScenarioActivationServiceInterface;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
import com.efs.shared.exception.DashboardDataUnavailableException;
import com.efs.shared.exception.InvalidDashboardFilterException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    private static final String INVALID_FILTER_REASON =
            "INVALID_DASHBOARD_FILTER";

    private static final String UNAUTHORIZED_TENANT_REASON =
            "UNAUTHORIZED_TENANT_SCOPE";

    private static final String AVAILABLE_COMPONENTS_KEY =
            "EFS.DASHBOARD.COMPONENTS.AVAILABLE";

    private static final String DEFAULT_COMPONENTS_KEY =
            "EFS.DASHBOARD.COMPONENTS.DEFAULT";

    private static final List<DashboardComponent>
            ALL_COMPONENTS =
            List.of(
                    DashboardComponent.values()
            );

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final SystemConfigurationServiceInterface
            systemConfigurationService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

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
            SystemConfigurationServiceInterface systemConfigurationService,
            TenantOrganizationLookupServiceInterface tenantOrganizationLookupService,
            AlertServiceInterface alertService,
            CaseServiceInterface caseService,
            RiskAssessmentServiceInterface riskAssessmentService,
            ScenarioActivationServiceInterface scenarioActivationService,
            AuditEventServiceInterface auditEventService) {

        this.userAccountLookupService =
                userAccountLookupService;

        this.systemConfigurationService =
                systemConfigurationService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;

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

        return getDashboard(
                securityContext,
                DashboardFilterCriteria.defaults()
        );
    }

    @Override
    public DashboardResponse getDashboard(
            SecurityContext securityContext,
            DashboardFilterCriteria filterCriteria) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Objects.requireNonNull(
                filterCriteria,
                "filterCriteria is required"
        );

        requireDashboardViewPermission(
                securityContext
        );

        UserAccountReference authorizedUser =
                resolveAuthorizedUser(
                        securityContext
                );

        UUID organizationId =
                authorizedUser.organizationId();

        UUID effectiveTenantId;

        try {
            effectiveTenantId =
                    resolveEffectiveTenant(
                            authorizedUser,
                            filterCriteria.tenantId()
                    );
        }
        catch (AccessDeniedException exception) {

            recordDashboardRejected(
                    securityContext,
                    organizationId,
                    filterCriteria,
                    UNAUTHORIZED_TENANT_REASON
            );

            throw exception;
        }

        List<DashboardComponent>
                availableComponents;

        List<DashboardComponent>
                defaultComponents;

        try {
            availableComponents =
                    resolveAvailableComponents(
                            organizationId,
                            effectiveTenantId
                    );

            defaultComponents =
                    resolveDefaultComponents(
                            organizationId,
                            effectiveTenantId,
                            availableComponents
                    );
        }
        catch (RuntimeException exception) {

            recordDashboardFailure(
                    securityContext,
                    organizationId,
                    null,
                    null,
                    exception
            );

            throw exception;
        }

        List<DashboardComponent>
                selectedComponents;

        try {
            selectedComponents =
                    resolveSelectedComponents(
                            filterCriteria,
                            availableComponents,
                            defaultComponents
                    );
        }
        catch (InvalidDashboardFilterException exception) {

            recordDashboardRejected(
                    securityContext,
                    organizationId,
                    filterCriteria,
                    INVALID_FILTER_REASON
            );

            throw exception;
        }

        DashboardFilterSource filterSource =
                filterCriteria.hasExplicitFilters()
                        ? DashboardFilterSource.EXPLICIT
                        : DashboardFilterSource.DEFAULT;

        DashboardEffectiveFilters effectiveFilters =
                new DashboardEffectiveFilters(
                        effectiveTenantId,
                        selectedComponents,
                        filterSource
                );

        Long criticalAlerts = null;
        Long openAlerts = null;
        Long openCases = null;
        Long closedCases = null;
        BigDecimal averageRiskScore = null;
        Long activatedDetectionScenarios = null;

        List<DashboardComponent>
                unavailableComponents =
                new ArrayList<>();

        if (
                selectedComponents.contains(
                        DashboardComponent.ALERT
                )
        ) {
            try {

                AlertDashboardMetricsResponse alertMetrics =
                        alertService.getDashboardMetrics(
                                organizationId,
                                effectiveTenantId
                        );

                criticalAlerts =
                        alertMetrics.getCriticalAlerts();

                openAlerts =
                        alertMetrics.getOpenAlerts();

            }
            catch (RuntimeException exception) {

                unavailableComponents.add(
                        DashboardComponent.ALERT
                );
            }
        }

        if (
                selectedComponents.contains(
                        DashboardComponent.CASE
                )
        ) {
            try {

                CaseDashboardMetricsResponse caseMetrics =
                        caseService.getDashboardMetrics(
                                organizationId,
                                effectiveTenantId
                        );

                openCases =
                        caseMetrics.getOpenCases();

                closedCases =
                        caseMetrics.getClosedCases();

            }
            catch (RuntimeException exception) {

                unavailableComponents.add(
                        DashboardComponent.CASE
                );
            }
        }

        if (
                selectedComponents.contains(
                        DashboardComponent.RISK
                )
        ) {
            try {

                averageRiskScore =
                        riskAssessmentService
                                .getAverageLatestRiskScore(
                                        organizationId,
                                        effectiveTenantId
                                );

            }
            catch (RuntimeException exception) {

                unavailableComponents.add(
                        DashboardComponent.RISK
                );
            }
        }

        if (
                selectedComponents.contains(
                        DashboardComponent.DETECTION
                )
        ) {
            try {

                activatedDetectionScenarios =
                        scenarioActivationService
                                .countActivatedDetectionScenarios(
                                        organizationId,
                                        effectiveTenantId
                                );

            }
            catch (RuntimeException exception) {

                unavailableComponents.add(
                        DashboardComponent.DETECTION
                );
            }
        }

        if (
                unavailableComponents.size()
                        == selectedComponents.size()
        ) {

            recordDashboardFailure(
                    securityContext,
                    organizationId,
                    DATA_UNAVAILABLE_REASON,
                    effectiveFilters,
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
                        unavailableComponents,
                        effectiveFilters
                );

        recordDashboardSuccess(
                securityContext,
                organizationId,
                dataStatus,
                unavailableComponents,
                effectiveFilters
        );

        return response;
    }

    @Override
    public DashboardFilterOptionsResponse
    getFilterOptions(
            SecurityContext securityContext,
            UUID tenantId) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireDashboardViewPermission(
                securityContext
        );

        UserAccountReference authorizedUser =
                resolveAuthorizedUser(
                        securityContext
                );

        UUID organizationId =
                authorizedUser.organizationId();

        UUID effectiveTenantId;

        try {
            effectiveTenantId =
                    resolveEffectiveTenant(
                            authorizedUser,
                            tenantId
                    );
        }
        catch (AccessDeniedException exception) {

            recordDashboardRejected(
                    securityContext,
                    organizationId,
                    new DashboardFilterCriteria(
                            tenantId,
                            null
                    ),
                    UNAUTHORIZED_TENANT_REASON
            );

            throw exception;
        }

        List<DashboardComponent>
                availableComponents;

        List<DashboardComponent>
                defaultComponents;

        try {
            availableComponents =
                    resolveAvailableComponents(
                            organizationId,
                            effectiveTenantId
                    );

            defaultComponents =
                    resolveDefaultComponents(
                            organizationId,
                            effectiveTenantId,
                            availableComponents
                    );
        }
        catch (RuntimeException exception) {

            recordDashboardFailure(
                    securityContext,
                    organizationId,
                    null,
                    null,
                    exception
            );

            throw exception;
        }

        return new DashboardFilterOptionsResponse(
                authorizedUser.tenantId() == null,
                effectiveTenantId,
                availableComponents,
                defaultComponents
        );
    }

    private UserAccountReference resolveAuthorizedUser(
            SecurityContext securityContext) {

        try {

            UserAccountReference authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext.getUserId()
                            );

            validateAuthorizedScope(
                    securityContext,
                    authorizedUser
            );

            return authorizedUser;

        }
        catch (RuntimeException exception) {

            recordDashboardFailure(
                    securityContext,
                    null,
                    null,
                    null,
                    exception
            );

            throw exception;
        }
    }

    private UUID resolveEffectiveTenant(
            UserAccountReference authorizedUser,
            UUID requestedTenantId) {

        UUID authoritativeTenantId =
                authorizedUser.tenantId();

        if (requestedTenantId == null) {
            return authoritativeTenantId;
        }

        if (authoritativeTenantId != null) {

            if (!Objects.equals(
                    authoritativeTenantId,
                    requestedTenantId
            )) {
                throw new AccessDeniedException(
                        "Requested tenant is outside authorized scope"
                );
            }

            return authoritativeTenantId;
        }

        UUID requestedOrganizationId;

        try {
            requestedOrganizationId =
                    tenantOrganizationLookupService
                            .getOrganizationIdByTenantId(
                                    requestedTenantId
                            );
        }
        catch (RuntimeException exception) {

            throw new AccessDeniedException(
                    "Requested tenant is outside authorized scope",
                    exception
            );
        }

        if (!Objects.equals(
                authorizedUser.organizationId(),
                requestedOrganizationId
        )) {
            throw new AccessDeniedException(
                    "Requested tenant is outside authorized scope"
            );
        }

        return requestedTenantId;
    }

    private List<DashboardComponent>
    resolveAvailableComponents(
            UUID organizationId,
            UUID tenantId) {

        return systemConfigurationService
                .resolveConfigurationValue(
                        AVAILABLE_COMPONENTS_KEY,
                        organizationId,
                        tenantId
                )
                .map(
                        value ->
                                parseConfiguredComponents(
                                        AVAILABLE_COMPONENTS_KEY,
                                        value
                                )
                )
                .orElse(
                        ALL_COMPONENTS
                );
    }

    private List<DashboardComponent>
    resolveDefaultComponents(
            UUID organizationId,
            UUID tenantId,
            List<DashboardComponent> availableComponents) {

        List<DashboardComponent> defaults =
                systemConfigurationService
                        .resolveConfigurationValue(
                                DEFAULT_COMPONENTS_KEY,
                                organizationId,
                                tenantId
                        )
                        .map(
                                value ->
                                        parseConfiguredComponents(
                                                DEFAULT_COMPONENTS_KEY,
                                                value
                                        )
                        )
                        .orElse(
                                availableComponents
                        );

        if (!availableComponents.containsAll(
                defaults
        )) {
            throw new IllegalStateException(
                    "Dashboard default components must be a subset "
                            + "of available components"
            );
        }

        return defaults;
    }

    private List<DashboardComponent>
    resolveSelectedComponents(
            DashboardFilterCriteria filterCriteria,
            List<DashboardComponent> availableComponents,
            List<DashboardComponent> defaultComponents) {

        if (filterCriteria.components() == null) {
            return defaultComponents;
        }

        List<DashboardComponent> requested =
                parseRequestedComponents(
                        filterCriteria.components()
                );

        if (!availableComponents.containsAll(
                requested
        )) {
            throw new InvalidDashboardFilterException(
                    "Requested Dashboard component is not available"
            );
        }

        return requested;
    }

    private List<DashboardComponent>
    parseRequestedComponents(
            String rawComponents) {

        if (
                rawComponents == null
                        || rawComponents.isBlank()
        ) {
            throw new InvalidDashboardFilterException(
                    "Dashboard components filter must not be empty"
            );
        }

        Set<DashboardComponent> components =
                EnumSet.noneOf(
                        DashboardComponent.class
                );

        String[] tokens =
                rawComponents.split(
                        ",",
                        -1
                );

        for (String token : tokens) {

            String normalized =
                    token.trim();

            if (normalized.isEmpty()) {
                throw new InvalidDashboardFilterException(
                        "Dashboard components filter contains an empty value"
                );
            }

            try {
                components.add(
                        DashboardComponent.valueOf(
                                normalized.toUpperCase(
                                        Locale.ROOT
                                )
                        )
                );
            }
            catch (IllegalArgumentException exception) {
                throw new InvalidDashboardFilterException(
                        "Unknown Dashboard component: "
                                + normalized
                );
            }
        }

        return canonicalizeComponents(
                components
        );
    }

    private List<DashboardComponent>
    parseConfiguredComponents(
            String configurationKey,
            String rawComponents) {

        if (
                rawComponents == null
                        || rawComponents.isBlank()
        ) {
            throw new IllegalStateException(
                    "Dashboard configuration is blank: "
                            + configurationKey
            );
        }

        Set<DashboardComponent> components =
                EnumSet.noneOf(
                        DashboardComponent.class
                );

        String[] tokens =
                rawComponents.split(
                        ",",
                        -1
                );

        for (String token : tokens) {

            String normalized =
                    token.trim();

            if (normalized.isEmpty()) {
                throw new IllegalStateException(
                        "Dashboard configuration contains "
                                + "an empty component: "
                                + configurationKey
                );
            }

            try {
                components.add(
                        DashboardComponent.valueOf(
                                normalized.toUpperCase(
                                        Locale.ROOT
                                )
                        )
                );
            }
            catch (IllegalArgumentException exception) {
                throw new IllegalStateException(
                        "Dashboard configuration contains "
                                + "an unknown component: "
                                + configurationKey,
                        exception
                );
            }
        }

        if (components.isEmpty()) {
            throw new IllegalStateException(
                    "Dashboard configuration must contain "
                            + "at least one component: "
                            + configurationKey
            );
        }

        return canonicalizeComponents(
                components
        );
    }

    private List<DashboardComponent>
    canonicalizeComponents(
            Set<DashboardComponent> components) {

        List<DashboardComponent> result =
                new ArrayList<>();

        for (
                DashboardComponent component :
                DashboardComponent.values()
        ) {
            if (components.contains(component)) {
                result.add(component);
            }
        }

        return List.copyOf(
                result
        );
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
            List<DashboardComponent> unavailableComponents,
            DashboardEffectiveFilters effectiveFilters) {

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

        addEffectiveFilterAuditDetails(
                details,
                effectiveFilters
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void recordDashboardRejected(
            SecurityContext securityContext,
            UUID organizationId,
            DashboardFilterCriteria criteria,
            String reason) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
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
                reason
        );

        details.put(
                "filterSource",
                criteria.hasExplicitFilters()
                        ? DashboardFilterSource.EXPLICIT.name()
                        : DashboardFilterSource.DEFAULT.name()
        );

        details.put(
                "tenantId",
                criteria.tenantId()
        );

        details.put(
                "components",
                criteria.components()
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
            DashboardEffectiveFilters effectiveFilters,
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

        if (effectiveFilters != null) {
            addEffectiveFilterAuditDetails(
                    details,
                    effectiveFilters
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

    private void addEffectiveFilterAuditDetails(
            Map<String, Object> details,
            DashboardEffectiveFilters effectiveFilters) {

        details.put(
                "filterSource",
                effectiveFilters
                        .getFilterSource()
                        .name()
        );

        details.put(
                "tenantId",
                effectiveFilters
                        .getTenantId()
        );

        details.put(
                "components",
                effectiveFilters
                        .getComponents()
                        .stream()
                        .map(Enum::name)
                        .toList()
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
