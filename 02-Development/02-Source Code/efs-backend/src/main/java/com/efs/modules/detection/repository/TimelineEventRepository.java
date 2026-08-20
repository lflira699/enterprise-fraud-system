package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimelineEventRepository
        extends JpaRepository<TimelineEvent, UUID> {

    Optional<TimelineEvent> findByTimelineEventId(
            UUID timelineEventId
    );

    List<TimelineEvent>
    findByCustomerIdOrderByEventTimestampAsc(
            UUID customerId
    );

    List<TimelineEvent>
    findByTransactionIdOrderByEventTimestampAsc(
            UUID transactionId
    );

    List<TimelineEvent>
    findByCorrelationIdOrderByEventTimestampAsc(
            UUID correlationId
    );

    List<TimelineEvent>
    findByCorrelationIdOrderBySequenceNumberAsc(
            UUID correlationId
    );

    List<TimelineEvent>
    findByEventTypeOrderByEventTimestampAsc(
            String eventType
    );

    List<TimelineEvent>
    findByEventSourceOrderByEventTimestampAsc(
            String eventSource
    );
}