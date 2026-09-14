package com.efs.modules.reporting.service;

import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportDefinitionResponse;
import com.efs.modules.reporting.dto.ReportGenerationRequest;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface ReportServiceInterface {

    List<ReportDefinitionResponse>
    getAvailableDefinitions(
            SecurityContext securityContext
    );

    GeneratedReportResponse generateReport(
            ReportGenerationRequest request,
            SecurityContext securityContext
    );

    GeneratedReportResponse getReport(
            UUID reportId,
            SecurityContext securityContext
    );
}
