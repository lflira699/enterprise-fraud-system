package com.efs.modules.administration.service;

import com.efs.modules.administration.entity.SystemConfiguration;

import java.util.Optional;
import java.util.UUID;

public interface SystemConfigurationServiceInterface {

    Optional<SystemConfiguration> resolveConfiguration(
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