package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.CorrelationEventRequest;
import com.efs.modules.detection.dto.CorrelationEventResponse;

import java.util.List;
import java.util.UUID;

public interface CorrelationEventServiceInterface {

    CorrelationEventResponse createCorrelationEvent(
            CorrelationEventRequest request
    );

    CorrelationEventResponse getCorrelationEventById(
            UUID correlationEventId
    );

    List<CorrelationEventResponse> getEventsByCorrelation(
            UUID correlationId
    );

    List<CorrelationEventResponse> getCorrelationsByEvent(
            UUID eventId
    );

    List<CorrelationEventResponse> getEventsByRole(
            String eventRole
    );
}