package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserSession;
import com.efs.modules.administration.repository.UserSessionRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserSessionLogoutTransactionIntegrationTest {

    @Autowired
    private UserSessionBindingService userSessionBindingService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

            assertThrows(
                    RuntimeException.class,
                    () -> userSessionBindingService.logout(
                            securityContext
                    )
            );

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

    }
}