package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditConfigurationChangeResponse;

import java.util.List;
import java.util.UUID;

public interface AuditConfigurationChangeServiceInterface {

    AuditConfigurationChangeResponse createAuditConfigurationChange(
            AuditConfigurationChangeRequest request
    );

    AuditConfigurationChangeResponse getAuditConfigurationChangeById(
            UUID configurationChangeId
    );

    List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByAuditEventId(
            UUID auditEventId
    );

    List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByConfigurationKey(
            String configurationKey
    );

    List<AuditConfigurationChangeResponse>
    getAuditConfigurationChangesByChangedBy(
            UUID changedBy
    );
}