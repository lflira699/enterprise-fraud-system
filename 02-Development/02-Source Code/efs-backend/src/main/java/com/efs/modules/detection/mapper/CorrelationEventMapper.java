package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.CorrelationEventRequest;
import com.efs.modules.detection.dto.CorrelationEventResponse;
import com.efs.modules.detection.entity.CorrelationEvent;
import org.springframework.stereotype.Component;

@Component
public class CorrelationEventMapper {

    public CorrelationEvent toEntity(
            CorrelationEventRequest request) {

        CorrelationEvent correlationEvent =
                new CorrelationEvent();

        correlationEvent.setCorrelationId(
                request.getCorrelationId()
        );

        correlationEvent.setEventId(
                request.getEventId()
        );

        correlationEvent.setEventRole(
                request.getEventRole()
        );

        return correlationEvent;
    }

    public CorrelationEventResponse toResponse(
            CorrelationEvent correlationEvent) {

        CorrelationEventResponse response =
                new CorrelationEventResponse();

        response.setCorrelationEventId(
                correlationEvent.getCorrelationEventId()
        );

        response.setCorrelationId(
                correlationEvent.getCorrelationId()
        );

        response.setEventId(
                correlationEvent.getEventId()
        );

        response.setEventRole(
                correlationEvent.getEventRole()
        );

        response.setCreatedAt(
                correlationEvent.getCreatedAt()
        );

        return response;
    }
}