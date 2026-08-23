package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationWebhookRequest;
import com.efs.modules.integration.dto.IntegrationWebhookResponse;
import com.efs.modules.integration.entity.IntegrationWebhook;
import org.springframework.stereotype.Component;

@Component
public class IntegrationWebhookMapper {

    public IntegrationWebhook toEntity(
            IntegrationWebhookRequest request
    ) {
        IntegrationWebhook entity =
                new IntegrationWebhook();

        entity.setEndpointId(request.getEndpointId());
        entity.setEventName(request.getEventName());
        entity.setTargetUrl(request.getTargetUrl());
        entity.setHttpMethod(request.getHttpMethod());
        entity.setRetryCount(request.getRetryCount());
        entity.setLastExecution(request.getLastExecution());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationWebhook entity,
            IntegrationWebhookRequest request
    ) {
        entity.setEndpointId(request.getEndpointId());
        entity.setEventName(request.getEventName());
        entity.setTargetUrl(request.getTargetUrl());
        entity.setHttpMethod(request.getHttpMethod());
        entity.setRetryCount(request.getRetryCount());
        entity.setLastExecution(request.getLastExecution());
        entity.setStatus(request.getStatus());
    }

    public IntegrationWebhookResponse toResponse(
            IntegrationWebhook entity
    ) {
        IntegrationWebhookResponse response =
                new IntegrationWebhookResponse();

        response.setWebhookId(entity.getWebhookId());
        response.setEndpointId(entity.getEndpointId());
        response.setEventName(entity.getEventName());
        response.setTargetUrl(entity.getTargetUrl());
        response.setHttpMethod(entity.getHttpMethod());
        response.setRetryCount(entity.getRetryCount());
        response.setLastExecution(entity.getLastExecution());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}