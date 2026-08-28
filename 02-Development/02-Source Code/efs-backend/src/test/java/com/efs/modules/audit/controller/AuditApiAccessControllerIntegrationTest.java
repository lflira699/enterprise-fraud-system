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
class AuditApiAccessControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "84848484-8484-8484-8484-848484848484"
            );

    private static final UUID API_CLIENT_ID =
            UUID.fromString(
                    "85858585-8585-8585-8585-858585858585"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "86868686-8686-8686-8686-868686868686"
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
        insertApiClient();
    }

    @Test
    void shouldCreateAuditApiAccessThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "apiClientId",
                                API_CLIENT_ID.toString()
                        ),
                        Map.entry(
                                "endpoint",
                                "/api/v1/rules"
                        ),
                        Map.entry(
                                "httpMethod",
                                "GET"
                        ),
                        Map.entry(
                                "responseCode",
                                200
                        ),
                        Map.entry(
                                "executionTimeMs",
                                125
                        ),
                        Map.entry(
                                "requestSize",
                                1024L
                        ),
                        Map.entry(
                                "responseSize",
                                4096L
                        ),
                        Map.entry(
                                "ipAddress",
                                "192.168.50.10"
                        ),
                        Map.entry(
                                "correlationId",
                                CORRELATION_ID.toString()
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/api-access")
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
                        jsonPath("$.apiAccessId").exists()
                )
                .andExpect(
                        jsonPath("$.apiClientId")
                                .value(
                                        API_CLIENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.endpoint")
                                .value("/api/v1/rules")
                )
                .andExpect(
                        jsonPath("$.httpMethod")
                                .value("GET")
                )
                .andExpect(
                        jsonPath("$.responseCode")
                                .value(200)
                )
                .andExpect(
                        jsonPath("$.executionTimeMs")
                                .value(125)
                )
                .andExpect(
                        jsonPath("$.requestSize")
                                .value(1024)
                )
                .andExpect(
                        jsonPath("$.responseSize")
                                .value(4096)
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.requestedAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditApiAccessByIdThroughApi()
            throws Exception {

        UUID apiAccessId =
                UUID.randomUUID();

        insertAuditApiAccess(
                apiAccessId,
                "/api/v1/rules",
                "GET",
                200,
                100,
                100L,
                200L,
                "192.168.50.20",
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/{apiAccessId}",
                                apiAccessId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.apiAccessId")
                                .value(
                                        apiAccessId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.apiClientId")
                                .value(
                                        API_CLIENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.endpoint")
                                .value("/api/v1/rules")
                )
                .andExpect(
                        jsonPath("$.responseCode")
                                .value(200)
                );
    }

    @Test
    void shouldRetrieveAuditApiAccessesByApiClientIdThroughApi()
            throws Exception {

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/rules",
                "GET",
                200,
                100,
                100L,
                200L,
                "192.168.50.30",
                UUID.randomUUID()
        );

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/cases",
                "POST",
                201,
                150,
                300L,
                500L,
                "192.168.50.31",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/client/{apiClientId}",
                                API_CLIENT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].apiClientId")
                                .value(
                                        API_CLIENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].apiClientId")
                                .value(
                                        API_CLIENT_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditApiAccessesByEndpointThroughApi()
            throws Exception {

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/rules",
                "GET",
                200,
                100,
                null,
                null,
                "192.168.50.40",
                UUID.randomUUID()
        );

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/rules",
                "POST",
                201,
                180,
                null,
                null,
                "192.168.50.41",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/endpoint"
                        )
                                .param(
                                        "endpoint",
                                        "/api/v1/rules"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].endpoint")
                                .value("/api/v1/rules")
                )
                .andExpect(
                        jsonPath("$[1].endpoint")
                                .value("/api/v1/rules")
                );
    }

    @Test
    void shouldRetrieveAuditApiAccessesByResponseCodeThroughApi()
            throws Exception {

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/rules",
                "GET",
                403,
                90,
                null,
                null,
                "192.168.50.50",
                UUID.randomUUID()
        );

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/cases",
                "GET",
                403,
                95,
                null,
                null,
                "192.168.50.51",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/response-code/{responseCode}",
                                403
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].responseCode")
                                .value(403)
                )
                .andExpect(
                        jsonPath("$[1].responseCode")
                                .value(403)
                );
    }

    @Test
    void shouldRetrieveAuditApiAccessesByCorrelationIdThroughApi()
            throws Exception {

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/rules",
                "GET",
                200,
                100,
                100L,
                200L,
                "192.168.50.60",
                CORRELATION_ID
        );

        insertAuditApiAccess(
                UUID.randomUUID(),
                "/api/v1/cases",
                "POST",
                201,
                150,
                300L,
                500L,
                "192.168.50.61",
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/correlation/{correlationId}",
                                CORRELATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditApiAccessId()
            throws Exception {

        UUID unknownApiAccessId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/api-access/{apiAccessId}",
                                unknownApiAccessId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertAuditApiAccess(
            UUID apiAccessId,
            String endpoint,
            String httpMethod,
            Integer responseCode,
            Integer executionTimeMs,
            Long requestSize,
            Long responseSize,
            String ipAddress,
            UUID correlationId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_api_access (
                    api_access_id,
                    api_client_id,
                    endpoint,
                    http_method,
                    response_code,
                    execution_time_ms,
                    request_size,
                    response_size,
                    ip_address,
                    correlation_id,
                    requested_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS inet),
                    ?,
                    clock_timestamp()
                )
                """,
                apiAccessId,
                API_CLIENT_ID,
                endpoint,
                httpMethod,
                responseCode,
                executionTimeMs,
                requestSize,
                responseSize,
                ipAddress,
                correlationId
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
                "EFS-AUDIT-API-ACCESS-API-ORG",
                "EFS Audit API Access API Organization",
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
                "EFS Audit API Access API Client",
                "EFS-AUDIT-API-ACCESS-API",
                "API_KEY",
                "ACTIVE"
        );
    }
}