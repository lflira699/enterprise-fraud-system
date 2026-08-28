package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.entity.AuditEvent;
import com.efs.modules.audit.mapper.AuditEventMapper;
import com.efs.modules.audit.repository.AuditEventRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditEventService
        implements AuditEventServiceInterface {

    private final AuditEventRepository auditEventRepository;
    private final AuditEventMapper auditEventMapper;

    public AuditEventService(
            AuditEventRepository auditEventRepository,
            AuditEventMapper auditEventMapper) {

        this.auditEventRepository = auditEventRepository;
        this.auditEventMapper = auditEventMapper;
    }

    @Override
    @Transactional
    public AuditEventResponse createAuditEvent(
            AuditEventRequest request) {

        AuditEvent auditEvent =
                auditEventMapper.toEntity(request);

        auditEvent.setEventTimestamp(
                LocalDateTime.now()
        );

        AuditEvent savedAuditEvent =
                auditEventRepository.save(auditEvent);

        return auditEventMapper.toResponse(savedAuditEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEventResponse getAuditEventById(
            UUID auditEventId) {

        AuditEvent auditEvent =
                auditEventRepository
                        .findById(auditEventId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audit event not found: "
                                                + auditEventId
                                )
                        );

        return auditEventMapper.toResponse(auditEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByEventType(
            String eventType) {

        return auditEventRepository
                .findByEventTypeOrderByEventTimestampDesc(
                        eventType
                )
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByEntity(
            String entityType,
            UUID entityId) {

        return auditEventRepository
                .findByEntityTypeAndEntityIdOrderByEventTimestampDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByUserId(
            UUID userId) {

        return auditEventRepository
                .findByUserIdOrderByEventTimestampDesc(
                        userId
                )
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByOrganizationId(
            UUID organizationId) {

        return auditEventRepository
                .findByOrganizationIdOrderByEventTimestampDesc(
                        organizationId
                )
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> getAuditEventsByCorrelationId(
            UUID correlationId) {

        return auditEventRepository
                .findByCorrelationIdOrderByEventTimestampDesc(
                        correlationId
                )
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }
}