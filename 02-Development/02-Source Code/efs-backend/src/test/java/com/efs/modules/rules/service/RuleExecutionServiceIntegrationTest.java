package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleExecutionServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "17171717-1717-1717-1717-171717171717"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "18181818-1818-1818-1818-181818181818"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "19191919-1919-1919-1919-191919191919"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "20202020-2020-2020-2020-202020202020"
            );

    private static final UUID RULE_ID =
            UUID.fromString(
                    "21212121-2121-2121-2121-212121212121"
            );

    private static final UUID RULE_VERSION_ID =
            UUID.fromString(
                    "22222222-2323-2323-2323-232323232323"
            );

    private static final UUID POLICY_ID =
            UUID.fromString(
                    "24242424-2424-2424-2424-242424242424"
            );

    @Autowired
    private RuleExecutionServiceInterface service;

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
    void shouldCreateAndRetrieveRuleExecutionById() {

        RuleExecutionResponse created =
                service.createRuleExecution(
                        buildRequest(
                                "COMPLETED",
                                true,
                                15,
                                null
                        )
                );

        assertNotNull(
                created.getExecutionId()
        );

        assertEquals(
                RULE_ID,
                created.getRuleId()
        );

        assertEquals(
                RULE_VERSION_ID,
                created.getRuleVersionId()
        );

        assertEquals(
                POLICY_ID,
                created.getPolicyId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                "COMPLETED",
                created.getExecutionStatus()
        );

        assertEquals(
                Boolean.TRUE,
                created.getMatched()
        );

        assertEquals(
                Integer.valueOf(15),
                created.getExecutionTimeMs()
        );

        assertNull(
                created.getErrorCode()
        );

        assertEquals(
                "EFS-RULE-ENGINE-TEST",
                created.getEngineInstance()
        );

        assertNotNull(
                created.getExecutedAt()
        );

        RuleExecutionResponse retrieved =
                service.getRuleExecutionById(
                        created.getExecutionId()
                );

        assertEquals(
                created.getExecutionId(),
                retrieved.getExecutionId()
        );
    }

    @Test
    void shouldReturnExecutionsByRuleId() {

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        true,
                        10,
                        null
                )
        );

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        false,
                        20,
                        null
                )
        );

        List<RuleExecutionResponse> executions =
                service.getRuleExecutionsByRuleId(
                        RULE_ID
                );

        assertEquals(
                2,
                executions.size()
        );

        assertEquals(
                RULE_ID,
                executions.get(0).getRuleId()
        );

        assertEquals(
                RULE_ID,
                executions.get(1).getRuleId()
        );
    }

    @Test
    void shouldReturnExecutionsByRuleVersionId() {

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        true,
                        12,
                        null
                )
        );

        List<RuleExecutionResponse> executions =
                service.getRuleExecutionsByRuleVersionId(
                        RULE_VERSION_ID
                );

        assertEquals(
                1,
                executions.size()
        );

        assertEquals(
                RULE_VERSION_ID,
                executions.get(0).getRuleVersionId()
        );
    }

    @Test
    void shouldReturnExecutionsByPolicyId() {

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        true,
                        18,
                        null
                )
        );

        List<RuleExecutionResponse> executions =
                service.getRuleExecutionsByPolicyId(
                        POLICY_ID
                );

        assertEquals(
                1,
                executions.size()
        );

        assertEquals(
                POLICY_ID,
                executions.get(0).getPolicyId()
        );
    }

    @Test
    void shouldReturnExecutionsByTransactionId() {

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        true,
                        9,
                        null
                )
        );

        service.createRuleExecution(
                buildRequest(
                        "ERROR",
                        false,
                        30,
                        "RULE_EVALUATION_ERROR"
                )
        );

        List<RuleExecutionResponse> executions =
                service.getRuleExecutionsByTransactionId(
                        TRANSACTION_ID
                );

        assertEquals(
                2,
                executions.size()
        );

        assertEquals(
                TRANSACTION_ID,
                executions.get(0).getTransactionId()
        );

        assertEquals(
                TRANSACTION_ID,
                executions.get(1).getTransactionId()
        );
    }

    @Test
    void shouldReturnExecutionsByStatus() {

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        true,
                        11,
                        null
                )
        );

        service.createRuleExecution(
                buildRequest(
                        "COMPLETED",
                        false,
                        14,
                        null
                )
        );

        service.createRuleExecution(
                buildRequest(
                        "ERROR",
                        false,
                        25,
                        "RULE_ENGINE_ERROR"
                )
        );

        List<RuleExecutionResponse> completedExecutions =
                service.getRuleExecutionsByStatus(
                        "COMPLETED"
                );

        assertEquals(
                2,
                completedExecutions.size()
        );

        assertEquals(
                "COMPLETED",
                completedExecutions.get(0).getExecutionStatus()
        );

        assertEquals(
                "COMPLETED",
                completedExecutions.get(1).getExecutionStatus()
        );
    }

    @Test
    void shouldPreserveErrorInformation() {

        RuleExecutionResponse created =
                service.createRuleExecution(
                        buildRequest(
                                "ERROR",
                                false,
                                45,
                                "RULE_ENGINE_ERROR"
                        )
                );

        assertEquals(
                "ERROR",
                created.getExecutionStatus()
        );

        assertEquals(
                Boolean.FALSE,
                created.getMatched()
        );

        assertEquals(
                Integer.valueOf(45),
                created.getExecutionTimeMs()
        );

        assertEquals(
                "RULE_ENGINE_ERROR",
                created.getErrorCode()
        );
    }

    @Test
    void shouldAllowOptionalRuleReferencesToBeNull() {

        RuleExecutionRequest request =
                new RuleExecutionRequest();

        request.setRuleId(
                null
        );

        request.setRuleVersionId(
                null
        );

        request.setPolicyId(
                null
        );

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setExecutionStatus(
                "COMPLETED"
        );

        request.setMatched(
                false
        );

        request.setExecutionTimeMs(
                5
        );

        request.setErrorCode(
                null
        );

        request.setEngineInstance(
                "EFS-RULE-ENGINE-TEST"
        );

        RuleExecutionResponse created =
                service.createRuleExecution(
                        request
                );

        assertNotNull(
                created.getExecutionId()
        );

        assertNull(
                created.getRuleId()
        );

        assertNull(
                created.getRuleVersionId()
        );

        assertNull(
                created.getPolicyId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );
    }

    @Test
    void shouldRejectUnknownExecutionId() {

        UUID unknownExecutionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleExecutionById(
                        unknownExecutionId
                )
        );
    }

    private RuleExecutionRequest buildRequest(
            String executionStatus,
            boolean matched,
            int executionTimeMs,
            String errorCode) {

        RuleExecutionRequest request =
                new RuleExecutionRequest();

        request.setRuleId(
                RULE_ID
        );

        request.setRuleVersionId(
                RULE_VERSION_ID
        );

        request.setPolicyId(
                POLICY_ID
        );

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setExecutionStatus(
                executionStatus
        );

        request.setMatched(
                matched
        );

        request.setExecutionTimeMs(
                executionTimeMs
        );

        request.setErrorCode(
                errorCode
        );

        request.setEngineInstance(
                "EFS-RULE-ENGINE-TEST"
        );

        return request;
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
                "EFS-RULE-EXECUTION-TEST-ORG",
                "EFS Rule Execution Test Organization",
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
                "EFS-RULE-EXECUTION-CUSTOMER",
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
                "efs.rule.execution.test",
                "EFS Rule Execution Test User",
                "efs.rule.execution.test@example.com",
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
                "EFS-RULE-EXECUTION-TRANSACTION",
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
                "RULE-EXECUTION-001",
                "Rule Execution Integration Test",
                "Rule used by V56 integration tests",
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
                "Rule execution integration version",
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
                "POLICY-EXECUTION-001",
                "Rule Execution Test Policy",
                "Policy used by V56 integration tests",
                "TRANSACTION",
                ORGANIZATION_ID,
                "ACTIVE",
                (short) 1
        );
    }
}