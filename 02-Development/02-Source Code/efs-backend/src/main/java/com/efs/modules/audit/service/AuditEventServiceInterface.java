package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;

import java.util.List;
import java.util.UUID;

public interface AuditEventServiceInterface {

    AuditEventResponse createAuditEvent(
            AuditEventRequest request
    );

    AuditEventResponse getAuditEventById(
            UUID auditEventId
    );

    List<AuditEventResponse> getAuditEventsByEventType(
            String eventType
    );

    List<AuditEventResponse> getAuditEventsByEntity(
            String entityType,
            UUID entityId
    );

    List<AuditEventResponse> getAuditEventsByUserId(
            UUID userId
    );

    List<AuditEventResponse> getAuditEventsByOrganizationId(
            UUID organizationId
    );

    List<AuditEventResponse> getAuditEventsByCorrelationId(
            UUID correlationId
    );
}