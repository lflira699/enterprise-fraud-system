package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountDashboardScopeLookupTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountLookupService userAccountLookupService;

    @Test
    void shouldReturnAuthoritativeActiveUserScope() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        UserAccount account =
                new UserAccount();

        account.setUserId(
                userId
        );

        account.setOrganizationId(
                organizationId
        );

        account.setTenantId(
                tenantId
        );

        account.setEmail(
                "dashboard.scope@example.com"
        );

        account.setAccountStatus(
                "ACTIVE"
        );

        when(
                userAccountRepository.findById(
                        userId
                )
        ).thenReturn(
                Optional.of(
                        account
                )
        );

        UserAccountReference reference =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        assertEquals(
                userId,
                reference.userId()
        );

        assertEquals(
                organizationId,
                reference.organizationId()
        );

        assertEquals(
                tenantId,
                reference.tenantId()
        );
    }

    @Test
    void shouldSupportAuthorizedOrganizationLevelUser() {

        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        UserAccount account =
                new UserAccount();

        account.setUserId(
                userId
        );

        account.setOrganizationId(
                organizationId
        );

        account.setTenantId(
                null
        );

        account.setEmail(
                "dashboard.organization@example.com"
        );

        account.setAccountStatus(
                "ACTIVE"
        );

        when(
                userAccountRepository.findById(
                        userId
                )
        ).thenReturn(
                Optional.of(
                        account
                )
        );

        UserAccountReference reference =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        assertEquals(
                organizationId,
                reference.organizationId()
        );

        assertEquals(
                null,
                reference.tenantId()
        );
    }

    @Test
    void inactiveUserShouldFailClosed() {

        UUID userId = UUID.randomUUID();

        UserAccount account =
                new UserAccount();

        account.setUserId(
                userId
        );

        account.setOrganizationId(
                UUID.randomUUID()
        );

        account.setAccountStatus(
                "INACTIVE"
        );

        when(
                userAccountRepository.findById(
                        userId
                )
        ).thenReturn(
                Optional.of(
                        account
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        )
        );
    }

    @Test
    void missingOrganizationShouldFailClosed() {

        UUID userId = UUID.randomUUID();

        UserAccount account =
                new UserAccount();

        account.setUserId(
                userId
        );

        account.setAccountStatus(
                "ACTIVE"
        );

        when(
                userAccountRepository.findById(
                        userId
                )
        ).thenReturn(
                Optional.of(
                        account
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        )
        );
    }

    @Test
    void missingUserIdShouldFailBeforeRepositoryAccess() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userAccountLookupService
                        .getAuthorizedUser(
                                null
                        )
        );

        verifyNoInteractions(
                userAccountRepository
        );
    }
}
