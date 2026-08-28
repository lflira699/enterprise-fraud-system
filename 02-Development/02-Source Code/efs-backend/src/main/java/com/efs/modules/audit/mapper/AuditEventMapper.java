package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.entity.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditEventMapper {

    public AuditEvent toEntity(
            AuditEventRequest request) {

        AuditEvent auditEvent =
                new AuditEvent();

        auditEvent.setOrganizationId(
                request.getOrganizationId()
        );

        auditEvent.setTenantId(
                request.getTenantId()
        );

        auditEvent.setUserId(
                request.getUserId()
        );

        auditEvent.setSessionId(
                request.getSessionId()
        );

        auditEvent.setEventType(
                request.getEventType()
        );

        auditEvent.setEntityType(
                request.getEntityType()
        );

        auditEvent.setEntityId(
                request.getEntityId()
        );

        auditEvent.setAction(
                request.getAction()
        );

        auditEvent.setSourceComponent(
                request.getSourceComponent()
        );

        auditEvent.setIpAddress(
                request.getIpAddress()
        );

        auditEvent.setCorrelationId(
                request.getCorrelationId()
        );

        auditEvent.setEventResult(
                request.getEventResult()
        );

        auditEvent.setEventDetails(
                request.getEventDetails()
        );

        return auditEvent;
    }

    public AuditEventResponse toResponse(
            AuditEvent auditEvent) {

        AuditEventResponse response =
                new AuditEventResponse();

        response.setAuditEventId(
                auditEvent.getAuditEventId()
        );

        response.setEventTimestamp(
                auditEvent.getEventTimestamp()
        );

        response.setOrganizationId(
                auditEvent.getOrganizationId()
        );

        response.setTenantId(
                auditEvent.getTenantId()
        );

        response.setUserId(
                auditEvent.getUserId()
        );

        response.setSessionId(
                auditEvent.getSessionId()
        );

        response.setEventType(
                auditEvent.getEventType()
        );

        response.setEntityType(
                auditEvent.getEntityType()
        );

        response.setEntityId(
                auditEvent.getEntityId()
        );

        response.setAction(
                auditEvent.getAction()
        );

        response.setSourceComponent(
                auditEvent.getSourceComponent()
        );

        response.setIpAddress(
                auditEvent.getIpAddress()
        );

        response.setCorrelationId(
                auditEvent.getCorrelationId()
        );

        response.setEventResult(
                auditEvent.getEventResult()
        );

        response.setEventDetails(
                auditEvent.getEventDetails()
        );

        return response;
    }
}