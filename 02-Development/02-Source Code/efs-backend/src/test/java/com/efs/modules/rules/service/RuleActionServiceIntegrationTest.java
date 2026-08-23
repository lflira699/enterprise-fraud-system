package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleActionRequest;
import com.efs.modules.rules.dto.RuleActionResponse;
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
class RuleActionServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "99999999-9999-9999-9999-999999999999"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
            );

    @Autowired
    private RuleActionServiceInterface service;

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
                "EFS-RULE-ACTION-TEST-ORG",
                "EFS Rule Action Test Organization",
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
                "efs.rule.action.test",
                "EFS Rule Action Test User",
                "efs.rule.action.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateAndRetrieveRuleActionById() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-ACTION-001"
                );

        RuleActionResponse created =
                service.createRuleAction(
                        ruleVersionId,
                        buildRequest(
                                "CREATE_ALERT",
                                (short) 1,
                                Map.of(
                                        "priority",
                                        "HIGH"
                                ),
                                false
                        )
                );

        assertNotNull(
                created.getActionId()
        );

        assertEquals(
                ruleVersionId,
                created.getRuleVersionId()
        );

        assertEquals(
                "CREATE_ALERT",
                created.getActionType()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getExecutionOrder()
        );

        assertEquals(
                "HIGH",
                created.getParameterJson().get("priority")
        );

        assertEquals(
                Boolean.FALSE,
                created.getIsAsync()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        RuleActionResponse retrieved =
                service.getRuleActionById(
                        created.getActionId()
                );

        assertEquals(
                created.getActionId(),
                retrieved.getActionId()
        );
    }

    @Test
    void shouldReturnActionsOrderedByExecutionOrder() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-ACTION-002"
                );

        service.createRuleAction(
                ruleVersionId,
                buildRequest(
                        "CREATE_CASE",
                        (short) 3,
                        Map.of(
                                "queue",
                                "FRAUD"
                        ),
                        false
                )
        );

        service.createRuleAction(
                ruleVersionId,
                buildRequest(
                        "CREATE_ALERT",
                        (short) 1,
                        Map.of(
                                "priority",
                                "HIGH"
                        ),
                        false
                )
        );

        List<RuleActionResponse> actions =
                service.getRuleActionsByRuleVersionId(
                        ruleVersionId
                );

        assertEquals(
                2,
                actions.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                actions.get(0).getExecutionOrder()
        );

        assertEquals(
                Short.valueOf((short) 3),
                actions.get(1).getExecutionOrder()
        );
    }

    @Test
    void shouldReturnActionsByType() {

        UUID firstRuleVersionId =
                createRuleVersion(
                        "RULE-ACTION-003"
                );

        UUID secondRuleVersionId =
                createRuleVersion(
                        "RULE-ACTION-004"
                );

        service.createRuleAction(
                firstRuleVersionId,
                buildRequest(
                        "CREATE_ALERT",
                        (short) 1,
                        Map.of(
                                "priority",
                                "HIGH"
                        ),
                        false
                )
        );

        service.createRuleAction(
                secondRuleVersionId,
                buildRequest(
                        "CREATE_ALERT",
                        (short) 1,
                        Map.of(
                                "priority",
                                "CRITICAL"
                        ),
                        true
                )
        );

        List<RuleActionResponse> actions =
                service.getRuleActionsByType(
                        "CREATE_ALERT"
                );

        assertEquals(
                2,
                actions.size()
        );

        assertEquals(
                "CREATE_ALERT",
                actions.get(0).getActionType()
        );

        assertEquals(
                "CREATE_ALERT",
                actions.get(1).getActionType()
        );
    }

    @Test
    void shouldPreserveJsonParameters() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-ACTION-005"
                );

        Map<String, Object> parameters =
                Map.of(
                        "priority",
                        "CRITICAL",
                        "queue",
                        "ATO",
                        "requiresReview",
                        true
                );

        RuleActionResponse created =
                service.createRuleAction(
                        ruleVersionId,
                        buildRequest(
                                "CREATE_CASE",
                                (short) 1,
                                parameters,
                                true
                        )
                );

        assertEquals(
                "CRITICAL",
                created.getParameterJson().get("priority")
        );

        assertEquals(
                "ATO",
                created.getParameterJson().get("queue")
        );

        assertEquals(
                Boolean.TRUE,
                created.getParameterJson().get("requiresReview")
        );

        assertEquals(
                Boolean.TRUE,
                created.getIsAsync()
        );
    }

    @Test
    void shouldAllowNullParameterJson() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-ACTION-006"
                );

        RuleActionResponse created =
                service.createRuleAction(
                        ruleVersionId,
                        buildRequest(
                                "NO_ACTION",
                                (short) 1,
                                null,
                                false
                        )
                );

        assertNotNull(
                created.getActionId()
        );

        assertEquals(
                null,
                created.getParameterJson()
        );
    }

    @Test
    void shouldRejectCreationForUnknownRuleVersion() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        RuleActionRequest request =
                buildRequest(
                        "CREATE_ALERT",
                        (short) 1,
                        Map.of(
                                "priority",
                                "HIGH"
                        ),
                        false
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createRuleAction(
                        unknownRuleVersionId,
                        request
                )
        );
    }

    @Test
    void shouldRejectUnknownActionId() {

        UUID unknownActionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleActionById(
                        unknownActionId
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleVersionWhenListingActions() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleActionsByRuleVersionId(
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
                "Rule Action Integration Test",
                "Rule used by V52 integration tests",
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

    private RuleActionRequest buildRequest(
            String actionType,
            short executionOrder,
            Map<String, Object> parameterJson,
            boolean isAsync) {

        RuleActionRequest request =
                new RuleActionRequest();

        request.setActionType(
                actionType
        );

        request.setExecutionOrder(
                executionOrder
        );

        request.setParameterJson(
                parameterJson
        );

        request.setIsAsync(
                isAsync
        );

        return request;
    }
}