package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.dto.AuditExportResponse;
import com.efs.modules.audit.entity.AuditExport;
import org.springframework.stereotype.Component;

@Component
public class AuditExportMapper {

    public AuditExport toEntity(
            AuditExportRequest request) {

        AuditExport auditExport =
                new AuditExport();

        auditExport.setUserId(
                request.getUserId()
        );

        auditExport.setOrganizationId(
                request.getOrganizationId()
        );

        auditExport.setExportType(
                request.getExportType()
        );

        auditExport.setResourceType(
                request.getResourceType()
        );

        auditExport.setResourceId(
                request.getResourceId()
        );

        auditExport.setFileFormat(
                request.getFileFormat()
        );

        auditExport.setRecordCount(
                request.getRecordCount()
        );

        auditExport.setExportReason(
                request.getExportReason()
        );

        return auditExport;
    }

    public AuditExportResponse toResponse(
            AuditExport auditExport) {

        AuditExportResponse response =
                new AuditExportResponse();

        response.setExportId(
                auditExport.getExportId()
        );

        response.setUserId(
                auditExport.getUserId()
        );

        response.setOrganizationId(
                auditExport.getOrganizationId()
        );

        response.setExportType(
                auditExport.getExportType()
        );

        response.setResourceType(
                auditExport.getResourceType()
        );

        response.setResourceId(
                auditExport.getResourceId()
        );

        response.setFileFormat(
                auditExport.getFileFormat()
        );

        response.setRecordCount(
                auditExport.getRecordCount()
        );

        response.setExportReason(
                auditExport.getExportReason()
        );

        response.setExportedAt(
                auditExport.getExportedAt()
        );

        return response;
    }
}