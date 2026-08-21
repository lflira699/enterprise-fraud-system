package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemConfigurationRepository
        extends JpaRepository<SystemConfiguration, UUID> {

    Optional<SystemConfiguration>
    findFirstByConfigurationKeyAndOrganizationIdAndTenantIdOrderByUpdatedAtDesc(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    );

    Optional<SystemConfiguration>
    findFirstByConfigurationKeyAndOrganizationIdAndTenantIdIsNullOrderByUpdatedAtDesc(
            String configurationKey,
            UUID organizationId
    );

    Optional<SystemConfiguration>
    findFirstByConfigurationKeyAndOrganizationIdIsNullAndTenantIdIsNullOrderByUpdatedAtDesc(
            String configurationKey
    );

    List<SystemConfiguration>
    findByOrganizationIdAndTenantIdOrderByConfigurationKeyAsc(
            UUID organizationId,
            UUID tenantId
    );

    List<SystemConfiguration>
    findByOrganizationIdAndTenantIdIsNullOrderByConfigurationKeyAsc(
            UUID organizationId
    );

    List<SystemConfiguration>
    findByConfigurationKeyOrderByUpdatedAtDesc(
            String configurationKey
    );
}