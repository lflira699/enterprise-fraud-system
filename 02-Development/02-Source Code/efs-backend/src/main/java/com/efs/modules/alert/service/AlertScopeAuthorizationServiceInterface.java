package com.efs.modules.alert.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.shared.security.SecurityContext;

import java.util.UUID;

public interface AlertScopeAuthorizationServiceInterface {

    UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission
    );

    void requireVisibleAlert(
            UUID alertId,
            UserAccountReference actor,
            String notFoundMessage
    );

    boolean isAlertVisible(
            UUID alertId,
            UserAccountReference actor
    );

    boolean isScopeVisible(
            UUID organizationId,
            UUID tenantId,
            UserAccountReference actor
    );
}