package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.SystemConfiguration;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT configuration
            FROM SystemConfiguration configuration
            WHERE configuration.configurationKey = :configurationKey
              AND configuration.organizationId = :organizationId
              AND configuration.tenantId IS NULL
            """)
    Optional<SystemConfiguration>
    findOrganizationConfigurationForUpdate(
            @Param("configurationKey")
            String configurationKey,
            @Param("organizationId")
            UUID organizationId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT configuration
            FROM SystemConfiguration configuration
            WHERE configuration.configurationKey = :configurationKey
              AND configuration.organizationId = :organizationId
              AND configuration.tenantId = :tenantId
            """)
    Optional<SystemConfiguration>
    findTenantConfigurationForUpdate(
            @Param("configurationKey")
            String configurationKey,
            @Param("organizationId")
            UUID organizationId,
            @Param("tenantId")
            UUID tenantId
    );
}