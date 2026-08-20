package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.CorrelationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CorrelationEventRepository
        extends JpaRepository<CorrelationEvent, UUID> {

    Optional<CorrelationEvent> findByCorrelationEventId(
            UUID correlationEventId
    );

    List<CorrelationEvent>
    findByCorrelationIdOrderByCreatedAtAsc(
            UUID correlationId
    );

    List<CorrelationEvent>
    findByEventIdOrderByCreatedAtAsc(
            UUID eventId
    );

    List<CorrelationEvent>
    findByEventRoleOrderByCreatedAtAsc(
            String eventRole
    );
}