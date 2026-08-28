package com.efs.modules.audit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditHistoryControllerIntegrationTest {

    private static final UUID SOURCE_RECORD_ID =
            UUID.fromString(
                    "c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4"
            );

    private static final String CHECKSUM =
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAuditHistoryThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "sourceTable",
                                "audit_event"
                        ),
                        Map.entry(
                                "sourceRecordId",
                                SOURCE_RECORD_ID.toString()
                        ),
                        Map.entry(
                                "organizationId",
                                ORGANIZATION_ID.toString()
                        ),
                        Map.entry(
                                "tenantId",
                                TENANT_ID.toString()
                        ),
                        Map.entry(
                                "correlationId",
                                CORRELATION_ID.toString()
                        ),
                        Map.entry(
                                "eventTimestamp",
                                LocalDateTime.now().toString()
                        ),
                        Map.entry(
                                "archivedPayload",
                                Map.of(
                                        "eventType",
                                        "LOGIN",
                                        "eventResult",
                                        "SUCCESS"
                                )
                        ),
                        Map.entry(
                                "checksumSha256",
                                CHECKSUM
                        ),
                        Map.entry(
                                "retentionUntil",
                                LocalDateTime.now()
                                        .plusYears(7)
                                        .toString()
                        )
                );

        mockMvc.perform(
                        post(
                                "/api/v1/audit/history"
                        )
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
                        jsonPath("$.historyId").exists()
                )
                .andExpect(
                        jsonPath("$.sourceTable")
                                .value("audit_event")
                )
                .andExpect(
                        jsonPath("$.sourceRecordId")
                                .value(
                                        SOURCE_RECORD_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.archivedPayload.eventType")
                                .value("LOGIN")
                )
                .andExpect(
                        jsonPath("$.checksumSha256")
                                .value(CHECKSUM)
                )
                .andExpect(
                        jsonPath("$.archivedAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditHistoryByIdThroughApi()
            throws Exception {

        UUID historyId =
                UUID.randomUUID();

        insertAuditHistory(
                historyId,
                "audit_event",
                SOURCE_RECORD_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/{historyId}",
                                historyId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.historyId")
                                .value(
                                        historyId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sourceTable")
                                .value("audit_event")
                )
                .andExpect(
                        jsonPath("$.sourceRecordId")
                                .value(
                                        SOURCE_RECORD_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditHistoryBySourceThroughApi()
            throws Exception {

        UUID historyId =
                UUID.randomUUID();

        insertAuditHistory(
                historyId,
                "audit_event",
                SOURCE_RECORD_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/source"
                        )
                                .param(
                                        "sourceTable",
                                        "audit_event"
                                )
                                .param(
                                        "sourceRecordId",
                                        SOURCE_RECORD_ID.toString()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.historyId")
                                .value(
                                        historyId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sourceTable")
                                .value("audit_event")
                );
    }

    @Test
    void shouldRetrieveAuditHistoryByOrganizationIdThroughApi()
            throws Exception {

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_event",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_login",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/organization/{organizationId}",
                                ORGANIZATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditHistoryByTenantIdThroughApi()
            throws Exception {

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_event",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_export",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/tenant/{tenantId}",
                                TENANT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditHistoryByCorrelationIdThroughApi()
            throws Exception {

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_event",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                CORRELATION_ID
        );

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_login",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/correlation/{correlationId}",
                                CORRELATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
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
    void shouldRetrieveAuditHistoryBySourceTableThroughApi()
            throws Exception {

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_event",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        insertAuditHistory(
                UUID.randomUUID(),
                "audit_event",
                UUID.randomUUID(),
                ORGANIZATION_ID,
                TENANT_ID,
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/source-table"
                        )
                                .param(
                                        "sourceTable",
                                        "audit_event"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].sourceTable")
                                .value("audit_event")
                )
                .andExpect(
                        jsonPath("$[1].sourceTable")
                                .value("audit_event")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditHistoryId()
            throws Exception {

        UUID unknownHistoryId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/{historyId}",
                                unknownHistoryId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditHistorySource()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/audit/history/source"
                        )
                                .param(
                                        "sourceTable",
                                        "audit_event"
                                )
                                .param(
                                        "sourceRecordId",
                                        UUID.randomUUID().toString()
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertAuditHistory(
            UUID historyId,
            String sourceTable,
            UUID sourceRecordId,
            UUID organizationId,
            UUID tenantId,
            UUID correlationId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_history (
                    history_id,
                    source_table,
                    source_record_id,
                    organization_id,
                    tenant_id,
                    correlation_id,
                    event_timestamp,
                    archived_payload,
                    checksum_sha256,
                    archived_at,
                    retention_until
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp(),
                    CAST(? AS jsonb),
                    ?,
                    clock_timestamp(),
                    NULL
                )
                """,
                historyId,
                sourceTable,
                sourceRecordId,
                organizationId,
                tenantId,
                correlationId,
                "{\"eventType\":\"LOGIN\",\"eventResult\":\"SUCCESS\"}",
                CHECKSUM
        );
    }
}