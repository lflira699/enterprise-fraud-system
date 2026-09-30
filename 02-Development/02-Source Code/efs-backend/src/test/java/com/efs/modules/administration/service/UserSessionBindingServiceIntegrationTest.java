package com.efs.modules.administration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserSessionBindingServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "18118118-1181-4181-8181-181181181181"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "18218218-2182-4182-8182-182182182182"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "18318318-3183-4183-8183-183183183183"
            );

    private static final UUID OTHER_USER_ID =
            UUID.fromString(
                    "18418418-4184-4184-8184-184184184184"
            );

    private static final UUID ACTIVE_SESSION_ID =
            UUID.fromString(
                    "18518518-5185-4185-8185-185185185185"
            );

    private static final UUID INVALIDATED_SESSION_ID =
            UUID.fromString(
                    "18618618-6186-4186-8186-186186186186"
            );

    private static final UUID EXPIRED_SESSION_ID =
            UUID.fromString(
                    "18718718-7187-4187-8187-187187187187"
            );

    private static final UUID NEW_SESSION_ID =
            UUID.fromString(
                    "18818818-8188-4188-8188-188188188188"
            );

    @Autowired
    private UserSessionBindingService
            userSessionBindingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();

        insertUser(
                USER_ID,
                "session.user@example.com"
        );

        insertUser(
                OTHER_USER_ID,
                "other.session.user@example.com"
        );

        insertSession(
                ACTIVE_SESSION_ID,
                USER_ID,
                "ACTIVE"
        );

        insertSession(
                INVALIDATED_SESSION_ID,
                USER_ID,
                "INVALIDATED"
        );

        insertSession(
                EXPIRED_SESSION_ID,
                USER_ID,
                "EXPIRED"
        );
    }

    @Test
    void shouldEstablishNewActiveSession() {

        userSessionBindingService.establishSession(
                NEW_SESSION_ID,
                USER_ID
        );

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.user_session
                        WHERE session_id = ?
                          AND user_id = ?
                          AND session_status = 'ACTIVE'
                          AND login_time IS NOT NULL
                          AND created_at IS NOT NULL
                        """,
                        Integer.class,
                        NEW_SESSION_ID,
                        USER_ID
                );

        assertEquals(
                1,
                count
        );

        assertDoesNotThrow(
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        NEW_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldReuseExistingActiveSessionIdempotently() {

        userSessionBindingService.establishSession(
                ACTIVE_SESSION_ID,
                USER_ID
        );

        userSessionBindingService.establishSession(
                ACTIVE_SESSION_ID,
                USER_ID
        );

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.user_session
                        WHERE session_id = ?
                          AND user_id = ?
                          AND session_status = 'ACTIVE'
                        """,
                        Integer.class,
                        ACTIVE_SESSION_ID,
                        USER_ID
                );

        assertEquals(
                1,
                count
        );
    }

    @Test
    void shouldRejectExistingSessionBoundToDifferentUser() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .establishSession(
                                        ACTIVE_SESSION_ID,
                                        OTHER_USER_ID
                                )
        );
    }

    @Test
    void shouldRejectInvalidatedSessionReactivation() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .establishSession(
                                        INVALIDATED_SESSION_ID,
                                        USER_ID
                                )
        );

        assertEquals(
                "INVALIDATED",
                sessionStatus(
                        INVALIDATED_SESSION_ID
                )
        );
    }

    @Test
    void shouldRejectExpiredSessionReactivation() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .establishSession(
                                        EXPIRED_SESSION_ID,
                                        USER_ID
                                )
        );

        assertEquals(
                "EXPIRED",
                sessionStatus(
                        EXPIRED_SESSION_ID
                )
        );
    }

    @Test
    void shouldRejectEstablishmentWithoutSessionId() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .establishSession(
                                        null,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectEstablishmentWithoutUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userSessionBindingService
                                .establishSession(
                                        NEW_SESSION_ID,
                                        null
                                )
        );
    }

    @Test
    void shouldAcceptActiveSessionForBoundUser() {

        assertDoesNotThrow(
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        ACTIVE_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectMissingSession() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        NEW_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectSessionBoundToDifferentUser() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        ACTIVE_SESSION_ID,
                                        OTHER_USER_ID
                                )
        );
    }

    @Test
    void shouldRejectInvalidatedSession() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        INVALIDATED_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectExpiredSession() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        EXPIRED_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectMissingSessionId() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        null,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectMissingUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        ACTIVE_SESSION_ID,
                                        null
                                )
        );
    }

    @Test
    void shouldInvalidateActiveSessionAndRecordLogoutTime() {

        userSessionBindingService.invalidateSession(
                ACTIVE_SESSION_ID,
                USER_ID
        );

        String status =
                jdbcTemplate.queryForObject(
                        """
                        SELECT session_status
                        FROM administration.user_session
                        WHERE session_id = ?
                        """,
                        String.class,
                        ACTIVE_SESSION_ID
                );

        LocalDateTime logoutTime =
                jdbcTemplate.queryForObject(
                        """
                        SELECT logout_time
                        FROM administration.user_session
                        WHERE session_id = ?
                        """,
                        LocalDateTime.class,
                        ACTIVE_SESSION_ID
                );

        assertEquals(
                "INVALIDATED",
                status
        );

        assertNotNull(
                logoutTime
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .requireActiveSession(
                                        ACTIVE_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectLogoutForSessionBoundToDifferentUser() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        ACTIVE_SESSION_ID,
                                        OTHER_USER_ID
                                )
        );
    }

    @Test
    void shouldRejectLogoutForMissingSession() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        NEW_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectAlreadyInvalidatedSessionLogout() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        INVALIDATED_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectExpiredSessionLogout() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        EXPIRED_SESSION_ID,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectLogoutWithoutSessionId() {

        assertThrows(
                IllegalStateException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        null,
                                        USER_ID
                                )
        );
    }

    @Test
    void shouldRejectLogoutWithoutUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userSessionBindingService
                                .invalidateSession(
                                        ACTIVE_SESSION_ID,
                                        null
                                )
        );
    }

    private String sessionStatus(
            UUID sessionId) {

        return jdbcTemplate.queryForObject(
                """
                SELECT session_status
                FROM administration.user_session
                WHERE session_id = ?
                """,
                String.class,
                sessionId
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
                "EFS-UC001-ORG",
                "EFS UC-001 Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant() {

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
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-UC001-TENANT",
                "EFS UC-001 Test Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    private void insertUser(
            UUID userId,
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
                TENANT_ID,
                "user." + userId,
                "EFS UC-001 Session User",
                email,
                "OIDC",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertSession(
            UUID sessionId,
            UUID userId,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_session (
                    session_id,
                    user_id,
                    session_status
                )
                VALUES (?, ?, ?)
                """,
                sessionId,
                userId,
                status
        );
    }
}