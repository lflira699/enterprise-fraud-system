package com.efs.modules.dashboard.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.alert.service.AlertServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseDashboardMetricsResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.service.DashboardService;
import com.efs.modules.detection.service.ScenarioActivationServiceInterface;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardEndToEndIsolationTest {

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
    private SecurityContextProvider
            securityContextProvider;

    @Mock
    private SecurityContext
            securityContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        DashboardService dashboardService =
                new DashboardService(
                        userAccountLookupService,
                        alertService,
                        caseService,
                        riskAssessmentService,
                        scenarioActivationService,
                        auditEventService
                );

        DashboardController controller =
                new DashboardController(
                        dashboardService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                controller
                        )
                        .setControllerAdvice(
                                new GlobalExceptionHandler()
                        )
                        .build();
    }

    @Test
    void shouldTraverseHttpControllerServiceScopeAndAllOwnerContracts()
            throws Exception {

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

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.generatedAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.dataStatus")
                                .value("COMPLETE")
                )
                .andExpect(
                        jsonPath("$.criticalAlerts")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.openAlerts")
                                .value(8)
                )
                .andExpect(
                        jsonPath("$.openCases")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.closedCases")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.averageRiskScore")
                                .value(64.50)
                )
                .andExpect(
                        jsonPath("$.activatedDetectionScenarios")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.unavailableComponents")
                                .isEmpty()
                );

        verify(
                userAccountLookupService
        ).getAuthorizedUser(
                userId
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
    void oneOwnerFailureShouldRemainHttp200AndExposePartialState()
            throws Exception {

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
                        "forced case owner failure"
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

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.dataStatus")
                                .value("PARTIAL")
                )
                .andExpect(
                        jsonPath("$.criticalAlerts")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.openAlerts")
                                .value(6)
                )
                .andExpect(
                        jsonPath("$.openCases")
                                .value(
                                        nullValue()
                                )
                )
                .andExpect(
                        jsonPath("$.closedCases")
                                .value(
                                        nullValue()
                                )
                )
                .andExpect(
                        jsonPath("$.averageRiskScore")
                                .value(41.00)
                )
                .andExpect(
                        jsonPath("$.activatedDetectionScenarios")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.unavailableComponents[0]")
                                .value("CASE")
                );
    }

    @Test
    void allOwnerFailuresShouldTraverseToUniformHttp503()
            throws Exception {

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
                new IllegalStateException(
                        "forced alert owner failure"
                )
        );

        when(
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced case owner failure"
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenThrow(
                new IllegalStateException(
                        "forced risk owner failure"
                )
        );

        when(
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        ).thenThrow(
                new IllegalStateException(
                        "forced detection owner failure"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "uc039-e2e-correlation"
                                )
                )
                .andExpect(
                        status().isServiceUnavailable()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(503)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "DASHBOARD_DATA_UNAVAILABLE"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Dashboard data is unavailable"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc039-e2e-correlation"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/dashboard"
                                )
                );
    }

    private void authorize(
            UUID userId,
            UUID organizationId,
            UUID tenantId) {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

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
                        "dashboard-e2e@example.com"
                )
        );
    }
}
