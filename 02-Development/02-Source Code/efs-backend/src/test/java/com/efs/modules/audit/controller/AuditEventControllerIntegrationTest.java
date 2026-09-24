package com.efs.modules.audit.controller;

import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditEventControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "69696969-6969-6969-6969-696969696969"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "70707070-7070-7070-7070-707070707070"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "71717171-7171-7171-7171-717171717171"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "72727272-7272-7272-7272-727272727272"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @BeforeTransaction
    void prepareCommittedAuthorizationFixtures() {

        insertOrganization();
        insertUser();
    }

    @BeforeEach
    void setUp() {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of(
                                "audit.view"
                        ),
                        Set.of()
                )
        );
    }

    @Test
    void shouldCreateAuditEventThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "organizationId",
                                ORGANIZATION_ID.toString()
                        ),
                        Map.entry(
                                "userId",
                                USER_ID.toString()
                        ),
                        Map.entry(
                                "eventType",
                                "RULE_UPDATED"
                        ),
                        Map.entry(
                                "entityType",
                                "RULE"
                        ),
                        Map.entry(
                                "entityId",
                                ENTITY_ID.toString()
                        ),
                        Map.entry(
                                "action",
                                "UPDATE"
                        ),
                        Map.entry(
                                "sourceComponent",
                                "RULE_ENGINE"
                        ),
                        Map.entry(
                                "ipAddress",
                                "192.168.10.25"
                        ),
                        Map.entry(
                                "correlationId",
                                CORRELATION_ID.toString()
                        ),
                        Map.entry(
                                "eventResult",
                                "SUCCESS"
                        ),
                        Map.entry(
                                "eventDetails",
                                Map.of(
                                        "statusBefore",
                                        "DRAFT",
                                        "statusAfter",
                                        "ACTIVE"
                                )
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/events")
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
                        jsonPath("$.auditEventId").exists()
                )
                .andExpect(
                        jsonPath("$.eventTimestamp").exists()
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RULE_UPDATED")
                )
                .andExpect(
                        jsonPath("$.entityType")
                                .value("RULE")
                )
                .andExpect(
                        jsonPath("$.entityId")
                                .value(
                                        ENTITY_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.action")
                                .value("UPDATE")
                )
                .andExpect(
                        jsonPath("$.sourceComponent")
                                .value("RULE_ENGINE")
                )
                .andExpect(
                        jsonPath("$.eventResult")
                                .value("SUCCESS")
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventDetails.statusAfter")
                                .value("ACTIVE")
                );
    }

    @Test
    void shouldRetrieveAuditEventByIdThroughApi()
            throws Exception {

        UUID auditEventId =
                UUID.randomUUID();

        insertAuditEvent(
                auditEventId,
                "RULE_CREATED",
                "RULE",
                ENTITY_ID,
                "CREATE",
                "RULE_ENGINE",
                "192.168.10.30",
                "SUCCESS",
                CORRELATION_ID,
                """
                {"status":"DRAFT"}
                """
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/{auditEventId}",
                                auditEventId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        auditEventId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RULE_CREATED")
                )
                .andExpect(
                        jsonPath("$.entityId")
                                .value(
                                        ENTITY_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventDetails.status")
                                .value("DRAFT")
                );
    }

    @Test
    void shouldRetrieveAuditEventsByEventTypeThroughApi()
            throws Exception {

        insertAuditEvent(
                UUID.randomUUID(),
                "RULE_UPDATED",
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.40",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        insertAuditEvent(
                UUID.randomUUID(),
                "RULE_UPDATED",
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.41",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/type/{eventType}",
                                "RULE_UPDATED"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].eventType")
                                .value("RULE_UPDATED")
                )
                .andExpect(
                        jsonPath("$[1].eventType")
                                .value("RULE_UPDATED")
                );
    }

    @Test
    void shouldRetrieveAuditEventsByEntityThroughApi()
            throws Exception {

        insertAuditEvent(
                UUID.randomUUID(),
                "RULE_CREATED",
                "RULE",
                ENTITY_ID,
                "CREATE",
                "RULE_ENGINE",
                "192.168.10.50",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        insertAuditEvent(
                UUID.randomUUID(),
                "RULE_UPDATED",
                "RULE",
                ENTITY_ID,
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.51",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/entity/{entityType}/{entityId}",
                                "RULE",
                                ENTITY_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].entityId")
                                .value(
                                        ENTITY_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].entityId")
                                .value(
                                        ENTITY_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditEventsByUserIdThroughApi()
            throws Exception {

        insertAuditEvent(
                UUID.randomUUID(),
                "USER_ACTION",
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.60",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        insertAuditEvent(
                UUID.randomUUID(),
                "USER_ACTION",
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.61",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/user/{userId}",
                                USER_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$[?(@.eventType == 'USER_ACTION')]",
                                hasSize(2)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[?(@.eventType == 'USER_ACTION')].userId",
                                everyItem(
                                        is(
                                                USER_ID.toString()
                                        )
                                )
                        )
                );
    }

    @Test
    void shouldRetrieveAuditEventsByOrganizationIdThroughApi()
            throws Exception {

        insertAuditEvent(
                UUID.randomUUID(),
                "ORGANIZATION_EVENT",
                "RULE",
                UUID.randomUUID(),
                "CREATE",
                "RULE_ENGINE",
                "192.168.10.70",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        insertAuditEvent(
                UUID.randomUUID(),
                "ORGANIZATION_EVENT",
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.71",
                "SUCCESS",
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/organization/{organizationId}",
                                ORGANIZATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$[?(@.eventType == 'ORGANIZATION_EVENT')]",
                                hasSize(2)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[?(@.eventType == 'ORGANIZATION_EVENT')].organizationId",
                                everyItem(
                                        is(
                                                ORGANIZATION_ID.toString()
                                        )
                                )
                        )
                );
    }

    @Test
    void shouldRetrieveAuditEventsByCorrelationIdThroughApi()
            throws Exception {

        insertAuditEvent(
                UUID.randomUUID(),
                "CORRELATED_EVENT",
                "RULE",
                UUID.randomUUID(),
                "CREATE",
                "RULE_ENGINE",
                "192.168.10.80",
                "SUCCESS",
                CORRELATION_ID,
                null
        );

        insertAuditEvent(
                UUID.randomUUID(),
                "CORRELATED_EVENT",
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                "RULE_ENGINE",
                "192.168.10.81",
                "SUCCESS",
                CORRELATION_ID,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/correlation/{correlationId}",
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
    void shouldReturnNotFoundForUnknownAuditEventId()
            throws Exception {

        UUID unknownAuditEventId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/{auditEventId}",
                                unknownAuditEventId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnForbiddenWhenAuditViewPermissionIsMissingForLegacyRead()
            throws Exception {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of(),
                        Set.of()
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/events/type/{eventType}",
                                "MISSING_PERMISSION_TEST"
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }
    private void insertAuditEvent(
            UUID auditEventId,
            String eventType,
            String entityType,
            UUID entityId,
            String action,
            String sourceComponent,
            String ipAddress,
            String eventResult,
            UUID correlationId,
            String eventDetails) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
                    organization_id,
                    user_id,
                    event_type,
                    entity_type,
                    entity_id,
                    action,
                    source_component,
                    ip_address,
                    correlation_id,
                    event_result,
                    event_details
                )
                VALUES (
                    ?,
                    clock_timestamp(),
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS inet),
                    ?,
                    ?,
                    CAST(? AS jsonb)
                )
                """,
                auditEventId,
                ORGANIZATION_ID,
                USER_ID,
                eventType,
                entityType,
                entityId,
                action,
                sourceComponent,
                ipAddress,
                correlationId,
                eventResult,
                eventDetails
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
                ON CONFLICT DO NOTHING
                """,
                ORGANIZATION_ID,
                "EFS-AUDIT-EVENT-API-ORG",
                "EFS Audit Event API Organization",
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
                ON CONFLICT DO NOTHING
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.audit.event.api",
                "EFS Audit Event API User",
                "efs.audit.event.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}