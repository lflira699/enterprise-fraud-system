package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAccountRepository
        extends JpaRepository<UserAccount, UUID> {

    List<UserAccount>
            findByUserIdInAndOrganizationIdAndTenantIdAndAccountStatus(
                    List<UUID> userIds,
                    UUID organizationId,
                    UUID tenantId,
                    String accountStatus
            );
}