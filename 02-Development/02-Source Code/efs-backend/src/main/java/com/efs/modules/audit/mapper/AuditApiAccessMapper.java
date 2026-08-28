package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditApiAccessRequest;
import com.efs.modules.audit.dto.AuditApiAccessResponse;
import com.efs.modules.audit.entity.AuditApiAccess;
import org.springframework.stereotype.Component;

@Component
public class AuditApiAccessMapper {

    public AuditApiAccess toEntity(
            AuditApiAccessRequest request) {

        AuditApiAccess auditApiAccess =
                new AuditApiAccess();

        auditApiAccess.setApiClientId(
                request.getApiClientId()
        );

        auditApiAccess.setEndpoint(
                request.getEndpoint()
        );

        auditApiAccess.setHttpMethod(
                request.getHttpMethod()
        );

        auditApiAccess.setResponseCode(
                request.getResponseCode()
        );

        auditApiAccess.setExecutionTimeMs(
                request.getExecutionTimeMs()
        );

        auditApiAccess.setRequestSize(
                request.getRequestSize()
        );

        auditApiAccess.setResponseSize(
                request.getResponseSize()
        );

        auditApiAccess.setIpAddress(
                request.getIpAddress()
        );

        auditApiAccess.setCorrelationId(
                request.getCorrelationId()
        );

        return auditApiAccess;
    }

    public AuditApiAccessResponse toResponse(
            AuditApiAccess auditApiAccess) {

        AuditApiAccessResponse response =
                new AuditApiAccessResponse();

        response.setApiAccessId(
                auditApiAccess.getApiAccessId()
        );

        response.setApiClientId(
                auditApiAccess.getApiClientId()
        );

        response.setEndpoint(
                auditApiAccess.getEndpoint()
        );

        response.setHttpMethod(
                auditApiAccess.getHttpMethod()
        );

        response.setResponseCode(
                auditApiAccess.getResponseCode()
        );

        response.setExecutionTimeMs(
                auditApiAccess.getExecutionTimeMs()
        );

        response.setRequestSize(
                auditApiAccess.getRequestSize()
        );

        response.setResponseSize(
                auditApiAccess.getResponseSize()
        );

        response.setIpAddress(
                auditApiAccess.getIpAddress()
        );

        response.setCorrelationId(
                auditApiAccess.getCorrelationId()
        );

        response.setRequestedAt(
                auditApiAccess.getRequestedAt()
        );

        return response;
    }
}