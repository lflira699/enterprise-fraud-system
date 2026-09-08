package com.efs.modules.rules.service;

import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class RuleVersionEvaluatorIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "41414141-4141-4141-4141-414141414141"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "42424242-4242-4242-4242-424242424242"
            );

    @Autowired
    private RuleVersionEvaluator evaluator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldEvaluatePersistedDraftRuleVersionWhenConditionsMatch() {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-TEST-EVAL-001"
                );

        insertCondition(
                ruleVersionId,
                (short) 1,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}",
                null
        );

        insertCondition(
                ruleVersionId,
                (short) 2,
                "transaction.country",
                "EQUALS",
                "{\"value\":\"GT\"}",
                "AND"
        );

        boolean result =
                evaluator.evaluate(
                        ruleVersionId,
                        Map.of(
                                "transaction",
                                Map.of(
                                        "amount",
                                        7500,
                                        "country",
                                        "GT"
                                )
                        )
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPersistedConditionDoesNotMatch() {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-TEST-EVAL-002"
                );

        insertCondition(
                ruleVersionId,
                (short) 1,
                "transaction.amount",
                "BETWEEN",
                """
                {
                    "minimum": 1000,
                    "maximum": 5000
                }
                """,
                null
        );

        boolean result =
                evaluator.evaluate(
                        ruleVersionId,
                        Map.of(
                                "transaction.amount",
                                7500
                        )
                );

        assertFalse(result);
    }

    @Test
    void shouldRejectUnknownRuleVersion() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        evaluator.evaluate(
                                unknownRuleVersionId,
                                Map.of(
                                        "transaction.amount",
                                        7500
                                )
                        )
        );
    }

    @Test
    void shouldRejectRuleVersionWithoutConditions() {

        UUID ruleVersionId =
                insertRuleVersion(
                        "RULE-TEST-EVAL-003"
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        evaluator.evaluate(
                                ruleVersionId,
                                Map.of(
                                        "transaction.amount",
                                        7500
                                )
                        )
        );
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
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleId,
                ruleCode,
                "UC-025 Persisted Rule Test",
                "Rule used by RuleVersionEvaluator integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "INACTIVE"
        );

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleVersionId,
                ruleId,
                1,
                "UC-025 Persisted Rule Test",
                "Rule version used by UC-025 evaluator tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                null,
                "DRAFT",
                "Initial test version",
                CREATED_BY,
                null
        );

        return ruleVersionId;
    }

    private void insertCondition(
            UUID ruleVersionId,
            short conditionOrder,
            String attributeName,
            String comparisonOperator,
            String comparisonValueJson,
            String logicalOperator) {

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
                VALUES (
                    uuidv7(),
                    ?,
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    ?,
                    TRUE
                )
                """,
                ruleVersionId,
                conditionOrder,
                attributeName,
                comparisonOperator,
                comparisonValueJson,
                logicalOperator
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
                "EFS-UC025-EVAL-ORG",
                "EFS UC-025 Evaluation Organization",
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
                CREATED_BY,
                ORGANIZATION_ID,
                "efs.uc025.evaluator",
                "EFS UC-025 Evaluator",
                "efs.uc025.evaluator@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}