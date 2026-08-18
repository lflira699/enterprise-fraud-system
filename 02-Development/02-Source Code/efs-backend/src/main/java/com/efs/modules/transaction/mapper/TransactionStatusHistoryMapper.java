package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;
import com.efs.modules.transaction.entity.TransactionStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusHistoryMapper {

    public TransactionStatusHistory toEntity(
            TransactionStatusHistoryRequest request) {

        TransactionStatusHistory history =
                new TransactionStatusHistory();

        history.setPreviousStatus(
                request.getPreviousStatus()
        );

        history.setCurrentStatus(
                request.getCurrentStatus()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        history.setChangedAt(
                request.getChangedAt()
        );

        return history;
    }

    public TransactionStatusHistoryResponse toResponse(
            TransactionStatusHistory history) {

        TransactionStatusHistoryResponse response =
                new TransactionStatusHistoryResponse();

        response.setHistoryId(
                history.getHistoryId()
        );

        response.setTransactionId(
                history.getTransactionId()
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