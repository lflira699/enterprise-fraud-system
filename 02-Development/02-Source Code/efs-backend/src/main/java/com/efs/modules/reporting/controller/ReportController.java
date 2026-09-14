package com.efs.modules.reporting.controller;

import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportDefinitionResponse;
import com.efs.modules.reporting.dto.ReportGenerationRequest;
import com.efs.modules.reporting.service.ReportServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportServiceInterface
            reportService;

    private final SecurityContextProvider
            securityContextProvider;

    public ReportController(
            ReportServiceInterface reportService,
            SecurityContextProvider securityContextProvider) {

        this.reportService =
                reportService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @GetMapping("/definitions")
    public ResponseEntity<
            List<ReportDefinitionResponse>>
    getDefinitions() {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                reportService
                        .getAvailableDefinitions(
                                securityContext
                        )
        );
    }

    @PostMapping
    public ResponseEntity<GeneratedReportResponse>
    generateReport(
            @RequestBody
            ReportGenerationRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        reportService
                                .generateReport(
                                        request,
                                        securityContext
                                )
                );
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<GeneratedReportResponse>
    getReport(
            @PathVariable UUID reportId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        );
    }
}
