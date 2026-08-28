package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEntityChangeResponse;
import com.efs.modules.audit.entity.AuditEntityChange;
import com.efs.modules.audit.mapper.AuditEntityChangeMapper;
import com.efs.modules.audit.repository.AuditEntityChangeRepository;
import com.efs.modules.audit.repository.AuditEventRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditEntityChangeService
        implements AuditEntityChangeServiceInterface {

    private final AuditEntityChangeRepository auditEntityChangeRepository;
    private final AuditEventRepository auditEventRepository;
    private final AuditEntityChangeMapper auditEntityChangeMapper;

    public AuditEntityChangeService(
            AuditEntityChangeRepository auditEntityChangeRepository,
            AuditEventRepository auditEventRepository,
            AuditEntityChangeMapper auditEntityChangeMapper) {

        this.auditEntityChangeRepository =
                auditEntityChangeRepository;

        this.auditEventRepository =
                auditEventRepository;

        this.auditEntityChangeMapper =
                auditEntityChangeMapper;
    }

    @Override
    @Transactional
    public AuditEntityChangeResponse createAuditEntityChange(
            AuditEntityChangeRequest request) {

        auditEventRepository
                .findById(request.getAuditEventId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Audit event not found: "
                                        + request.getAuditEventId()
                        )
                );

        AuditEntityChange change =
                auditEntityChangeMapper.toEntity(request);

        change.setChangedAt(
                LocalDateTime.now()
        );

        AuditEntityChange savedChange =
                auditEntityChangeRepository.save(change);

        return auditEntityChangeMapper.toResponse(
                savedChange
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEntityChangeResponse getAuditEntityChangeById(
            UUID changeId) {

        AuditEntityChange change =
                auditEntityChangeRepository
                        .findById(changeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audit entity change not found: "
                                                + changeId
                                )
                        );

        return auditEntityChangeMapper.toResponse(
                change
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEntityChangeResponse>
    getAuditEntityChangesByAuditEventId(
            UUID auditEventId) {

        return auditEntityChangeRepository
                .findByAuditEventIdOrderByChangedAtDesc(
                        auditEventId
                )
                .stream()
                .map(
                        auditEntityChangeMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEntityChangeResponse>
    getAuditEntityChangesByEntity(
            String entityType,
            UUID entityId) {

        return auditEntityChangeRepository
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(
                        auditEntityChangeMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEntityChangeResponse>
    getAuditEntityChangesByOperation(
            String operation) {

        return auditEntityChangeRepository
                .findByOperationOrderByChangedAtDesc(
                        operation
                )
                .stream()
                .map(
                        auditEntityChangeMapper::toResponse
                )
                .toList();
    }
}