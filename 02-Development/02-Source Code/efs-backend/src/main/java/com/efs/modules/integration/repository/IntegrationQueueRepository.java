package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationQueueRepository
        extends JpaRepository<IntegrationQueue, UUID> {

    List<IntegrationQueue> findByBrokerOrderByQueueNameAsc(
            String broker
    );

    List<IntegrationQueue> findByStatusOrderByQueueNameAsc(
            String status
    );
}