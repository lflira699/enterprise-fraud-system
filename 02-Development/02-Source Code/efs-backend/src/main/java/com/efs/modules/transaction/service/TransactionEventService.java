package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;
import com.efs.modules.transaction.entity.TransactionEvent;
import com.efs.modules.transaction.mapper.TransactionEventMapper;
import com.efs.modules.transaction.repository.TransactionEventRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionEventService
        implements TransactionEventServiceInterface {

    private final TransactionEventRepository transactionEventRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionEventMapper transactionEventMapper;

    public TransactionEventService(
            TransactionEventRepository transactionEventRepository,
            TransactionRepository transactionRepository,
            TransactionEventMapper transactionEventMapper) {

        this.transactionEventRepository = transactionEventRepository;
        this.transactionRepository = transactionRepository;
        this.transactionEventMapper = transactionEventMapper;
    }

    @Override
    @Transactional
    public TransactionEventResponse createEvent(
            UUID transactionId,
            TransactionEventRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionEvent event =
                transactionEventMapper.toEntity(request);

        event.setTransactionId(transactionId);

        if (event.getEventTimestamp() == null) {
            event.setEventTimestamp(LocalDateTime.now());
        }

        TransactionEvent savedEvent =
                transactionEventRepository.save(event);

        return transactionEventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionEventResponse getEventById(
            UUID eventId) {

        TransactionEvent event =
                transactionEventRepository
                        .findByEventId(eventId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction event not found: "
                                                + eventId
                                )
                        );

        return transactionEventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionEventResponse> getEventsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionEventRepository
                .findByTransactionIdOrderByEventTimestampDesc(transactionId)
                .stream()
                .map(transactionEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionEventResponse> getEventsByType(
            String eventType) {

        return transactionEventRepository
                .findByEventTypeOrderByEventTimestampDesc(eventType)
                .stream()
                .map(transactionEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionEventResponse> getEventsByComponentName(
            String componentName) {

        return transactionEventRepository
                .findByComponentNameOrderByEventTimestampDesc(componentName)
                .stream()
                .map(transactionEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionEventResponse> getEventsByCorrelationId(
            UUID correlationId) {

        return transactionEventRepository
                .findByCorrelationIdOrderByEventTimestampDesc(correlationId)
                .stream()
                .map(transactionEventMapper::toResponse)
                .toList();
    }
}