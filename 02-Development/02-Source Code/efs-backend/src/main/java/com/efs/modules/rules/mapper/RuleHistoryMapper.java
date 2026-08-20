package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleHistoryResponse;
import com.efs.modules.rules.entity.RuleHistory;
import org.springframework.stereotype.Component;

@Component
public class RuleHistoryMapper {

    public RuleHistory toEntity(
            RuleHistoryRequest request) {

        RuleHistory history =
                new RuleHistory();

        history.setEntityType(
                request.getEntityType()
        );

        history.setEntityId(
                request.getEntityId()
        );

        history.setOperationType(
                request.getOperationType()
        );

        history.setPreviousValue(
                request.getPreviousValue()
        );

        history.setCurrentValue(
                request.getCurrentValue()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        history.setCorrelationId(
                request.getCorrelationId()
        );

        return history;
    }

    public RuleHistoryResponse toResponse(
            RuleHistory history) {

        RuleHistoryResponse response =
                new RuleHistoryResponse();

        response.setHistoryId(
                history.getHistoryId()
        );

        response.setEntityType(
                history.getEntityType()
        );

        response.setEntityId(
                history.getEntityId()
        );

        response.setOperationType(
                history.getOperationType()
        );

        response.setPreviousValue(
                history.getPreviousValue()
        );

        response.setCurrentValue(
                history.getCurrentValue()
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

        response.setCorrelationId(
                history.getCorrelationId()
        );

        return response;
    }
}