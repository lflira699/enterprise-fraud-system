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
import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardFilterSource;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.detection.service.ScenarioActivationServiceInterface;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
import com.efs.shared.exception.DashboardDataUnavailableException;
import com.efs.shared.exception.InvalidDashboardFilterException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardFilterManagementTest {

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private SystemConfigurationServiceInterface
            systemConfigurationService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    @Mock
    private AlertServiceInterface
            alertService;

    @Mock
    private CaseServiceInterface
            caseService;

    @Mock
    private RiskAssessmentServiceInterface
            riskAssessmentService;

    @Mock
    private ScenarioActivationServiceInterface
            scenarioActivationService;

    @Mock
    private AuditEventServiceInterface
            auditEventService;

    @Mock
    private SecurityContext
            securityContext;

    @InjectMocks
    private DashboardService
            dashboardService;

    @Test
    void noExplicitFiltersShouldFallbackToAllComponents() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        successfulProviders(
                organizationId,
                tenantId
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext,
                        DashboardFilterCriteria.defaults()
                );

        assertEquals(
                DashboardDataStatus.COMPLETE,
                response.getDataStatus()
        );

        assertEquals(
                tenantId,
                response.getEffectiveFilters()
                        .getTenantId()
        );

        assertEquals(
                DashboardFilterSource.DEFAULT,
                response.getEffectiveFilters()
                        .getFilterSource()
        );

        assertEquals(
                List.of(
                        DashboardComponent.ALERT,
                        DashboardComponent.CASE,
                        DashboardComponent.RISK,
                        DashboardComponent.DETECTION
                ),
                response.getEffectiveFilters()
                        .getComponents()
        );
    }

    @Test
    void explicitComponentsShouldCallOnlySelectedOwners() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                alertService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new AlertDashboardMetricsResponse(
                        3L,
                        7L
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                new BigDecimal("54.00")
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext,
                        new DashboardFilterCriteria(
                                null,
                                "ALERT,RISK"
                        )
                );

        assertEquals(
                DashboardDataStatus.COMPLETE,
                response.getDataStatus()
        );

        assertEquals(
                DashboardFilterSource.EXPLICIT,
                response.getEffectiveFilters()
                        .getFilterSource()
        );

        assertEquals(
                List.of(
                        DashboardComponent.ALERT,
                        DashboardComponent.RISK
                ),
                response.getEffectiveFilters()
                        .getComponents()
        );

        assertNull(
                response.getOpenCases()
        );

        assertNull(
                response.getClosedCases()
        );

        assertNull(
                response.getActivatedDetectionScenarios()
        );

        verifyNoInteractions(
                caseService,
                scenarioActivationService
        );
    }

    @Test
    void selectedOwnerFailureShouldProducePartial() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                alertService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new AlertDashboardMetricsResponse(
                        1L,
                        2L
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced case failure"
                )
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext,
                        new DashboardFilterCriteria(
                                null,
                                "ALERT,CASE"
                        )
                );

        assertEquals(
                DashboardDataStatus.PARTIAL,
                response.getDataStatus()
        );

        assertEquals(
                List.of(
                        DashboardComponent.CASE
                ),
                response.getUnavailableComponents()
        );
    }

    @Test
    void allSelectedOwnersUnavailableShouldProduce503Condition() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenThrow(
                new IllegalStateException(
                        "forced risk failure"
                )
        );

        assertThrows(
                DashboardDataUnavailableException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        null,
                                        "RISK"
                                )
                        )
        );

        verifyNoInteractions(
                alertService,
                caseService,
                scenarioActivationService
        );
    }

    @Test
    void unknownComponentShouldBeRejected() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        assertThrows(
                InvalidDashboardFilterException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        null,
                                        "ALERT,UNKNOWN"
                                )
                        )
        );

        verifyNoInteractions(
                alertService,
                caseService,
                riskAssessmentService,
                scenarioActivationService
        );

        verify(
                auditEventService
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );
    }

    @Test
    void blankComponentsShouldBeRejected() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        assertThrows(
                InvalidDashboardFilterException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        null,
                                        ""
                                )
                        )
        );
    }

    @Test
    void unavailableConfiguredComponentShouldBeRejected() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.AVAILABLE",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        "ALERT,CASE"
                )
        );

        assertThrows(
                InvalidDashboardFilterException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        null,
                                        "RISK"
                                )
                        )
        );
    }

    @Test
    void configuredDefaultsShouldControlOwnerCalls() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.AVAILABLE",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        "ALERT,CASE"
                )
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.DEFAULT",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        "CASE"
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new CaseDashboardMetricsResponse(
                        4L,
                        6L
                )
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext,
                        DashboardFilterCriteria.defaults()
                );

        assertEquals(
                List.of(
                        DashboardComponent.CASE
                ),
                response.getEffectiveFilters()
                        .getComponents()
        );

        verifyNoInteractions(
                alertService,
                riskAssessmentService,
                scenarioActivationService
        );
    }

    @Test
    void organizationScopeShouldAllowTenantInSameOrganization() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID selectedTenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                null
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                selectedTenantId
                        )
        ).thenReturn(
                organizationId
        );

        when(
                alertService.getDashboardMetrics(
                        organizationId,
                        selectedTenantId
                )
        ).thenReturn(
                new AlertDashboardMetricsResponse(
                        2L,
                        5L
                )
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext,
                        new DashboardFilterCriteria(
                                selectedTenantId,
                                "ALERT"
                        )
                );

        assertEquals(
                selectedTenantId,
                response.getEffectiveFilters()
                        .getTenantId()
        );
    }

    @Test
    void organizationScopeShouldRejectTenantFromDifferentOrganization() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID selectedTenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                null
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                selectedTenantId
                        )
        ).thenReturn(
                UUID.randomUUID()
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        selectedTenantId,
                                        "ALERT"
                                )
                        )
        );

        verifyNoInteractions(
                alertService,
                caseService,
                riskAssessmentService,
                scenarioActivationService
        );
    }

    @Test
    void tenantScopeShouldRejectTenantSwitch() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID authoritativeTenantId = UUID.randomUUID();
        UUID requestedTenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                authoritativeTenantId
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        dashboardService.getDashboard(
                                securityContext,
                                new DashboardFilterCriteria(
                                        requestedTenantId,
                                        "ALERT"
                                )
                        )
        );

        verifyNoInteractions(
                tenantOrganizationLookupService,
                alertService,
                caseService,
                riskAssessmentService,
                scenarioActivationService
        );
    }

    private void authorize(
            UUID userId,
            UUID organizationId,
            UUID tenantId) {

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        when(
                securityContext.getUserId()
        ).thenReturn(
                userId
        );

        when(
                securityContext.getTenantId()
        ).thenReturn(
                tenantId
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        )
        ).thenReturn(
                new UserAccountReference(
                        userId,
                        organizationId,
                        tenantId,
                        "dashboard-filter@example.com"
                )
        );
    }

    private void successfulProviders(
            UUID organizationId,
            UUID tenantId) {

        when(
                alertService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new AlertDashboardMetricsResponse(
                        1L,
                        2L
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new CaseDashboardMetricsResponse(
                        3L,
                        4L
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                new BigDecimal("50.00")
        );

        when(
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(5L);
    }
}
