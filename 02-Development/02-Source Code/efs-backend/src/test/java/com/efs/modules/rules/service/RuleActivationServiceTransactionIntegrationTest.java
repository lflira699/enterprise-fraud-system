package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.rules.dto.RuleActivationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class RuleActivationServiceTransactionIntegrationTest {

    @Autowired
    private RuleServiceInterface ruleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private AuditEntityChangeServiceInterface
            auditEntityChangeService;

    @Test
    void shouldRollbackRuleActivationWhenAuditEntityChangeFails() {

        UUID organizationId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        UUID ruleId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        String suffix =
                ruleId
                        .toString()
                        .substring(0, 8);

        doThrow(
                new IllegalStateException(
                        "forced audit entity change failure"
                )
        )
                .when(auditEntityChangeService)
                .createAuditEntityChange(
                        any(AuditEntityChangeRequest.class)
                );

        RuleActivationRequest request =
                new RuleActivationRequest();

        request.setChangedBy(
                userId
        );

        request.setChangeReason(
                "Activation rollback verification"
        );

        request.setCorrelationId(
                correlationId
        );

        TransactionTemplate transactionTemplate =
                new TransactionTemplate(
                        transactionManager
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        transactionTemplate.executeWithoutResult(
                                transactionStatus -> {

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
                                            organizationId,
                                            "RULE-ACT-TX-" + suffix,
                                            "EFS Rule Activation Transaction Test Organization",
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
                                            userId,
                                            organizationId,
                                            "efs.rule.activate.tx." + suffix,
                                            "EFS Rule Activation Transaction Test User",
                                            "efs.rule.activate.tx."
                                                    + suffix
                                                    + "@example.com",
                                            "LOCAL",
                                            false,
                                            "ACTIVE",
                                            0
                                    );

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
                                            "RULE-ACT-TX-" + suffix,
                                            "Rule Activation Transaction Test",
                                            "Rule activation transaction test",
                                            "TRANSACTION",
                                            "HIGH",
                                            (short) 2,
                                            "FRAUD_RULES",
                                            1,
                                            "INACTIVE"
                                    );

                                    ruleService.activateRule(
                                            ruleId,
                                            request
                                    );
                                }
                        )
        );

        Integer ruleCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                ruleCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND operation_type = 'ACTIVATION'
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        ruleId,
                        correlationId
                );

        assertEquals(
                0,
                historyCount
        );

        Integer auditEventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND event_type = 'RULE_ACTIVATED'
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        ruleId,
                        correlationId
                );

        assertEquals(
                0,
                auditEventCount
        );

        Integer entityChangeCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_entity_change
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                entityChangeCount
        );

        Integer userCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.user_account
                        WHERE user_id = ?
                        """,
                        Integer.class,
                        userId
                );

        assertEquals(
                0,
                userCount
        );

        Integer organizationCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.organization
                        WHERE organization_id = ?
                        """,
                        Integer.class,
                        organizationId
                );

        assertEquals(
                0,
                organizationCount
        );
    }
}