package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationMessageRequest;
import com.efs.modules.integration.dto.IntegrationMessageResponse;
import com.efs.modules.integration.entity.IntegrationMessage;
import org.springframework.stereotype.Component;

@Component
public class IntegrationMessageMapper {

    public IntegrationMessage toEntity(
            IntegrationMessageRequest request
    ) {
        IntegrationMessage entity =
                new IntegrationMessage();

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

        return entity;
    }

    public void updateEntity(
            IntegrationMessage entity,
            IntegrationMessageRequest request
    ) {
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
    }

    public IntegrationMessageResponse toResponse(
            IntegrationMessage entity
    ) {
        IntegrationMessageResponse response =
                new IntegrationMessageResponse();

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
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}