package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.dto.AuditLoginResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AuditLoginServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "78787878-7878-7878-7878-787878787878"
            );

    @Autowired
    private AuditLoginServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAndRetrieveAuditLoginById()
            throws Exception {

        AuditLoginResponse created =
                service.createAuditLogin(
                        buildRequest(
                                USER_ID,
                                InetAddress.getByName("192.168.20.10"),
                                "DEVICE-FP-001",
                                "PASSWORD",
                                "SUCCESS",
                                "SUCCESS",
                                null,
                                "GT"
                        )
                );

        assertNotNull(
                created.getLoginId()
        );

        assertEquals(
                USER_ID,
                created.getUserId()
        );

        assertNotNull(
                created.getLoginTimestamp()
        );

        assertNotNull(
                created.getIpAddress()
        );

        assertEquals(
                "DEVICE-FP-001",
                created.getDeviceFingerprint()
        );

        assertEquals(
                "PASSWORD",
                created.getAuthenticationMethod()
        );

        assertEquals(
                "SUCCESS",
                created.getMfaResult()
        );

        assertEquals(
                "SUCCESS",
                created.getLoginResult()
        );

        assertNull(
                created.getFailureReason()
        );

        assertEquals(
                "GT",
                created.getCountryCode()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        AuditLoginResponse retrieved =
                service.getAuditLoginById(
                        created.getLoginId()
                );

        assertEquals(
                created.getLoginId(),
                retrieved.getLoginId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditLoginRequest request =
                new AuditLoginRequest();

        request.setAuthenticationMethod(
                "PASSWORD"
        );

        request.setLoginResult(
                "FAILURE"
        );

        request.setFailureReason(
                "Authentication failed"
        );

        AuditLoginResponse created =
                service.createAuditLogin(request);

        assertNotNull(
                created.getLoginId()
        );

        assertNotNull(
                created.getLoginTimestamp()
        );

        assertNull(
                created.getUserId()
        );

        assertNull(
                created.getIpAddress()
        );

        assertNull(
                created.getDeviceFingerprint()
        );

        assertNull(
                created.getMfaResult()
        );

        assertEquals(
                "FAILURE",
                created.getLoginResult()
        );

        assertEquals(
                "Authentication failed",
                created.getFailureReason()
        );

        assertNull(
                created.getCountryCode()
        );

        assertNotNull(
                created.getCreatedAt()
        );
    }

    @Test
    void shouldReturnAuditLoginsByUserId()
            throws Exception {

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.20"),
                        "DEVICE-FP-020",
                        "PASSWORD",
                        "SUCCESS",
                        "SUCCESS",
                        null,
                        "GT"
                )
        );

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.21"),
                        "DEVICE-FP-021",
                        "PASSWORD",
                        "SUCCESS",
                        "FAILURE",
                        "Invalid password",
                        "GT"
                )
        );

        List<AuditLoginResponse> logins =
                service.getAuditLoginsByUserId(
                        USER_ID
                );

        assertEquals(
                2,
                logins.size()
        );

        assertEquals(
                USER_ID,
                logins.get(0).getUserId()
        );

        assertEquals(
                USER_ID,
                logins.get(1).getUserId()
        );
    }

    @Test
    void shouldReturnAuditLoginsByLoginResult()
            throws Exception {

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.30"),
                        "DEVICE-FP-030",
                        "PASSWORD",
                        "SUCCESS",
                        "FAILURE",
                        "Invalid password",
                        "GT"
                )
        );

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.31"),
                        "DEVICE-FP-031",
                        "PASSWORD",
                        "FAILURE",
                        "FAILURE",
                        "MFA failed",
                        "GT"
                )
        );

        List<AuditLoginResponse> logins =
                service.getAuditLoginsByLoginResult(
                        "FAILURE"
                );

        assertEquals(
                2,
                logins.size()
        );

        assertEquals(
                "FAILURE",
                logins.get(0).getLoginResult()
        );

        assertEquals(
                "FAILURE",
                logins.get(1).getLoginResult()
        );
    }

    @Test
    void shouldReturnAuditLoginsByIpAddress()
            throws Exception {

        InetAddress ipAddress =
                InetAddress.getByName(
                        "192.168.20.40"
                );

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        ipAddress,
                        "DEVICE-FP-040",
                        "PASSWORD",
                        "SUCCESS",
                        "SUCCESS",
                        null,
                        "GT"
                )
        );

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        ipAddress,
                        "DEVICE-FP-041",
                        "MFA",
                        "SUCCESS",
                        "SUCCESS",
                        null,
                        "GT"
                )
        );

        List<AuditLoginResponse> logins =
                service.getAuditLoginsByIpAddress(
                        "192.168.20.40"
                );

        assertEquals(
                2,
                logins.size()
        );

        assertEquals(
                ipAddress,
                logins.get(0).getIpAddress()
        );

        assertEquals(
                ipAddress,
                logins.get(1).getIpAddress()
        );
    }

    @Test
    void shouldReturnAuditLoginsByAuthenticationMethod()
            throws Exception {

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.50"),
                        "DEVICE-FP-050",
                        "PASSWORD",
                        "SUCCESS",
                        "SUCCESS",
                        null,
                        "GT"
                )
        );

        service.createAuditLogin(
                buildRequest(
                        USER_ID,
                        InetAddress.getByName("192.168.20.51"),
                        "DEVICE-FP-051",
                        "PASSWORD",
                        "SUCCESS",
                        "FAILURE",
                        "Invalid password",
                        "GT"
                )
        );

        List<AuditLoginResponse> logins =
                service.getAuditLoginsByAuthenticationMethod(
                        "PASSWORD"
                );

        assertEquals(
                2,
                logins.size()
        );

        assertEquals(
                "PASSWORD",
                logins.get(0).getAuthenticationMethod()
        );

        assertEquals(
                "PASSWORD",
                logins.get(1).getAuthenticationMethod()
        );
    }

    @Test
    void shouldRejectUnknownAuditLoginId() {

        UUID unknownLoginId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAuditLoginById(
                        unknownLoginId
                )
        );
    }

    @Test
    void shouldRejectInvalidIpAddressSearch() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getAuditLoginsByIpAddress(
                        "invalid-ip-address"
                )
        );
    }

    private AuditLoginRequest buildRequest(
            UUID userId,
            InetAddress ipAddress,
            String deviceFingerprint,
            String authenticationMethod,
            String mfaResult,
            String loginResult,
            String failureReason,
            String countryCode) {

        AuditLoginRequest request =
                new AuditLoginRequest();

        request.setUserId(
                userId
        );

        request.setIpAddress(
                ipAddress
        );

        request.setDeviceFingerprint(
                deviceFingerprint
        );

        request.setAuthenticationMethod(
                authenticationMethod
        );

        request.setMfaResult(
                mfaResult
        );

        request.setLoginResult(
                loginResult
        );

        request.setFailureReason(
                failureReason
        );

        request.setCountryCode(
                countryCode
        );

        return request;
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
                "EFS-AUDIT-LOGIN-TEST-ORG",
                "EFS Audit Login Test Organization",
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
                "efs.audit.login.test",
                "EFS Audit Login Test User",
                "efs.audit.login.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}