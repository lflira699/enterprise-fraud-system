package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEntityChangeResponse;

import java.util.List;
import java.util.UUID;

public interface AuditEntityChangeServiceInterface {

    AuditEntityChangeResponse createAuditEntityChange(
            AuditEntityChangeRequest request
    );

    AuditEntityChangeResponse getAuditEntityChangeById(
            UUID changeId
    );

    List<AuditEntityChangeResponse> getAuditEntityChangesByAuditEventId(
            UUID auditEventId
    );

    List<AuditEntityChangeResponse> getAuditEntityChangesByEntity(
            String entityType,
            UUID entityId
    );

    List<AuditEntityChangeResponse> getAuditEntityChangesByOperation(
            String operation
    );
}