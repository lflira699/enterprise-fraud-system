package com.efs.modules.audit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditLoginControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "79797979-7979-7979-7979-797979797979"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "80808080-8080-8080-8080-808080808080"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAuditLoginThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "userId",
                                USER_ID.toString()
                        ),
                        Map.entry(
                                "ipAddress",
                                "192.168.30.10"
                        ),
                        Map.entry(
                                "deviceFingerprint",
                                "DEVICE-FP-API-001"
                        ),
                        Map.entry(
                                "authenticationMethod",
                                "PASSWORD"
                        ),
                        Map.entry(
                                "mfaResult",
                                "SUCCESS"
                        ),
                        Map.entry(
                                "loginResult",
                                "SUCCESS"
                        ),
                        Map.entry(
                                "countryCode",
                                "GT"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/logins")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.loginId").exists()
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.loginTimestamp").exists()
                )
                .andExpect(
                        jsonPath("$.deviceFingerprint")
                                .value("DEVICE-FP-API-001")
                )
                .andExpect(
                        jsonPath("$.authenticationMethod")
                                .value("PASSWORD")
                )
                .andExpect(
                        jsonPath("$.mfaResult")
                                .value("SUCCESS")
                )
                .andExpect(
                        jsonPath("$.loginResult")
                                .value("SUCCESS")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditLoginByIdThroughApi()
            throws Exception {

        UUID loginId =
                UUID.randomUUID();

        insertAuditLogin(
                loginId,
                USER_ID,
                "192.168.30.20",
                "DEVICE-FP-API-020",
                "PASSWORD",
                "SUCCESS",
                "SUCCESS",
                null,
                "GT"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/{loginId}",
                                loginId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.loginId")
                                .value(
                                        loginId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.authenticationMethod")
                                .value("PASSWORD")
                )
                .andExpect(
                        jsonPath("$.loginResult")
                                .value("SUCCESS")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                );
    }

    @Test
    void shouldRetrieveAuditLoginsByUserIdThroughApi()
            throws Exception {

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.30",
                "DEVICE-FP-API-030",
                "PASSWORD",
                "SUCCESS",
                "SUCCESS",
                null,
                "GT"
        );

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.31",
                "DEVICE-FP-API-031",
                "PASSWORD",
                "SUCCESS",
                "FAILURE",
                "Invalid password",
                "GT"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/user/{userId}",
                                USER_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].userId")
                                .value(
                                        USER_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditLoginsByLoginResultThroughApi()
            throws Exception {

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.40",
                "DEVICE-FP-API-040",
                "PASSWORD",
                "SUCCESS",
                "FAILURE",
                "Invalid password",
                "GT"
        );

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.41",
                "DEVICE-FP-API-041",
                "PASSWORD",
                "FAILURE",
                "FAILURE",
                "MFA failed",
                "GT"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/result/{loginResult}",
                                "FAILURE"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].loginResult")
                                .value("FAILURE")
                )
                .andExpect(
                        jsonPath("$[1].loginResult")
                                .value("FAILURE")
                );
    }

    @Test
    void shouldRetrieveAuditLoginsByIpAddressThroughApi()
            throws Exception {

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.50",
                "DEVICE-FP-API-050",
                "PASSWORD",
                "SUCCESS",
                "SUCCESS",
                null,
                "GT"
        );

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.50",
                "DEVICE-FP-API-051",
                "MFA",
                "SUCCESS",
                "SUCCESS",
                null,
                "GT"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/ip/{ipAddress}",
                                "192.168.30.50"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                );
    }

    @Test
    void shouldRetrieveAuditLoginsByAuthenticationMethodThroughApi()
            throws Exception {

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.60",
                "DEVICE-FP-API-060",
                "PASSWORD",
                "SUCCESS",
                "SUCCESS",
                null,
                "GT"
        );

        insertAuditLogin(
                UUID.randomUUID(),
                USER_ID,
                "192.168.30.61",
                "DEVICE-FP-API-061",
                "PASSWORD",
                "SUCCESS",
                "FAILURE",
                "Invalid password",
                "GT"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/authentication-method/{authenticationMethod}",
                                "PASSWORD"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].authenticationMethod")
                                .value("PASSWORD")
                )
                .andExpect(
                        jsonPath("$[1].authenticationMethod")
                                .value("PASSWORD")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditLoginId()
            throws Exception {

        UUID unknownLoginId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/logins/{loginId}",
                                unknownLoginId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertAuditLogin(
            UUID loginId,
            UUID userId,
            String ipAddress,
            String deviceFingerprint,
            String authenticationMethod,
            String mfaResult,
            String loginResult,
            String failureReason,
            String countryCode) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_login (
                    login_id,
                    user_id,
                    login_timestamp,
                    ip_address,
                    device_fingerprint,
                    authentication_method,
                    mfa_result,
                    login_result,
                    failure_reason,
                    country_code,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    clock_timestamp(),
                    CAST(? AS inet),
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS char(2)),
                    clock_timestamp()
                )
                """,
                loginId,
                userId,
                ipAddress,
                deviceFingerprint,
                authenticationMethod,
                mfaResult,
                loginResult,
                failureReason,
                countryCode
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
                "EFS-AUDIT-LOGIN-API-ORG",
                "EFS Audit Login API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.audit.login.api",
                "EFS Audit Login API User",
                "efs.audit.login.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}