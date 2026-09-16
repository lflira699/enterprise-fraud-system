package com.efs.modules.reporting.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.audit.service.AuditExportServiceInterface;
import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportExportOptionsResponse;
import com.efs.modules.reporting.dto.ReportExportRequest;
import com.efs.modules.reporting.dto.ReportExportResult;
import com.efs.modules.reporting.export.ReportExportDocument;
import com.efs.modules.reporting.export.ReportExportDocumentAssembler;
import com.efs.modules.reporting.export.ReportExportFormat;
import com.efs.modules.reporting.export.ReportExportRenderer;
import com.efs.shared.exception.ReportException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportExportServiceTest {

    private ReportServiceInterface reportService;

    private ReportExportDocumentAssembler assembler;

    private AuditExportServiceInterface auditExportService;

    private AuditEventServiceInterface auditEventService;

    private ReportExportRenderer pdfRenderer;

    private ReportExportRenderer xlsxRenderer;

    private ReportExportRenderer csvRenderer;

    private ReportExportService service;

    @BeforeEach
    void setUp() {

        reportService =
                mock(
                        ReportServiceInterface.class
                );

        assembler =
                mock(
                        ReportExportDocumentAssembler.class
                );

        auditExportService =
                mock(
                        AuditExportServiceInterface.class
                );

        auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        pdfRenderer =
                mock(
                        ReportExportRenderer.class
                );

        xlsxRenderer =
                mock(
                        ReportExportRenderer.class
                );

        csvRenderer =
                mock(
                        ReportExportRenderer.class
                );

        when(
                pdfRenderer.getFormat()
        ).thenReturn(
                ReportExportFormat.PDF
        );

        when(
                xlsxRenderer.getFormat()
        ).thenReturn(
                ReportExportFormat.XLSX
        );

        when(
                csvRenderer.getFormat()
        ).thenReturn(
                ReportExportFormat.CSV
        );

        service =
                new ReportExportService(
                        reportService,
                        assembler,
                        List.of(
                                pdfRenderer,
                                xlsxRenderer,
                                csvRenderer
                        ),
                        auditExportService,
                        auditEventService
                );
    }

    @Test
    void returnsExportOptionsForAuthorizedReport() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "OPERATIONAL_SUMMARY"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view",
                                "dashboard.view"
                        )
                );

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        ReportExportOptionsResponse response =
                service.getExportOptions(
                        reportId,
                        securityContext
                );

        assertEquals(
                reportId,
                response.getReportId()
        );

        assertEquals(
                List.of(
                        "PDF",
                        "XLSX",
                        "CSV"
                ),
                response.getFormats()
        );
    }

    @Test
    void exportsPersistedSnapshotAndAuditsSuccess() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "INVESTIGATION_CASES"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view",
                                "case.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "CSV"
        );

        ReportExportDocument document =
                new ReportExportDocument(
                        reportId,
                        "INVESTIGATION_CASES",
                        report.getGeneratedAt(),
                        Map.of(),
                        2L,
                        List.of(
                                "caseNumber"
                        ),
                        List.of(
                                Map.of(
                                        "caseNumber",
                                        "CASE-1"
                                ),
                                Map.of(
                                        "caseNumber",
                                        "CASE-2"
                                )
                        )
                );

        byte[] bytes =
                "csv-content".getBytes();

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        when(
                assembler.assemble(
                        report
                )
        ).thenReturn(
                document
        );

        when(
                csvRenderer.render(
                        document
                )
        ).thenReturn(
                bytes
        );

        ReportExportResult result =
                service.exportReport(
                        reportId,
                        request,
                        securityContext
                );

        assertArrayEquals(
                bytes,
                result.getContent()
        );

        assertEquals(
                "CSV",
                result.getFormat()
        );

        assertEquals(
                2L,
                result.getRecordCount()
        );

        assertEquals(
                "efs-report-INVESTIGATION_CASES-"
                        + reportId
                        + ".csv",
                result.getFileName()
        );

        ArgumentCaptor<AuditExportRequest>
                auditExportCaptor =
                ArgumentCaptor.forClass(
                        AuditExportRequest.class
                );

        verify(
                auditExportService
        ).createAuditExport(
                auditExportCaptor.capture()
        );

        AuditExportRequest auditExport =
                auditExportCaptor.getValue();

        assertEquals(
                "REPORT",
                auditExport.getExportType()
        );

        assertEquals(
                "REPORT",
                auditExport.getResourceType()
        );

        assertEquals(
                reportId,
                auditExport.getResourceId()
        );

        assertEquals(
                "CSV",
                auditExport.getFileFormat()
        );

        assertEquals(
                2L,
                auditExport.getRecordCount()
        );

        ArgumentCaptor<AuditEventRequest>
                auditEventCaptor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                auditEventCaptor.capture()
        );

        AuditEventRequest auditEvent =
                auditEventCaptor.getValue();

        assertEquals(
                "REPORT_EXPORT",
                auditEvent.getEventType()
        );

        assertEquals(
                "SUCCESS",
                auditEvent.getEventResult()
        );

        assertEquals(
                "REPORTING",
                auditEvent.getSourceComponent()
        );
    }

    @Test
    void rejectsMissingExportPermissionBeforeReportLookup() {

        UUID reportId =
                UUID.randomUUID();

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.view",
                                "case.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "CSV"
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.exportReport(
                                reportId,
                                request,
                                securityContext
                        )
        );

        verify(
                reportService,
                never()
        ).getReport(
                any(),
                any()
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );
    }

    @Test
    void rejectsMissingCurrentSourcePermission() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "INVESTIGATION_CASES"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "CSV"
        );

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.exportReport(
                                reportId,
                                request,
                                securityContext
                        )
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );
    }

    @Test
    void rejectsUnsupportedFormat() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "OPERATIONAL_SUMMARY"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view",
                                "dashboard.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "JSON"
        );

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                service.exportReport(
                                        reportId,
                                        request,
                                        securityContext
                                )
                );

        assertEquals(
                "INVALID_REPORT_EXPORT_FORMAT",
                exception.getErrorCode()
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );
    }

    @Test
    void missingFormatShouldRejectAsInvalidRequest() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "OPERATIONAL_SUMMARY"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view",
                                "dashboard.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                service.exportReport(
                                        reportId,
                                        request,
                                        securityContext
                                )
                );

        assertEquals(
                "INVALID_REPORT_EXPORT_REQUEST",
                exception.getErrorCode()
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                any()
        );
    }

    @Test
    void blankFormatShouldRejectAsInvalidRequest() {

        UUID reportId =
                UUID.randomUUID();

        GeneratedReportResponse report =
                report(
                        reportId,
                        "INVESTIGATION_CASES"
                );

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.export",
                                "report.view",
                                "case.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "   "
        );

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                report
        );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                service.exportReport(
                                        reportId,
                                        request,
                                        securityContext
                                )
                );

        assertEquals(
                "INVALID_REPORT_EXPORT_REQUEST",
                exception.getErrorCode()
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                any()
        );
    }

    @Test
    void missingExportPermissionShouldUseRequiresNewRejectedAudit() {

        UUID reportId =
                UUID.randomUUID();

        SecurityContext securityContext =
                context(
                        Set.of(
                                "report.view",
                                "case.view"
                        )
                );

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "CSV"
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.exportReport(
                                reportId,
                                request,
                                securityContext
                        )
        );

        verify(
                reportService,
                never()
        ).getReport(
                any(),
                any()
        );

        verify(
                auditExportService,
                never()
        ).createAuditExport(
                any()
        );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                any()
        );
    }
    private GeneratedReportResponse report(
            UUID reportId,
            String reportCode) {

        return new GeneratedReportResponse(
                reportId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                reportCode,
                Map.of(),
                Map.of(),
                UUID.randomUUID(),
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        15
                )
        );
    }

    private SecurityContext context(
            Set<String> permissions) {

        return new SecurityContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(),
                permissions,
                Set.of()
        );
    }
}