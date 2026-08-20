package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.TimelineEventRequest;
import com.efs.modules.detection.dto.TimelineEventResponse;
import com.efs.modules.detection.entity.TimelineEvent;
import com.efs.modules.detection.mapper.TimelineEventMapper;
import com.efs.modules.detection.repository.TimelineEventRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TimelineEventService
        implements TimelineEventServiceInterface {

    private final TimelineEventRepository timelineEventRepository;
    private final TimelineEventMapper timelineEventMapper;

    public TimelineEventService(
            TimelineEventRepository timelineEventRepository,
            TimelineEventMapper timelineEventMapper) {

        this.timelineEventRepository =
                timelineEventRepository;
        this.timelineEventMapper =
                timelineEventMapper;
    }

    @Override
    @Transactional
    public TimelineEventResponse createTimelineEvent(
            TimelineEventRequest request) {

        TimelineEvent timelineEvent =
                timelineEventMapper.toEntity(request);

        timelineEvent.setCreatedAt(
                LocalDateTime.now()
        );

        TimelineEvent savedEvent =
                timelineEventRepository.save(timelineEvent);

        return timelineEventMapper.toResponse(
                savedEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TimelineEventResponse getTimelineEventById(
            UUID timelineEventId) {

        TimelineEvent timelineEvent =
                timelineEventRepository
                        .findByTimelineEventId(timelineEventId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Timeline event not found: "
                                                + timelineEventId
                                )
                        );

        return timelineEventMapper.toResponse(
                timelineEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineEventResponse>
    getEventsByCustomer(UUID customerId) {

        return timelineEventRepository
                .findByCustomerIdOrderByEventTimestampAsc(customerId)
                .stream()
                .map(timelineEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineEventResponse>
    getEventsByTransaction(UUID transactionId) {

        return timelineEventRepository
                .findByTransactionIdOrderByEventTimestampAsc(
                        transactionId
                )
                .stream()
                .map(timelineEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineEventResponse>
    getEventsByCorrelation(UUID correlationId) {

        return timelineEventRepository
                .findByCorrelationIdOrderByEventTimestampAsc(
                        correlationId
                )
                .stream()
                .map(timelineEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineEventResponse>
    getEventsByType(String eventType) {

        return timelineEventRepository
                .findByEventTypeOrderByEventTimestampAsc(
                        eventType
                )
                .stream()
                .map(timelineEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineEventResponse>
    getEventsBySource(String eventSource) {

        return timelineEventRepository
                .findByEventSourceOrderByEventTimestampAsc(
                        eventSource
                )
                .stream()
                .map(timelineEventMapper::toResponse)
                .toList();
    }
}