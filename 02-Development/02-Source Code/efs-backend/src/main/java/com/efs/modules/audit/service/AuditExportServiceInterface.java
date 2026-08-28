package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.dto.AuditExportResponse;

import java.util.List;
import java.util.UUID;

public interface AuditExportServiceInterface {

    AuditExportResponse createAuditExport(
            AuditExportRequest request
    );

    AuditExportResponse getAuditExportById(
            UUID exportId
    );

    List<AuditExportResponse> getAuditExportsByUserId(
            UUID userId
    );

    List<AuditExportResponse> getAuditExportsByOrganizationId(
            UUID organizationId
    );

    List<AuditExportResponse> getAuditExportsByExportType(
            String exportType
    );

    List<AuditExportResponse> getAuditExportsByResource(
            String resourceType,
            UUID resourceId
    );
}