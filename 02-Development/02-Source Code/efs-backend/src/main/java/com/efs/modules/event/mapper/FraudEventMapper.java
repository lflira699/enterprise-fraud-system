package com.efs.modules.event.mapper;

import com.efs.modules.event.dto.FraudEventResponse;
import com.efs.modules.event.entity.FraudEvent;

public final class FraudEventMapper {

    private FraudEventMapper() {
    }

    public static FraudEventResponse toResponse(
            FraudEvent fraudEvent) {

        FraudEventResponse response =
                new FraudEventResponse();

        response.setFraudEventId(
                fraudEvent.getFraudEventId()
        );

        response.setOrganizationId(
                fraudEvent.getOrganizationId()
        );

        response.setTenantId(
                fraudEvent.getTenantId()
        );

        response.setTransactionId(
                fraudEvent.getTransactionId()
        );

        response.setEventType(
                fraudEvent.getEventType()
        );

        response.setSourceType(
                fraudEvent.getSourceType()
        );

        response.setSourceReference(
                fraudEvent.getSourceReference()
        );

        response.setIdempotencyKey(
                fraudEvent.getIdempotencyKey()
        );

        response.setCorrelationId(
                fraudEvent.getCorrelationId()
        );

        response.setNormalizedPayload(
                fraudEvent.getNormalizedPayload()
        );

        response.setOccurredAt(
                fraudEvent.getOccurredAt()
        );

        response.setReceivedAt(
                fraudEvent.getReceivedAt()
        );

        response.setCreatedAt(
                fraudEvent.getCreatedAt()
        );

        return response;
    }
}