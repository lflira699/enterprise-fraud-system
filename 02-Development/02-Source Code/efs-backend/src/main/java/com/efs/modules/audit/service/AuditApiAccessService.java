package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditApiAccessRequest;
import com.efs.modules.audit.dto.AuditApiAccessResponse;
import com.efs.modules.audit.entity.AuditApiAccess;
import com.efs.modules.audit.mapper.AuditApiAccessMapper;
import com.efs.modules.audit.repository.AuditApiAccessRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditApiAccessService
        implements AuditApiAccessServiceInterface {

    private final AuditApiAccessRepository auditApiAccessRepository;
    private final AuditApiAccessMapper auditApiAccessMapper;

    public AuditApiAccessService(
            AuditApiAccessRepository auditApiAccessRepository,
            AuditApiAccessMapper auditApiAccessMapper) {

        this.auditApiAccessRepository =
                auditApiAccessRepository;

        this.auditApiAccessMapper =
                auditApiAccessMapper;
    }

    @Override
    @Transactional
    public AuditApiAccessResponse createAuditApiAccess(
            AuditApiAccessRequest request) {

        AuditApiAccess auditApiAccess =
                auditApiAccessMapper.toEntity(request);

        auditApiAccess.setRequestedAt(
                LocalDateTime.now()
        );

        AuditApiAccess savedAuditApiAccess =
                auditApiAccessRepository.save(
                        auditApiAccess
                );

        return auditApiAccessMapper.toResponse(
                savedAuditApiAccess
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditApiAccessResponse getAuditApiAccessById(
            UUID apiAccessId) {

        AuditApiAccess auditApiAccess =
                auditApiAccessRepository
                        .findById(apiAccessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audit API access not found: "
                                                + apiAccessId
                                )
                        );

        return auditApiAccessMapper.toResponse(
                auditApiAccess
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditApiAccessResponse>
    getAuditApiAccessesByApiClientId(
            UUID apiClientId) {

        return auditApiAccessRepository
                .findByApiClientIdOrderByRequestedAtDesc(
                        apiClientId
                )
                .stream()
                .map(auditApiAccessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditApiAccessResponse>
    getAuditApiAccessesByEndpoint(
            String endpoint) {

        return auditApiAccessRepository
                .findByEndpointOrderByRequestedAtDesc(
                        endpoint
                )
                .stream()
                .map(auditApiAccessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditApiAccessResponse>
    getAuditApiAccessesByResponseCode(
            Integer responseCode) {

        return auditApiAccessRepository
                .findByResponseCodeOrderByRequestedAtDesc(
                        responseCode
                )
                .stream()
                .map(auditApiAccessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditApiAccessResponse>
    getAuditApiAccessesByCorrelationId(
            UUID correlationId) {

        return auditApiAccessRepository
                .findByCorrelationIdOrderByRequestedAtDesc(
                        correlationId
                )
                .stream()
                .map(auditApiAccessMapper::toResponse)
                .toList();
    }
}