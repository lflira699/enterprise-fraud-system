package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditConfigurationChangeResponse;
import com.efs.modules.audit.entity.AuditConfigurationChange;
import com.efs.modules.audit.mapper.AuditConfigurationChangeMapper;
import com.efs.modules.audit.repository.AuditConfigurationChangeRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditConfigurationChangeService
        implements AuditConfigurationChangeServiceInterface {

    private final AuditConfigurationChangeRepository
            auditConfigurationChangeRepository;

    private final AuditConfigurationChangeMapper
            auditConfigurationChangeMapper;

    public AuditConfigurationChangeService(
            AuditConfigurationChangeRepository auditConfigurationChangeRepository,
            AuditConfigurationChangeMapper auditConfigurationChangeMapper) {

        this.auditConfigurationChangeRepository =
                auditConfigurationChangeRepository;

        this.auditConfigurationChangeMapper =
                auditConfigurationChangeMapper;
    }

    @Override
    @Transactional
    public AuditConfigurationChangeResponse
    createAuditConfigurationChange(
            AuditConfigurationChangeRequest request) {

        AuditConfigurationChange configurationChange =
                auditConfigurationChangeMapper.toEntity(
                        request
                );

        configurationChange.setChangedAt(
                LocalDateTime.now()
        );

        AuditConfigurationChange savedConfigurationChange =
                auditConfigurationChangeRepository.save(
                        configurationChange
                );

        return auditConfigurationChangeMapper.toResponse(
                savedConfigurationChange
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditConfigurationChangeResponse
    getAuditConfigurationChangeById(
            UUID configurationChangeId) {

        AuditConfigurationChange configurationChange =
                auditConfigurationChangeRepository
                        .findById(configurationChangeId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Audit configuration change not found: "
                                                        + configurationChangeId
                                        )
                        );

        return auditConfigurationChangeMapper.toResponse(
                configurationChange
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByAuditEventId(
            UUID auditEventId) {

        return auditConfigurationChangeRepository
                .findByAuditEventIdOrderByChangedAtDesc(
                        auditEventId
                )
                .stream()
                .map(
                        auditConfigurationChangeMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByConfigurationKey(
            String configurationKey) {

        return auditConfigurationChangeRepository
                .findByConfigurationKeyOrderByChangedAtDesc(
                        configurationKey
                )
                .stream()
                .map(
                        auditConfigurationChangeMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByChangedBy(
            UUID changedBy) {

        return auditConfigurationChangeRepository
                .findByChangedByOrderByChangedAtDesc(
                        changedBy
                )
                .stream()
                .map(
                        auditConfigurationChangeMapper::toResponse
                )
                .toList();
    }
}