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
class RuleParameterControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "ffffffff-ffff-ffff-ffff-ffffffffffff"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "12121212-1212-1212-1212-121212121212"
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
                "EFS-RULE-PARAMETER-API-ORG",
                "EFS Rule Parameter API Organization",
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
                "efs.rule.parameter.api",
                "EFS Rule Parameter API User",
                "efs.rule.parameter.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateRuleParameterThroughApi() throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-PARAMETER-API-001"
                );

        String requestBody =
                """
                {
                    "parameterName": "risk_threshold",
                    "parameterType": "INTEGER",
                    "parameterValue": {
                        "value": 80
                    },
                    "isSensitive": false,
                    "validationExpression": "value >= 0"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/versions/{ruleVersionId}/parameters",
                                ruleVersionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parameterId").exists())
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.parameterName").value(
                        "risk_threshold"
                ))
                .andExpect(jsonPath("$.parameterType").value(
                        "INTEGER"
                ))
                .andExpect(jsonPath("$.parameterValue.value").value(80))
                .andExpect(jsonPath("$.isSensitive").value(false))
                .andExpect(jsonPath("$.validationExpression").value(
                        "value >= 0"
                ));
    }

    @Test
    void shouldRetrieveRuleParameterByIdThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-PARAMETER-API-002"
                );

        UUID parameterId =
                insertRuleParameter(
                        ruleVersionId,
                        "transaction_threshold",
                        "DECIMAL",
                        "{\"value\":10000}",
                        false,
                        "value > 0"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/parameters/{parameterId}",
                                parameterId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parameterId").value(
                        parameterId.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.parameterName").value(
                        "transaction_threshold"
                ))
                .andExpect(jsonPath("$.parameterType").value(
                        "DECIMAL"
                ))
                .andExpect(jsonPath("$.parameterValue.value").value(10000))
                .andExpect(jsonPath("$.isSensitive").value(false))
                .andExpect(jsonPath("$.validationExpression").value(
                        "value > 0"
                ));
    }

    @Test
    void shouldRetrieveParametersByRuleVersionThroughApi()
            throws Exception {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-PARAMETER-API-003"
                );

        insertRuleParameter(
                ruleVersionId,
                "minimum_amount",
                "DECIMAL",
                "{\"value\":5000}",
                false,
                null
        );

        insertRuleParameter(
                ruleVersionId,
                "risk_threshold",
                "INTEGER",
                "{\"value\":80}",
                false,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/versions/{ruleVersionId}/parameters",
                                ruleVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$[1].ruleVersionId").value(
                        ruleVersionId.toString()
                ));
    }

    @Test
    void shouldRetrieveParametersByNameThroughApi()
            throws Exception {

        UUID firstRuleVersionId =
                insertRuleVersion(
                        "RULE-PARAMETER-API-004"
                );

        UUID secondRuleVersionId =
                insertRuleVersion(
                        "RULE-PARAMETER-API-005"
                );

        insertRuleParameter(
                firstRuleVersionId,
                "risk_threshold",
                "INTEGER",
                "{\"value\":80}",
                false,
                null
        );

        insertRuleParameter(
                secondRuleVersionId,
                "risk_threshold",
                "INTEGER",
                "{\"value\":90}",
                false,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/parameters/name/{parameterName}",
                                "risk_threshold"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].parameterName").value(
                        "risk_threshold"
                ))
                .andExpect(jsonPath("$[1].parameterName").value(
                        "risk_threshold"
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
                "Rule Parameter API Test",
                "Rule used by V53 API integration tests",
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

    private UUID insertRuleParameter(
            UUID ruleVersionId,
            String parameterName,
            String parameterType,
            String parameterValueJson,
            boolean isSensitive,
            String validationExpression) {

        UUID parameterId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_parameter (
                    parameter_id,
                    rule_version_id,
                    parameter_name,
                    parameter_type,
                    parameter_value,
                    is_sensitive,
                    validation_expression
                )
                VALUES (?, ?, ?, ?, CAST(? AS jsonb), ?, ?)
                """,
                parameterId,
                ruleVersionId,
                parameterName,
                parameterType,
                parameterValueJson,
                isSensitive,
                validationExpression
        );

        return parameterId;
    }
}