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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReportExportService
        implements ReportExportServiceInterface {

    private static final String
            REPORT_EXPORT_PERMISSION =
            "report.export";

    private static final String
            REPORT_EXPORT_EVENT =
            "REPORT_EXPORT";

    private static final String
            REPORT_ENTITY_TYPE =
            "REPORT";

    private static final String
            REPORT_EXPORT_ACTION =
            "EXPORT";

    private static final String
            REPORT_SOURCE_COMPONENT =
            "REPORTING";

    private static final Map<String, String>
            SOURCE_PERMISSIONS =
            Map.of(
                    "OPERATIONAL_SUMMARY",
                    "dashboard.view",
                    "INVESTIGATION_CASES",
                    "case.view"
            );

    private final ReportServiceInterface
            reportService;

    private final ReportExportDocumentAssembler
            documentAssembler;

    private final Map<
            ReportExportFormat,
            ReportExportRenderer>
            renderers;

    private final AuditExportServiceInterface
            auditExportService;

    private final AuditEventServiceInterface
            auditEventService;

    public ReportExportService(
            ReportServiceInterface reportService,
            ReportExportDocumentAssembler documentAssembler,
            List<ReportExportRenderer> renderers,
            AuditExportServiceInterface auditExportService,
            AuditEventServiceInterface auditEventService) {

        this.reportService =
                reportService;

        this.documentAssembler =
                documentAssembler;

        this.auditExportService =
                auditExportService;

        this.auditEventService =
                auditEventService;

        EnumMap<
                ReportExportFormat,
                ReportExportRenderer>
                rendererMap =
                new EnumMap<>(
                        ReportExportFormat.class
                );

        for (
                ReportExportRenderer renderer :
                renderers
        ) {

            ReportExportFormat format =
                    Objects.requireNonNull(
                            renderer.getFormat(),
                            "renderer format is required"
                    );

            ReportExportRenderer previous =
                    rendererMap.put(
                            format,
                            renderer
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate report export renderer for "
                                + format
                );
            }
        }

        for (
                ReportExportFormat format :
                ReportExportFormat.values()
        ) {

            if (!rendererMap.containsKey(format)) {
                throw new IllegalStateException(
                        "Missing report export renderer for "
                                + format
                );
            }
        }

        this.renderers =
                Map.copyOf(
                        rendererMap
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportExportOptionsResponse
    getExportOptions(
            UUID reportId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                reportId,
                "reportId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireExportPermission(
                securityContext
        );

        GeneratedReportResponse report =
                reportService.getReport(
                        reportId,
                        securityContext
                );

        requireSourcePermission(
                report,
                securityContext
        );

        return new ReportExportOptionsResponse(
                report.getReportId(),
                report.getReportCode(),
                ReportExportFormat
                        .supportedNames()
        );
    }

    @Override
    @Transactional
    public ReportExportResult exportReport(
            UUID reportId,
            ReportExportRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                reportId,
                "reportId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        if (
                !securityContext.hasPermission(
                        REPORT_EXPORT_PERMISSION
                )
        ) {

            recordRejected(
                    securityContext,
                    null,
                    securityContext.getTenantId(),
                    reportId,
                    null,
                    safeFormat(
                            request
                    ),
                    "MISSING_PERMISSION"
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + REPORT_EXPORT_PERMISSION
            );
        }

        GeneratedReportResponse report =
                null;

        try {

            report =
                    reportService.getReport(
                            reportId,
                            securityContext
                    );

            requireSourcePermission(
                    report,
                    securityContext
            );

            ReportExportFormat format =
                    validateRequestAndFormat(
                            request
                    );

            ReportExportDocument document =
                    documentAssembler.assemble(
                            report
                    );

            ReportExportRenderer renderer =
                    renderers.get(
                            format
                    );

            if (renderer == null) {
                throw exportFailure(
                        "Selected report export renderer is unavailable"
                );
            }

            byte[] content;

            try {
                content =
                        renderer.render(
                                document
                        );
            }
            catch (ReportException exception) {
                throw exception;
            }
            catch (RuntimeException exception) {
                throw exportFailure(
                        "Report file could not be generated",
                        exception
                );
            }

            if (
                    content == null
                            || content.length == 0
            ) {
                throw exportFailure(
                        "Report file could not be generated"
                );
            }

            String fileName =
                    fileName(
                            report,
                            format
                    );

            AuditExportRequest auditExportRequest =
                    new AuditExportRequest();

            auditExportRequest.setUserId(
                    securityContext.getUserId()
            );

            auditExportRequest.setOrganizationId(
                    report.getOrganizationId()
            );

            auditExportRequest.setExportType(
                    REPORT_ENTITY_TYPE
            );

            auditExportRequest.setResourceType(
                    REPORT_ENTITY_TYPE
            );

            auditExportRequest.setResourceId(
                    report.getReportId()
            );

            auditExportRequest.setFileFormat(
                    format.name()
            );

            auditExportRequest.setRecordCount(
                    document.recordCount()
            );

            auditExportService.createAuditExport(
                    auditExportRequest
            );

            recordSuccess(
                    securityContext,
                    report,
                    format,
                    document.recordCount()
            );

            return new ReportExportResult(
                    content,
                    format.getMediaType(),
                    fileName,
                    format.name(),
                    document.recordCount()
            );

        }
        catch (AccessDeniedException exception) {

            recordRejected(
                    securityContext,
                    report == null
                            ? null
                            : report.getOrganizationId(),
                    report == null
                            ? securityContext.getTenantId()
                            : report.getTenantId(),
                    reportId,
                    report == null
                            ? null
                            : report.getReportCode(),
                    safeFormat(
                            request
                    ),
                    "MISSING_PERMISSION"
            );

            throw exception;
        }
        catch (ReportException exception) {

            if (
                    exception.getStatus()
                            .is4xxClientError()
            ) {

                recordRejected(
                        securityContext,
                        report == null
                                ? null
                                : report.getOrganizationId(),
                        report == null
                                ? securityContext.getTenantId()
                                : report.getTenantId(),
                        reportId,
                        report == null
                                ? null
                                : report.getReportCode(),
                        safeFormat(
                                request
                        ),
                        exception.getErrorCode()
                );

            }
            else {

                recordFailure(
                        securityContext,
                        report == null
                                ? null
                                : report.getOrganizationId(),
                        report == null
                                ? securityContext.getTenantId()
                                : report.getTenantId(),
                        reportId,
                        report == null
                                ? null
                                : report.getReportCode(),
                        safeFormat(
                                request
                        ),
                        exception.getErrorCode(),
                        exception
                );
            }

            throw exception;
        }
        catch (RuntimeException exception) {

            recordFailure(
                    securityContext,
                    report == null
                            ? null
                            : report.getOrganizationId(),
                    report == null
                            ? securityContext.getTenantId()
                            : report.getTenantId(),
                    reportId,
                    report == null
                            ? null
                            : report.getReportCode(),
                    safeFormat(
                            request
                    ),
                    "REPORT_EXPORT_FAILED",
                    exception
            );

            throw exportFailure(
                    "Report export failed",
                    exception
            );
        }
    }

    private void requireExportPermission(
            SecurityContext securityContext) {

        if (
                !securityContext.hasPermission(
                        REPORT_EXPORT_PERMISSION
                )
        ) {
            throw new AccessDeniedException(
                    "Missing required permission: "
                            + REPORT_EXPORT_PERMISSION
            );
        }
    }

    private void requireSourcePermission(
            GeneratedReportResponse report,
            SecurityContext securityContext) {

        String sourcePermission =
                SOURCE_PERMISSIONS.get(
                        report.getReportCode()
                );

        if (sourcePermission == null) {
            throw exportFailure(
                    "Generated report type cannot be exported"
            );
        }

        if (
                !securityContext.hasPermission(
                        sourcePermission
                )
        ) {
            throw new AccessDeniedException(
                    "Missing required source permission: "
                            + sourcePermission
            );
        }
    }

    private ReportExportFormat
    validateRequestAndFormat(
            ReportExportRequest request) {

        if (request == null) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_EXPORT_REQUEST",
                    "Report export request is required"
            );
        }

        if (request.hasUnsupportedFields()) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_EXPORT_REQUEST",
                    "Report export request contains unsupported fields"
            );
        }

        if (
                request.getFormat() == null
                        || request.getFormat().isBlank()
        ) {
            throw new ReportException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REPORT_EXPORT_REQUEST",
                    "Report export format is required"
            );
        }

        return ReportExportFormat
                .from(
                        request.getFormat()
                )
                .orElseThrow(
                        () ->
                                new ReportException(
                                        HttpStatus.BAD_REQUEST,
                                        "INVALID_REPORT_EXPORT_FORMAT",
                                        "Report export format must be PDF, XLSX or CSV"
                                )
                );
    }

    private String fileName(
            GeneratedReportResponse report,
            ReportExportFormat format) {

        return "efs-report-"
                + report.getReportCode()
                + "-"
                + report.getReportId()
                + "."
                + format.getExtension();
    }

    private void recordSuccess(
            SecurityContext securityContext,
            GeneratedReportResponse report,
            ReportExportFormat format,
            long recordCount) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        report.getOrganizationId(),
                        report.getTenantId(),
                        report.getReportId(),
                        "SUCCESS"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                report.getReportCode()
        );

        details.put(
                "format",
                format.name()
        );

        details.put(
                "organizationId",
                report.getOrganizationId()
        );

        details.put(
                "tenantId",
                report.getTenantId()
        );

        details.put(
                "recordCount",
                recordCount
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void recordRejected(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID reportId,
            String reportCode,
            String format,
            String reason) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        tenantId,
                        reportId,
                        "REJECTED"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                reportCode
        );

        details.put(
                "format",
                format
        );

        details.put(
                "reason",
                reason
        );

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private void recordFailure(
            SecurityContext securityContext,
            UUID organizationId,
            UUID tenantId,
            UUID reportId,
            String reportCode,
            String format,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                baseAuditRequest(
                        securityContext,
                        organizationId,
                        tenantId,
                        reportId,
                        "FAILURE"
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reportCode",
                reportCode
        );

        details.put(
                "format",
                format
        );

        details.put(
                "reason",
                reason
        );

        details.put(
                "errorType",
                exception.getClass()
                        .getSimpleName()
        );

        details.put(
                "errorMessage",
                exception.getMessage()
        );

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
            UUID tenantId,
            UUID reportId,
            String eventResult) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                REPORT_EXPORT_EVENT
        );

        request.setEntityType(
                REPORT_ENTITY_TYPE
        );

        request.setEntityId(
                reportId
        );

        request.setAction(
                REPORT_EXPORT_ACTION
        );

        request.setSourceComponent(
                REPORT_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        return request;
    }

    private String safeFormat(
            ReportExportRequest request) {

        if (request == null) {
            return null;
        }

        String format =
                request.getFormat();

        if (format == null) {
            return null;
        }

        String normalized =
                format.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return normalized.toUpperCase(
                Locale.ROOT
        );
    }

    private ReportException exportFailure(
            String message) {

        return new ReportException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "REPORT_EXPORT_FAILED",
                message
        );
    }

    private ReportException exportFailure(
            String message,
            RuntimeException exception) {

        return new ReportException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "REPORT_EXPORT_FAILED",
                message
                        + ": "
                        + exception.getClass()
                                .getSimpleName()
        );
    }
}