package com.efs.modules.reporting.controller;

import com.efs.modules.reporting.dto.ReportExportOptionsResponse;
import com.efs.modules.reporting.dto.ReportExportRequest;
import com.efs.modules.reporting.dto.ReportExportResult;
import com.efs.modules.reporting.service.ReportExportServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportExportController {

    private final ReportExportServiceInterface
            reportExportService;

    private final SecurityContextProvider
            securityContextProvider;

    public ReportExportController(
            ReportExportServiceInterface reportExportService,
            SecurityContextProvider securityContextProvider) {

        this.reportExportService =
                reportExportService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @GetMapping("/{reportId}/export-options")
    public ResponseEntity<
            ReportExportOptionsResponse>
    getExportOptions(
            @PathVariable UUID reportId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                reportExportService
                        .getExportOptions(
                                reportId,
                                securityContext
                        )
        );
    }

    @PostMapping("/{reportId}/exports")
    public ResponseEntity<byte[]>
    exportReport(
            @PathVariable UUID reportId,
            @RequestBody(required = false)
            ReportExportRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        ReportExportResult result =
                reportExportService
                        .exportReport(
                                reportId,
                                request,
                                securityContext
                        );

        byte[] content =
                result.getContent();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        result.getMediaType()
                )
        );

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(
                                result.getFileName()
                        )
                        .build()
        );

        headers.setContentLength(
                content.length
        );

        return ResponseEntity.ok()
                .headers(
                        headers
                )
                .body(
                        content
                );
    }
}