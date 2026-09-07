package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.rules.dto.RuleUpdateRequest;
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
class RuleServiceTransactionIntegrationTest {

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
    void shouldRollbackRuleVersionUpdateWhenAuditEntityChangeFails() {

        UUID organizationId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        UUID ruleId =
                UUID.randomUUID();

        UUID ruleVersionId =
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

        RuleUpdateRequest request =
                new RuleUpdateRequest();

        request.setRuleName(
                "Rule Name That Must Roll Back"
        );

        request.setChangedBy(
                userId
        );

        request.setChangeReason(
                "Rollback verification"
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
                                            "RULE-TX-" + suffix,
                                            "EFS Rule Transaction Test Organization",
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
                                            "efs.rule.tx." + suffix,
                                            "EFS Rule Transaction Test User",
                                            "efs.rule.tx."
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
                                            "RULE-TX-" + suffix,
                                            "Original Rule Name",
                                            "Original description",
                                            "TRANSACTION",
                                            "HIGH",
                                            (short) 2,
                                            "FRAUD_RULES",
                                            1,
                                            "ACTIVE"
                                    );

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
                                                NULL,
                                                ?, ?, ?, NULL,
                                                CURRENT_TIMESTAMP
                                            )
                                            """,
                                            ruleVersionId,
                                            ruleId,
                                            1,
                                            "Original Rule Name",
                                            "Original description",
                                            "TRANSACTION",
                                            "HIGH",
                                            (short) 2,
                                            "FRAUD_RULES",
                                            "PUBLISHED",
                                            "Initial published rule version",
                                            userId
                                    );

                                    ruleService.updateRule(
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

        Integer ruleVersionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_version
                        WHERE rule_id = ?
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                ruleVersionCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND operation_type = 'UPDATE'
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
                          AND event_type = 'RULE_UPDATED'
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