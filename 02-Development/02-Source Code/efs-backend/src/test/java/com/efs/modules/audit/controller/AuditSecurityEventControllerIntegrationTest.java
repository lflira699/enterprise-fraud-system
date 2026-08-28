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
class AuditSecurityEventControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "89898989-8989-8989-8989-898989898989"
            );

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "90909090-9090-9090-9090-909090909090"
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
        insertAuditEvent();
    }

    @Test
    void shouldCreateAuditSecurityEventThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "auditEventId",
                                AUDIT_EVENT_ID.toString()
                        ),
                        Map.entry(
                                "organizationId",
                                ORGANIZATION_ID.toString()
                        ),
                        Map.entry(
                                "userId",
                                USER_ID.toString()
                        ),
                        Map.entry(
                                "eventCategory",
                                "AUTHENTICATION"
                        ),
                        Map.entry(
                                "severity",
                                "HIGH"
                        ),
                        Map.entry(
                                "sourceIp",
                                "192.168.70.10"
                        ),
                        Map.entry(
                                "affectedResource",
                                "USER_ACCOUNT"
                        ),
                        Map.entry(
                                "mitigationAction",
                                "ACCESS_REVIEW"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/security-events")
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
                        jsonPath("$.securityEventId").exists()
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
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
                        jsonPath("$.eventCategory")
                                .value("AUTHENTICATION")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.affectedResource")
                                .value("USER_ACCOUNT")
                )
                .andExpect(
                        jsonPath("$.mitigationAction")
                                .value("ACCESS_REVIEW")
                )
                .andExpect(
                        jsonPath("$.detectedAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditSecurityEventByIdThroughApi()
            throws Exception {

        UUID securityEventId =
                UUID.randomUUID();

        insertAuditSecurityEvent(
                securityEventId,
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "AUTHENTICATION",
                "HIGH",
                "192.168.70.20",
                "USER_ACCOUNT",
                "ACCESS_REVIEW"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/{securityEventId}",
                                securityEventId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.securityEventId")
                                .value(
                                        securityEventId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
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
                        jsonPath("$.eventCategory")
                                .value("AUTHENTICATION")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("HIGH")
                );
    }

    @Test
    void shouldRetrieveAuditSecurityEventsByUserIdThroughApi()
            throws Exception {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "AUTHENTICATION",
                "HIGH",
                "192.168.70.30",
                "USER_ACCOUNT",
                "ACCESS_REVIEW"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "ACCESS_CONTROL",
                "MEDIUM",
                "192.168.70.31",
                "SESSION",
                "SESSION_REVIEW"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/user/{userId}",
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
    void shouldRetrieveAuditSecurityEventsByOrganizationIdThroughApi()
            throws Exception {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "AUTHENTICATION",
                "HIGH",
                "192.168.70.40",
                "USER_ACCOUNT",
                "ACCESS_REVIEW"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "SUSPICIOUS_ACCESS",
                "CRITICAL",
                "192.168.70.41",
                "API",
                "BLOCK_ACCESS"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/organization/{organizationId}",
                                ORGANIZATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
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
    void shouldRetrieveAuditSecurityEventsByAuditEventIdThroughApi()
            throws Exception {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "AUTHENTICATION",
                "HIGH",
                "192.168.70.50",
                "USER_ACCOUNT",
                "ACCESS_REVIEW"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "ACCESS_CONTROL",
                "MEDIUM",
                "192.168.70.51",
                "SESSION",
                "SESSION_REVIEW"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/audit-event/{auditEventId}",
                                AUDIT_EVENT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditSecurityEventsBySeverityThroughApi()
            throws Exception {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "AUTHENTICATION",
                "CRITICAL",
                "192.168.70.60",
                "USER_ACCOUNT",
                "BLOCK_ACCESS"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "SUSPICIOUS_ACCESS",
                "CRITICAL",
                "192.168.70.61",
                "API",
                "BLOCK_ACCESS"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/severity/{severity}",
                                "CRITICAL"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].severity")
                                .value("CRITICAL")
                )
                .andExpect(
                        jsonPath("$[1].severity")
                                .value("CRITICAL")
                );
    }

    @Test
    void shouldRetrieveAuditSecurityEventsByEventCategoryThroughApi()
            throws Exception {

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "SUSPICIOUS_ACCESS",
                "HIGH",
                "192.168.70.70",
                "USER_ACCOUNT",
                "ACCESS_REVIEW"
        );

        insertAuditSecurityEvent(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "SUSPICIOUS_ACCESS",
                "CRITICAL",
                "192.168.70.71",
                "API",
                "BLOCK_ACCESS"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/security-events/category/{eventCategory}",
                                "SUSPICIOUS_ACCESS"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].eventCategory")
                                .value("SUSPICIOUS_ACCESS")
                )
                .andExpect(
                        jsonPath("$[1].eventCategory")
                                .value("SUSPICIOUS_ACCESS")
                );
    }

    private void insertAuditSecurityEvent(
            UUID securityEventId,
            UUID auditEventId,
            UUID organizationId,
            UUID userId,
            String eventCategory,
            String severity,
            String sourceIp,
            String affectedResource,
            String mitigationAction) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_security_event (
                    security_event_id,
                    audit_event_id,
                    organization_id,
                    user_id,
                    event_category,
                    severity,
                    source_ip,
                    affected_resource,
                    mitigation_action,
                    detected_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS inet),
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                securityEventId,
                auditEventId,
                organizationId,
                userId,
                eventCategory,
                severity,
                sourceIp,
                affectedResource,
                mitigationAction
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
                "EFS-AUDIT-SECURITY-API-ORG",
                "EFS Audit Security API Organization",
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
                "efs.audit.security.api",
                "EFS Audit Security API User",
                "efs.audit.security.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertAuditEvent() {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
                    organization_id,
                    user_id,
                    event_type,
                    action,
                    source_component,
                    event_result
                )
                VALUES (
                    ?,
                    clock_timestamp(),
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """,
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "SECURITY_EVENT_TEST",
                "DETECT",
                "AUDIT",
                "SUCCESS"
        );
    }
}