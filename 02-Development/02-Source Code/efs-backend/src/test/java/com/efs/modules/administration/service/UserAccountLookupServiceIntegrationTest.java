package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserAccountLookupServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "17117117-1171-4171-8171-171171171171"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "17217217-2172-4172-8172-172172172172"
            );

    private static final UUID OTHER_TENANT_ID =
            UUID.fromString(
                    "17317317-3173-4173-8173-173173173173"
            );

    private static final UUID ACTIVE_USER_ID =
            UUID.fromString(
                    "17417417-4174-4174-8174-174174174174"
            );

    private static final UUID INACTIVE_USER_ID =
            UUID.fromString(
                    "17517517-5175-4175-8175-175175175175"
            );

    private static final UUID OTHER_TENANT_USER_ID =
            UUID.fromString(
                    "17617617-6176-4176-8176-176176176176"
            );

    @Autowired
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();

        insertTenant(
                TENANT_ID,
                "EFS-USER-LOOKUP-TENANT"
        );

        insertTenant(
                OTHER_TENANT_ID,
                "EFS-USER-LOOKUP-OTHER-TENANT"
        );

        insertUser(
                ACTIVE_USER_ID,
                TENANT_ID,
                "ACTIVE",
                "active.user@example.com"
        );

        insertUser(
                INACTIVE_USER_ID,
                TENANT_ID,
                "INACTIVE",
                "inactive.user@example.com"
        );

        insertUser(
                OTHER_TENANT_USER_ID,
                OTHER_TENANT_ID,
                "ACTIVE",
                "other.tenant.user@example.com"
        );
    }

    @Test
    void shouldReturnOnlyActiveUsersWithinRequestedScope() {

        List<UserAccountReference> users =
                userAccountLookupService
                        .findAuthorizedUsers(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                List.of(
                                        ACTIVE_USER_ID,
                                        INACTIVE_USER_ID,
                                        OTHER_TENANT_USER_ID
                                )
                        );

        assertEquals(
                1,
                users.size()
        );

        UserAccountReference user =
                users.get(0);

        assertEquals(
                ACTIVE_USER_ID,
                user.userId()
        );

        assertEquals(
                ORGANIZATION_ID,
                user.organizationId()
        );

        assertEquals(
                TENANT_ID,
                user.tenantId()
        );

        assertEquals(
                "active.user@example.com",
                user.email()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoRecipientIdsAreRequested() {

        List<UserAccountReference> users =
                userAccountLookupService
                        .findAuthorizedUsers(
                                ORGANIZATION_ID,
                                TENANT_ID,
                                List.of()
                        );

        assertTrue(
                users.isEmpty()
        );
    }

    @Test
    void shouldRejectNullUserId() {

        List<UUID> userIds =
                new ArrayList<>();

        userIds.add(
                ACTIVE_USER_ID
        );

        userIds.add(
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userAccountLookupService
                                .findAuthorizedUsers(
                                        ORGANIZATION_ID,
                                        TENANT_ID,
                                        userIds
                                )
        );
    }

    @Test
    void shouldRejectMissingRequiredScope() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userAccountLookupService
                                .findAuthorizedUsers(
                                        null,
                                        TENANT_ID,
                                        List.of(
                                                ACTIVE_USER_ID
                                        )
                                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userAccountLookupService
                                .findAuthorizedUsers(
                                        ORGANIZATION_ID,
                                        null,
                                        List.of(
                                                ACTIVE_USER_ID
                                        )
                                )
        );
    }

    private void insertOrganization() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-USER-LOOKUP-ORG",
                "EFS User Lookup Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant(
            UUID tenantId,
            String tenantCode) {

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                tenantId,
                ORGANIZATION_ID,
                tenantCode,
                tenantCode,
                "ACTIVE",
                "TEST"
        );
    }

    private void insertUser(
            UUID userId,
            UUID tenantId,
            String accountStatus,
            String email) {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    tenant_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                ORGANIZATION_ID,
                tenantId,
                "user." + userId,
                "EFS User Lookup Test User",
                email,
                "LOCAL",
                false,
                accountStatus,
                0
        );
    }
}