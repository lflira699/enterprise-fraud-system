package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationMessageRepository
        extends JpaRepository<IntegrationMessage, UUID> {

    List<IntegrationMessage> findByConnectorIdOrderByCreatedAtDesc(
            UUID connectorId
    );

    List<IntegrationMessage> findByCorrelationIdOrderByCreatedAtDesc(
            UUID correlationId
    );

    List<IntegrationMessage> findByMessageStatusOrderByCreatedAtDesc(
            String messageStatus
    );
}