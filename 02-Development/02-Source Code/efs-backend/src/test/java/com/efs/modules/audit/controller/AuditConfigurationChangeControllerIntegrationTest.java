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
class AuditConfigurationChangeControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "94949494-9494-9494-9494-949494949494"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "95959595-9595-9595-9595-959595959595"
            );

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "96969696-9696-9696-9696-969696969696"
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
    void shouldCreateAuditConfigurationChangeThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "auditEventId",
                                AUDIT_EVENT_ID.toString()
                        ),
                        Map.entry(
                                "configurationKey",
                                "fraud.rules.threshold"
                        ),
                        Map.entry(
                                "previousValue",
                                Map.of(
                                        "value",
                                        70
                                )
                        ),
                        Map.entry(
                                "currentValue",
                                Map.of(
                                        "value",
                                        80
                                )
                        ),
                        Map.entry(
                                "changedBy",
                                USER_ID.toString()
                        ),
                        Map.entry(
                                "changeReason",
                                "Controlled configuration update"
                        )
                );

        mockMvc.perform(
                        post(
                                "/api/v1/audit/configuration-changes"
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
                        jsonPath("$.configurationChangeId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.configurationKey")
                                .value(
                                        "fraud.rules.threshold"
                                )
                )
                .andExpect(
                        jsonPath("$.previousValue.value")
                                .value(70)
                )
                .andExpect(
                        jsonPath("$.currentValue.value")
                                .value(80)
                )
                .andExpect(
                        jsonPath("$.changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.changeReason")
                                .value(
                                        "Controlled configuration update"
                                )
                )
                .andExpect(
                        jsonPath("$.changedAt")
                                .exists()
                );
    }

    @Test
    void shouldRetrieveAuditConfigurationChangeByIdThroughApi()
            throws Exception {

        UUID configurationChangeId =
                UUID.randomUUID();

        insertConfigurationChange(
                configurationChangeId,
                "fraud.rules.threshold",
                USER_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/configuration-changes/{configurationChangeId}",
                                configurationChangeId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.configurationChangeId")
                                .value(
                                        configurationChangeId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.configurationKey")
                                .value(
                                        "fraud.rules.threshold"
                                )
                )
                .andExpect(
                        jsonPath("$.changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveConfigurationChangesByAuditEventIdThroughApi()
            throws Exception {

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.feature.toggle",
                USER_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/configuration-changes/audit-event/{auditEventId}",
                                AUDIT_EVENT_ID
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
    void shouldRetrieveConfigurationChangesByConfigurationKeyThroughApi()
            throws Exception {

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.rules.threshold",
                USER_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/configuration-changes/configuration-key"
                        )
                                .param(
                                        "configurationKey",
                                        "fraud.rules.threshold"
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
                        jsonPath("$[0].configurationKey")
                                .value(
                                        "fraud.rules.threshold"
                                )
                )
                .andExpect(
                        jsonPath("$[1].configurationKey")
                                .value(
                                        "fraud.rules.threshold"
                                )
                );
    }

    @Test
    void shouldRetrieveConfigurationChangesByChangedByThroughApi()
            throws Exception {

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                "fraud.feature.toggle",
                USER_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/configuration-changes/changed-by/{changedBy}",
                                USER_ID
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
                        jsonPath("$[0].changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownConfigurationChangeId()
            throws Exception {

        UUID unknownConfigurationChangeId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/configuration-changes/{configurationChangeId}",
                                unknownConfigurationChangeId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertConfigurationChange(
            UUID configurationChangeId,
            String configurationKey,
            UUID changedBy) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_configuration_change (
                    configuration_change_id,
                    audit_event_id,
                    configuration_key,
                    previous_value,
                    current_value,
                    changed_by,
                    change_reason,
                    changed_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    CAST(? AS jsonb),
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                configurationChangeId,
                AUDIT_EVENT_ID,
                configurationKey,
                "{\"value\":70}",
                "{\"value\":80}",
                changedBy,
                "Controller integration test change"
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
                "EFS-AUDIT-CONFIG-API-ORG",
                "EFS Audit Configuration API Organization",
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
                "efs.audit.configuration.api",
                "EFS Audit Configuration API User",
                "efs.audit.configuration.api@example.com",
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
                "CONFIGURATION_CHANGE_API_TEST",
                "UPDATE_CONFIGURATION",
                "AUDIT",
                "SUCCESS"
        );
    }
}