package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditHistoryRequest;
import com.efs.modules.audit.dto.AuditHistoryResponse;
import com.efs.modules.audit.entity.AuditHistory;
import com.efs.modules.audit.mapper.AuditHistoryMapper;
import com.efs.modules.audit.repository.AuditHistoryRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditHistoryService
        implements AuditHistoryServiceInterface {

    private final AuditHistoryRepository auditHistoryRepository;
    private final AuditHistoryMapper auditHistoryMapper;

    public AuditHistoryService(
            AuditHistoryRepository auditHistoryRepository,
            AuditHistoryMapper auditHistoryMapper) {

        this.auditHistoryRepository =
                auditHistoryRepository;

        this.auditHistoryMapper =
                auditHistoryMapper;
    }

    @Override
    @Transactional
    public AuditHistoryResponse createAuditHistory(
            AuditHistoryRequest request) {

        AuditHistory auditHistory =
                auditHistoryMapper.toEntity(
                        request
                );

        AuditHistory savedAuditHistory =
                auditHistoryRepository.saveAndFlush(
                        auditHistory
                );

        return auditHistoryMapper.toResponse(
                savedAuditHistory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditHistoryResponse getAuditHistoryById(
            UUID historyId) {

        AuditHistory auditHistory =
                auditHistoryRepository
                        .findById(historyId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Audit history not found: "
                                                        + historyId
                                        )
                        );

        return auditHistoryMapper.toResponse(
                auditHistory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditHistoryResponse getAuditHistoryBySource(
            String sourceTable,
            UUID sourceRecordId) {

        AuditHistory auditHistory =
                auditHistoryRepository
                        .findBySourceTableAndSourceRecordId(
                                sourceTable,
                                sourceRecordId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Audit history source not found: "
                                                        + sourceTable
                                                        + " / "
                                                        + sourceRecordId
                                        )
                        );

        return auditHistoryMapper.toResponse(
                auditHistory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditHistoryResponse>
    getAuditHistoryByOrganizationId(
            UUID organizationId) {

        return auditHistoryRepository
                .findByOrganizationIdOrderByArchivedAtDesc(
                        organizationId
                )
                .stream()
                .map(auditHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditHistoryResponse>
    getAuditHistoryByTenantId(
            UUID tenantId) {

        return auditHistoryRepository
                .findByTenantIdOrderByArchivedAtDesc(
                        tenantId
                )
                .stream()
                .map(auditHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditHistoryResponse>
    getAuditHistoryByCorrelationId(
            UUID correlationId) {

        return auditHistoryRepository
                .findByCorrelationIdOrderByArchivedAtDesc(
                        correlationId
                )
                .stream()
                .map(auditHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditHistoryResponse>
    getAuditHistoryBySourceTable(
            String sourceTable) {

        return auditHistoryRepository
                .findBySourceTableOrderByArchivedAtDesc(
                        sourceTable
                )
                .stream()
                .map(auditHistoryMapper::toResponse)
                .toList();
    }
}