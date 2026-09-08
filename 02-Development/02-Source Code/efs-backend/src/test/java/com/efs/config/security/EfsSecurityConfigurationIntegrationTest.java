package com.efs.config.security;

import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
class EfsSecurityConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

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

        UUID userId =
                UUID.randomUUID();

        when(
                jwtDecoder.decode(
                        "valid-token"
                )
        ).thenReturn(
                buildJwt(
                        userId
                )
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
                                        userId.toString()
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

    private Jwt buildJwt(
            UUID userId) {

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
                        userId.toString()
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