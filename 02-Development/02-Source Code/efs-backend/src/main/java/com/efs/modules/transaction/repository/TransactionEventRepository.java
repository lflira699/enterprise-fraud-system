package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionEventRepository
        extends JpaRepository<TransactionEvent, UUID> {

    Optional<TransactionEvent> findByEventId(
            UUID eventId
    );

    List<TransactionEvent> findByTransactionIdOrderByEventTimestampDesc(
            UUID transactionId
    );

    List<TransactionEvent> findByEventTypeOrderByEventTimestampDesc(
            String eventType
    );

    List<TransactionEvent> findByComponentNameOrderByEventTimestampDesc(
            String componentName
    );

    List<TransactionEvent> findByCorrelationIdOrderByEventTimestampDesc(
            UUID correlationId
    );
}