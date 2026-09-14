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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

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
    private SecurityContext securityContext;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void missingPermissionShouldRejectBeforeScopeAndProviders() {

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () -> dashboardService.getDashboard(
                        securityContext
                )
        );

        verifyNoInteractions(
                userAccountLookupService,
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
    void shouldReturnCompleteDashboardUsingAuthoritativeScope() {

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
                        8L
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenReturn(
                new CaseDashboardMetricsResponse(
                        5L,
                        2L
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                new BigDecimal("64.50")
        );

        when(
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(4L);

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext
                );

        assertEquals(
                DashboardDataStatus.COMPLETE,
                response.getDataStatus()
        );

        assertEquals(
                3L,
                response.getCriticalAlerts()
        );

        assertEquals(
                8L,
                response.getOpenAlerts()
        );

        assertEquals(
                5L,
                response.getOpenCases()
        );

        assertEquals(
                2L,
                response.getClosedCases()
        );

        assertEquals(
                new BigDecimal("64.50"),
                response.getAverageRiskScore()
        );

        assertEquals(
                4L,
                response.getActivatedDetectionScenarios()
        );

        assertTrue(
                response.getUnavailableComponents()
                        .isEmpty()
        );

        verify(
                alertService
        ).getDashboardMetrics(
                organizationId,
                tenantId
        );

        verify(
                caseService
        ).getDashboardMetrics(
                organizationId,
                tenantId
        );

        verify(
                riskAssessmentService
        ).getAverageLatestRiskScore(
                organizationId,
                tenantId
        );

        verify(
                scenarioActivationService
        ).countActivatedDetectionScenarios(
                organizationId,
                tenantId
        );
    }

    @Test
    void providerFailureShouldReturnPartialWithNullMetrics() {

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
                        2L,
                        6L
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced case provider failure"
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                new BigDecimal("41.00")
        );

        when(
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(3L);

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext
                );

        assertEquals(
                DashboardDataStatus.PARTIAL,
                response.getDataStatus()
        );

        assertNull(
                response.getOpenCases()
        );

        assertNull(
                response.getClosedCases()
        );

        assertEquals(
                1,
                response.getUnavailableComponents()
                        .size()
        );

        assertEquals(
                DashboardComponent.CASE,
                response.getUnavailableComponents()
                        .get(0)
        );
    }

    @Test
    void nullRiskScoreShouldRemainCompleteWhenProviderSucceeded() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        successfulNonRiskProviders(
                organizationId,
                tenantId
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(null);

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext
                );

        assertEquals(
                DashboardDataStatus.COMPLETE,
                response.getDataStatus()
        );

        assertNull(
                response.getAverageRiskScore()
        );

        assertTrue(
                response.getUnavailableComponents()
                        .isEmpty()
        );
    }

    @Test
    void allProviderFailuresShouldRaiseDataUnavailable() {

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
        ).thenThrow(
                new IllegalStateException("alert")
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenThrow(
                new IllegalStateException("case")
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenThrow(
                new IllegalStateException("risk")
        );

        when(
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenThrow(
                new IllegalStateException("detection")
        );

        assertThrows(
                DashboardDataUnavailableException.class,
                () -> dashboardService.getDashboard(
                        securityContext
                )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        assertEquals(
                "FAILURE",
                captor.getValue()
                        .getEventResult()
        );

        assertEquals(
                "DASHBOARD_DATA_UNAVAILABLE",
                captor.getValue()
                        .getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void scopeMismatchShouldFailBeforeOwnerProviders() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID authenticatedTenantId =
                UUID.randomUUID();
        UUID authoritativeTenantId =
                UUID.randomUUID();

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        when(
                securityContext.getUserId()
        ).thenReturn(userId);

        when(
                securityContext.getTenantId()
        ).thenReturn(
                authenticatedTenantId
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
                        authoritativeTenantId,
                        "dashboard@example.com"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> dashboardService.getDashboard(
                        securityContext
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
        ).createAuditEventRequiresNew(
                any(AuditEventRequest.class)
        );
    }

    @Test
    void organizationLevelScopeShouldRemainTenantNull() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                null
        );

        successfulNonRiskProviders(
                organizationId,
                null
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                null
                        )
        ).thenReturn(
                new BigDecimal("50.00")
        );

        DashboardResponse response =
                dashboardService.getDashboard(
                        securityContext
                );

        assertEquals(
                DashboardDataStatus.COMPLETE,
                response.getDataStatus()
        );

        verify(
                alertService
        ).getDashboardMetrics(
                organizationId,
                null
        );

        verify(
                caseService
        ).getDashboardMetrics(
                organizationId,
                null
        );

        verify(
                riskAssessmentService
        ).getAverageLatestRiskScore(
                organizationId,
                null
        );

        verify(
                scenarioActivationService
        ).countActivatedDetectionScenarios(
                organizationId,
                null
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
        ).thenReturn(userId);

        when(
                securityContext.getTenantId()
        ).thenReturn(tenantId);

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
                        "dashboard@example.com"
                )
        );
    }

    private void successfulNonRiskProviders(
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
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(5L);
    }
}
