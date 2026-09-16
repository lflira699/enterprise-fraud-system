package com.efs.modules.reporting.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.audit.service.AuditExportServiceInterface;
import com.efs.modules.casemanagement.service.CaseServiceInterface;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.modules.reporting.entity.GeneratedReport;
import com.efs.modules.reporting.export.CsvReportExportRenderer;
import com.efs.modules.reporting.export.PdfReportExportRenderer;
import com.efs.modules.reporting.export.ReportExportDocumentAssembler;
import com.efs.modules.reporting.export.ReportExportRenderer;
import com.efs.modules.reporting.export.XlsxReportExportRenderer;
import com.efs.modules.reporting.repository.GeneratedReportRepository;
import com.efs.modules.reporting.service.ReportExportService;
import com.efs.modules.reporting.service.ReportService;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportExportEndToEndIsolationTest {

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
    private AuditExportServiceInterface
            auditExportService;

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

        List<ReportExportRenderer> renderers =
                List.of(
                        new PdfReportExportRenderer(),
                        new XlsxReportExportRenderer(),
                        new CsvReportExportRenderer()
                );

        ReportExportService reportExportService =
                new ReportExportService(
                        reportService,
                        new ReportExportDocumentAssembler(),
                        renderers,
                        auditExportService,
                        auditEventService
                );

        ReportExportController controller =
                new ReportExportController(
                        reportExportService,
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
    void exportOptionsShouldTraverseReportViewAndTenantIsolation()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                investigationReport(
                        organizationId,
                        tenantId,
                        userId
                );

        authorize(
                userId,
                organizationId,
                tenantId,
                "case.view"
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/reports/{reportId}/export-options",
                                report.getReportId()
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.reportId")
                                .value(
                                        report.getReportId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.reportCode")
                                .value(
                                        "INVESTIGATION_CASES"
                                )
                )
                .andExpect(
                        jsonPath("$.formats[0]")
                                .value("PDF")
                )
                .andExpect(
                        jsonPath("$.formats[1]")
                                .value("XLSX")
                )
                .andExpect(
                        jsonPath("$.formats[2]")
                                .value("CSV")
                );

        verify(
                generatedReportRepository
        ).findByReportIdAndOrganizationIdAndTenantId(
                report.getReportId(),
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

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void csvExportShouldUsePersistedSnapshotWithoutSourceRequery()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                investigationReport(
                        organizationId,
                        tenantId,
                        userId
                );

        authorize(
                userId,
                organizationId,
                tenantId,
                "case.view"
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/reports/{reportId}/exports",
                                        report.getReportId()
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                """
                                                {
                                                  "format": "CSV"
                                                }
                                                """
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                content().contentType(
                                        "text/csv;charset=UTF-8"
                                )
                        )
                        .andReturn();

        String csv =
                new String(
                        result.getResponse()
                                .getContentAsByteArray(),
                        StandardCharsets.UTF_8
                );

        assertTrue(
                csv.contains(
                        "caseNumber"
                )
        );

        assertTrue(
                csv.contains(
                        "CASE-UC042-001"
                )
        );

        assertTrue(
                result.getResponse()
                        .getHeader(
                                HttpHeaders.CONTENT_DISPOSITION
                        )
                        .contains(
                                "attachment"
                        )
        );

        assertTrue(
                result.getResponse()
                        .getHeader(
                                HttpHeaders.CONTENT_DISPOSITION
                        )
                        .contains(
                                ".csv"
                        )
        );

        verify(
                auditExportService
        ).createAuditExport(
                any()
        );

        verify(
                auditEventService
        ).createAuditEvent(
                any()
        );

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void pdfExportShouldGenerateRealPdfFromOperationalSnapshot()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                operationalReport(
                        organizationId,
                        tenantId,
                        userId
                );

        authorize(
                userId,
                organizationId,
                tenantId,
                "dashboard.view"
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/reports/{reportId}/exports",
                                        report.getReportId()
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                """
                                                {
                                                  "format": "PDF"
                                                }
                                                """
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                content().contentType(
                                        MediaType.APPLICATION_PDF
                                )
                        )
                        .andReturn();

        byte[] bytes =
                result.getResponse()
                        .getContentAsByteArray();

        assertTrue(
                bytes.length > 4
        );

        assertEquals(
                "%PDF",
                new String(
                        bytes,
                        0,
                        4,
                        StandardCharsets.US_ASCII
                )
        );

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void xlsxExportShouldGenerateRealWorkbookFromPersistedSnapshot()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                investigationReport(
                        organizationId,
                        tenantId,
                        userId
                );

        authorize(
                userId,
                organizationId,
                tenantId,
                "case.view"
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/reports/{reportId}/exports",
                                        report.getReportId()
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                """
                                                {
                                                  "format": "XLSX"
                                                }
                                                """
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                content().contentType(
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                )
                        )
                        .andReturn();

        byte[] bytes =
                result.getResponse()
                        .getContentAsByteArray();

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(
                                new ByteArrayInputStream(
                                        bytes
                                )
                        )
        ) {

            assertEquals(
                    "Report",
                    workbook.getSheetAt(
                            0
                    ).getSheetName()
            );

            assertTrue(
                    workbook.getSheetAt(
                            0
                    ).getPhysicalNumberOfRows()
                            > 0
            );
        }

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void invalidFormatShouldTraverseToUniformHttp400WithoutSuccessfulExportAudit()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                investigationReport(
                        organizationId,
                        tenantId,
                        userId
                );

        authorize(
                userId,
                organizationId,
                tenantId,
                "case.view"
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports/{reportId}/exports",
                                report.getReportId()
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "uc042-invalid-format"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "format": "JSON"
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
                                        "INVALID_REPORT_EXPORT_FORMAT"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc042-invalid-format"
                                )
                );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void missingCurrentSourcePermissionShouldRejectExport()
            throws Exception {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        GeneratedReport report =
                investigationReport(
                        organizationId,
                        tenantId,
                        userId
                );

        when(
                securityContext.hasPermission(
                        "report.export"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        "report.view"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        "case.view"
                )
        ).thenReturn(false);

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
                        "uc042-source-denied@example.com"
                )
        );

        stubTenantReport(
                report,
                organizationId,
                tenantId
        );

        ServletException servletException =
                assertThrows(
                        ServletException.class,
                        () ->
                                mockMvc.perform(
                                        post(
                                                "/api/v1/reports/{reportId}/exports",
                                                report.getReportId()
                                        )
                                                .contentType(
                                                        MediaType.APPLICATION_JSON
                                                )
                                                .content(
                                                        """
                                                        {
                                                          "format": "CSV"
                                                        }
                                                        """
                                                )
                                )
                );

        AccessDeniedException accessDenied =
                assertInstanceOf(
                        AccessDeniedException.class,
                        servletException.getCause()
                );

        assertTrue(
                accessDenied.getMessage()
                        .contains(
                                "case.view"
                        )
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    @Test
    void missingReportViewPermissionShouldRejectBeforeRepositoryLookup()
            throws Exception {

        UUID reportId =
                UUID.randomUUID();

        when(
                securityContext.hasPermission(
                        "report.export"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        "report.view"
                )
        ).thenReturn(false);

        ServletException servletException =
                assertThrows(
                        ServletException.class,
                        () ->
                                mockMvc.perform(
                                        post(
                                                "/api/v1/reports/{reportId}/exports",
                                                reportId
                                        )
                                                .contentType(
                                                        MediaType.APPLICATION_JSON
                                                )
                                                .content(
                                                        """
                                                        {
                                                          "format": "CSV"
                                                        }
                                                        """
                                                )
                                )
                );

        AccessDeniedException accessDenied =
                assertInstanceOf(
                        AccessDeniedException.class,
                        servletException.getCause()
                );

        assertTrue(
                accessDenied.getMessage()
                        .contains(
                                "report.view"
                        )
        );

        verify(
                generatedReportRepository,
                never()
        ).findByReportIdAndOrganizationIdAndTenantId(
                any(UUID.class),
                any(UUID.class),
                any(UUID.class)
        );

        verify(
                generatedReportRepository,
                never()
        ).findByReportIdAndOrganizationId(
                any(UUID.class),
                any(UUID.class)
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verifyNoInteractions(
                dashboardService,
                caseService
        );
    }

    private void authorize(
            UUID userId,
            UUID organizationId,
            UUID tenantId,
            String sourcePermission) {

        when(
                securityContext.hasPermission(
                        "report.export"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        "report.view"
                )
        ).thenReturn(true);

        when(
                securityContext.hasPermission(
                        sourcePermission
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
                        "uc042-export@example.com"
                )
        );
    }

    private void stubTenantReport(
            GeneratedReport report,
            UUID organizationId,
            UUID tenantId) {

        when(
                generatedReportRepository
                        .findByReportIdAndOrganizationIdAndTenantId(
                                report.getReportId(),
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        report
                )
        );
    }

    private GeneratedReport investigationReport(
            UUID organizationId,
            UUID tenantId,
            UUID generatedBy) {

        Map<String, Object> caseSnapshot =
                new LinkedHashMap<>();

        caseSnapshot.put(
                "caseId",
                UUID.randomUUID()
        );

        caseSnapshot.put(
                "caseNumber",
                "CASE-UC042-001"
        );

        caseSnapshot.put(
                "organizationId",
                organizationId
        );

        caseSnapshot.put(
                "transactionId",
                null
        );

        caseSnapshot.put(
                "customerId",
                null
        );

        caseSnapshot.put(
                "caseType",
                "FRAUD"
        );

        caseSnapshot.put(
                "category",
                "ACCOUNT"
        );

        caseSnapshot.put(
                "severity",
                "HIGH"
        );

        caseSnapshot.put(
                "priority",
                "HIGH"
        );

        caseSnapshot.put(
                "currentStatus",
                "OPEN"
        );

        caseSnapshot.put(
                "assignedTeam",
                null
        );

        caseSnapshot.put(
                "assignedUser",
                null
        );

        caseSnapshot.put(
                "createdAt",
                LocalDateTime.of(
                        2026,
                        9,
                        15,
                        12,
                        0
                )
        );

        caseSnapshot.put(
                "updatedAt",
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        0
                )
        );

        caseSnapshot.put(
                "dueDate",
                null
        );

        caseSnapshot.put(
                "closedAt",
                null
        );

        caseSnapshot.put(
                "tenantId",
                tenantId
        );

        Map<String, Object> content =
                new LinkedHashMap<>();

        content.put(
                "recordCount",
                1
        );

        content.put(
                "cases",
                List.of(
                        caseSnapshot
                )
        );

        GeneratedReport report =
                new GeneratedReport(
                        organizationId,
                        tenantId,
                        "INVESTIGATION_CASES",
                        Map.of(
                                "priority",
                                "HIGH"
                        ),
                        content,
                        generatedBy,
                        LocalDateTime.of(
                                2026,
                                9,
                                16,
                                6,
                                0
                        )
                );

        report.setReportId(
                UUID.randomUUID()
        );

        return report;
    }

    private GeneratedReport operationalReport(
            UUID organizationId,
            UUID tenantId,
            UUID generatedBy) {

        Map<String, Object> effectiveFilters =
                new LinkedHashMap<>();

        effectiveFilters.put(
                "tenantId",
                tenantId
        );

        effectiveFilters.put(
                "components",
                List.of(
                        "ALERT",
                        "CASE",
                        "RISK",
                        "DETECTION"
                )
        );

        effectiveFilters.put(
                "filterSource",
                "DEFAULT"
        );

        Map<String, Object> content =
                new LinkedHashMap<>();

        content.put(
                "generatedAt",
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        0
                )
        );

        content.put(
                "dataStatus",
                "COMPLETE"
        );

        content.put(
                "criticalAlerts",
                2L
        );

        content.put(
                "openAlerts",
                5L
        );

        content.put(
                "openCases",
                4L
        );

        content.put(
                "closedCases",
                8L
        );

        content.put(
                "averageRiskScore",
                new BigDecimal(
                        "42.5000"
                )
        );

        content.put(
                "activatedDetectionScenarios",
                6L
        );

        content.put(
                "unavailableComponents",
                List.of()
        );

        content.put(
                "effectiveFilters",
                effectiveFilters
        );

        GeneratedReport report =
                new GeneratedReport(
                        organizationId,
                        tenantId,
                        "OPERATIONAL_SUMMARY",
                        Map.of(
                                "tenantId",
                                tenantId
                        ),
                        content,
                        generatedBy,
                        LocalDateTime.of(
                                2026,
                                9,
                                16,
                                6,
                                0
                        )
                );

        report.setReportId(
                UUID.randomUUID()
        );

        return report;
    }
}