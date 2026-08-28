package com.efs.modules.audit.mapper;

import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditConfigurationChangeResponse;
import com.efs.modules.audit.entity.AuditConfigurationChange;
import org.springframework.stereotype.Component;

@Component
public class AuditConfigurationChangeMapper {

    public AuditConfigurationChange toEntity(
            AuditConfigurationChangeRequest request) {

        AuditConfigurationChange configurationChange =
                new AuditConfigurationChange();

        configurationChange.setAuditEventId(
                request.getAuditEventId()
        );

        configurationChange.setConfigurationKey(
                request.getConfigurationKey()
        );

        configurationChange.setPreviousValue(
                request.getPreviousValue()
        );

        configurationChange.setCurrentValue(
                request.getCurrentValue()
        );

        configurationChange.setChangedBy(
                request.getChangedBy()
        );

        configurationChange.setChangeReason(
                request.getChangeReason()
        );

        return configurationChange;
    }

    public AuditConfigurationChangeResponse toResponse(
            AuditConfigurationChange configurationChange) {

        AuditConfigurationChangeResponse response =
                new AuditConfigurationChangeResponse();

        response.setConfigurationChangeId(
                configurationChange.getConfigurationChangeId()
        );

        response.setAuditEventId(
                configurationChange.getAuditEventId()
        );

        response.setConfigurationKey(
                configurationChange.getConfigurationKey()
        );

        response.setPreviousValue(
                configurationChange.getPreviousValue()
        );

        response.setCurrentValue(
                configurationChange.getCurrentValue()
        );

        response.setChangedBy(
                configurationChange.getChangedBy()
        );

        response.setChangeReason(
                configurationChange.getChangeReason()
        );

        response.setChangedAt(
                configurationChange.getChangedAt()
        );

        return response;
    }
}