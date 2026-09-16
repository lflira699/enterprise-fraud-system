package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

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
}