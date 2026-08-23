package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationEndpointRepository
        extends JpaRepository<IntegrationEndpoint, UUID> {

    Optional<IntegrationEndpoint> findByEndpointCode(String endpointCode);

    List<IntegrationEndpoint> findByStatusOrderByEndpointNameAsc(
            String status
    );
}