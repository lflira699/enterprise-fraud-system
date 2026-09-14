package com.efs.modules.dashboard.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.alert.service.AlertServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardFilterEndToEndIsolationTest {

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
                        systemConfigurationService,
                        tenantOrganizationLookupService,
                        alertService,
                        caseService,
                        riskAssessmentService,
                        scenarioActivationService,
                        auditEventService
                );

        DashboardController dashboardController =
                new DashboardController(
                        dashboardService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                dashboardController
                        )
                        .setControllerAdvice(
                                new GlobalExceptionHandler()
                        )
                        .build();

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @Test
    void explicitComponentsShouldTraverseHttpToSelectedOwnerContracts()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        defaultConfiguration(
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
                        4L,
                        9L
                )
        );

        when(
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                new BigDecimal("67.50")
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "components",
                                        "ALERT,RISK"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.dataStatus")
                                .value("COMPLETE")
                )
                .andExpect(
                        jsonPath("$.criticalAlerts")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.openAlerts")
                                .value(9)
                )
                .andExpect(
                        jsonPath("$.averageRiskScore")
                                .value(67.50)
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.filterSource"
                        ).value(
                                "EXPLICIT"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.tenantId"
                        ).value(
                                tenantId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.components[0]"
                        ).value(
                                "ALERT"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.components[1]"
                        ).value(
                                "RISK"
                        )
                )
                .andExpect(
                        jsonPath("$.unavailableComponents")
                                .isEmpty()
                );

        verify(
                alertService
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

        verifyNoInteractions(
                caseService,
                scenarioActivationService
        );
    }

    @Test
    void filterOptionsShouldTraverseHttpToSystemConfiguration()
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
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.AVAILABLE",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        "ALERT,CASE,RISK"
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
                        "ALERT,CASE"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard/filters"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.tenantSelectionAllowed"
                        ).value(false)
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveTenantId"
                        ).value(
                                tenantId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.availableComponents[0]"
                        ).value("ALERT")
                )
                .andExpect(
                        jsonPath(
                                "$.availableComponents[1]"
                        ).value("CASE")
                )
                .andExpect(
                        jsonPath(
                                "$.availableComponents[2]"
                        ).value("RISK")
                )
                .andExpect(
                        jsonPath(
                                "$.defaultComponents[0]"
                        ).value("ALERT")
                )
                .andExpect(
                        jsonPath(
                                "$.defaultComponents[1]"
                        ).value("CASE")
                );
    }

    @Test
    void organizationScopeShouldTraverseTenantSelection()
            throws Exception {

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

        defaultConfiguration(
                organizationId,
                selectedTenantId
        );

        when(
                alertService.getDashboardMetrics(
                        organizationId,
                        selectedTenantId
                )
        ).thenReturn(
                new AlertDashboardMetricsResponse(
                        2L,
                        3L
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "tenantId",
                                        selectedTenantId.toString()
                                )
                                .param(
                                        "components",
                                        "ALERT"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.tenantId"
                        ).value(
                                selectedTenantId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.components[0]"
                        ).value(
                                "ALERT"
                        )
                );

        verify(
                tenantOrganizationLookupService
        ).getOrganizationIdByTenantId(
                selectedTenantId
        );

        verify(
                alertService
        ).getDashboardMetrics(
                organizationId,
                selectedTenantId
        );
    }

    @Test
    void invalidComponentShouldTraverseToUniformHttp400()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        defaultConfiguration(
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "components",
                                        "UNKNOWN"
                                )
                                .header(
                                        "X-Correlation-ID",
                                        "uc040-e2e-invalid-filter"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "INVALID_DASHBOARD_FILTER"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc040-e2e-invalid-filter"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/dashboard"
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
    void allSelectedOwnerFailureShouldTraverseToHttp503()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        defaultConfiguration(
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
                        "forced risk owner failure"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "components",
                                        "RISK"
                                )
                                .header(
                                        "X-Correlation-ID",
                                        "uc040-e2e-provider-failure"
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
                        jsonPath("$.correlationId")
                                .value(
                                        "uc040-e2e-provider-failure"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/dashboard"
                                )
                );

        verifyNoInteractions(
                alertService,
                caseService,
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
                        "dashboard-filter-e2e@example.com"
                )
        );
    }

    private void defaultConfiguration(
            UUID organizationId,
            UUID tenantId) {

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.AVAILABLE",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.DASHBOARD.COMPONENTS.DEFAULT",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.empty()
        );
    }
}
