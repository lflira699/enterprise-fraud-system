package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseHistoryRequest;
import com.efs.modules.casemanagement.dto.CaseHistoryResponse;
import com.efs.modules.casemanagement.entity.CaseHistory;
import org.springframework.stereotype.Component;

@Component
public class CaseHistoryMapper {

    public CaseHistory toEntity(
            CaseHistoryRequest request) {

        CaseHistory history =
                new CaseHistory();

        history.setEventType(
                request.getEventType()
        );

        history.setEventDescription(
                request.getEventDescription()
        );

        history.setPreviousValue(
                request.getPreviousValue()
        );

        history.setNewValue(
                request.getNewValue()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        return history;
    }

    public CaseHistoryResponse toResponse(
            CaseHistory history) {

        CaseHistoryResponse response =
                new CaseHistoryResponse();

        response.setHistoryId(
                history.getHistoryId()
        );

        response.setCaseId(
                history.getCaseId()
        );

        response.setEventType(
                history.getEventType()
        );

        response.setEventDescription(
                history.getEventDescription()
        );

        response.setPreviousValue(
                history.getPreviousValue()
        );

        response.setNewValue(
                history.getNewValue()
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