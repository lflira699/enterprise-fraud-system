package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseStatusHistoryResponse;
import com.efs.modules.casemanagement.entity.CaseStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class CaseStatusHistoryMapper {

    public CaseStatusHistoryResponse toResponse(
            CaseStatusHistory history) {

        CaseStatusHistoryResponse response =
                new CaseStatusHistoryResponse();

        response.setHistoryId(
                history.getHistoryId()
        );

        response.setCaseId(
                history.getCaseId()
        );

        response.setPreviousStatus(
                history.getPreviousStatus()
        );

        response.setCurrentStatus(
                history.getCurrentStatus()
        );

        response.setChangeReason(
                history.getChangeReason()
        );

        response.setChangedBy(
                history.getChangedBy()
        );

        response.setChangedAt(
                history.getChangedAt()
        );

        return response;
    }
}