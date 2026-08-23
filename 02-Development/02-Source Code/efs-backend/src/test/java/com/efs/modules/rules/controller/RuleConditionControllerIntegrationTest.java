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
class RuleConditionControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
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
                "EFS-RULE-CONDITION-API-ORG",
                "EFS Rule Condition API Organization",
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
                "efs.rule.condition.api",
                "EFS Rule Condition API User",
                "efs.rule.condition.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateRuleConditionThroughApi() throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-CONDITION-API-001"
                );

        String requestBody =
                """
                {
                    "conditionOrder": 1,
                    "attributeName": "transaction.amount",
                    "comparisonOperator": "GREATER_THAN",
                    "comparisonValue": {
                        "value": 10000
                    },
                    "logicalOperator": null,
                    "isRequired": true
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/versions/{ruleVersionId}/conditions",
                                ruleVersionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conditionId").exists())
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.conditionOrder").value(1))
                .andExpect(jsonPath("$.attributeName").value(
                        "transaction.amount"
                ))
                .andExpect(jsonPath("$.comparisonOperator").value(
                        "GREATER_THAN"
                ))
                .andExpect(jsonPath("$.comparisonValue.value").value(
                        10000
                ))
                .andExpect(jsonPath("$.isRequired").value(true));
    }

    @Test
    void shouldRetrieveRuleConditionByIdThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-CONDITION-API-002"
                );

        UUID conditionId =
                insertRuleCondition(
                        ruleVersionId,
                        (short) 1,
                        "transaction.country",
                        "EQUALS",
                        "{\"value\":\"GT\"}",
                        null,
                        true
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/conditions/{conditionId}",
                                conditionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conditionId").value(
                        conditionId.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.conditionOrder").value(1))
                .andExpect(jsonPath("$.attributeName").value(
                        "transaction.country"
                ))
                .andExpect(jsonPath("$.comparisonOperator").value(
                        "EQUALS"
                ))
                .andExpect(jsonPath("$.comparisonValue.value").value(
                        "GT"
                ))
                .andExpect(jsonPath("$.isRequired").value(true));
    }

    @Test
    void shouldRetrieveConditionsByRuleVersionThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-CONDITION-API-003"
                );

        insertRuleCondition(
                ruleVersionId,
                (short) 3,
                "transaction.country",
                "EQUALS",
                "{\"value\":\"GT\"}",
                "AND",
                true
        );

        insertRuleCondition(
                ruleVersionId,
                (short) 1,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}",
                null,
                true
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/versions/{ruleVersionId}/conditions",
                                ruleVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].conditionOrder").value(1))
                .andExpect(jsonPath("$[0].attributeName").value(
                        "transaction.amount"
                ))
                .andExpect(jsonPath("$[1].conditionOrder").value(3))
                .andExpect(jsonPath("$[1].attributeName").value(
                        "transaction.country"
                ));
    }

    @Test
    void shouldRetrieveConditionsByAttributeNameThroughApi()
            throws Exception {

        UUID firstRuleVersionId =
                insertRuleVersion(
                        "RULE-CONDITION-API-004"
                );

        UUID secondRuleVersionId =
                insertRuleVersion(
                        "RULE-CONDITION-API-005"
                );

        insertRuleCondition(
                firstRuleVersionId,
                (short) 1,
                "device.risk_score",
                "GREATER_THAN",
                "{\"value\":80}",
                null,
                true
        );

        insertRuleCondition(
                secondRuleVersionId,
                (short) 1,
                "device.risk_score",
                "GREATER_THAN",
                "{\"value\":90}",
                null,
                true
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/conditions/attribute/{attributeName}",
                                "device.risk_score"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attributeName").value(
                        "device.risk_score"
                ))
                .andExpect(jsonPath("$[1].attributeName").value(
                        "device.risk_score"
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
                "Rule Condition API Test",
                "Rule used by V51 API integration tests",
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

    private UUID insertRuleCondition(
            UUID ruleVersionId,
            short conditionOrder,
            String attributeName,
            String comparisonOperator,
            String comparisonValueJson,
            String logicalOperator,
            boolean isRequired) {

        UUID conditionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_condition (
                    condition_id,
                    rule_version_id,
                    condition_order,
                    attribute_name,
                    comparison_operator,
                    comparison_value,
                    logical_operator,
                    is_required
                )
                VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb), ?, ?)
                """,
                conditionId,
                ruleVersionId,
                conditionOrder,
                attributeName,
                comparisonOperator,
                comparisonValueJson,
                logicalOperator,
                isRequired
        );

        return conditionId;
    }
}