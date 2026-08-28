package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditApiAccessRequest;
import com.efs.modules.audit.dto.AuditApiAccessResponse;

import java.util.List;
import java.util.UUID;

public interface AuditApiAccessServiceInterface {

    AuditApiAccessResponse createAuditApiAccess(
            AuditApiAccessRequest request
    );

    AuditApiAccessResponse getAuditApiAccessById(
            UUID apiAccessId
    );

    List<AuditApiAccessResponse> getAuditApiAccessesByApiClientId(
            UUID apiClientId
    );

    List<AuditApiAccessResponse> getAuditApiAccessesByEndpoint(
            String endpoint
    );

    List<AuditApiAccessResponse> getAuditApiAccessesByResponseCode(
            Integer responseCode
    );

    List<AuditApiAccessResponse> getAuditApiAccessesByCorrelationId(
            UUID correlationId
    );
}