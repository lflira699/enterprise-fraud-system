package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditHistoryRequest;
import com.efs.modules.audit.dto.AuditHistoryResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AuditHistoryServiceIntegrationTest {

    private static final UUID SOURCE_RECORD_ID =
            UUID.fromString(
                    "b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b3"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "b4b4b4b4-b4b4-b4b4-b4b4-b4b4b4b4b4b4"
            );

    private static final String CHECKSUM =
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Autowired
    private AuditHistoryServiceInterface auditHistoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveAuditHistoryById() {

        AuditHistoryRequest request =
                buildRequest(
                        SOURCE_RECORD_ID,
                        ORGANIZATION_ID,
                        TENANT_ID,
                        CORRELATION_ID
                );

        AuditHistoryResponse created =
                auditHistoryService.createAuditHistory(
                        request
                );

        assertNotNull(created);
        assertNotNull(created.getHistoryId());

        assertEquals(
                "audit_event",
                created.getSourceTable()
        );

        assertEquals(
                SOURCE_RECORD_ID,
                created.getSourceRecordId()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                created.getTenantId()
        );

        assertEquals(
                CORRELATION_ID,
                created.getCorrelationId()
        );

        assertEquals(
                "LOGIN",
                created.getArchivedPayload().get("eventType")
        );

        assertEquals(
                CHECKSUM,
                created.getChecksumSha256()
        );

        assertNotNull(
                created.getArchivedAt()
        );

        AuditHistoryResponse retrieved =
                auditHistoryService.getAuditHistoryById(
                        created.getHistoryId()
                );

        assertEquals(
                created.getHistoryId(),
                retrieved.getHistoryId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditHistoryRequest request =
                new AuditHistoryRequest();

        request.setSourceTable(
                "audit_login"
        );

        request.setSourceRecordId(
                UUID.randomUUID()
        );

        request.setEventTimestamp(
                LocalDateTime.now()
        );

        request.setArchivedPayload(
                Map.of(
                        "loginResult",
                        "SUCCESS"
                )
        );

        request.setChecksumSha256(
                CHECKSUM
        );

        AuditHistoryResponse created =
                auditHistoryService.createAuditHistory(
                        request
                );

        assertNotNull(
                created.getHistoryId()
        );

        assertNull(
                created.getOrganizationId()
        );

        assertNull(
                created.getTenantId()
        );

        assertNull(
                created.getCorrelationId()
        );

        assertNull(
                created.getRetentionUntil()
        );

        assertNotNull(
                created.getArchivedAt()
        );
    }

    @Test
    void shouldRetrieveAuditHistoryBySource() {

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

        AuditHistoryResponse result =
                auditHistoryService.getAuditHistoryBySource(
                        "audit_event",
                        SOURCE_RECORD_ID
                );

        assertEquals(
                historyId,
                result.getHistoryId()
        );

        assertEquals(
                SOURCE_RECORD_ID,
                result.getSourceRecordId()
        );
    }

    @Test
    void shouldReturnAuditHistoryByOrganizationId() {

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

        List<AuditHistoryResponse> results =
                auditHistoryService
                        .getAuditHistoryByOrganizationId(
                                ORGANIZATION_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        ORGANIZATION_ID.equals(
                                                result.getOrganizationId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditHistoryByTenantId() {

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

        List<AuditHistoryResponse> results =
                auditHistoryService.getAuditHistoryByTenantId(
                        TENANT_ID
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        TENANT_ID.equals(
                                                result.getTenantId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditHistoryByCorrelationId() {

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

        List<AuditHistoryResponse> results =
                auditHistoryService
                        .getAuditHistoryByCorrelationId(
                                CORRELATION_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        CORRELATION_ID.equals(
                                                result.getCorrelationId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditHistoryBySourceTable() {

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

        List<AuditHistoryResponse> results =
                auditHistoryService
                        .getAuditHistoryBySourceTable(
                                "audit_event"
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "audit_event".equals(
                                                result.getSourceTable()
                                        )
                        )
        );
    }

    @Test
    void shouldRejectUnknownAuditHistoryId() {

        UUID unknownHistoryId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        auditHistoryService.getAuditHistoryById(
                                unknownHistoryId
                        )
        );
    }

    @Test
    void shouldRejectUnknownAuditHistorySource() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        auditHistoryService.getAuditHistoryBySource(
                                "audit_event",
                                UUID.randomUUID()
                        )
        );
    }

    private AuditHistoryRequest buildRequest(
            UUID sourceRecordId,
            UUID organizationId,
            UUID tenantId,
            UUID correlationId) {

        AuditHistoryRequest request =
                new AuditHistoryRequest();

        request.setSourceTable(
                "audit_event"
        );

        request.setSourceRecordId(
                sourceRecordId
        );

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventTimestamp(
                LocalDateTime.now()
        );

        request.setArchivedPayload(
                Map.of(
                        "eventType",
                        "LOGIN",
                        "eventResult",
                        "SUCCESS"
                )
        );

        request.setChecksumSha256(
                CHECKSUM
        );

        request.setRetentionUntil(
                LocalDateTime.now().plusYears(7)
        );

        return request;
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