package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditApiAccessRequest;
import com.efs.modules.audit.dto.AuditApiAccessResponse;
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
class AuditApiAccessServiceIntegrationTest {

    private static final UUID API_CLIENT_ID =
            UUID.fromString(
                    "81818181-8181-8181-8181-818181818181"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "83838383-8383-8383-8383-838383838383"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "82828282-8282-8282-8282-828282828282"
            );

    @Autowired
    private AuditApiAccessServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertApiClient();
    }

    @Test
    void shouldCreateAndRetrieveAuditApiAccessById()
            throws Exception {

        AuditApiAccessResponse created =
                service.createAuditApiAccess(
                        buildRequest(
                                API_CLIENT_ID,
                                "/api/v1/rules",
                                "GET",
                                200,
                                125,
                                1024L,
                                4096L,
                                InetAddress.getByName(
                                        "192.168.40.10"
                                ),
                                CORRELATION_ID
                        )
                );

        assertNotNull(
                created.getApiAccessId()
        );

        assertEquals(
                API_CLIENT_ID,
                created.getApiClientId()
        );

        assertEquals(
                "/api/v1/rules",
                created.getEndpoint()
        );

        assertEquals(
                "GET",
                created.getHttpMethod()
        );

        assertEquals(
                200,
                created.getResponseCode()
        );

        assertEquals(
                125,
                created.getExecutionTimeMs()
        );

        assertEquals(
                1024L,
                created.getRequestSize()
        );

        assertEquals(
                4096L,
                created.getResponseSize()
        );

        assertNotNull(
                created.getIpAddress()
        );

        assertEquals(
                CORRELATION_ID,
                created.getCorrelationId()
        );

        assertNotNull(
                created.getRequestedAt()
        );

        AuditApiAccessResponse retrieved =
                service.getAuditApiAccessById(
                        created.getApiAccessId()
                );

        assertEquals(
                created.getApiAccessId(),
                retrieved.getApiAccessId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditApiAccessRequest request =
                new AuditApiAccessRequest();

        request.setApiClientId(
                API_CLIENT_ID
        );

        request.setEndpoint(
                "/api/v1/health"
        );

        request.setHttpMethod(
                "GET"
        );

        request.setResponseCode(
                200
        );

        request.setExecutionTimeMs(
                15
        );

        AuditApiAccessResponse created =
                service.createAuditApiAccess(
                        request
                );

        assertNotNull(
                created.getApiAccessId()
        );

        assertNull(
                created.getRequestSize()
        );

        assertNull(
                created.getResponseSize()
        );

        assertNull(
                created.getIpAddress()
        );

        assertNull(
                created.getCorrelationId()
        );

        assertNotNull(
                created.getRequestedAt()
        );
    }

    @Test
    void shouldReturnAuditApiAccessesByApiClientId()
            throws Exception {

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/rules",
                        "GET",
                        200,
                        100,
                        100L,
                        200L,
                        InetAddress.getByName(
                                "192.168.40.20"
                        ),
                        UUID.randomUUID()
                )
        );

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/cases",
                        "POST",
                        201,
                        150,
                        300L,
                        500L,
                        InetAddress.getByName(
                                "192.168.40.21"
                        ),
                        UUID.randomUUID()
                )
        );

        List<AuditApiAccessResponse> accesses =
                service.getAuditApiAccessesByApiClientId(
                        API_CLIENT_ID
                );

        assertEquals(
                2,
                accesses.size()
        );

        assertEquals(
                API_CLIENT_ID,
                accesses.get(0).getApiClientId()
        );

        assertEquals(
                API_CLIENT_ID,
                accesses.get(1).getApiClientId()
        );
    }

    @Test
    void shouldReturnAuditApiAccessesByEndpoint()
            throws Exception {

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/rules",
                        "GET",
                        200,
                        100,
                        100L,
                        200L,
                        InetAddress.getByName(
                                "192.168.40.30"
                        ),
                        UUID.randomUUID()
                )
        );

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/rules",
                        "POST",
                        201,
                        180,
                        300L,
                        500L,
                        InetAddress.getByName(
                                "192.168.40.31"
                        ),
                        UUID.randomUUID()
                )
        );

        List<AuditApiAccessResponse> accesses =
                service.getAuditApiAccessesByEndpoint(
                        "/api/v1/rules"
                );

        assertEquals(
                2,
                accesses.size()
        );

        assertEquals(
                "/api/v1/rules",
                accesses.get(0).getEndpoint()
        );

        assertEquals(
                "/api/v1/rules",
                accesses.get(1).getEndpoint()
        );
    }

    @Test
    void shouldReturnAuditApiAccessesByResponseCode()
            throws Exception {

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/rules",
                        "GET",
                        403,
                        90,
                        null,
                        null,
                        InetAddress.getByName(
                                "192.168.40.40"
                        ),
                        UUID.randomUUID()
                )
        );

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/cases",
                        "GET",
                        403,
                        95,
                        null,
                        null,
                        InetAddress.getByName(
                                "192.168.40.41"
                        ),
                        UUID.randomUUID()
                )
        );

        List<AuditApiAccessResponse> accesses =
                service.getAuditApiAccessesByResponseCode(
                        403
                );

        assertEquals(
                2,
                accesses.size()
        );

        assertEquals(
                403,
                accesses.get(0).getResponseCode()
        );

        assertEquals(
                403,
                accesses.get(1).getResponseCode()
        );
    }

    @Test
    void shouldReturnAuditApiAccessesByCorrelationId()
            throws Exception {

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/rules",
                        "GET",
                        200,
                        100,
                        100L,
                        200L,
                        InetAddress.getByName(
                                "192.168.40.50"
                        ),
                        CORRELATION_ID
                )
        );

        service.createAuditApiAccess(
                buildRequest(
                        API_CLIENT_ID,
                        "/api/v1/cases",
                        "POST",
                        201,
                        150,
                        300L,
                        500L,
                        InetAddress.getByName(
                                "192.168.40.51"
                        ),
                        CORRELATION_ID
                )
        );

        List<AuditApiAccessResponse> accesses =
                service.getAuditApiAccessesByCorrelationId(
                        CORRELATION_ID
                );

        assertEquals(
                2,
                accesses.size()
        );

        assertEquals(
                CORRELATION_ID,
                accesses.get(0).getCorrelationId()
        );

        assertEquals(
                CORRELATION_ID,
                accesses.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldRejectUnknownAuditApiAccessId() {

        UUID unknownApiAccessId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAuditApiAccessById(
                        unknownApiAccessId
                )
        );
    }

    private AuditApiAccessRequest buildRequest(
            UUID apiClientId,
            String endpoint,
            String httpMethod,
            Integer responseCode,
            Integer executionTimeMs,
            Long requestSize,
            Long responseSize,
            InetAddress ipAddress,
            UUID correlationId) {

        AuditApiAccessRequest request =
                new AuditApiAccessRequest();

        request.setApiClientId(
                apiClientId
        );

        request.setEndpoint(
                endpoint
        );

        request.setHttpMethod(
                httpMethod
        );

        request.setResponseCode(
                responseCode
        );

        request.setExecutionTimeMs(
                executionTimeMs
        );

        request.setRequestSize(
                requestSize
        );

        request.setResponseSize(
                responseSize
        );

        request.setIpAddress(
                ipAddress
        );

        request.setCorrelationId(
                correlationId
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
                "EFS-AUDIT-API-ACCESS-ORG",
                "EFS Audit API Access Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertApiClient() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.api_client (
                    api_client_id,
                    organization_id,
                    client_name,
                    client_identifier,
                    authentication_type,
                    client_status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                API_CLIENT_ID,
                ORGANIZATION_ID,
                "EFS Audit API Access Test Client",
                "EFS-AUDIT-API-ACCESS-TEST",
                "API_KEY",
                "ACTIVE"
        );
    }
}