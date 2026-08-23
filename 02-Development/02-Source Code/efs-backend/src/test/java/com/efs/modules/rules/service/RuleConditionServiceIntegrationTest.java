package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleConditionRequest;
import com.efs.modules.rules.dto.RuleConditionResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleConditionServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "55555555-5555-5555-5555-555555555555"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "66666666-6666-6666-6666-666666666666"
            );

    @Autowired
    private RuleConditionServiceInterface service;

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
                "EFS-RULE-CONDITION-TEST-ORG",
                "EFS Rule Condition Test Organization",
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
                "efs.rule.condition.test",
                "EFS Rule Condition Test User",
                "efs.rule.condition.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateAndRetrieveRuleConditionById() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-CONDITION-001"
                );

        RuleConditionResponse created =
                service.createRuleCondition(
                        ruleVersionId,
                        buildRequest(
                                (short) 1,
                                "transaction.amount",
                                "GREATER_THAN",
                                Map.of(
                                        "value",
                                        10000
                                ),
                                null,
                                true
                        )
                );

        assertNotNull(
                created.getConditionId()
        );

        assertEquals(
                ruleVersionId,
                created.getRuleVersionId()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getConditionOrder()
        );

        assertEquals(
                "transaction.amount",
                created.getAttributeName()
        );

        assertEquals(
                "GREATER_THAN",
                created.getComparisonOperator()
        );

        assertEquals(
                10000,
                created.getComparisonValue().get("value")
        );

        assertEquals(
                Boolean.TRUE,
                created.getIsRequired()
        );

        RuleConditionResponse retrieved =
                service.getRuleConditionById(
                        created.getConditionId()
                );

        assertEquals(
                created.getConditionId(),
                retrieved.getConditionId()
        );
    }

    @Test
    void shouldReturnConditionsOrderedByConditionOrder() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-CONDITION-002"
                );

        service.createRuleCondition(
                ruleVersionId,
                buildRequest(
                        (short) 3,
                        "transaction.country",
                        "EQUALS",
                        Map.of(
                                "value",
                                "GT"
                        ),
                        "AND",
                        true
                )
        );

        service.createRuleCondition(
                ruleVersionId,
                buildRequest(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                5000
                        ),
                        null,
                        true
                )
        );

        List<RuleConditionResponse> conditions =
                service.getRuleConditionsByRuleVersionId(
                        ruleVersionId
                );

        assertEquals(
                2,
                conditions.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                conditions.get(0).getConditionOrder()
        );

        assertEquals(
                Short.valueOf((short) 3),
                conditions.get(1).getConditionOrder()
        );
    }

    @Test
    void shouldReturnConditionsByAttributeName() {

        UUID firstRuleVersionId =
                createRuleVersion(
                        "RULE-CONDITION-003"
                );

        UUID secondRuleVersionId =
                createRuleVersion(
                        "RULE-CONDITION-004"
                );

        service.createRuleCondition(
                firstRuleVersionId,
                buildRequest(
                        (short) 1,
                        "device.risk_score",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                80
                        ),
                        null,
                        true
                )
        );

        service.createRuleCondition(
                secondRuleVersionId,
                buildRequest(
                        (short) 1,
                        "device.risk_score",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                90
                        ),
                        null,
                        true
                )
        );

        List<RuleConditionResponse> conditions =
                service.getRuleConditionsByAttributeName(
                        "device.risk_score"
                );

        assertEquals(
                2,
                conditions.size()
        );

        assertEquals(
                "device.risk_score",
                conditions.get(0).getAttributeName()
        );

        assertEquals(
                "device.risk_score",
                conditions.get(1).getAttributeName()
        );
    }

    @Test
    void shouldPreserveJsonComparisonValue() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-CONDITION-005"
                );

        Map<String, Object> comparisonValue =
                Map.of(
                        "minimum",
                        1000,
                        "maximum",
                        5000,
                        "currency",
                        "GTQ"
                );

        RuleConditionResponse created =
                service.createRuleCondition(
                        ruleVersionId,
                        buildRequest(
                                (short) 1,
                                "transaction.amount",
                                "BETWEEN",
                                comparisonValue,
                                null,
                                true
                        )
                );

        assertEquals(
                1000,
                created.getComparisonValue().get("minimum")
        );

        assertEquals(
                5000,
                created.getComparisonValue().get("maximum")
        );

        assertEquals(
                "GTQ",
                created.getComparisonValue().get("currency")
        );
    }

    @Test
    void shouldRejectCreationForUnknownRuleVersion() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        RuleConditionRequest request =
                buildRequest(
                        (short) 1,
                        "transaction.amount",
                        "GREATER_THAN",
                        Map.of(
                                "value",
                                10000
                        ),
                        null,
                        true
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createRuleCondition(
                        unknownRuleVersionId,
                        request
                )
        );
    }

    @Test
    void shouldRejectUnknownConditionId() {

        UUID unknownConditionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleConditionById(
                        unknownConditionId
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleVersionWhenListingConditions() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleConditionsByRuleVersionId(
                        unknownRuleVersionId
                )
        );
    }

    private UUID createRuleVersion(
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
                "Rule Condition Integration Test",
                "Rule used by V51 integration tests",
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

    private RuleConditionRequest buildRequest(
            short conditionOrder,
            String attributeName,
            String comparisonOperator,
            Map<String, Object> comparisonValue,
            String logicalOperator,
            boolean isRequired) {

        RuleConditionRequest request =
                new RuleConditionRequest();

        request.setConditionOrder(
                conditionOrder
        );

        request.setAttributeName(
                attributeName
        );

        request.setComparisonOperator(
                comparisonOperator
        );

        request.setComparisonValue(
                comparisonValue
        );

        request.setLogicalOperator(
                logicalOperator
        );

        request.setIsRequired(
                isRequired
        );

        return request;
    }
}