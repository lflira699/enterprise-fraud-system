package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditHistoryRequest;
import com.efs.modules.audit.dto.AuditHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface AuditHistoryServiceInterface {

    AuditHistoryResponse createAuditHistory(
            AuditHistoryRequest request
    );

    AuditHistoryResponse getAuditHistoryById(
            UUID historyId
    );

    AuditHistoryResponse getAuditHistoryBySource(
            String sourceTable,
            UUID sourceRecordId
    );

    List<AuditHistoryResponse> getAuditHistoryByOrganizationId(
            UUID organizationId
    );

    List<AuditHistoryResponse> getAuditHistoryByTenantId(
            UUID tenantId
    );

    List<AuditHistoryResponse> getAuditHistoryByCorrelationId(
            UUID correlationId
    );

    List<AuditHistoryResponse> getAuditHistoryBySourceTable(
            String sourceTable
    );
}