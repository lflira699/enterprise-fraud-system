package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationConnector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationConnectorRepository
        extends JpaRepository<IntegrationConnector, UUID> {

    List<IntegrationConnector> findByEndpointId(UUID endpointId);

    List<IntegrationConnector> findByStatusOrderByConnectorNameAsc(
            String status
    );
}