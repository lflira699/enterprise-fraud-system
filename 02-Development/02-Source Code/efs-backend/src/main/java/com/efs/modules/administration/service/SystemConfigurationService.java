package com.efs.modules.administration.service;

import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.repository.SystemConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SystemConfigurationService
        implements SystemConfigurationServiceInterface {

    private final SystemConfigurationRepository repository;

    public SystemConfigurationService(
            SystemConfigurationRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<SystemConfiguration> resolveConfiguration(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    ) {
        validateConfigurationKey(configurationKey);

        if (tenantId != null && organizationId == null) {
            throw new IllegalArgumentException(
                    "Tenant-scoped configuration requires organizationId"
            );
        }

        if (organizationId != null && tenantId != null) {
            Optional<SystemConfiguration> tenantConfiguration =
                    repository
                            .findFirstByConfigurationKeyAndOrganizationIdAndTenantIdOrderByUpdatedAtDesc(
                                    configurationKey,
                                    organizationId,
                                    tenantId
                            );

            if (tenantConfiguration.isPresent()) {
                return tenantConfiguration;
            }
        }

        if (organizationId != null) {
            Optional<SystemConfiguration> organizationConfiguration =
                    repository
                            .findFirstByConfigurationKeyAndOrganizationIdAndTenantIdIsNullOrderByUpdatedAtDesc(
                                    configurationKey,
                                    organizationId
                            );

            if (organizationConfiguration.isPresent()) {
                return organizationConfiguration;
            }
        }

        return repository
                .findFirstByConfigurationKeyAndOrganizationIdIsNullAndTenantIdIsNullOrderByUpdatedAtDesc(
                        configurationKey
                );
    }

    @Override
    public Optional<String> resolveConfigurationValue(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    ) {
        return resolveConfiguration(
                configurationKey,
                organizationId,
                tenantId
        ).map(SystemConfiguration::getConfigurationValue);
    }

    private void validateConfigurationKey(String configurationKey) {
        if (configurationKey == null || configurationKey.isBlank()) {
            throw new IllegalArgumentException(
                    "configurationKey must not be null or blank"
            );
        }
    }
}