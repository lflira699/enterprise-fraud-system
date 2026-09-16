package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.ConfigurationChangeRequestCreateRequest;
import com.efs.modules.administration.dto.ConfigurationChangeRequestResponse;
import com.efs.modules.administration.dto.ConfigurationEffectiveResponse;
import com.efs.modules.administration.dto.ConfigurationVersionResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface ConfigurationGovernanceServiceInterface {

    List<ConfigurationEffectiveResponse>
    getEffectiveConfigurations(
            UUID tenantId,
            SecurityContext securityContext
    );

    ConfigurationEffectiveResponse
    getEffectiveConfiguration(
            String configurationKey,
            UUID tenantId,
            SecurityContext securityContext
    );

    List<ConfigurationVersionResponse>
    getAppliedVersions(
            String configurationKey,
            UUID tenantId,
            SecurityContext securityContext
    );

    ConfigurationChangeRequestResponse createChangeRequest(
            ConfigurationChangeRequestCreateRequest request,
            SecurityContext securityContext
    );

    ConfigurationChangeRequestResponse getChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext
    );

    ConfigurationChangeRequestResponse approveChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext
    );

    ConfigurationChangeRequestResponse rejectChangeRequest(
            UUID changeRequestId,
            String rejectionReason,
            SecurityContext securityContext
    );

    ConfigurationChangeRequestResponse publishChangeRequest(
            UUID changeRequestId,
            SecurityContext securityContext
    );
}