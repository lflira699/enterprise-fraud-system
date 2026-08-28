package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEntityChangeResponse;
import com.efs.modules.audit.entity.AuditEntityChange;
import org.springframework.stereotype.Component;

@Component
public class AuditEntityChangeMapper {

    public AuditEntityChange toEntity(
            AuditEntityChangeRequest request) {

        AuditEntityChange change =
                new AuditEntityChange();

        change.setAuditEventId(
                request.getAuditEventId()
        );

        change.setEntityType(
                request.getEntityType()
        );

        change.setEntityId(
                request.getEntityId()
        );

        change.setOperation(
                request.getOperation()
        );

        change.setPreviousValue(
                request.getPreviousValue()
        );

        change.setCurrentValue(
                request.getCurrentValue()
        );

        return change;
    }

    public AuditEntityChangeResponse toResponse(
            AuditEntityChange change) {

        AuditEntityChangeResponse response =
                new AuditEntityChangeResponse();

        response.setChangeId(
                change.getChangeId()
        );

        response.setAuditEventId(
                change.getAuditEventId()
        );

        response.setEntityType(
                change.getEntityType()
        );

        response.setEntityId(
                change.getEntityId()
        );

        response.setOperation(
                change.getOperation()
        );

        response.setPreviousValue(
                change.getPreviousValue()
        );

        response.setCurrentValue(
                change.getCurrentValue()
        );

        response.setChangedAt(
                change.getChangedAt()
        );

        return response;
    }
}