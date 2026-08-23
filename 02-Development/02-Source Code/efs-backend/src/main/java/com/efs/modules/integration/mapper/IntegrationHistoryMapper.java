package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationHistoryRequest;
import com.efs.modules.integration.dto.IntegrationHistoryResponse;
import com.efs.modules.integration.entity.IntegrationHistory;
import org.springframework.stereotype.Component;

@Component
public class IntegrationHistoryMapper {

    public IntegrationHistory toEntity(
            IntegrationHistoryRequest request
    ) {
        IntegrationHistory entity =
                new IntegrationHistory();

        entity.setMessageId(request.getMessageId());
        entity.setConnectorId(request.getConnectorId());
        entity.setCorrelationId(request.getCorrelationId());
        entity.setRequestId(request.getRequestId());
        entity.setMessageType(request.getMessageType());
        entity.setSourceSystem(request.getSourceSystem());
        entity.setTargetSystem(request.getTargetSystem());
        entity.setPayloadJson(request.getPayloadJson());
        entity.setProcessingTimeMs(
                request.getProcessingTimeMs()
        );
        entity.setMessageStatus(
                request.getMessageStatus()
        );
        entity.setOriginalCreatedAt(
                request.getOriginalCreatedAt()
        );

        return entity;
    }

    public void updateEntity(
            IntegrationHistory entity,
            IntegrationHistoryRequest request
    ) {
        entity.setMessageId(request.getMessageId());
        entity.setConnectorId(request.getConnectorId());
        entity.setCorrelationId(request.getCorrelationId());
        entity.setRequestId(request.getRequestId());
        entity.setMessageType(request.getMessageType());
        entity.setSourceSystem(request.getSourceSystem());
        entity.setTargetSystem(request.getTargetSystem());
        entity.setPayloadJson(request.getPayloadJson());
        entity.setProcessingTimeMs(
                request.getProcessingTimeMs()
        );
        entity.setMessageStatus(
                request.getMessageStatus()
        );
        entity.setOriginalCreatedAt(
                request.getOriginalCreatedAt()
        );
    }

    public IntegrationHistoryResponse toResponse(
            IntegrationHistory entity
    ) {
        IntegrationHistoryResponse response =
                new IntegrationHistoryResponse();

        response.setHistoryId(entity.getHistoryId());
        response.setMessageId(entity.getMessageId());
        response.setConnectorId(entity.getConnectorId());
        response.setCorrelationId(
                entity.getCorrelationId()
        );
        response.setRequestId(entity.getRequestId());
        response.setMessageType(entity.getMessageType());
        response.setSourceSystem(entity.getSourceSystem());
        response.setTargetSystem(entity.getTargetSystem());
        response.setPayloadJson(entity.getPayloadJson());
        response.setProcessingTimeMs(
                entity.getProcessingTimeMs()
        );
        response.setMessageStatus(
                entity.getMessageStatus()
        );
        response.setOriginalCreatedAt(
                entity.getOriginalCreatedAt()
        );
        response.setArchivedAt(entity.getArchivedAt());

        return response;
    }
}