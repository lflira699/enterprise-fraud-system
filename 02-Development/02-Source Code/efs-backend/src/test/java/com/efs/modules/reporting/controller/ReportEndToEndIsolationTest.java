package com.efs.modules.reporting.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardEffectiveFilters;
import com.efs.modules.dashboard.dto.DashboardFilterSource;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.modules.reporting.entity.GeneratedReport;
import com.efs.modules.reporting.repository.GeneratedReportRepository;
import com.efs.modules.reporting.service.ReportService;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportEndToEndIsolationTest {

    @Mock
    private GeneratedReportRepository
            generatedReportRepository;

    @Mock
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Mock
    private SystemConfigurationServiceInterface
            systemConfigurationService;

    @Mock
    private DashboardServiceInterface
            dashboardService;

    @Mock
    private CaseServiceInterface
            caseService;

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

        ObjectMapper objectMapper =
                new ObjectMapper()
                        .findAndRegisterModules();

        ReportService reportService =
                new ReportService(
                        generatedReportRepository,
                        userAccountLookupService,
                        systemConfigurationService,
                        dashboardService,
                        caseService,
                        auditEventService,
                        objectMapper
                );

        ReportController controller =
                new ReportController(
                        reportService,
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

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @Test
    void operationalSummaryShouldTraverseHttpToDashboardContractAndPersistSnapshot()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        authorizeGenerate(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        DashboardResponse dashboard =
                new DashboardResponse(
                        LocalDateTime.now(),
                        DashboardDataStatus.COMPLETE,
                        1L,
                        5L,
                        3L,
                        2L,
                        null,
                        4L,
                        List.of(),
                        new DashboardEffectiveFilters(
                                tenantId,
                                List.of(
                                        com.efs.modules.dashboard.dto.DashboardComponent.ALERT,
                                        com.efs.modules.dashboard.dto.DashboardComponent.CASE
                                ),
                                DashboardFilterSource.EXPLICIT
                        )
                );

        when(
                dashboardService.getDashboard(
                        eq(securityContext),
                        any()
                )
        ).thenReturn(
                dashboard
        );

        when(
                generatedReportRepository
                        .saveAndFlush(
                                any(GeneratedReport.class)
                        )
        ).thenAnswer(
                invocation -> {

                    GeneratedReport report =
                            invocation.getArgument(0);

                    report.setReportId(
                            reportId
                    );

                    return report;
                }
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "OPERATIONAL_SUMMARY",
                                          "criteria": {
                                            "components": [
                                              "ALERT",
                                              "CASE"
                                            ]
                                          }
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.reportId")
                                .value(
                                        reportId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.reportCode")
                                .value(
                                        "OPERATIONAL_SUMMARY"
                                )
                )
                .andExpect(
                        jsonPath("$.tenantId")
                                .value(
                                        tenantId.toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.criteria.components[0]"
                        ).value("ALERT")
                )
                .andExpect(
                        jsonPath(
                                "$.criteria.components[1]"
                        ).value("CASE")
                )
                .andExpect(
                        jsonPath(
                                "$.content.openAlerts"
                        ).value(5)
                );

        verify(
                generatedReportRepository
        ).saveAndFlush(
                any(GeneratedReport.class)
        );

        verifyNoInteractions(
                caseService
        );
    }

    @Test
    void investigationCasesShouldTraverseHttpToCaseContract()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        authorizeGenerate(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "case.view"
                )
        ).thenReturn(true);

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of("100")
        );

        CaseResponse caseResponse =
                mock(CaseResponse.class);

        when(
                caseService.searchCases(
                        "OPEN",
                        "HIGH",
                        null,
                        null,
                        0,
                        100,
                        "createdAt",
                        "DESC",
                        securityContext
                )
        ).thenReturn(
                new PageResponse<>(
                        List.of(
                                caseResponse
                        ),
                        0,
                        100,
                        1,
                        1,
                        false,
                        false
                )
        );

        when(
                generatedReportRepository
                        .saveAndFlush(
                                any(GeneratedReport.class)
                        )
        ).thenAnswer(
                invocation -> {

                    GeneratedReport report =
                            invocation.getArgument(0);

                    report.setReportId(
                            reportId
                    );

                    return report;
                }
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "INVESTIGATION_CASES",
                                          "criteria": {
                                            "status": "OPEN",
                                            "priority": "HIGH"
                                          }
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.reportCode")
                                .value(
                                        "INVESTIGATION_CASES"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content.recordCount"
                        ).value(1)
                );

        verify(
                caseService
        ).searchCases(
                "OPEN",
                "HIGH",
                null,
                null,
                0,
                100,
                "createdAt",
                "DESC",
                securityContext
        );

        verifyNoInteractions(
                dashboardService
        );
    }

    @Test
    void invalidCriteriaShouldTraverseToUniformHttp400()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorizeGenerate(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "uc041-invalid-criteria"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "OPERATIONAL_SUMMARY",
                                          "criteria": {
                                            "priority": "HIGH"
                                          }
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "INVALID_REPORT_CRITERIA"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc041-invalid-criteria"
                                )
                );

        verifyNoInteractions(
                dashboardService,
                caseService,
                generatedReportRepository
        );
    }

    @Test
    void noDataShouldTraverseToUniformHttp422()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorizeGenerate(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "case.view"
                )
        ).thenReturn(true);

        when(
                systemConfigurationService
                        .resolveConfigurationValue(
                                "EFS.REPORT.MAX_RECORDS",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of("100")
        );

        when(
                caseService.searchCases(
                        null,
                        null,
                        null,
                        null,
                        0,
                        100,
                        "createdAt",
                        "DESC",
                        securityContext
                )
        ).thenReturn(
                new PageResponse<>(
                        List.of(),
                        0,
                        100,
                        0,
                        0,
                        false,
                        false
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "uc041-no-data-e2e"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "INVESTIGATION_CASES",
                                          "criteria": {}
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "REPORT_NO_DATA"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc041-no-data-e2e"
                                )
                );

        verify(
                generatedReportRepository,
                never()
        ).saveAndFlush(
                any(GeneratedReport.class)
        );
    }

    @Test
    void generatedReportReadShouldTraverseHttpWithTenantIsolation()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        when(
                securityContext.hasPermission(
                        "report.view"
                )
        ).thenReturn(true);

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
                        "report-e2e-view@example.com"
                )
        );

        GeneratedReport report =
                new GeneratedReport(
                        organizationId,
                        tenantId,
                        "OPERATIONAL_SUMMARY",
                        Map.of(),
                        Map.of(
                                "snapshot",
                                "immutable"
                        ),
                        userId,
                        LocalDateTime.now()
                );

        report.setReportId(
                reportId
        );

        when(
                generatedReportRepository
                        .findByReportIdAndOrganizationIdAndTenantId(
                                reportId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(report)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/reports/{reportId}",
                                reportId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.reportId")
                                .value(
                                        reportId.toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content.snapshot"
                        ).value(
                                "immutable"
                        )
                );

        verify(
                generatedReportRepository
        ).findByReportIdAndOrganizationIdAndTenantId(
                reportId,
                organizationId,
                tenantId
        );

        verify(
                generatedReportRepository,
                never()
        ).findByReportIdAndOrganizationId(
                any(UUID.class),
                any(UUID.class)
        );
    }

    private void authorizeGenerate(
            UUID userId,
            UUID organizationId,
            UUID tenantId) {

        when(
                securityContext.hasPermission(
                        "report.generate"
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
                        "report-e2e@example.com"
                )
        );
    }
}
