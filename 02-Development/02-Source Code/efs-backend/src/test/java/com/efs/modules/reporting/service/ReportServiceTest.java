package com.efs.modules.reporting.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.dto.DashboardComponent;
import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardEffectiveFilters;
import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardFilterSource;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportCriteriaRequest;
import com.efs.modules.reporting.dto.ReportDefinitionResponse;
import com.efs.modules.reporting.dto.ReportGenerationRequest;
import com.efs.modules.reporting.entity.GeneratedReport;
import com.efs.modules.reporting.repository.GeneratedReportRepository;
import com.efs.shared.exception.ReportException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

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
    private ObjectMapper
            objectMapper;

    @Mock
    private SecurityContext
            securityContext;

    private ReportService reportService;

    @BeforeEach
    void setUp() {

        reportService =
                new ReportService(
                        generatedReportRepository,
                        userAccountLookupService,
                        systemConfigurationService,
                        dashboardService,
                        caseService,
                        auditEventService,
                        objectMapper
                );
    }

    @Test
    void operationalSummaryShouldUseDashboardPublicContract() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        DashboardResponse dashboardResponse =
                new DashboardResponse(
                        LocalDateTime.now(),
                        DashboardDataStatus.COMPLETE,
                        2L,
                        7L,
                        4L,
                        3L,
                        new BigDecimal("55.00"),
                        6L,
                        List.of(),
                        new DashboardEffectiveFilters(
                                tenantId,
                                List.of(
                                        DashboardComponent.ALERT,
                                        DashboardComponent.CASE
                                ),
                                DashboardFilterSource.EXPLICIT
                        )
                );

        when(
                dashboardService.getDashboard(
                        eq(securityContext),
                        any(DashboardFilterCriteria.class)
                )
        ).thenReturn(
                dashboardResponse
        );

        when(
                objectMapper.convertValue(
                        eq(dashboardResponse),
                        any(TypeReference.class)
                )
        ).thenReturn(
                Map.of(
                        "openAlerts",
                        7L
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

        ReportCriteriaRequest criteria =
                new ReportCriteriaRequest();

        criteria.setComponents(
                List.of(
                        "ALERT",
                        "CASE"
                )
        );

        ReportGenerationRequest request =
                request(
                        "OPERATIONAL_SUMMARY",
                        criteria
                );

        GeneratedReportResponse response =
                reportService.generateReport(
                        request,
                        securityContext
                );

        assertEquals(
                reportId,
                response.getReportId()
        );

        assertEquals(
                "OPERATIONAL_SUMMARY",
                response.getReportCode()
        );

        assertEquals(
                tenantId,
                response.getTenantId()
        );

        assertEquals(
                List.of(
                        "ALERT",
                        "CASE"
                ),
                response.getCriteria()
                        .get("components")
        );

        verifyNoInteractions(
                caseService
        );

        verify(
                auditEventService
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );
    }

    @Test
    void investigationCasesShouldUseCasePublicContract() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        authorize(
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

        PageResponse<CaseResponse> page =
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
                );

        when(
                caseService.searchCases(
                        "OPEN",
                        "HIGH",
                        null,
                        "FRAUD",
                        0,
                        100,
                        "createdAt",
                        "DESC",
                        securityContext
                )
        ).thenReturn(
                page
        );

        when(
                objectMapper.convertValue(
                        eq(caseResponse),
                        any(TypeReference.class)
                )
        ).thenReturn(
                Map.of(
                        "caseId",
                        UUID.randomUUID()
                                .toString()
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

        ReportCriteriaRequest criteria =
                new ReportCriteriaRequest();

        criteria.setStatus(
                "OPEN"
        );

        criteria.setPriority(
                "HIGH"
        );

        criteria.setAssignedTeam(
                "FRAUD"
        );

        GeneratedReportResponse response =
                reportService.generateReport(
                        request(
                                "INVESTIGATION_CASES",
                                criteria
                        ),
                        securityContext
                );

        assertEquals(
                "INVESTIGATION_CASES",
                response.getReportCode()
        );

        assertEquals(
                1,
                response.getContent()
                        .get("recordCount")
        );

        verifyNoInteractions(
                dashboardService
        );
    }

    @Test
    void operationalSummaryShouldRejectCaseCriteria() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        ReportCriteriaRequest criteria =
                new ReportCriteriaRequest();

        criteria.setPriority(
                "HIGH"
        );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                reportService.generateReport(
                                        request(
                                                "OPERATIONAL_SUMMARY",
                                                criteria
                                        ),
                                        securityContext
                                )
                );

        assertEquals(
                "INVALID_REPORT_CRITERIA",
                exception.getErrorCode()
        );

        verifyNoInteractions(
                dashboardService,
                caseService,
                generatedReportRepository
        );
    }

    @Test
    void investigationCasesShouldRejectNoData() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
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
                Optional.empty()
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

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                reportService.generateReport(
                                        request(
                                                "INVESTIGATION_CASES",
                                                new ReportCriteriaRequest()
                                        ),
                                        securityContext
                                )
                );

        assertEquals(
                "REPORT_NO_DATA",
                exception.getErrorCode()
        );

        verify(
                generatedReportRepository,
                never()
        ).saveAndFlush(
                any(GeneratedReport.class)
        );
    }

    @Test
    void investigationCasesShouldRejectConfiguredLimitExceeded() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
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
                        101,
                        2,
                        true,
                        false
                )
        );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                reportService.generateReport(
                                        request(
                                                "INVESTIGATION_CASES",
                                                new ReportCriteriaRequest()
                                        ),
                                        securityContext
                                )
                );

        assertEquals(
                "REPORT_RESULT_LIMIT_EXCEEDED",
                exception.getErrorCode()
        );

        verify(
                generatedReportRepository,
                never()
        ).saveAndFlush(
                any(GeneratedReport.class)
        );
    }

    @Test
    void definitionsShouldExposeOnlyAuthorizedSources() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        "case.view"
                )
        ).thenReturn(false);

        List<ReportDefinitionResponse> definitions =
                reportService
                        .getAvailableDefinitions(
                                securityContext
                        );

        assertEquals(
                1,
                definitions.size()
        );

        assertEquals(
                "OPERATIONAL_SUMMARY",
                definitions.get(0)
                        .getReportCode()
        );
    }

    @Test
    void missingSourcePermissionShouldRejectGeneration() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        authorize(
                userId,
                organizationId,
                tenantId
        );

        when(
                securityContext.hasPermission(
                        "dashboard.view"
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        reportService.generateReport(
                                request(
                                        "OPERATIONAL_SUMMARY",
                                        new ReportCriteriaRequest()
                                ),
                                securityContext
                        )
        );

        verifyNoInteractions(
                dashboardService,
                caseService,
                generatedReportRepository
        );
    }

    @Test
    void tenantScopedReportReadShouldUseExactTenantScope() {

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
                        "report-view@example.com"
                )
        );

        GeneratedReport report =
                generatedReport(
                        reportId,
                        organizationId,
                        tenantId,
                        userId
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

        GeneratedReportResponse response =
                reportService.getReport(
                        reportId,
                        securityContext
                );

        assertEquals(
                reportId,
                response.getReportId()
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

    @Test
    void organizationScopedReportReadShouldUseOrganizationScope() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        when(
                securityContext.hasPermission(
                        "report.view"
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
                null
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
                        null,
                        "report-org-view@example.com"
                )
        );

        GeneratedReport report =
                generatedReport(
                        reportId,
                        organizationId,
                        UUID.randomUUID(),
                        userId
                );

        when(
                generatedReportRepository
                        .findByReportIdAndOrganizationId(
                                reportId,
                                organizationId
                        )
        ).thenReturn(
                Optional.of(report)
        );

        reportService.getReport(
                reportId,
                securityContext
        );

        verify(
                generatedReportRepository
        ).findByReportIdAndOrganizationId(
                reportId,
                organizationId
        );
    }

    private void authorize(
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
                        "report-generate@example.com"
                )
        );
    }

    private ReportGenerationRequest request(
            String reportCode,
            ReportCriteriaRequest criteria) {

        ReportGenerationRequest request =
                new ReportGenerationRequest();

        request.setReportCode(
                reportCode
        );

        request.setCriteria(
                criteria
        );

        return request;
    }

    private GeneratedReport generatedReport(
            UUID reportId,
            UUID organizationId,
            UUID tenantId,
            UUID generatedBy) {

        GeneratedReport report =
                new GeneratedReport(
                        organizationId,
                        tenantId,
                        "OPERATIONAL_SUMMARY",
                        Map.of(),
                        Map.of(
                                "value",
                                "snapshot"
                        ),
                        generatedBy,
                        LocalDateTime.now()
                );

        report.setReportId(
                reportId
        );

        return report;
    }
}
