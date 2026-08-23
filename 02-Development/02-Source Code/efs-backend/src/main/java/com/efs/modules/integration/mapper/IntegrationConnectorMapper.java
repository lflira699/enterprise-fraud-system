package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationConnectorRequest;
import com.efs.modules.integration.dto.IntegrationConnectorResponse;
import com.efs.modules.integration.entity.IntegrationConnector;
import org.springframework.stereotype.Component;

@Component
public class IntegrationConnectorMapper {

    public IntegrationConnector toEntity(
            IntegrationConnectorRequest request
    ) {
        IntegrationConnector entity =
                new IntegrationConnector();

        entity.setEndpointId(request.getEndpointId());
        entity.setConnectorName(request.getConnectorName());
        entity.setConnectorType(request.getConnectorType());
        entity.setProvider(request.getProvider());
        entity.setVersion(request.getVersion());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationConnector entity,
            IntegrationConnectorRequest request
    ) {
        entity.setEndpointId(request.getEndpointId());
        entity.setConnectorName(request.getConnectorName());
        entity.setConnectorType(request.getConnectorType());
        entity.setProvider(request.getProvider());
        entity.setVersion(request.getVersion());
        entity.setStatus(request.getStatus());
    }

    public IntegrationConnectorResponse toResponse(
            IntegrationConnector entity
    ) {
        IntegrationConnectorResponse response =
                new IntegrationConnectorResponse();

        response.setConnectorId(entity.getConnectorId());
        response.setEndpointId(entity.getEndpointId());
        response.setConnectorName(entity.getConnectorName());
        response.setConnectorType(entity.getConnectorType());
        response.setProvider(entity.getProvider());
        response.setVersion(entity.getVersion());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}