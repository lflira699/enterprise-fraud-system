package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface AuditLogReviewServiceInterface {

    PageResponse<AuditEventResponse> searchAuditEvents(
            UUID userId,
            String from,
            String to,
            String entityType,
            UUID entityId,
            String action,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext
    );

    AuditEventResponse getAuditEventById(
            UUID auditEventId,
            SecurityContext securityContext
    );

    List<AuditEventResponse> getAuditEventsByEventType(
            String eventType,
            SecurityContext securityContext
    );

    List<AuditEventResponse> getAuditEventsByEntity(
            String entityType,
            UUID entityId,
            SecurityContext securityContext
    );

    List<AuditEventResponse> getAuditEventsByUserId(
            UUID userId,
            SecurityContext securityContext
    );

    List<AuditEventResponse> getAuditEventsByOrganizationId(
            UUID organizationId,
            SecurityContext securityContext
    );

    List<AuditEventResponse> getAuditEventsByCorrelationId(
            UUID correlationId,
            SecurityContext securityContext
    );
}