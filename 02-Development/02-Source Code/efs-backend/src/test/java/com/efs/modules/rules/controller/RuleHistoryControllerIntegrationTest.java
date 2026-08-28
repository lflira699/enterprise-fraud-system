package com.efs.modules.rules.controller;

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
class RuleHistoryControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "61616161-6161-6161-6161-616161616161"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "62626262-6262-6262-6262-626262626262"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "63636363-6363-6363-6363-636363636363"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "64646464-6464-6464-6464-646464646464"
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
    }

    @Test
    void shouldCreateRuleHistoryThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.of(
                        "entityType", "RULE",
                        "entityId", ENTITY_ID.toString(),
                        "operationType", "UPDATE",
                        "previousValue",
                        Map.of(
                                "status", "DRAFT"
                        ),
                        "currentValue",
                        Map.of(
                                "status", "ACTIVE"
                        ),
                        "changeReason", "Rule activated",
                        "changedBy", USER_ID.toString(),
                        "correlationId", CORRELATION_ID.toString()
                );

        mockMvc.perform(
                        post("/api/v1/rules/history")
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
                        jsonPath("$.operationType")
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
                        jsonPath("$.changeReason")
                                .value("Rule activated")
                )
                .andExpect(
                        jsonPath("$.changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.changedAt").exists()
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveRuleHistoryByIdThroughApi()
            throws Exception {

        UUID historyId =
                UUID.randomUUID();

        insertRuleHistory(
                historyId,
                "RULE",
                ENTITY_ID,
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """,
                "Rule created",
                USER_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/{historyId}",
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
                        jsonPath("$.entityType")
                                .value("RULE")
                )
                .andExpect(
                        jsonPath("$.operationType")
                                .value("CREATE")
                )
                .andExpect(
                        jsonPath("$.currentValue.status")
                                .value("DRAFT")
                );
    }

    @Test
    void shouldRetrieveRuleHistoriesByEntityThroughApi()
            throws Exception {

        UUID firstHistoryId =
                UUID.randomUUID();

        UUID secondHistoryId =
                UUID.randomUUID();

        insertRuleHistory(
                firstHistoryId,
                "RULE",
                ENTITY_ID,
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """,
                "Rule created",
                USER_ID,
                CORRELATION_ID
        );

        sleepBriefly();

        insertRuleHistory(
                secondHistoryId,
                "RULE",
                ENTITY_ID,
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """,
                "Rule activated",
                USER_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/entity/{entityType}/{entityId}",
                                "RULE",
                                ENTITY_ID
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
                        jsonPath("$[0].historyId")
                                .value(
                                        secondHistoryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].operationType")
                                .value("UPDATE")
                )
                .andExpect(
                        jsonPath("$[1].historyId")
                                .value(
                                        firstHistoryId.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveRuleHistoriesByChangedByThroughApi()
            throws Exception {

        UUID firstHistoryId =
                UUID.randomUUID();

        UUID secondHistoryId =
                UUID.randomUUID();

        insertRuleHistory(
                firstHistoryId,
                "RULE",
                UUID.randomUUID(),
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """,
                "Created",
                USER_ID,
                UUID.randomUUID()
        );

        sleepBriefly();

        insertRuleHistory(
                secondHistoryId,
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"priority":1}
                """,
                """
                {"priority":2}
                """,
                "Updated",
                USER_ID,
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/changed-by/{changedBy}",
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
                        jsonPath("$[0].historyId")
                                .value(
                                        secondHistoryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].changedBy")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].historyId")
                                .value(
                                        firstHistoryId.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveRuleHistoriesByOperationTypeThroughApi()
            throws Exception {

        UUID firstHistoryId =
                UUID.randomUUID();

        UUID secondHistoryId =
                UUID.randomUUID();

        insertRuleHistory(
                firstHistoryId,
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """,
                "Rule updated",
                USER_ID,
                UUID.randomUUID()
        );

        sleepBriefly();

        insertRuleHistory(
                secondHistoryId,
                "POLICY",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"priority":1}
                """,
                """
                {"priority":2}
                """,
                "Policy updated",
                USER_ID,
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/operation/{operationType}",
                                "UPDATE"
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
                        jsonPath("$[0].historyId")
                                .value(
                                        secondHistoryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].operationType")
                                .value("UPDATE")
                )
                .andExpect(
                        jsonPath("$[1].historyId")
                                .value(
                                        firstHistoryId.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveRuleHistoriesByCorrelationIdThroughApi()
            throws Exception {

        UUID firstHistoryId =
                UUID.randomUUID();

        UUID secondHistoryId =
                UUID.randomUUID();

        insertRuleHistory(
                firstHistoryId,
                "RULE",
                UUID.randomUUID(),
                "CREATE",
                null,
                """
                {"status":"DRAFT"}
                """,
                "Created",
                USER_ID,
                CORRELATION_ID
        );

        sleepBriefly();

        insertRuleHistory(
                secondHistoryId,
                "RULE",
                UUID.randomUUID(),
                "UPDATE",
                """
                {"status":"DRAFT"}
                """,
                """
                {"status":"ACTIVE"}
                """,
                "Updated",
                USER_ID,
                CORRELATION_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/correlation/{correlationId}",
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
                        jsonPath("$[0].historyId")
                                .value(
                                        secondHistoryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].correlationId")
                                .value(
                                        CORRELATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].historyId")
                                .value(
                                        firstHistoryId.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownHistoryId()
            throws Exception {

        UUID unknownHistoryId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/rules/history/{historyId}",
                                unknownHistoryId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertRuleHistory(
            UUID historyId,
            String entityType,
            UUID entityId,
            String operationType,
            String previousValue,
            String currentValue,
            String changeReason,
            UUID changedBy,
            UUID correlationId) {

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_history (
                    history_id,
                    entity_type,
                    entity_id,
                    operation_type,
                    previous_value,
                    current_value,
                    change_reason,
                    changed_by,
                    changed_at,
                    correlation_id
                )
                VALUES (
                    ?, ?, ?, ?,
                    CAST(? AS jsonb),
                    CAST(? AS jsonb),
                    ?, ?,
                    clock_timestamp(),
                    ?
                )
                """,
                historyId,
                entityType,
                entityId,
                operationType,
                previousValue,
                currentValue,
                changeReason,
                changedBy,
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
                "EFS-RULE-HISTORY-API",
                "EFS Rule History API Test Organization",
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
                "efs.rule.history.api",
                "EFS Rule History API Test User",
                "efs.rule.history.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void sleepBriefly() {

        try {
            Thread.sleep(5);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}