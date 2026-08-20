package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleHistoryRepository
        extends JpaRepository<RuleHistory, UUID> {

    Optional<RuleHistory> findByHistoryId(
            UUID historyId
    );

    List<RuleHistory> findByEntityTypeAndEntityIdOrderByChangedAtDesc(
            String entityType,
            UUID entityId
    );

    List<RuleHistory> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );

    List<RuleHistory> findByOperationTypeOrderByChangedAtDesc(
            String operationType
    );

    List<RuleHistory> findByCorrelationIdOrderByChangedAtDesc(
            UUID correlationId
    );
}