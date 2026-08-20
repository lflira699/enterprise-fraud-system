package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.Correlation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CorrelationRepository
        extends JpaRepository<Correlation, UUID> {

    Optional<Correlation> findByCorrelationId(
            UUID correlationId
    );

    List<Correlation> findByCustomerIdOrderByCreatedAtDesc(
            UUID customerId
    );

    List<Correlation> findByTransactionIdOrderByCreatedAtDesc(
            UUID transactionId
    );

    List<Correlation> findByCorrelationKeyOrderByCreatedAtDesc(
            String correlationKey
    );

    List<Correlation> findByCorrelationTypeOrderByCreatedAtDesc(
            String correlationType
    );

    List<Correlation> findByCorrelationStatusOrderByCreatedAtDesc(
            String correlationStatus
    );
}