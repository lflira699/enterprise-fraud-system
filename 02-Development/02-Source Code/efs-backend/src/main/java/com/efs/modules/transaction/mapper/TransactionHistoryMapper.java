package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;
import com.efs.modules.transaction.entity.TransactionHistory;
import org.springframework.stereotype.Component;

@Component
public class TransactionHistoryMapper {

    public TransactionHistory toEntity(
            TransactionHistoryRequest request) {

        TransactionHistory history =
                new TransactionHistory();

        history.setVersionNumber(
                request.getVersionNumber()
        );

        history.setSnapshotJson(
                request.getSnapshotJson()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        return history;
    }

    public TransactionHistoryResponse toResponse(
            TransactionHistory history) {

        TransactionHistoryResponse response =
                new TransactionHistoryResponse();

        response.setHistoryId(
                history.getHistoryId()
        );

        response.setTransactionId(
                history.getTransactionId()
        );

        response.setVersionNumber(
                history.getVersionNumber()
        );

        response.setSnapshotJson(
                history.getSnapshotJson()
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