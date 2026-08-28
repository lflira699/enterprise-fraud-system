package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditSecurityEventRequest;
import com.efs.modules.audit.dto.AuditSecurityEventResponse;

import java.util.List;
import java.util.UUID;

public interface AuditSecurityEventServiceInterface {

    AuditSecurityEventResponse createAuditSecurityEvent(
            AuditSecurityEventRequest request
    );

    AuditSecurityEventResponse getAuditSecurityEventById(
            UUID securityEventId
    );

    List<AuditSecurityEventResponse> getAuditSecurityEventsByUserId(
            UUID userId
    );

    List<AuditSecurityEventResponse> getAuditSecurityEventsByOrganizationId(
            UUID organizationId
    );

    List<AuditSecurityEventResponse> getAuditSecurityEventsByAuditEventId(
            UUID auditEventId
    );

    List<AuditSecurityEventResponse> getAuditSecurityEventsBySeverity(
            String severity
    );

    List<AuditSecurityEventResponse> getAuditSecurityEventsByEventCategory(
            String eventCategory
    );
}