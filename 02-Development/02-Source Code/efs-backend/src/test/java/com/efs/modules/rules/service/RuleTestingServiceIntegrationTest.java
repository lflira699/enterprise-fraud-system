package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleTestingServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "51515151-5151-5151-5151-515151515151"
            );

    private static final UUID EXECUTED_BY =
            UUID.fromString(
                    "52525252-5252-5252-5252-525252525252"
            );

    @Autowired
    private RuleTestingService ruleTestingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @BeforeTransaction
    void ensureCommittedAuditActor() {

        Number organizationCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.organization
                        WHERE organization_id = ?
                        """,
                        Number.class,
                        ORGANIZATION_ID
                );

        if (organizationCount.longValue() == 0L) {
            insertOrganization();
        }

        Number userCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.user_account
                        WHERE user_id = ?
                        """,
                        Number.class,
                        EXECUTED_BY
                );

        if (userCount.longValue() == 0L) {
            insertUser();
        }
    }


    @Test
    void shouldEvaluatePersistAuditAndSimulationWithoutOperationalDecisions() {

        UUID ruleId =
                insertRule(
                        "RULE-TESTING-001"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId
                );

        insertCondition(
                ruleVersionId,
                (short) 1,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}",
                null
        );

        UUID correlationId =
                UUID.randomUUID();

        RuleSimulationResponse result =
                ruleTestingService.execute(
                        ruleId,
                        ruleVersionId,
                        "UC-025 Integration Test",
                        "dataset://uc025/integration-001",
                        List.of(
                                Map.of(
                                        "transaction.amount",
                                        7500
                                ),
                                Map.of(
                                        "transaction.amount",
                                        2500
                                ),
                                Map.of(
                                        "transaction.amount",
                                        9000
                                )
                        ),
                        EXECUTED_BY,
                        correlationId
                );

        assertNotNull(
                result.getSimulationId()
        );

        assertEquals(
                "RULE_VERSION",
                result.getEntityType()
        );

        assertEquals(
                ruleVersionId,
                result.getEntityId()
        );

        assertEquals(
                Long.valueOf(3L),
                result.getSampleSize()
        );

        assertEquals(
                Long.valueOf(2L),
                result.getMatchCount()
        );

        assertEquals(
                Long.valueOf(0L),
                result.getApproveCount()
        );

        assertEquals(
                Long.valueOf(0L),
                result.getRejectCount()
        );

        assertEquals(
                Long.valueOf(0L),
                result.getReviewCount()
        );

        assertEquals(
                "COMPLETED",
                result.getSimulationStatus()
        );

        assertNotNull(
                result.getStartedAt()
        );

        assertNotNull(
                result.getCompletedAt()
        );

        Number simulationCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_simulation
                        WHERE simulation_id = ?
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND simulation_status = 'COMPLETED'
                          AND sample_size = 3
                          AND match_count = 2
                          AND approve_count = 0
                          AND reject_count = 0
                          AND review_count = 0
                        """,
                        Number.class,
                        result.getSimulationId(),
                        ruleVersionId
                );

        assertEquals(
                1L,
                simulationCount.longValue()
        );

        String nonMatchCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT result_summary ->> 'nonMatchCount'
                        FROM rules.rule_simulation
                        WHERE simulation_id = ?
                        """,
                        String.class,
                        result.getSimulationId()
                );

        assertEquals(
                "1",
                nonMatchCount
        );

        Number auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_TEST_EXECUTED'
                          AND entity_type = 'RULE'
                          AND entity_id = ?
                          AND user_id = ?
                          AND action = 'TEST'
                          AND source_component = 'RULE_ENGINE'
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                        """,
                        Number.class,
                        ruleId,
                        EXECUTED_BY,
                        correlationId
                );

        assertEquals(
                1L,
                auditCount.longValue()
        );

        Number historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_id = ?
                        """,
                        Number.class,
                        ruleId
                );

        assertEquals(
                0L,
                historyCount.longValue()
        );
    }

    @Test
    void shouldRejectRuleVersionThatBelongsToAnotherRule() {

        UUID requestedRuleId =
                insertRule(
                        "RULE-TESTING-002"
                );

        UUID otherRuleId =
                insertRule(
                        "RULE-TESTING-003"
                );

        UUID otherRuleVersionId =
                insertRuleVersion(
                        otherRuleId
                );

        insertCondition(
                otherRuleVersionId,
                (short) 1,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}",
                null
        );

        UUID correlationId =
                UUID.randomUUID();

        assertThrows(
                ValidationException.class,
                () ->
                        ruleTestingService.execute(
                                requestedRuleId,
                                otherRuleVersionId,
                                "Invalid Rule Version Test",
                                "dataset://uc025/mismatch",
                                List.of(
                                        Map.of(
                                                "transaction.amount",
                                                7500
                                        )
                                ),
                                EXECUTED_BY,
                                correlationId
                        )
        );

        assertNoSimulation(
                otherRuleVersionId
        );

        assertEquals(
                0L,
                countAuditEvents(
                        correlationId
                )
        );
    }

    @Test
    void shouldAuditRejectedRuleVersionWithoutConditions() {

        UUID ruleId =
                insertRule(
                        "RULE-TESTING-004"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId
                );

        UUID correlationId =
                UUID.randomUUID();

        assertThrows(
                ValidationException.class,
                () ->
                        ruleTestingService.execute(
                                ruleId,
                                ruleVersionId,
                                "Unavailable Rule Test",
                                "dataset://uc025/no-conditions",
                                List.of(
                                        Map.of(
                                                "transaction.amount",
                                                7500
                                        )
                                ),
                                EXECUTED_BY,
                                correlationId
                        )
        );

        assertNoSimulation(
                ruleVersionId
        );

        Number auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_TEST_REJECTED'
                          AND entity_type = 'RULE'
                          AND entity_id = ?
                          AND user_id = ?
                          AND action = 'TEST'
                          AND source_component = 'RULE_ENGINE'
                          AND correlation_id = ?
                          AND event_result = 'REJECTED'
                          AND event_details ->> 'reason'
                              = 'RULE_VERSION_HAS_NO_CONDITIONS'
                        """,
                        Number.class,
                        ruleId,
                        EXECUTED_BY,
                        correlationId
                );

        assertEquals(
                1L,
                auditCount.longValue()
        );
    }

    @Test
    void shouldAuditExecutionFailureAndPersistNoSimulation() {

        UUID ruleId =
                insertRule(
                        "RULE-TESTING-005"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId
                );

        /*
         * REGEX exists in the architectural operator catalog,
         * but its comparison_value contract is not implemented
         * by the current controlled evaluator yet.
         * This deliberately forces an execution error.
         */
        insertCondition(
                ruleVersionId,
                (short) 1,
                "transaction.reference",
                "REGEX",
                "{\"value\":\"^ABC\"}",
                null
        );

        UUID correlationId =
                UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        ruleTestingService.execute(
                                ruleId,
                                ruleVersionId,
                                "Execution Failure Test",
                                "dataset://uc025/execution-error",
                                List.of(
                                        Map.of(
                                                "transaction.reference",
                                                "ABC-123"
                                        )
                                ),
                                EXECUTED_BY,
                                correlationId
                        )
        );

        assertNoSimulation(
                ruleVersionId
        );

        Number auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_TEST_FAILED'
                          AND entity_type = 'RULE'
                          AND entity_id = ?
                          AND user_id = ?
                          AND action = 'TEST'
                          AND source_component = 'RULE_ENGINE'
                          AND correlation_id = ?
                          AND event_result = 'FAILURE'
                          AND event_details ->> 'ruleVersionId' = ?
                          AND event_details ->> 'datasetReference'
                              = 'dataset://uc025/execution-error'
                          AND event_details ->> 'errorType'
                              = 'IllegalArgumentException'
                        """,
                        Number.class,
                        ruleId,
                        EXECUTED_BY,
                        correlationId,
                        ruleVersionId.toString()
                );

        assertEquals(
                1L,
                auditCount.longValue()
        );
    }

    private long countAuditEvents(
            UUID correlationId) {

        Number count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE correlation_id = ?
                        """,
                        Number.class,
                        correlationId
                );

        return count.longValue();
    }

    private void assertNoSimulation(
            UUID ruleVersionId) {

        Number simulationCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_simulation
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        Number.class,
                        ruleVersionId
                );

        assertEquals(
                0L,
                simulationCount.longValue()
        );
    }

    private UUID insertRule(
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
                "UC-025 Rule Testing",
                "Rule used by UC-025 RuleTestingService tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "INACTIVE"
        );

        return ruleId;
    }

    private UUID insertRuleVersion(
            UUID ruleId) {

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
                "UC-025 Rule Testing Version",
                "Rule version used by UC-025 tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                null,
                "DRAFT",
                "UC-025 testing version",
                EXECUTED_BY,
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
                "EFS-UC025-RULE-TEST-ORG",
                "EFS UC-025 Rule Testing Organization",
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
                EXECUTED_BY,
                ORGANIZATION_ID,
                "efs.uc025.rule.testing",
                "EFS UC-025 Rule Testing User",
                "efs.uc025.rule.testing@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}