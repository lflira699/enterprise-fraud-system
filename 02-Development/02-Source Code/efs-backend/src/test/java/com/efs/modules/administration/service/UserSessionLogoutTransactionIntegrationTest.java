package com.efs.modules.administration.service;

import com.efs.modules.administration.controller.AuthenticationController;
import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserSession;
import com.efs.modules.administration.repository.UserSessionRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserSessionLogoutTransactionIntegrationTest {

    private UUID cleanupOrganizationId;
    private UUID cleanupTenantId;
    private UUID cleanupUserId;
    private UUID cleanupSessionId;

    @AfterEach
    void cleanupUc002TestData() throws java.sql.SQLException {
        if (cleanupSessionId == null
                && cleanupUserId == null
                && cleanupTenantId == null
                && cleanupOrganizationId == null) {
            return;
        }

        javax.sql.DataSource migrationDataSource =
                flyway.getConfiguration().getDataSource();

        if (migrationDataSource == null
                || jdbcTemplate.getDataSource() == null) {
            throw new IllegalStateException(
                    "UC002 cleanup datasource unavailable"
            );
        }

        String runtimeDatabase;
        String runtimeUrl;

        try (java.sql.Connection runtimeConnection =
                     jdbcTemplate.getDataSource().getConnection();
             java.sql.Statement statement =
                     runtimeConnection.createStatement();
             java.sql.ResultSet result =
                     statement.executeQuery("SELECT current_database()")) {

            if (!result.next()) {
                throw new IllegalStateException(
                        "UC002 runtime database identity unavailable"
                );
            }

            runtimeDatabase = result.getString(1);
            runtimeUrl = runtimeConnection.getMetaData().getURL();
        }

        try (java.sql.Connection connection =
                     migrationDataSource.getConnection()) {

            String migrationDatabase;
            String migrationUser;
            String migrationSuperuser;

            try (java.sql.Statement statement =
                         connection.createStatement();
                 java.sql.ResultSet result = statement.executeQuery(
                         "SELECT current_database(), current_user, "
                         + "current_setting('is_superuser')"
                 )) {

                if (!result.next()) {
                    throw new IllegalStateException(
                            "UC002 migration database identity unavailable"
                    );
                }

                migrationDatabase = result.getString(1);
                migrationUser = result.getString(2);
                migrationSuperuser = result.getString(3);
            }

            if (!runtimeDatabase.equals(migrationDatabase)
                    || !runtimeUrl.equals(
                            connection.getMetaData().getURL()
                    )
                    || !"efs_admin".equals(migrationUser)
                    || !"off".equals(migrationSuperuser)) {
                throw new IllegalStateException(
                        "UC002 cleanup database or role mismatch"
                );
            }

            connection.setAutoCommit(false);

            try {
                if (cleanupSessionId != null) {
                    try (java.sql.PreparedStatement statement =
                                 connection.prepareStatement(
                                         "DELETE FROM administration.user_session "
                                         + "WHERE session_id = ?"
                                 )) {
                        statement.setObject(1, cleanupSessionId);
                        statement.executeUpdate();
                    }
                }

                if (cleanupUserId != null) {
                    try (java.sql.PreparedStatement statement =
                                 connection.prepareStatement(
                                         "DELETE FROM administration.user_account "
                                         + "WHERE user_id = ?"
                                 )) {
                        statement.setObject(1, cleanupUserId);
                        statement.executeUpdate();
                    }
                }

                if (cleanupTenantId != null) {
                    try (java.sql.PreparedStatement statement =
                                 connection.prepareStatement(
                                         "DELETE FROM administration.tenant "
                                         + "WHERE tenant_id = ?"
                                 )) {
                        statement.setObject(1, cleanupTenantId);
                        statement.executeUpdate();
                    }
                }

                if (cleanupOrganizationId != null) {
                    try (java.sql.PreparedStatement statement =
                                 connection.prepareStatement(
                                         "DELETE FROM administration.organization "
                                         + "WHERE organization_id = ?"
                                 )) {
                        statement.setObject(1, cleanupOrganizationId);
                        statement.executeUpdate();
                    }
                }

                connection.commit();
            } catch (java.sql.SQLException | RuntimeException failure) {
                try {
                    connection.rollback();
                } catch (java.sql.SQLException rollbackFailure) {
                    failure.addSuppressed(rollbackFailure);
                }
                throw failure;
            }
        }
    }

    @Autowired
    private UserSessionBindingService userSessionBindingService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @MockitoBean
    private AuditEventServiceInterface auditEventService;

    @MockitoBean
    private UserAccountLookupServiceInterface userAccountLookupService;

    @Test
    void logoutShouldRollbackSessionInvalidationWhenSuccessAuditFails() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        UUID sessionId =
                UUID.randomUUID();

        String suffix =
                userId.toString();

        cleanupOrganizationId = organizationId;
        cleanupTenantId = tenantId;
        cleanupUserId = userId;
        cleanupSessionId = sessionId;


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
                    organizationId,
                    "UC002-" + suffix,
                    "EFS UC-002 Rollback Test Organization",
                    "GT",
                    "America/Guatemala",
                    "ACTIVE"
            );

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
                    organizationId,
                    "UC002-" + suffix,
                    "EFS UC-002 Rollback Test Tenant",
                    "ACTIVE",
                    "TEST"
            );

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
                    organizationId,
                    tenantId,
                    "user." + suffix,
                    "EFS UC-002 Rollback Test User",
                    "uc002." + suffix + "@example.com",
                    "OIDC",
                    false,
                    "ACTIVE",
                    0
            );

            LocalDateTime now =
                    LocalDateTime.now();

            UserSession session =
                    new UserSession(
                            sessionId,
                            userId,
                            now,
                            "ACTIVE",
                            now
                    );

            userSessionRepository.saveAndFlush(
                    session
            );

            SecurityContext securityContext =
                    new SecurityContext(
                            userId,
                            tenantId,
                            sessionId,
                            Set.of("USER"),
                            Set.of(),
                            Set.of()
                    );

            UserAccountReference authorizedUser =
                    new UserAccountReference(
                            userId,
                            organizationId,
                            tenantId,
                            "uc002." + suffix + "@example.com"
                    );

            when(
                    userAccountLookupService.getAuthorizedUser(
                            userId
                    )
            ).thenReturn(
                    authorizedUser
            );

            RuntimeException auditFailure =
                    new RuntimeException(
                            "success audit failure"
                    );

            doThrow(
                    auditFailure
            ).when(
                    auditEventService
            ).createAuditEvent(
                    any(AuditEventRequest.class)
            );

            UserSessionBindingService.LogoutSuccessAuditFailureException thrown =
                    assertThrows(
                            UserSessionBindingService.LogoutSuccessAuditFailureException.class,
                            () -> userSessionBindingService.logout(securityContext)
                    );
            assertSame(auditFailure, thrown.getCause());

            UserSession persisted =
                    userSessionRepository
                            .findById(sessionId)
                            .orElseThrow();

            assertEquals(
                    "ACTIVE",
                    persisted.getSessionStatus()
            );

            assertNull(
                    persisted.getLogoutTime()
            );

            SecurityContextProvider provider = mock(SecurityContextProvider.class);
            when(provider.getCurrentContext()).thenReturn(securityContext);
            AuthenticationController controller =
                    new AuthenticationController(userSessionBindingService, provider);

            assertThrows(
                    UserSessionBindingService.LogoutSuccessAuditFailureException.class,
                    controller::logout
            );

            UserSession recovered = userSessionRepository
                    .findById(sessionId)
                    .orElseThrow();
            assertEquals("INVALIDATED", recovered.getSessionStatus());
            assertNotNull(recovered.getLogoutTime());
            assertThrows(
                    IllegalStateException.class,
                    () -> userSessionBindingService.establishSession(sessionId, userId)
            );

    }
}