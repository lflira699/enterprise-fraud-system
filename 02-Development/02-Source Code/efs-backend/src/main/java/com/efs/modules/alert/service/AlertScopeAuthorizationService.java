package com.efs.modules.alert.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.repository.AlertRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AlertScopeAuthorizationService
        implements AlertScopeAuthorizationServiceInterface {

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AlertRepository
            alertRepository;

    public AlertScopeAuthorizationService(
            UserAccountLookupServiceInterface userAccountLookupService,
            AlertRepository alertRepository) {

        this.userAccountLookupService =
                userAccountLookupService;

        this.alertRepository =
                alertRepository;
    }

    @Override
    public UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Objects.requireNonNull(
                requiredPermission,
                "requiredPermission is required"
        );

        if (!securityContext.hasPermission(
                requiredPermission
        )) {

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + requiredPermission
            );
        }

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext.getUserId()
                        );

        if (!Objects.equals(
                securityContext.getTenantId(),
                actor.tenantId()
        )) {

            throw new AccessDeniedException(
                    "Authenticated tenant scope mismatch"
            );
        }

        return actor;
    }

    @Override
    public void requireVisibleAlert(
            UUID alertId,
            UserAccountReference actor,
            String notFoundMessage) {

        if (!isAlertVisible(
                alertId,
                actor
        )) {

            throw new ResourceNotFoundException(
                    notFoundMessage
            );
        }
    }

    @Override
    public boolean isAlertVisible(
            UUID alertId,
            UserAccountReference actor) {

        Objects.requireNonNull(
                alertId,
                "alertId is required"
        );

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

        return alertRepository
                .findByAlertId(
                        alertId
                )
                .map(
                        alert ->
                                isVisible(
                                        alert,
                                        actor
                                )
                )
                .orElse(false);
    }

    @Override
    public boolean isScopeVisible(
            UUID organizationId,
            UUID tenantId,
            UserAccountReference actor) {

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

        if (
            organizationId == null
                    ||
            actor.organizationId() == null
                    ||
            !actor.organizationId()
                    .equals(
                            organizationId
                    )
        ) {
            return false;
        }

        UUID actorTenantId =
                actor.tenantId();

        if (actorTenantId == null) {
            return true;
        }

        return actorTenantId.equals(
                tenantId
        );
    }

    private boolean isVisible(
            Alert alert,
            UserAccountReference actor) {

        if (alert == null) {
            return false;
        }

        return isScopeVisible(
                alert.getOrganizationId(),
                alert.getTenantId(),
                actor
        );
    }
}