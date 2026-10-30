package com.efs.modules.event.controller;

import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive HTTP authorization of Fraud Event search with persisted EFS RBAC.
 *
 * Runs ONLY against the explicitly isolated, already-migrated regression
 * database. Test data is committed before the HTTP request because Event
 * search SUCCESS audit uses REQUIRES_NEW. No production objects are changed.
 *
 * Test-only RBAC fixtures and their immutable audit evidence remain in this
 * independent regression database. No audit record is updated or deleted.
 * Never run against Development, an institutional database, or a shared dataset.
 */
@SpringBootTest(properties = {
        "efs.security.enabled=true",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class FraudEventAuthorizationIsolatedIT {

    private static final String ISOLATED_DATABASE =
            "efs_v194_backend_test";

    private static final int ISOLATED_HOST_PORT = 55434;

    private static final String TEST_TOKEN =
            "efs-event-authorization-positive-token";

    private final UUID organizationId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID roleId = UUID.randomUUID();
    private final UUID userRoleId = UUID.randomUUID();
    private final UUID rolePermissionId = UUID.randomUUID();
    private final UUID correlationId = UUID.randomUUID();
    private final String sid = "efs-event-test-" + UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserAccountLookupServiceInterface userAccountLookupService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        requireIsolatedRegressionDatabase();
        requireRuntimeIdentity();
        requireAuditImmutability();

        // Only the already-approved permission catalog entry is consumed.
        UUID permissionId = jdbcTemplate.queryForObject(
                """
                SELECT permission_id
                FROM administration.permission
                WHERE permission_code = 'event.view'
                """,
                UUID.class
        );
        assertNotNull(permissionId, "event.view must exist in the catalog");

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id, organization_code, legal_name,
                    country_code, timezone, status
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                organizationId,
                "EFS-EVENT-AUTH-" + organizationId.toString().substring(0, 8),
                "EFS Event Authorization Integration Test",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id, organization_id, tenant_code,
                    tenant_name, status, environment
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                tenantId,
                organizationId,
                "EFS-EVENT-" + tenantId.toString().substring(0, 8),
                "EFS Event Authorization Test Tenant",
                "ACTIVE",
                "TEST"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id, organization_id, tenant_id,
                    username, full_name, email,
                    authentication_provider, mfa_enabled,
                    account_status, failed_login_attempts
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                organizationId,
                tenantId,
                "event.auth." + userId,
                "EFS Event Authorization Test User",
                "event.auth." + userId + "@example.com",
                "OIDC",
                false,
                "ACTIVE",
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.role (
                    role_id, organization_id, role_code,
                    role_name, is_system, status
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                roleId,
                organizationId,
                "EFS-EVENT-VIEW-" + roleId,
                "EFS Test Event Viewer",
                false,
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.role_permission (
                    role_permission_id, role_id, permission_id
                ) VALUES (?, ?, ?)
                """,
                rolePermissionId,
                roleId,
                permissionId
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_role (
                    user_role_id, user_id, role_id,
                    effective_from, assigned_at
                ) VALUES (
                    ?, ?, ?,
                    CURRENT_TIMESTAMP - INTERVAL '1 day',
                    CURRENT_TIMESTAMP
                )
                """,
                userRoleId,
                userId,
                roleId
        );
    }

    @Test
    void shouldAuthorizePersistedEventViewAndAuditSuccessExactlyOnce()
            throws Exception {

        Jwt jwt = buildJwt();
        assertFalse(jwt.hasClaim("permissions"));
        assertFalse(jwt.hasClaim("roles"));

        Set<String> effectivePermissions =
                userAccountLookupService.getAuthorizedPermissionCodes(userId);

        assertTrue(
                effectivePermissions.contains("event.view"),
                "event.view must be resolved from persisted Administration RBAC"
        );

        when(jwtDecoder.decode(TEST_TOKEN)).thenReturn(jwt);

        mockMvc.perform(
                        get("/api/v1/events")
                                .param("correlationId", correlationId.toString())
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + TEST_TOKEN
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25));

        Integer totalSearchAudits = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM audit.audit_event
                WHERE user_id = ?
                  AND correlation_id = ?
                  AND event_type = 'FRAUD_EVENT_SEARCH'
                """,
                Integer.class,
                userId,
                correlationId
        );

        Integer successfulSearchAudits = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM audit.audit_event
                WHERE user_id = ?
                  AND organization_id = ?
                  AND tenant_id = ?
                  AND correlation_id = ?
                  AND event_type = 'FRAUD_EVENT_SEARCH'
                  AND entity_type = 'FRAUD_EVENT'
                  AND entity_id IS NULL
                  AND action = 'SEARCH'
                  AND source_component = 'EVENT'
                  AND event_result = 'SUCCESS'
                  AND event_details ->> 'permissionCode' = 'event.view'
                """,
                Integer.class,
                userId,
                organizationId,
                tenantId,
                correlationId
        );

        assertEquals(1, totalSearchAudits, "Expected exactly one search audit");
        assertEquals(1, successfulSearchAudits,
                "Expected exactly one correctly scoped SUCCESS audit");
    }

    private void requireIsolatedRegressionDatabase() {
        String database = jdbcTemplate.queryForObject(
                "SELECT current_database()", String.class
        );

        String jdbcUrl = jdbcTemplate.execute(
                (ConnectionCallback<String>) connection ->
                        connection.getMetaData().getURL()
        );

        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException("Isolated PostgreSQL JDBC URL required");
        }

        URI jdbcUri = URI.create(jdbcUrl.substring("jdbc:".length()));
        String host = jdbcUri.getHost();

        boolean loopback = "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host);

        if (!ISOLATED_DATABASE.equals(database)
                || !loopback
                || jdbcUri.getPort() != ISOLATED_HOST_PORT
                || !('/' + ISOLATED_DATABASE).equals(jdbcUri.getPath())) {
            throw new IllegalStateException(
                    "Refusing Event Authorization test: "
                            + "database must be the local isolated "
                            + ISOLATED_DATABASE + " regression DB on port "
                            + ISOLATED_HOST_PORT
            );
        }
    }

    private void requireRuntimeIdentity() {
        assertEquals("efs_app", jdbcTemplate.queryForObject(
                "SELECT current_user", String.class));
        assertEquals("efs_app", jdbcTemplate.queryForObject(
                "SELECT session_user", String.class));
        assertEquals("off", jdbcTemplate.queryForObject(
                "SHOW is_superuser", String.class));
    }

    private void requireAuditImmutability() {
        for (String table : List.of(
                "audit.audit_event",
                "audit.audit_entity_change")) {
            Boolean allowed = jdbcTemplate.queryForObject(
                    "SELECT has_table_privilege(current_user, ?, 'DELETE')",
                    Boolean.class, table);
            assertFalse(Boolean.TRUE.equals(allowed),
                    "Runtime must not delete audit evidence: " + table);
        }
    }

    private Jwt buildJwt() {
        Instant now = Instant.now();

        return Jwt.withTokenValue(TEST_TOKEN)
                .header("alg", "RS256")
                .subject("external-keycloak-test-subject")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .claim("iss", "https://issuer.example")
                .claim("aud", List.of("efs-backend"))
                .claim("user_id", userId.toString())
                .claim("tenant_id", tenantId.toString())
                .claim("sid", sid)
                .claim("scope", "rules.read")
                .build();
    }
}
