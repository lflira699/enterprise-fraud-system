package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditSecurityEventRequest;
import com.efs.modules.audit.dto.AuditSecurityEventResponse;
import com.efs.modules.audit.entity.AuditSecurityEvent;
import com.efs.modules.audit.mapper.AuditSecurityEventMapper;
import com.efs.modules.audit.repository.AuditSecurityEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class AuditSecurityEventService
        implements AuditSecurityEventServiceInterface {

    private final AuditSecurityEventRepository auditSecurityEventRepository;
    private final AuditSecurityEventMapper auditSecurityEventMapper;

    public AuditSecurityEventService(
            AuditSecurityEventRepository auditSecurityEventRepository,
            AuditSecurityEventMapper auditSecurityEventMapper) {

        this.auditSecurityEventRepository =
                auditSecurityEventRepository;

        this.auditSecurityEventMapper =
                auditSecurityEventMapper;
    }

    @Override
    public AuditSecurityEventResponse createAuditSecurityEvent(
            AuditSecurityEventRequest request) {

        AuditSecurityEvent securityEvent =
                auditSecurityEventMapper.toEntity(request);

        AuditSecurityEvent savedSecurityEvent =
                auditSecurityEventRepository.saveAndFlush(
                        securityEvent
                );

        return auditSecurityEventMapper.toResponse(
                savedSecurityEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditSecurityEventResponse getAuditSecurityEventById(
            UUID securityEventId) {

        AuditSecurityEvent securityEvent =
                auditSecurityEventRepository
                        .findById(securityEventId)
                        .orElseThrow(
                                () -> new NoSuchElementException(
                                        "Audit security event not found: "
                                                + securityEventId
                                )
                        );

        return auditSecurityEventMapper.toResponse(
                securityEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditSecurityEventResponse>
            getAuditSecurityEventsByUserId(
                    UUID userId) {

        return auditSecurityEventRepository
                .findByUserIdOrderByDetectedAtDesc(userId)
                .stream()
                .map(auditSecurityEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditSecurityEventResponse>
            getAuditSecurityEventsByOrganizationId(
                    UUID organizationId) {

        return auditSecurityEventRepository
                .findByOrganizationIdOrderByDetectedAtDesc(
                        organizationId
                )
                .stream()
                .map(auditSecurityEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditSecurityEventResponse>
            getAuditSecurityEventsByAuditEventId(
                    UUID auditEventId) {

        return auditSecurityEventRepository
                .findByAuditEventIdOrderByDetectedAtDesc(
                        auditEventId
                )
                .stream()
                .map(auditSecurityEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditSecurityEventResponse>
            getAuditSecurityEventsBySeverity(
                    String severity) {

        return auditSecurityEventRepository
                .findBySeverityOrderByDetectedAtDesc(severity)
                .stream()
                .map(auditSecurityEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditSecurityEventResponse>
            getAuditSecurityEventsByEventCategory(
                    String eventCategory) {

        return auditSecurityEventRepository
                .findByEventCategoryOrderByDetectedAtDesc(
                        eventCategory
                )
                .stream()
                .map(auditSecurityEventMapper::toResponse)
                .toList();
    }
}