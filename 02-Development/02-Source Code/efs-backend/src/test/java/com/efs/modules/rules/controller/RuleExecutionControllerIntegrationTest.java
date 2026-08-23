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

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleExecutionControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "25252525-2525-2525-2525-252525252525"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "26262626-2626-2626-2626-262626262626"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "27272727-2727-2727-2727-272727272727"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "28282828-2828-2828-2828-282828282828"
            );

    private static final UUID RULE_ID =
            UUID.fromString(
                    "29292929-2929-2929-2929-292929292929"
            );

    private static final UUID RULE_VERSION_ID =
            UUID.fromString(
                    "30303030-3030-3030-3030-303030303030"
            );

    private static final UUID POLICY_ID =
            UUID.fromString(
                    "31313131-3131-3131-3131-313131313131"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertCustomer();
        insertUser();
        insertTransaction();
        insertRule();
        insertRuleVersion();
        insertPolicy();
    }

    @Test
    void shouldCreateRuleExecutionThroughApi()
            throws Exception {

        String requestBody =
                """
                {
                    "ruleId": "%s",
                    "ruleVersionId": "%s",
                    "policyId": "%s",
                    "transactionId": "%s",
                    "executionStatus": "COMPLETED",
                    "matched": true,
                    "executionTimeMs": 15,
                    "errorCode": null,
                    "engineInstance": "EFS-RULE-ENGINE-API"
                }
                """.formatted(
                        RULE_ID,
                        RULE_VERSION_ID,
                        POLICY_ID,
                        TRANSACTION_ID
                );

        mockMvc.perform(
                        post("/api/v1/rules/executions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.executionId").exists())
                .andExpect(jsonPath("$.ruleId").value(
                        RULE_ID.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        RULE_VERSION_ID.toString()
                ))
                .andExpect(jsonPath("$.policyId").value(
                        POLICY_ID.toString()
                ))
                .andExpect(jsonPath("$.transactionId").value(
                        TRANSACTION_ID.toString()
                ))
                .andExpect(jsonPath("$.executionStatus").value(
                        "COMPLETED"
                ))
                .andExpect(jsonPath("$.matched").value(true))
                .andExpect(jsonPath("$.executionTimeMs").value(15))
                .andExpect(jsonPath("$.engineInstance").value(
                        "EFS-RULE-ENGINE-API"
                ))
                .andExpect(jsonPath("$.executedAt").exists());
    }

    @Test
    void shouldRetrieveRuleExecutionByIdThroughApi()
            throws Exception {

        UUID executionId =
                insertRuleExecution(
                        "COMPLETED",
                        true,
                        10,
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/{executionId}",
                                executionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value(
                        executionId.toString()
                ))
                .andExpect(jsonPath("$.transactionId").value(
                        TRANSACTION_ID.toString()
                ))
                .andExpect(jsonPath("$.executionStatus").value(
                        "COMPLETED"
                ))
                .andExpect(jsonPath("$.matched").value(true));
    }

    @Test
    void shouldRetrieveExecutionsByRuleThroughApi()
            throws Exception {

        insertRuleExecution(
                "COMPLETED",
                true,
                11,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/rule/{ruleId}",
                                RULE_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleId").value(
                        RULE_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveExecutionsByRuleVersionThroughApi()
            throws Exception {

        insertRuleExecution(
                "COMPLETED",
                true,
                12,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/version/{ruleVersionId}",
                                RULE_VERSION_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleVersionId").value(
                        RULE_VERSION_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveExecutionsByPolicyThroughApi()
            throws Exception {

        insertRuleExecution(
                "COMPLETED",
                false,
                13,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/policy/{policyId}",
                                POLICY_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].policyId").value(
                        POLICY_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveExecutionsByTransactionThroughApi()
            throws Exception {

        insertRuleExecution(
                "COMPLETED",
                true,
                14,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/transaction/{transactionId}",
                                TRANSACTION_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(
                        TRANSACTION_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveExecutionsByStatusThroughApi()
            throws Exception {

        insertRuleExecution(
                "ERROR",
                false,
                25,
                "RULE_ENGINE_ERROR"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/executions/status/{executionStatus}",
                                "ERROR"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].executionStatus").value(
                        "ERROR"
                ))
                .andExpect(jsonPath("$[0].matched").value(false))
                .andExpect(jsonPath("$[0].errorCode").value(
                        "RULE_ENGINE_ERROR"
                ));
    }

    private UUID insertRuleExecution(
            String executionStatus,
            boolean matched,
            int executionTimeMs,
            String errorCode) {

        UUID executionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_execution (
                    execution_id,
                    rule_id,
                    rule_version_id,
                    policy_id,
                    transaction_id,
                    execution_status,
                    matched,
                    execution_time_ms,
                    error_code,
                    executed_at,
                    engine_instance
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    ?
                )
                """,
                executionId,
                RULE_ID,
                RULE_VERSION_ID,
                POLICY_ID,
                TRANSACTION_ID,
                executionStatus,
                matched,
                executionTimeMs,
                errorCode,
                "EFS-RULE-ENGINE-API"
        );

        return executionId;
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
                "EFS-RULE-EXECUTION-API-ORG",
                "EFS Rule Execution API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertCustomer() {

        jdbcTemplate.update(
                """
                INSERT INTO customer.customer (
                    customer_id,
                    customer_number,
                    customer_type,
                    risk_level,
                    risk_score,
                    customer_status,
                    record_status,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CUSTOMER_ID,
                "EFS-RULE-EXECUTION-API-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
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
                "efs.rule.execution.api",
                "EFS Rule Execution API User",
                "efs.rule.execution.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertTransaction() {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                TRANSACTION_ID,
                "EFS-RULE-EXECUTION-API-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("1000.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                USER_ID,
                1
        );
    }

    private void insertRule() {

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
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                RULE_ID,
                "RULE-EXECUTION-API-001",
                "Rule Execution API Test",
                "Rule used by V56 REST integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );
    }

    private void insertRuleVersion() {

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
                VALUES (
                    ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?,
                    ?,
                    ?,
                    NULL,
                    CURRENT_TIMESTAMP
                )
                """,
                RULE_VERSION_ID,
                RULE_ID,
                1,
                "PUBLISHED",
                "Rule execution API integration version",
                USER_ID
        );
    }

    private void insertPolicy() {

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_policy (
                    policy_id,
                    policy_code,
                    policy_name,
                    description,
                    policy_type,
                    organization_id,
                    tenant_id,
                    status,
                    effective_from,
                    effective_to,
                    priority,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?,
                    NULL,
                    ?,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                POLICY_ID,
                "POLICY-EXECUTION-API-001",
                "Rule Execution API Policy",
                "Policy used by V56 REST integration tests",
                "TRANSACTION",
                ORGANIZATION_ID,
                "ACTIVE",
                (short) 1
        );
    }
}