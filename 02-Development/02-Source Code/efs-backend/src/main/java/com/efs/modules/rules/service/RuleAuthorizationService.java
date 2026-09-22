package com.efs.modules.rules.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class RuleAuthorizationService
        implements RuleAuthorizationServiceInterface {

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    public RuleAuthorizationService(
            UserAccountLookupServiceInterface
                    userAccountLookupService) {

        this.userAccountLookupService =
                userAccountLookupService;
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
                                securityContext
                                        .getUserId()
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
    public void requireActor(
            UserAccountReference actor,
            UUID requestedActorId,
            String actorField) {

        Objects.requireNonNull(
                actor,
                "actor is required"
        );

        Objects.requireNonNull(
                actorField,
                "actorField is required"
        );

        if (!Objects.equals(
                actor.userId(),
                requestedActorId
        )) {

            throw new AccessDeniedException(
                    "Authenticated actor does not match "
                            + actorField
            );
        }
    }
}