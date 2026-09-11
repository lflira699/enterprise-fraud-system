package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserAccountLookupService
        implements UserAccountLookupServiceInterface {

    private static final String ACTIVE_ACCOUNT_STATUS =
            "ACTIVE";

    private final UserAccountRepository
            userAccountRepository;

    public UserAccountLookupService(
            UserAccountRepository userAccountRepository) {

        this.userAccountRepository =
                userAccountRepository;
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
}