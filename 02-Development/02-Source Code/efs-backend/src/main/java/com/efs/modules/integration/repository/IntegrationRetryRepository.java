package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationRetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationRetryRepository
        extends JpaRepository<IntegrationRetry, UUID> {

    List<IntegrationRetry> findByMessageIdOrderByRetryNumberAsc(
            UUID messageId
    );

    List<IntegrationRetry> findByRetryStatusOrderByCreatedAtDesc(
            String retryStatus
    );
}