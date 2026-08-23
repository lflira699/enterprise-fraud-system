package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationEventRequest;
import com.efs.modules.integration.dto.IntegrationEventResponse;
import com.efs.modules.integration.entity.IntegrationEvent;
import org.springframework.stereotype.Component;

@Component
public class IntegrationEventMapper {

    public IntegrationEvent toEntity(
            IntegrationEventRequest request
    ) {
        IntegrationEvent entity =
                new IntegrationEvent();

        entity.setEventName(request.getEventName());
        entity.setEventVersion(request.getEventVersion());
        entity.setEventPayload(request.getEventPayload());
        entity.setPublisher(request.getPublisher());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationEvent entity,
            IntegrationEventRequest request
    ) {
        entity.setEventName(request.getEventName());
        entity.setEventVersion(request.getEventVersion());
        entity.setEventPayload(request.getEventPayload());
        entity.setPublisher(request.getPublisher());
        entity.setStatus(request.getStatus());
    }

    public IntegrationEventResponse toResponse(
            IntegrationEvent entity
    ) {
        IntegrationEventResponse response =
                new IntegrationEventResponse();

        response.setEventId(entity.getEventId());
        response.setEventName(entity.getEventName());
        response.setEventVersion(entity.getEventVersion());
        response.setEventPayload(entity.getEventPayload());
        response.setPublishedAt(entity.getPublishedAt());
        response.setPublisher(entity.getPublisher());
        response.setStatus(entity.getStatus());

        return response;
    }
}