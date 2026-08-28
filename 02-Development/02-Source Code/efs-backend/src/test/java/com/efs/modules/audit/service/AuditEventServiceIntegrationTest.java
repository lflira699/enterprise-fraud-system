package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AuditEventServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "65656565-6565-6565-6565-656565656565"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "67676767-6767-6767-6767-676767676767"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "68686868-6868-6868-6868-686868686868"
            );

    @Autowired
    private AuditEventServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAndRetrieveAuditEventById()
            throws Exception {

        AuditEventResponse created =
                service.createAuditEvent(
                        buildRequest(
                                "RULE_UPDATED",
                                "RULE",
                                ENTITY_ID,
                                "UPDATE",
                                "RULE_ENGINE",
                                InetAddress.getByName("192.168.10.25"),
                                "SUCCESS",
                                Map.of(
                                        "statusBefore", "DRAFT",
                                        "statusAfter", "ACTIVE"
                                )
                        )
                );

        assertNotNull(
                created.getAuditEventId()
        );

        assertNotNull(
                created.getEventTimestamp()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                USER_ID,
                created.getUserId()
        );

        assertEquals(
                "RULE_UPDATED",
                created.getEventType()
        );

        assertEquals(
                "RULE",
                created.getEntityType()
        );

        assertEquals(
                ENTITY_ID,
                created.getEntityId()
        );

        assertEquals(
                "UPDATE",
                created.getAction()
        );

        assertEquals(
                "RULE_ENGINE",
                created.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                created.getEventResult()
        );

        assertEquals(
                CORRELATION_ID,
                created.getCorrelationId()
        );

        assertEquals(
                "ACTIVE",
                created.getEventDetails().get("statusAfter")
        );

        AuditEventResponse retrieved =
                service.getAuditEventById(
                        created.getAuditEventId()
                );

        assertEquals(
                created.getAuditEventId(),
                retrieved.getAuditEventId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setEventType(
                "SYSTEM_EVENT"
        );

        request.setAction(
                "CREATE"
        );

        request.setSourceComponent(
                "AUDIT"
        );

        request.setEventResult(
                "SUCCESS"
        );

        AuditEventResponse created =
                service.createAuditEvent(request);

        assertNotNull(
                created.getAuditEventId()
        );

        assertNotNull(
                created.getEventTimestamp()
        );

        assertNull(
                created.getOrganizationId()
        );

        assertNull(
                created.getTenantId()
        );

        assertNull(
                created.getUserId()
        );

        assertNull(
                created.getSessionId()
        );

        assertNull(
                created.getEntityType()
        );

        assertNull(
                created.getEntityId()
        );

        assertNull(
                created.getIpAddress()
        );

        assertNull(
                created.getCorrelationId()
        );

        assertNull(
                created.getEventDetails()
        );
    }

    @Test
    void shouldPreserveJsonEventDetails()
            throws Exception {

        AuditEventResponse created =
                service.createAuditEvent(
                        buildRequest(
                                "RULE_EVALUATED",
                                "RULE",
                                ENTITY_ID,
                                "EXECUTE",
                                "RULE_ENGINE",
                                InetAddress.getByName("10.10.10.10"),
                                "SUCCESS",
                                Map.of(
                                        "matched", true,
                                        "score", 87,
                                        "decision", "REVIEW"
                                )
                        )
                );

        assertNotNull(
                created.getEventDetails()
        );

        assertEquals(
                Boolean.TRUE,
                created.getEventDetails().get("matched")
        );

        assertEquals(
                87,
                created.getEventDetails().get("score")
        );

        assertEquals(
                "REVIEW",
                created.getEventDetails().get("decision")
        );
    }

    @Test
    void shouldReturnAuditEventsByEventType() {

        service.createAuditEvent(
                buildRequestUnchecked(
                        "RULE_UPDATED",
                        "RULE",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.10",
                        "SUCCESS",
                        null
                )
        );

        service.createAuditEvent(
                buildRequestUnchecked(
                        "RULE_UPDATED",
                        "RULE",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.11",
                        "SUCCESS",
                        null
                )
        );

        List<AuditEventResponse> events =
                service.getAuditEventsByEventType(
                        "RULE_UPDATED"
                );

        assertEquals(
                2,
                events.size()
        );

        assertEquals(
                "RULE_UPDATED",
                events.get(0).getEventType()
        );

        assertEquals(
                "RULE_UPDATED",
                events.get(1).getEventType()
        );
    }

    @Test
    void shouldReturnAuditEventsByEntity() {

        service.createAuditEvent(
                buildRequestUnchecked(
                        "RULE_CREATED",
                        "RULE",
                        ENTITY_ID,
                        "CREATE",
                        "RULE_ENGINE",
                        "192.168.1.20",
                        "SUCCESS",
                        null
                )
        );

        service.createAuditEvent(
                buildRequestUnchecked(
                        "RULE_UPDATED",
                        "RULE",
                        ENTITY_ID,
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.21",
                        "SUCCESS",
                        null
                )
        );

        List<AuditEventResponse> events =
                service.getAuditEventsByEntity(
                        "RULE",
                        ENTITY_ID
                );

        assertEquals(
                2,
                events.size()
        );

        assertEquals(
                ENTITY_ID,
                events.get(0).getEntityId()
        );

        assertEquals(
                ENTITY_ID,
                events.get(1).getEntityId()
        );
    }

    @Test
    void shouldReturnAuditEventsByUserId() {

        service.createAuditEvent(
                buildRequestUnchecked(
                        "USER_ACTION",
                        "RULE",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.30",
                        "SUCCESS",
                        null
                )
        );

        service.createAuditEvent(
                buildRequestUnchecked(
                        "USER_ACTION",
                        "POLICY",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.31",
                        "SUCCESS",
                        null
                )
        );

        List<AuditEventResponse> events =
                service.getAuditEventsByUserId(
                        USER_ID
                );

        assertEquals(
                2,
                events.size()
        );

        assertEquals(
                USER_ID,
                events.get(0).getUserId()
        );

        assertEquals(
                USER_ID,
                events.get(1).getUserId()
        );
    }

    @Test
    void shouldReturnAuditEventsByOrganizationId() {

        service.createAuditEvent(
                buildRequestUnchecked(
                        "ORGANIZATION_EVENT",
                        "RULE",
                        UUID.randomUUID(),
                        "CREATE",
                        "RULE_ENGINE",
                        "192.168.1.40",
                        "SUCCESS",
                        null
                )
        );

        service.createAuditEvent(
                buildRequestUnchecked(
                        "ORGANIZATION_EVENT",
                        "POLICY",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.41",
                        "SUCCESS",
                        null
                )
        );

        List<AuditEventResponse> events =
                service.getAuditEventsByOrganizationId(
                        ORGANIZATION_ID
                );

        assertEquals(
                2,
                events.size()
        );

        assertEquals(
                ORGANIZATION_ID,
                events.get(0).getOrganizationId()
        );

        assertEquals(
                ORGANIZATION_ID,
                events.get(1).getOrganizationId()
        );
    }

    @Test
    void shouldReturnAuditEventsByCorrelationId() {

        service.createAuditEvent(
                buildRequestUnchecked(
                        "CORRELATED_EVENT",
                        "RULE",
                        UUID.randomUUID(),
                        "CREATE",
                        "RULE_ENGINE",
                        "192.168.1.50",
                        "SUCCESS",
                        null
                )
        );

        service.createAuditEvent(
                buildRequestUnchecked(
                        "CORRELATED_EVENT",
                        "RULE",
                        UUID.randomUUID(),
                        "UPDATE",
                        "RULE_ENGINE",
                        "192.168.1.51",
                        "SUCCESS",
                        null
                )
        );

        List<AuditEventResponse> events =
                service.getAuditEventsByCorrelationId(
                        CORRELATION_ID
                );

        assertEquals(
                2,
                events.size()
        );

        assertEquals(
                CORRELATION_ID,
                events.get(0).getCorrelationId()
        );

        assertEquals(
                CORRELATION_ID,
                events.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldRejectUnknownAuditEventId() {

        UUID unknownAuditEventId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAuditEventById(
                        unknownAuditEventId
                )
        );
    }

    private AuditEventRequest buildRequest(
            String eventType,
            String entityType,
            UUID entityId,
            String action,
            String sourceComponent,
            InetAddress ipAddress,
            String eventResult,
            Map<String, Object> eventDetails) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setUserId(
                USER_ID
        );

        request.setEventType(
                eventType
        );

        request.setEntityType(
                entityType
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                action
        );

        request.setSourceComponent(
                sourceComponent
        );

        request.setIpAddress(
                ipAddress
        );

        request.setCorrelationId(
                CORRELATION_ID
        );

        request.setEventResult(
                eventResult
        );

        request.setEventDetails(
                eventDetails
        );

        return request;
    }

    private AuditEventRequest buildRequestUnchecked(
            String eventType,
            String entityType,
            UUID entityId,
            String action,
            String sourceComponent,
            String ipAddress,
            String eventResult,
            Map<String, Object> eventDetails) {

        try {
            return buildRequest(
                    eventType,
                    entityType,
                    entityId,
                    action,
                    sourceComponent,
                    InetAddress.getByName(ipAddress),
                    eventResult,
                    eventDetails
            );
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
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
                "EFS-AUDIT-EVENT-TEST-ORG",
                "EFS Audit Event Test Organization",
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
                "efs.audit.event.test",
                "EFS Audit Event Test User",
                "efs.audit.event.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}