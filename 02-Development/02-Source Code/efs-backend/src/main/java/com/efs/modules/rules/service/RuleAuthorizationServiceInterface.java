package com.efs.modules.rules.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.shared.security.SecurityContext;

import java.util.UUID;

public interface RuleAuthorizationServiceInterface {

    UserAccountReference authorize(
            SecurityContext securityContext,
            String requiredPermission
    );

    void requireActor(
            UserAccountReference actor,
            UUID requestedActorId,
            String actorField
    );
}