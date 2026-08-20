package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.TimelineEventRequest;
import com.efs.modules.detection.dto.TimelineEventResponse;
import com.efs.modules.detection.entity.TimelineEvent;
import org.springframework.stereotype.Component;

@Component
public class TimelineEventMapper {

    public TimelineEvent toEntity(
            TimelineEventRequest request) {

        TimelineEvent timelineEvent =
                new TimelineEvent();

        timelineEvent.setCustomerId(
                request.getCustomerId()
        );

        timelineEvent.setTransactionId(
                request.getTransactionId()
        );

        timelineEvent.setCorrelationId(
                request.getCorrelationId()
        );

        timelineEvent.setEventType(
                request.getEventType()
        );

        timelineEvent.setEventSource(
                request.getEventSource()
        );

        timelineEvent.setEventReferenceId(
                request.getEventReferenceId()
        );

        timelineEvent.setEventTimestamp(
                request.getEventTimestamp()
        );

        timelineEvent.setSequenceNumber(
                request.getSequenceNumber()
        );

        timelineEvent.setEventSummary(
                request.getEventSummary()
        );

        timelineEvent.setEventData(
                request.getEventData()
        );

        return timelineEvent;
    }

    public TimelineEventResponse toResponse(
            TimelineEvent timelineEvent) {

        TimelineEventResponse response =
                new TimelineEventResponse();

        response.setTimelineEventId(
                timelineEvent.getTimelineEventId()
        );

        response.setCustomerId(
                timelineEvent.getCustomerId()
        );

        response.setTransactionId(
                timelineEvent.getTransactionId()
        );

        response.setCorrelationId(
                timelineEvent.getCorrelationId()
        );

        response.setEventType(
                timelineEvent.getEventType()
        );

        response.setEventSource(
                timelineEvent.getEventSource()
        );

        response.setEventReferenceId(
                timelineEvent.getEventReferenceId()
        );

        response.setEventTimestamp(
                timelineEvent.getEventTimestamp()
        );

        response.setSequenceNumber(
                timelineEvent.getSequenceNumber()
        );

        response.setEventSummary(
                timelineEvent.getEventSummary()
        );

        response.setEventData(
                timelineEvent.getEventData()
        );

        response.setCreatedAt(
                timelineEvent.getCreatedAt()
        );

        return response;
    }
}