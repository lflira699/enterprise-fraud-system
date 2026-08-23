package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationSubscriptionRepository
        extends JpaRepository<IntegrationSubscription, UUID> {

    List<IntegrationSubscription> findByEventId(UUID eventId);

    List<IntegrationSubscription> findByStatusOrderByCreatedAtDesc(
            String status
    );
}