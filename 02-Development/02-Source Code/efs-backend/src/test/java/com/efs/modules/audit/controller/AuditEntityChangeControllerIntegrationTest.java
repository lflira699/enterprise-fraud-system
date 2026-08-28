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
class AuditEntityChangeControllerIntegrationTest {

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "75757575-7575-7575-7575-757575757575"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "76767676-7676-7676-7676-767676767676"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertAuditEvent(
                AUDIT_EVENT_ID
        );
    }

    @Test
    void shouldCreateAuditEntityChangeThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.of(
                        "auditEventId",
                        AUDIT_EVENT_ID.toString(),
                        "entityType",
                        "RULE",
                        "entityId",
                        ENTITY_ID.toString(),
                        "operation",
                        "UPDATE",
                        "previousValue",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        "currentValue",
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/entity-changes")
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
                        jsonPath("$.changeId").exists()
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
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
                        jsonPath("$.operation")
                                .value("UPDATE")
                )
                .andExpect(
                        jsonPath("$.previousValue.status")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$.currentValue.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.changedAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditEntityChangeByIdThroughApi()
            throws Exception {

        UUID changeId =
                UUID.randomUUID();

        insertAuditEntityChange(
                changeId,
                AUDIT_EVENT_ID,
                "RULE",
                ENTITY_ID,
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/entity-changes/{changeId}",
                                changeId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.changeId")
                                .value(
                                        changeId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.auditEventId")
                                .value(
                                        AUDIT_EVENT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.entityType")
                                .value("RULE")
                )
                .andExpect(
                        jsonPath("$.operation")
                                .value("CREATE")
                )
                .andExpect(
                        jsonPath("$.currentValue.status")
                                .value("DRAFT")
                );
    }

    @Test
    void shouldRetrieveAuditEntityChangesByAuditEventIdThroughApi()
            throws Exception {

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "RULE",
                UUID.randomUUID(),
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """
        );

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/entity-changes/event/{auditEventId}",
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
    void shouldRetrieveAuditEntityChangesByEntityThroughApi()
            throws Exception {

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "RULE",
                ENTITY_ID,
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """
        );

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "RULE",
                ENTITY_ID,
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/entity-changes/entity/{entityType}/{entityId}",
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
    void shouldRetrieveAuditEntityChangesByOperationThroughApi()
            throws Exception {

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """
        );

        insertAuditEntityChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"priority":1}
                """,
                """
                {"priority":2}
                """
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/entity-changes/operation/{operation}",
                                "UPDATE"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].operation")
                                .value("UPDATE")
                )
                .andExpect(
                        jsonPath("$[1].operation")
                                .value("UPDATE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditEntityChangeId()
            throws Exception {

        UUID unknownChangeId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/entity-changes/{changeId}",
                                unknownChangeId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownAuditEventId()
            throws Exception {

        UUID unknownAuditEventId =
                UUID.randomUUID();

        Map<String, Object> request =
                Map.of(
                        "auditEventId",
                        unknownAuditEventId.toString(),
                        "entityType",
                        "RULE",
                        "entityId",
                        ENTITY_ID.toString(),
                        "operation",
                        "UPDATE",
                        "previousValue",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        "currentValue",
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/audit/entity-changes")
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
                        status().isNotFound()
                );
    }

    private void insertAuditEvent(
            UUID auditEventId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
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
                    ?
                )
                """,
                auditEventId,
                "ENTITY_CHANGE_API_TEST",
                "UPDATE",
                "AUDIT",
                "SUCCESS"
        );
    }

    private void insertAuditEntityChange(
            UUID changeId,
            UUID auditEventId,
            String entityType,
            UUID entityId,
            String operation,
            String previousValue,
            String currentValue) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_entity_change (
                    change_id,
                    audit_event_id,
                    entity_type,
                    entity_id,
                    operation,
                    previous_value,
                    current_value,
                    changed_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    CAST(? AS jsonb),
                    clock_timestamp()
                )
                """,
                changeId,
                auditEventId,
                entityType,
                entityId,
                operation,
                previousValue,
                currentValue
        );
    }
}