package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationHistoryRepository
        extends JpaRepository<IntegrationHistory, UUID> {

    List<IntegrationHistory> findByMessageIdOrderByArchivedAtDesc(
            UUID messageId
    );

    List<IntegrationHistory> findByCorrelationIdOrderByArchivedAtDesc(
            UUID correlationId
    );
}