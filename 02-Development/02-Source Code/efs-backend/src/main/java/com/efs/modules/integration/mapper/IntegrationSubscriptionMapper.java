package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationSubscriptionRequest;
import com.efs.modules.integration.dto.IntegrationSubscriptionResponse;
import com.efs.modules.integration.entity.IntegrationSubscription;
import org.springframework.stereotype.Component;

@Component
public class IntegrationSubscriptionMapper {

    public IntegrationSubscription toEntity(
            IntegrationSubscriptionRequest request
    ) {
        IntegrationSubscription entity =
                new IntegrationSubscription();

        entity.setEventId(request.getEventId());
        entity.setSubscriber(request.getSubscriber());
        entity.setDeliveryType(request.getDeliveryType());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationSubscription entity,
            IntegrationSubscriptionRequest request
    ) {
        entity.setEventId(request.getEventId());
        entity.setSubscriber(request.getSubscriber());
        entity.setDeliveryType(request.getDeliveryType());
        entity.setStatus(request.getStatus());
    }

    public IntegrationSubscriptionResponse toResponse(
            IntegrationSubscription entity
    ) {
        IntegrationSubscriptionResponse response =
                new IntegrationSubscriptionResponse();

        response.setSubscriptionId(
                entity.getSubscriptionId()
        );
        response.setEventId(entity.getEventId());
        response.setSubscriber(entity.getSubscriber());
        response.setDeliveryType(entity.getDeliveryType());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}