package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserAccountLookupServiceInterface {

    List<UserAccountReference> findAuthorizedUsers(
            UUID organizationId,
            UUID tenantId,
            List<UUID> userIds
    );

    UserAccountReference getAuthorizedUser(
            UUID userId
    );

    Set<String> getAuthorizedRoleCodes(
            UUID userId
    );

    Set<String> getAuthorizedPermissionCodes(
            UUID userId
    );
}
