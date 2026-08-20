package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.CorrelationEventRequest;
import com.efs.modules.detection.dto.CorrelationEventResponse;
import com.efs.modules.detection.entity.CorrelationEvent;
import com.efs.modules.detection.mapper.CorrelationEventMapper;
import com.efs.modules.detection.repository.CorrelationEventRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CorrelationEventService
        implements CorrelationEventServiceInterface {

    private final CorrelationEventRepository correlationEventRepository;
    private final CorrelationEventMapper correlationEventMapper;

    public CorrelationEventService(
            CorrelationEventRepository correlationEventRepository,
            CorrelationEventMapper correlationEventMapper) {

        this.correlationEventRepository =
                correlationEventRepository;
        this.correlationEventMapper =
                correlationEventMapper;
    }

    @Override
    @Transactional
    public CorrelationEventResponse createCorrelationEvent(
            CorrelationEventRequest request) {

        CorrelationEvent correlationEvent =
                correlationEventMapper.toEntity(request);

        correlationEvent.setCreatedAt(
                LocalDateTime.now()
        );

        CorrelationEvent savedCorrelationEvent =
                correlationEventRepository.save(
                        correlationEvent
                );

        return correlationEventMapper.toResponse(
                savedCorrelationEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CorrelationEventResponse getCorrelationEventById(
            UUID correlationEventId) {

        CorrelationEvent correlationEvent =
                correlationEventRepository
                        .findByCorrelationEventId(
                                correlationEventId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Correlation event not found: "
                                                + correlationEventId
                                )
                        );

        return correlationEventMapper.toResponse(
                correlationEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationEventResponse>
    getEventsByCorrelation(UUID correlationId) {

        return correlationEventRepository
                .findByCorrelationIdOrderByCreatedAtAsc(
                        correlationId
                )
                .stream()
                .map(correlationEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationEventResponse>
    getCorrelationsByEvent(UUID eventId) {

        return correlationEventRepository
                .findByEventIdOrderByCreatedAtAsc(
                        eventId
                )
                .stream()
                .map(correlationEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationEventResponse>
    getEventsByRole(String eventRole) {

        return correlationEventRepository
                .findByEventRoleOrderByCreatedAtAsc(
                        eventRole
                )
                .stream()
                .map(correlationEventMapper::toResponse)
                .toList();
    }
}