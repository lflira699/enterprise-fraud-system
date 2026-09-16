package com.efs.modules.reporting.service;

import com.efs.modules.reporting.dto.ReportExportOptionsResponse;
import com.efs.modules.reporting.dto.ReportExportRequest;
import com.efs.modules.reporting.dto.ReportExportResult;
import com.efs.shared.security.SecurityContext;

import java.util.UUID;

public interface ReportExportServiceInterface {

    ReportExportOptionsResponse
    getExportOptions(
            UUID reportId,
            SecurityContext securityContext
    );

    ReportExportResult exportReport(
            UUID reportId,
            ReportExportRequest request,
            SecurityContext securityContext
    );
}