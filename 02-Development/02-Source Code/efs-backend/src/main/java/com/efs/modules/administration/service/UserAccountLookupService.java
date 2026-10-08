package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.administration.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserAccountLookupService
        implements UserAccountLookupServiceInterface {

    private static final String ACTIVE_ACCOUNT_STATUS =
            "ACTIVE";

    private final UserAccountRepository
            userAccountRepository;

    private final UserRoleRepository
            userRoleRepository;

    public UserAccountLookupService(
            UserAccountRepository userAccountRepository,
            UserRoleRepository userRoleRepository) {

        this.userAccountRepository =
                userAccountRepository;

        this.userRoleRepository =
                userRoleRepository;
    }

    @Override
    public List<UserAccountReference> findAuthorizedUsers(
            UUID organizationId,
            UUID tenantId,
            List<UUID> userIds) {

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "organizationId is required"
            );
        }

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "tenantId is required"
            );
        }

        if (userIds == null) {
            throw new IllegalArgumentException(
                    "userIds are required"
            );
        }

        for (UUID userId : userIds) {

            if (userId == null) {
                throw new IllegalArgumentException(
                        "userIds must not contain null values"
                );
            }
        }

        if (userIds.isEmpty()) {
            return List.of();
        }

        return userAccountRepository
                .findByUserIdInAndOrganizationIdAndTenantIdAndAccountStatus(
                        userIds,
                        organizationId,
                        tenantId,
                        ACTIVE_ACCOUNT_STATUS
                )
                .stream()
                .map(
                        this::toReference
                )
                .toList();
    }

    private UserAccountReference toReference(
            UserAccount userAccount) {

        return new UserAccountReference(
                userAccount.getUserId(),
                userAccount.getOrganizationId(),
                userAccount.getTenantId(),
                userAccount.getEmail()
        );
    }

    @Override
    public UserAccountReference getAuthorizedUser(
            UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "userId is required"
            );
        }

        UserAccount userAccount =
                userAccountRepository
                        .findById(
                                userId
                        )
                        .filter(
                                account ->
                                        ACTIVE_ACCOUNT_STATUS.equals(
                                                account.getAccountStatus()
                                        )
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Authorized user account is not available: "
                                                + userId
                                )
                        );

        if (userAccount.getOrganizationId() == null) {
            throw new IllegalStateException(
                    "Authorized user organizationId is required"
            );
        }

        return toReference(
                userAccount
        );
    }

    @Override
    public Set<String> getAuthorizedRoleCodes(
            UUID userId) {

        UserAccountReference authorizedUser =
                getAuthorizedUser(
                        userId
                );

        LocalDateTime asOf =
                LocalDateTime.now();

        return Set.copyOf(
                userRoleRepository
                        .findActiveRoleCodesByUserId(
                                userId,
                                authorizedUser.organizationId(),
                                asOf
                        )
        );
    }

    @Override
    public Set<String> getAuthorizedPermissionCodes(
            UUID userId) {

        UserAccountReference authorizedUser =
                getAuthorizedUser(
                        userId
                );

        LocalDateTime asOf =
                LocalDateTime.now();

        return Set.copyOf(
                userRoleRepository
                        .findActivePermissionCodesByUserId(
                                userId,
                                authorizedUser.organizationId(),
                                asOf
                        )
        );
    }
}
