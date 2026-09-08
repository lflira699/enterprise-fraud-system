package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleVersionPublishRequest;
import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class RuleVersionPublicationTransactionIntegrationTest {

    @Autowired
    private RuleVersionServiceInterface ruleVersionService;

    @Autowired
    private RuleSimulationServiceInterface ruleSimulationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private AuditEntityChangeServiceInterface
            auditEntityChangeService;

    @Test
    void shouldRollbackRuleVersionPublicationWhenAuditEntityChangeFails() {

        UUID organizationId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        UUID ruleId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        AtomicReference<UUID> ruleVersionId =
                new AtomicReference<>();

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

        RuleVersionPublishRequest publishRequest =
                new RuleVersionPublishRequest();

        publishRequest.setApprovedBy(
                userId
        );

        publishRequest.setChangeReason(
                "Publication rollback verification"
        );

        publishRequest.setCorrelationId(
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
                                            "RULE-PUB-TX-" + suffix,
                                            "EFS Rule Publication Transaction Test Organization",
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
                                            "efs.rule.publish.tx." + suffix,
                                            "EFS Rule Publication Transaction Test User",
                                            "efs.rule.publish.tx."
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
                                            "RULE-PUB-TX-" + suffix,
                                            "Rule Publication Transaction Test",
                                            "Rule version publication transaction test",
                                            "TRANSACTION",
                                            "HIGH",
                                            (short) 1,
                                            "FRAUD_RULES",
                                            1,
                                            "ACTIVE"
                                    );

                                    RuleVersionRequest versionRequest =
                                            new RuleVersionRequest();

                                    versionRequest.setVersionNumber(
                                            2
                                    );

                                    versionRequest.setRuleName(
                                            "Rule Publication Transaction Version"
                                    );

                                    versionRequest.setDescription(
                                            "Draft version used for rollback verification"
                                    );

                                    versionRequest.setCategory(
                                            "TRANSACTION"
                                    );

                                    versionRequest.setSeverity(
                                            "HIGH"
                                    );

                                    versionRequest.setPriority(
                                            (short) 1
                                    );

                                    versionRequest.setOwnerTeam(
                                            "FRAUD_RULES"
                                    );

                                    versionRequest.setEffectiveFrom(
                                            null
                                    );

                                    versionRequest.setEffectiveTo(
                                            null
                                    );

                                    versionRequest.setPublicationStatus(
                                            "DRAFT"
                                    );

                                    versionRequest.setChangeSummary(
                                            "UC-026 transaction rollback candidate"
                                    );

                                    versionRequest.setCreatedBy(
                                            userId
                                    );

                                    versionRequest.setApprovedBy(
                                            null
                                    );

                                    RuleVersionResponse version =
                                            ruleVersionService.createRuleVersion(
                                                    ruleId,
                                                    versionRequest
                                            );

                                    ruleVersionId.set(
                                            version.getRuleVersionId()
                                    );

                                    RuleSimulationRequest simulationRequest =
                                            new RuleSimulationRequest();

                                    simulationRequest.setSimulationName(
                                            "UC-026 Publication Transaction Controlled Test"
                                    );

                                    simulationRequest.setEntityType(
                                            "RULE_VERSION"
                                    );

                                    simulationRequest.setEntityId(
                                            ruleVersionId.get()
                                    );

                                    simulationRequest.setDatasetReference(
                                            "dataset://uc026/publication-transaction"
                                    );

                                    simulationRequest.setSampleSize(
                                            10L
                                    );

                                    simulationRequest.setSimulationStatus(
                                            "COMPLETED"
                                    );

                                    simulationRequest.setMatchCount(
                                            2L
                                    );

                                    simulationRequest.setApproveCount(
                                            6L
                                    );

                                    simulationRequest.setRejectCount(
                                            2L
                                    );

                                    simulationRequest.setReviewCount(
                                            2L
                                    );

                                    simulationRequest.setResultSummary(
                                            null
                                    );

                                    simulationRequest.setExecutedBy(
                                            userId
                                    );

                                    RuleSimulationResponse simulation =
                                            ruleSimulationService.createRuleSimulation(
                                                    simulationRequest
                                            );

                                    assertEquals(
                                            1,
                                            jdbcTemplate.update(
                                                    """
                                                    UPDATE rules.rule_simulation
                                                    SET simulation_source = 'CONTROLLED_RULE_TEST',
                                                        simulation_status = 'COMPLETED',
                                                        completed_at = CURRENT_TIMESTAMP
                                                    WHERE simulation_id = ?
                                                    """,
                                                    simulation.getSimulationId()
                                            )
                                    );

                                    ruleVersionService.publishRuleVersion(
                                            ruleId,
                                            ruleVersionId.get(),
                                            publishRequest
                                    );
                                }
                        )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        Integer.class,
                        ruleId
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_version
                        WHERE rule_version_id = ?
                        """,
                        Integer.class,
                        ruleVersionId.get()
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_simulation
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        Integer.class,
                        ruleVersionId.get()
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        ruleVersionId.get(),
                        correlationId
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND event_type = 'RULE_VERSION_PUBLISHED'
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        ruleVersionId.get(),
                        correlationId
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_entity_change
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        Integer.class,
                        ruleVersionId.get()
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.user_account
                        WHERE user_id = ?
                        """,
                        Integer.class,
                        userId
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.organization
                        WHERE organization_id = ?
                        """,
                        Integer.class,
                        organizationId
                )
        );
    }
}