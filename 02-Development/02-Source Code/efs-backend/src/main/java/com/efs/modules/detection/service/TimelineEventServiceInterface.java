package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.TimelineEventRequest;
import com.efs.modules.detection.dto.TimelineEventResponse;

import java.util.List;
import java.util.UUID;

public interface TimelineEventServiceInterface {

    TimelineEventResponse createTimelineEvent(
            TimelineEventRequest request
    );

    TimelineEventResponse getTimelineEventById(
            UUID timelineEventId
    );

    List<TimelineEventResponse> getEventsByCustomer(
            UUID customerId
    );

    List<TimelineEventResponse> getEventsByTransaction(
            UUID transactionId
    );

    List<TimelineEventResponse> getEventsByCorrelation(
            UUID correlationId
    );

    List<TimelineEventResponse> getEventsByType(
            String eventType
    );

    List<TimelineEventResponse> getEventsBySource(
            String eventSource
    );
}