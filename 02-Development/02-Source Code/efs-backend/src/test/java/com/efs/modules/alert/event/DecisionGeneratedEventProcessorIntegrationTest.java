package com.efs.modules.alert.event;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class DecisionGeneratedEventProcessorIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "81818181-8181-8181-8181-818181818181"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "82828282-8282-8282-8282-828282828282"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "83838383-8383-8383-8383-838383838383"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "84848484-8484-8484-8484-848484848484"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "85858585-8585-8585-8585-858585858585"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "86868686-8686-8686-8686-868686868686"
            );

    private static final UUID RULE_ID =
            UUID.fromString(
                    "87878787-8787-8787-8787-878787878787"
            );

    private static final UUID RULE_VERSION_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID RULE_ACTION_ID =
            UUID.fromString(
                    "89898989-8989-8989-8989-898989898989"
            );

    private static final UUID RULE_EXECUTION_ID =
            UUID.fromString(
                    "90909090-9090-9090-9090-909090909091"
            );

    private static final UUID MESSAGE_ID =
            UUID.fromString(
                    "91919191-9191-9191-9191-919191919191"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "92929292-9292-9292-9292-929292929292"
            );

    @Autowired
    private DecisionGeneratedEventProcessor processor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
        insertCustomer();
        insertTransaction();
        insertRiskAssessment();
        insertDecision();
        insertRule();
        insertRuleVersion();
        insertRuleAction();
        insertRuleExecution();
    }

    @Test
    void shouldCreateSingleAlertAndIgnoreDuplicateDecisionGeneratedEvent() {

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        MESSAGE_ID,
                        CORRELATION_ID,
                        DECISION_ID
                );

        processor.process(
                message
        );

        entityManager.flush();

        AlertRow alert =
                jdbcTemplate.queryForObject(
                        """
                        SELECT
                            customer_id,
                            transaction_id,
                            decision_id,
                            risk_assessment_id,
                            rule_id,
                            alert_type,
                            priority,
                            status,
                            correlation_id
                        FROM alert.alert
                        WHERE decision_id = ?
                        """,
                        (resultSet, rowNumber) ->
                                new AlertRow(
                                        resultSet.getObject(
                                                "customer_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "transaction_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "decision_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "risk_assessment_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "rule_id",
                                                UUID.class
                                        ),
                                        resultSet.getString(
                                                "alert_type"
                                        ),
                                        resultSet.getString(
                                                "priority"
                                        ),
                                        resultSet.getString(
                                                "status"
                                        ),
                                        resultSet.getObject(
                                                "correlation_id",
                                                UUID.class
                                        )
                                ),
                        DECISION_ID
                );

        assertEquals(
                CUSTOMER_ID,
                alert.customerId()
        );

        assertEquals(
                TRANSACTION_ID,
                alert.transactionId()
        );

        assertEquals(
                DECISION_ID,
                alert.decisionId()
        );

        assertEquals(
                RISK_ASSESSMENT_ID,
                alert.riskAssessmentId()
        );

        assertNull(
                alert.ruleId()
        );

        assertEquals(
                "FRAUD",
                alert.alertType()
        );

        assertEquals(
                "HIGH",
                alert.priority()
        );

        assertEquals(
                "NEW",
                alert.status()
        );

        assertEquals(
                CORRELATION_ID,
                alert.correlationId()
        );

        Integer processedEventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.processed_domain_event
                        WHERE message_id = ?
                          AND consumer_name = ?
                          AND event_type = ?
                        """,
                        Integer.class,
                        MESSAGE_ID,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        assertEquals(
                1,
                processedEventCount
        );

        processor.process(
                message
        );

        entityManager.flush();

        Integer alertCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert
                        WHERE decision_id = ?
                        """,
                        Integer.class,
                        DECISION_ID
                );

        assertEquals(
                1,
                alertCount
        );

        Integer processedEventCountAfterDuplicate =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.processed_domain_event
                        WHERE message_id = ?
                          AND consumer_name = ?
                          AND event_type = ?
                        """,
                        Integer.class,
                        MESSAGE_ID,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        assertEquals(
                1,
                processedEventCountAfterDuplicate
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
                "EFS-DECISION-GENERATED-IT-ORG",
                "EFS Decision Generated Integration Test Organization",
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
                "efs.decision.generated.it",
                "EFS Decision Generated Integration Test User",
                "efs.decision.generated.it@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
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
                "EFS-DECISION-GENERATED-IT-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
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
                "EFS-DECISION-GENERATED-IT-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("500.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );
    }

    private void insertRiskAssessment() {

        LocalDateTime now =
                LocalDateTime.now();

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    assessment_type,
                    assessment_stage,
                    overall_risk_score,
                    risk_level,
                    assessment_result,
                    confidence_score,
                    assessment_timestamp,
                    created_at,
                    updated_at,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                RISK_ASSESSMENT_ID,
                TRANSACTION_ID,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("90.00"),
                "ALTO",
                "REVIEW",
                new BigDecimal("92.00"),
                now,
                now,
                now,
                0
        );
    }

    private void insertDecision() {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_decision (
                    decision_id,
                    transaction_id,
                    risk_assessment_id,
                    decision_type,
                    decision_source,
                    confidence_score,
                    decision_reason,
                    decision_timestamp,
                    is_final
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                DECISION_ID,
                TRANSACTION_ID,
                RISK_ASSESSMENT_ID,
                "ESCALATE",
                "DECISION_ENGINE",
                new BigDecimal("92.00"),
                "Escalar inmediatamente",
                LocalDateTime.now(),
                false
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
                "DECISION-GENERATED-IT-001",
                "Decision Generated Integration Test Rule",
                "Rule used by DecisionGenerated processor integration test",
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
                    ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP
                )
                """,
                RULE_VERSION_ID,
                RULE_ID,
                1,
                null,
                "DRAFT",
                "DecisionGenerated integration test version",
                CREATED_BY,
                null
        );
    }

    private void insertRuleAction() {

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
                VALUES (
                    ?, ?, ?, ?,
                    CAST(? AS jsonb),
                    ?,
                    CURRENT_TIMESTAMP
                )
                """,
                RULE_ACTION_ID,
                RULE_VERSION_ID,
                "CREATE_ALERT",
                (short) 1,
                """
                {
                    "alertType": "FRAUD",
                    "priority": "HIGH"
                }
                """,
                false
        );
    }

    private void insertRuleExecution() {

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
                RULE_EXECUTION_ID,
                RULE_ID,
                RULE_VERSION_ID,
                null,
                TRANSACTION_ID,
                "COMPLETED",
                true,
                10,
                null,
                "EFS-RULE-ENGINE-IT"
        );
    }

    private record AlertRow(
            UUID customerId,
            UUID transactionId,
            UUID decisionId,
            UUID riskAssessmentId,
            UUID ruleId,
            String alertType,
            String priority,
            String status,
            UUID correlationId) {
    }
}
