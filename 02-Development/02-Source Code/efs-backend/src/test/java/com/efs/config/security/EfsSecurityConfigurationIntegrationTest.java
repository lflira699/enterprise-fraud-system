package com.efs.config.security;

import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = "efs.security.enabled=true"
)
@AutoConfigureMockMvc
@Import(
        EfsSecurityConfigurationIntegrationTest
                .SecurityProbeConfiguration.class
)
@Transactional
class EfsSecurityConfigurationIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "19119119-1191-4191-8191-191191191191"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "19219219-2192-4192-8192-192192192192"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "19319319-3193-4193-8193-193193193193"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "19419419-4194-4194-8194-194194194194"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        insertOrganization();
        insertTenant();
        insertUser();
    }

    @Test
    void shouldAllowHealthEndpointWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/actuator/health"
                        )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldRejectProtectedApiWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/security/probe"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldExposeAuthenticatedEfsIdentityToApplication()
            throws Exception {

        when(
                jwtDecoder.decode(
                        "valid-token"
                )
        ).thenReturn(
                buildJwt()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/security/probe"
                        )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer valid-token"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.userId"
                        )
                                .value(
                                        USER_ID.toString()
                                )
                );
    }

    @Test
    void shouldRejectInvalidBearerToken()
            throws Exception {

        when(
                jwtDecoder.decode(
                        "invalid-token"
                )
        ).thenThrow(
                new BadJwtException(
                        "Invalid test token"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/security/probe"
                        )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer invalid-token"
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    private Jwt buildJwt() {

        Instant now =
                Instant.now();

        return Jwt.withTokenValue(
                        "valid-token"
                )
                .header(
                        "alg",
                        "RS256"
                )
                .subject(
                        "external-keycloak-subject"
                )
                .issuedAt(
                        now
                )
                .expiresAt(
                        now.plusSeconds(
                                300
                        )
                )
                .claim(
                        "iss",
                        "https://issuer.example"
                )
                .claim(
                        "aud",
                        List.of(
                                "efs-backend"
                        )
                )
                .claim(
                        "user_id",
                        USER_ID.toString()
                )
                .claim(
                        "tenant_id",
                        TENANT_ID.toString()
                )
                .claim(
                        "session_id",
                        SESSION_ID.toString()
                )
                .claim(
                        "roles",
                        List.of(
                                "RULE_ADMINISTRATOR"
                        )
                )
                .claim(
                        "permissions",
                        List.of(
                                "RULE_HISTORY_READ"
                        )
                )
                .claim(
                        "scope",
                        "rules.read"
                )
                .build();
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
                "EFS-SECURITY-ORG",
                "EFS Security Test Organization",
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
                "EFS-SECURITY-TENANT",
                "EFS Security Test Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    private void insertUser() {

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
                USER_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                "efs.security.test.user",
                "EFS Security Test User",
                "efs.security.test.user@example.com",
                "OIDC",
                false,
                "ACTIVE",
                0
        );
    }

    @TestConfiguration
    static class SecurityProbeConfiguration {

        @Bean
        SecurityProbeController securityProbeController(
                SecurityContextProvider securityContextProvider) {

            return new SecurityProbeController(
                    securityContextProvider
            );
        }
    }

    @RestController
    static class SecurityProbeController {

        private final SecurityContextProvider securityContextProvider;

        SecurityProbeController(
                SecurityContextProvider securityContextProvider) {

            this.securityContextProvider =
                    securityContextProvider;
        }

        @GetMapping(
                "/api/v1/security/probe"
        )
        Map<String, String> getSecurityContext() {

            return Map.of(
                    "userId",
                    securityContextProvider
                            .getCurrentContext()
                            .getUserId()
                            .toString()
            );
        }
    }
}
