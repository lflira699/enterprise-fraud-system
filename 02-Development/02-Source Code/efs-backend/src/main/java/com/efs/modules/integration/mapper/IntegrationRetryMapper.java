package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationRetryRequest;
import com.efs.modules.integration.dto.IntegrationRetryResponse;
import com.efs.modules.integration.entity.IntegrationRetry;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRetryMapper {

    public IntegrationRetry toEntity(
            IntegrationRetryRequest request
    ) {
        IntegrationRetry entity =
                new IntegrationRetry();

        entity.setMessageId(request.getMessageId());
        entity.setRetryNumber(request.getRetryNumber());
        entity.setErrorDescription(
                request.getErrorDescription()
        );
        entity.setNextRetry(request.getNextRetry());
        entity.setRetryStatus(request.getRetryStatus());
        entity.setErrorCode(request.getErrorCode());

        return entity;
    }

    public void updateEntity(
            IntegrationRetry entity,
            IntegrationRetryRequest request
    ) {
        entity.setMessageId(request.getMessageId());
        entity.setRetryNumber(request.getRetryNumber());
        entity.setErrorDescription(
                request.getErrorDescription()
        );
        entity.setNextRetry(request.getNextRetry());
        entity.setRetryStatus(request.getRetryStatus());
        entity.setErrorCode(request.getErrorCode());
    }

    public IntegrationRetryResponse toResponse(
            IntegrationRetry entity
    ) {
        IntegrationRetryResponse response =
                new IntegrationRetryResponse();

        response.setRetryId(entity.getRetryId());
        response.setMessageId(entity.getMessageId());
        response.setRetryNumber(entity.getRetryNumber());
        response.setErrorDescription(
                entity.getErrorDescription()
        );
        response.setNextRetry(entity.getNextRetry());
        response.setRetryStatus(entity.getRetryStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setErrorCode(entity.getErrorCode());

        return response;
    }
}