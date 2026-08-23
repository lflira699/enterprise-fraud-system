package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationWebhookRepository
        extends JpaRepository<IntegrationWebhook, UUID> {

    List<IntegrationWebhook> findByEndpointId(UUID endpointId);

    List<IntegrationWebhook> findByStatusOrderByCreatedAtDesc(
            String status
    );
}