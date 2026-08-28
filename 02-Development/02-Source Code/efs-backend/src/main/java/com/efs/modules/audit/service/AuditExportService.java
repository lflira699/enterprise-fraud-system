package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.dto.AuditExportResponse;
import com.efs.modules.audit.entity.AuditExport;
import com.efs.modules.audit.mapper.AuditExportMapper;
import com.efs.modules.audit.repository.AuditExportRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditExportService
        implements AuditExportServiceInterface {

    private final AuditExportRepository auditExportRepository;
    private final AuditExportMapper auditExportMapper;

    public AuditExportService(
            AuditExportRepository auditExportRepository,
            AuditExportMapper auditExportMapper) {

        this.auditExportRepository =
                auditExportRepository;

        this.auditExportMapper =
                auditExportMapper;
    }

    @Override
    @Transactional
    public AuditExportResponse createAuditExport(
            AuditExportRequest request) {

        AuditExport auditExport =
                auditExportMapper.toEntity(
                        request
                );

        AuditExport savedAuditExport =
                auditExportRepository.saveAndFlush(
                        auditExport
                );

        return auditExportMapper.toResponse(
                savedAuditExport
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditExportResponse getAuditExportById(
            UUID exportId) {

        AuditExport auditExport =
                auditExportRepository
                        .findById(exportId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Audit export not found: "
                                                        + exportId
                                        )
                        );

        return auditExportMapper.toResponse(
                auditExport
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditExportResponse> getAuditExportsByUserId(
            UUID userId) {

        return auditExportRepository
                .findByUserIdOrderByExportedAtDesc(
                        userId
                )
                .stream()
                .map(auditExportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditExportResponse> getAuditExportsByOrganizationId(
            UUID organizationId) {

        return auditExportRepository
                .findByOrganizationIdOrderByExportedAtDesc(
                        organizationId
                )
                .stream()
                .map(auditExportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditExportResponse> getAuditExportsByExportType(
            String exportType) {

        return auditExportRepository
                .findByExportTypeOrderByExportedAtDesc(
                        exportType
                )
                .stream()
                .map(auditExportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditExportResponse> getAuditExportsByResource(
            String resourceType,
            UUID resourceId) {

        return auditExportRepository
                .findByResourceTypeAndResourceIdOrderByExportedAtDesc(
                        resourceType,
                        resourceId
                )
                .stream()
                .map(auditExportMapper::toResponse)
                .toList();
    }
}