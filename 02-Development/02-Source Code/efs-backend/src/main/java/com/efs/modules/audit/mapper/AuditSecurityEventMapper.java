package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditSecurityEventRequest;
import com.efs.modules.audit.dto.AuditSecurityEventResponse;
import com.efs.modules.audit.entity.AuditSecurityEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditSecurityEventMapper {

    public AuditSecurityEvent toEntity(
            AuditSecurityEventRequest request) {

        AuditSecurityEvent securityEvent =
                new AuditSecurityEvent();

        securityEvent.setAuditEventId(
                request.getAuditEventId()
        );

        securityEvent.setOrganizationId(
                request.getOrganizationId()
        );

        securityEvent.setUserId(
                request.getUserId()
        );

        securityEvent.setEventCategory(
                request.getEventCategory()
        );

        securityEvent.setSeverity(
                request.getSeverity()
        );

        securityEvent.setSourceIp(
                request.getSourceIp()
        );

        securityEvent.setAffectedResource(
                request.getAffectedResource()
        );

        securityEvent.setMitigationAction(
                request.getMitigationAction()
        );

        return securityEvent;
    }

    public AuditSecurityEventResponse toResponse(
            AuditSecurityEvent securityEvent) {

        AuditSecurityEventResponse response =
                new AuditSecurityEventResponse();

        response.setSecurityEventId(
                securityEvent.getSecurityEventId()
        );

        response.setAuditEventId(
                securityEvent.getAuditEventId()
        );

        response.setOrganizationId(
                securityEvent.getOrganizationId()
        );

        response.setUserId(
                securityEvent.getUserId()
        );

        response.setEventCategory(
                securityEvent.getEventCategory()
        );

        response.setSeverity(
                securityEvent.getSeverity()
        );

        response.setSourceIp(
                securityEvent.getSourceIp()
        );

        response.setAffectedResource(
                securityEvent.getAffectedResource()
        );

        response.setMitigationAction(
                securityEvent.getMitigationAction()
        );

        response.setDetectedAt(
                securityEvent.getDetectedAt()
        );

        return response;
    }
}