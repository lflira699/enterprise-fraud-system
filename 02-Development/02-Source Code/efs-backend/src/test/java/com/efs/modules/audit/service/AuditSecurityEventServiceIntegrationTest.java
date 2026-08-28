package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditSecurityEventRequest;
import com.efs.modules.audit.dto.AuditSecurityEventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuditSecurityEventServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "87878787-8787-8787-8787-878787878787"
            );

    @Autowired
    private AuditSecurityEventServiceInterface auditSecurityEventService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        insertOrganization();
    }

    @Test
    void shouldCreateAndRetrieveAuditSecurityEventById()
            throws Exception {

        AuditSecurityEventRequest request =
                new AuditSecurityEventRequest();

        request.setOrganizationId(ORGANIZATION_ID);
        request.setEventCategory("AUTHENTICATION");
        request.setSeverity("HIGH");
        request.setSourceIp(
                InetAddress.getByName("192.168.60.10")
        );
        request.setAffectedResource("USER_ACCOUNT");
        request.setMitigationAction("ACCESS_REVIEW");

        AuditSecurityEventResponse created =
                auditSecurityEventService
                        .createAuditSecurityEvent(request);

        assertNotNull(created);
        assertNotNull(created.getSecurityEventId());
        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );
        assertEquals(
                "AUTHENTICATION",
                created.getEventCategory()
        );
        assertEquals(
                "HIGH",
                created.getSeverity()
        );
        assertNotNull(created.getDetectedAt());

        AuditSecurityEventResponse retrieved =
                auditSecurityEventService
                        .getAuditSecurityEventById(
                                created.getSecurityEventId()
                        );

        assertEquals(
                created.getSecurityEventId(),
                retrieved.getSecurityEventId()
        );
        assertEquals(
                "AUTHENTICATION",
                retrieved.getEventCategory()
        );
    }

    @Test
    void shouldReturnAuditSecurityEventsByOrganizationId() {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "AUTHENTICATION",
                "HIGH"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "ACCESS_CONTROL",
                "MEDIUM"
        );

        List<AuditSecurityEventResponse> results =
                auditSecurityEventService
                        .getAuditSecurityEventsByOrganizationId(
                                ORGANIZATION_ID
                        );

        assertEquals(2, results.size());

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
    void shouldReturnAuditSecurityEventsBySeverity() {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "AUTHENTICATION",
                "CRITICAL"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "ACCESS_CONTROL",
                "CRITICAL"
        );

        List<AuditSecurityEventResponse> results =
                auditSecurityEventService
                        .getAuditSecurityEventsBySeverity(
                                "CRITICAL"
                        );

        assertEquals(2, results.size());

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "CRITICAL".equals(
                                                result.getSeverity()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditSecurityEventsByEventCategory() {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "SUSPICIOUS_ACCESS",
                "HIGH"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                ORGANIZATION_ID,
                "SUSPICIOUS_ACCESS",
                "MEDIUM"
        );

        List<AuditSecurityEventResponse> results =
                auditSecurityEventService
                        .getAuditSecurityEventsByEventCategory(
                                "SUSPICIOUS_ACCESS"
                        );

        assertEquals(2, results.size());

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "SUSPICIOUS_ACCESS".equals(
                                                result.getEventCategory()
                                        )
                        )
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditSecurityEventRequest request =
                new AuditSecurityEventRequest();

        request.setEventCategory("SYSTEM");
        request.setSeverity("LOW");

        AuditSecurityEventResponse created =
                auditSecurityEventService
                        .createAuditSecurityEvent(request);

        assertNotNull(created.getSecurityEventId());
        assertNull(created.getAuditEventId());
        assertNull(created.getOrganizationId());
        assertNull(created.getUserId());
        assertNull(created.getSourceIp());
        assertNull(created.getAffectedResource());
        assertNull(created.getMitigationAction());
        assertNotNull(created.getDetectedAt());
    }

    @Test
    void shouldRejectUnknownAuditSecurityEventId() {

        UUID unknownSecurityEventId =
                UUID.randomUUID();

        assertThrows(
                NoSuchElementException.class,
                () ->
                        auditSecurityEventService
                                .getAuditSecurityEventById(
                                        unknownSecurityEventId
                                )
        );
    }

    private void insertAuditSecurityEvent(
            UUID securityEventId,
            UUID organizationId,
            String eventCategory,
            String severity) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_security_event (
                    security_event_id,
                    organization_id,
                    event_category,
                    severity,
                    detected_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                securityEventId,
                organizationId,
                eventCategory,
                severity
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
                "EFS-AUDIT-SECURITY-EVENT-ORG",
                "EFS Audit Security Event Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }
}