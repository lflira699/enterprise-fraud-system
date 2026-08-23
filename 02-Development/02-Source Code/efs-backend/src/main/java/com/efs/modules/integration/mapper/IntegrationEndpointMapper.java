package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationEndpointRequest;
import com.efs.modules.integration.dto.IntegrationEndpointResponse;
import com.efs.modules.integration.entity.IntegrationEndpoint;
import org.springframework.stereotype.Component;

@Component
public class IntegrationEndpointMapper {

    public IntegrationEndpoint toEntity(
            IntegrationEndpointRequest request
    ) {
        IntegrationEndpoint entity =
                new IntegrationEndpoint();

        entity.setEndpointCode(request.getEndpointCode());
        entity.setEndpointName(request.getEndpointName());
        entity.setEndpointUrl(request.getEndpointUrl());
        entity.setProtocol(request.getProtocol());
        entity.setAuthenticationType(
                request.getAuthenticationType()
        );
        entity.setTimeoutSeconds(
                request.getTimeoutSeconds()
        );
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationEndpoint entity,
            IntegrationEndpointRequest request
    ) {
        entity.setEndpointCode(request.getEndpointCode());
        entity.setEndpointName(request.getEndpointName());
        entity.setEndpointUrl(request.getEndpointUrl());
        entity.setProtocol(request.getProtocol());
        entity.setAuthenticationType(
                request.getAuthenticationType()
        );
        entity.setTimeoutSeconds(
                request.getTimeoutSeconds()
        );
        entity.setStatus(request.getStatus());
    }

    public IntegrationEndpointResponse toResponse(
            IntegrationEndpoint entity
    ) {
        IntegrationEndpointResponse response =
                new IntegrationEndpointResponse();

        response.setEndpointId(entity.getEndpointId());
        response.setEndpointCode(entity.getEndpointCode());
        response.setEndpointName(entity.getEndpointName());
        response.setEndpointUrl(entity.getEndpointUrl());
        response.setProtocol(entity.getProtocol());
        response.setAuthenticationType(
                entity.getAuthenticationType()
        );
        response.setTimeoutSeconds(
                entity.getTimeoutSeconds()
        );
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}