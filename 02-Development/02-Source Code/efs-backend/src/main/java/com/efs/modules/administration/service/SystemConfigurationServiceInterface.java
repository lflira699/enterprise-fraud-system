package com.efs.modules.administration.service;

import com.efs.modules.administration.entity.SystemConfiguration;

import java.util.Optional;
import java.util.UUID;

public interface SystemConfigurationServiceInterface {

    record ResolvedConfiguration(
            String configurationValue,
            String configurationType) {
    }

    Optional<SystemConfiguration> resolveConfiguration(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    );

    Optional<ResolvedConfiguration> resolveConfigurationDetails(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    );

    Optional<String> resolveConfigurationValue(
            String configurationKey,
            UUID organizationId,
            UUID tenantId
    );
}