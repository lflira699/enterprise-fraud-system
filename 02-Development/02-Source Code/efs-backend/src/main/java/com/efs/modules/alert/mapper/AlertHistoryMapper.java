package com.efs.modules.alert.mapper;

import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.entity.AlertHistory;
import org.springframework.stereotype.Component;

@Component
public class AlertHistoryMapper {

    public AlertHistoryResponse toResponse(
            AlertHistory alertHistory) {

        AlertHistoryResponse response =
                new AlertHistoryResponse();

        response.setAlertHistoryId(
                alertHistory.getAlertHistoryId()
        );

        response.setAlertId(
                alertHistory.getAlertId()
        );

        response.setActionType(
                alertHistory.getActionType()
        );

        response.setPreviousStatus(
                alertHistory.getPreviousStatus()
        );

        response.setNewStatus(
                alertHistory.getNewStatus()
        );

        response.setChangedBy(
                alertHistory.getChangedBy()
        );

        response.setChangeReason(
                alertHistory.getChangeReason()
        );

        response.setChangedAt(
                alertHistory.getChangedAt()
        );

        return response;
    }
}