package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationEventRepository
        extends JpaRepository<IntegrationEvent, UUID> {

    List<IntegrationEvent> findByEventNameOrderByPublishedAtDesc(
            String eventName
    );

    List<IntegrationEvent> findByStatusOrderByPublishedAtDesc(
            String status
    );
}