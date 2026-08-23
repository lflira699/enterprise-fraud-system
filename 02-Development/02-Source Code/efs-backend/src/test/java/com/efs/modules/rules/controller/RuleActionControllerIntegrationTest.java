package com.efs.modules.rules.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleActionControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "cccccccc-cccc-cccc-cccc-cccccccccccc"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

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
                "EFS-RULE-ACTION-API-ORG",
                "EFS Rule Action API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

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
                CREATED_BY,
                ORGANIZATION_ID,
                "efs.rule.action.api",
                "EFS Rule Action API User",
                "efs.rule.action.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateRuleActionThroughApi() throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-ACTION-API-001"
                );

        String requestBody =
                """
                {
                    "actionType": "CREATE_ALERT",
                    "executionOrder": 1,
                    "parameterJson": {
                        "priority": "HIGH",
                        "queue": "FRAUD"
                    },
                    "isAsync": false
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/versions/{ruleVersionId}/actions",
                                ruleVersionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actionId").exists())
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.actionType").value(
                        "CREATE_ALERT"
                ))
                .andExpect(jsonPath("$.executionOrder").value(1))
                .andExpect(jsonPath("$.parameterJson.priority").value(
                        "HIGH"
                ))
                .andExpect(jsonPath("$.parameterJson.queue").value(
                        "FRAUD"
                ))
                .andExpect(jsonPath("$.isAsync").value(false))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveRuleActionByIdThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-ACTION-API-002"
                );

        UUID actionId =
                insertRuleAction(
                        ruleVersionId,
                        "CREATE_CASE",
                        (short) 1,
                        "{\"queue\":\"ATO\"}",
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/actions/{actionId}",
                                actionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionId").value(
                        actionId.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.actionType").value(
                        "CREATE_CASE"
                ))
                .andExpect(jsonPath("$.executionOrder").value(1))
                .andExpect(jsonPath("$.parameterJson.queue").value(
                        "ATO"
                ))
                .andExpect(jsonPath("$.isAsync").value(false));
    }

    @Test
    void shouldRetrieveActionsByRuleVersionThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-ACTION-API-003"
                );

        insertRuleAction(
                ruleVersionId,
                "CREATE_CASE",
                (short) 3,
                "{\"queue\":\"FRAUD\"}",
                false
        );

        insertRuleAction(
                ruleVersionId,
                "CREATE_ALERT",
                (short) 1,
                "{\"priority\":\"HIGH\"}",
                false
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/versions/{ruleVersionId}/actions",
                                ruleVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].executionOrder").value(1))
                .andExpect(jsonPath("$[0].actionType").value(
                        "CREATE_ALERT"
                ))
                .andExpect(jsonPath("$[1].executionOrder").value(3))
                .andExpect(jsonPath("$[1].actionType").value(
                        "CREATE_CASE"
                ));
    }

    @Test
    void shouldRetrieveActionsByTypeThroughApi()
            throws Exception {

        UUID firstRuleVersionId =
                insertRuleVersion(
                        "RULE-ACTION-API-004"
                );

        UUID secondRuleVersionId =
                insertRuleVersion(
                        "RULE-ACTION-API-005"
                );

        insertRuleAction(
                firstRuleVersionId,
                "CREATE_ALERT",
                (short) 1,
                "{\"priority\":\"HIGH\"}",
                false
        );

        insertRuleAction(
                secondRuleVersionId,
                "CREATE_ALERT",
                (short) 1,
                "{\"priority\":\"CRITICAL\"}",
                true
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/actions/type/{actionType}",
                                "CREATE_ALERT"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionType").value(
                        "CREATE_ALERT"
                ))
                .andExpect(jsonPath("$[1].actionType").value(
                        "CREATE_ALERT"
                ));
    }

    private UUID insertRuleVersion(
            String ruleCode) {

        UUID ruleId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule (
                    rule_id,
                    rule_code,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    current_version,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                ruleId,
                ruleCode,
                "Rule Action API Test",
                "Rule used by V52 API integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                ruleVersionId,
                ruleId,
                1,
                null,
                "DRAFT",
                "Initial rule version",
                CREATED_BY,
                null
        );

        return ruleVersionId;
    }

    private UUID insertRuleAction(
            UUID ruleVersionId,
            String actionType,
            short executionOrder,
            String parameterJson,
            boolean isAsync) {

        UUID actionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_action (
                    action_id,
                    rule_version_id,
                    action_type,
                    execution_order,
                    parameter_json,
                    is_async,
                    created_at
                )
                VALUES (?, ?, ?, ?, CAST(? AS jsonb), ?, CURRENT_TIMESTAMP)
                """,
                actionId,
                ruleVersionId,
                actionType,
                executionOrder,
                parameterJson,
                isAsync
        );

        return actionId;
    }
}