package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditHistoryRequest;
import com.efs.modules.audit.dto.AuditHistoryResponse;
import com.efs.modules.audit.entity.AuditHistory;
import org.springframework.stereotype.Component;

@Component
public class AuditHistoryMapper {

    public AuditHistory toEntity(
            AuditHistoryRequest request) {

        AuditHistory auditHistory =
                new AuditHistory();

        auditHistory.setSourceTable(
                request.getSourceTable()
        );

        auditHistory.setSourceRecordId(
                request.getSourceRecordId()
        );

        auditHistory.setOrganizationId(
                request.getOrganizationId()
        );

        auditHistory.setTenantId(
                request.getTenantId()
        );

        auditHistory.setCorrelationId(
                request.getCorrelationId()
        );

        auditHistory.setEventTimestamp(
                request.getEventTimestamp()
        );

        auditHistory.setArchivedPayload(
                request.getArchivedPayload()
        );

        auditHistory.setChecksumSha256(
                request.getChecksumSha256()
        );

        auditHistory.setRetentionUntil(
                request.getRetentionUntil()
        );

        return auditHistory;
    }

    public AuditHistoryResponse toResponse(
            AuditHistory auditHistory) {

        AuditHistoryResponse response =
                new AuditHistoryResponse();

        response.setHistoryId(
                auditHistory.getHistoryId()
        );

        response.setSourceTable(
                auditHistory.getSourceTable()
        );

        response.setSourceRecordId(
                auditHistory.getSourceRecordId()
        );

        response.setOrganizationId(
                auditHistory.getOrganizationId()
        );

        response.setTenantId(
                auditHistory.getTenantId()
        );

        response.setCorrelationId(
                auditHistory.getCorrelationId()
        );

        response.setEventTimestamp(
                auditHistory.getEventTimestamp()
        );

        response.setArchivedPayload(
                auditHistory.getArchivedPayload()
        );

        response.setChecksumSha256(
                auditHistory.getChecksumSha256()
        );

        response.setArchivedAt(
                auditHistory.getArchivedAt()
        );

        response.setRetentionUntil(
                auditHistory.getRetentionUntil()
        );

        return response;
    }
}