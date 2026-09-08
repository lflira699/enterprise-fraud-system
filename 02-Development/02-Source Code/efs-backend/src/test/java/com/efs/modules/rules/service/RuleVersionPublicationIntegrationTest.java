package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.dto.RuleVersionPublishRequest;
import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleVersionPublicationIntegrationTest {

    private UUID organizationId;

    private UUID userId;

    private String fixtureSuffix;

    @Autowired
    private RuleVersionServiceInterface ruleVersionService;

    @Autowired
    private RuleSimulationServiceInterface ruleSimulationService;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

        organizationId =
                UUID.randomUUID();

        userId =
                UUID.randomUUID();

        fixtureSuffix =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");

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
                "UC026-" + fixtureSuffix,
                "EFS UC-026 Publication Organization",
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
                "uc026." + fixtureSuffix,
                "EFS UC-026 Publisher",
                "uc026." + fixtureSuffix + "@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldPublishDraftRuleVersionAfterCompletedControlledTest() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-001"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        createCompletedControlledTest(
                version.getRuleVersionId()
        );

        UUID correlationId =
                UUID.randomUUID();

        RuleVersionResponse published =
                ruleVersionService.publishRuleVersion(
                        ruleId,
                        version.getRuleVersionId(),
                        buildPublishRequest(
                                correlationId
                        )
                );

        assertEquals(
                "PUBLISHED",
                published.getPublicationStatus()
        );

        assertEquals(
                userId,
                published.getApprovedBy()
        );

        assertNotNull(
                published.getEffectiveFrom()
        );

        entityManager.flush();

        assertEquals(
                "PUBLISHED",
                jdbcTemplate.queryForObject(
                        """
                        SELECT publication_status
                        FROM rules.rule_version
                        WHERE rule_version_id = ?
                        """,
                        String.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                userId,
                jdbcTemplate.queryForObject(
                        """
                        SELECT approved_by
                        FROM rules.rule_version
                        WHERE rule_version_id = ?
                        """,
                        UUID.class,
                        version.getRuleVersionId()
                )
        );

        assertNotNull(
                jdbcTemplate.queryForObject(
                        """
                        SELECT effective_from
                        FROM rules.rule_version
                        WHERE rule_version_id = ?
                        """,
                        LocalDateTime.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                Integer.valueOf(2),
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_version
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        Integer.class,
                        ruleId
                )
        );

        assertEquals(
                Integer.valueOf(1),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        Integer.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                userId,
                jdbcTemplate.queryForObject(
                        """
                        SELECT changed_by
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        UUID.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                correlationId,
                jdbcTemplate.queryForObject(
                        """
                        SELECT correlation_id
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        UUID.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                "UC-026 controlled publication",
                jdbcTemplate.queryForObject(
                        """
                        SELECT change_reason
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        String.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                "DRAFT",
                jdbcTemplate.queryForObject(
                        """
                        SELECT previous_value ->> 'publicationStatus'
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        String.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                "PUBLISHED",
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_value ->> 'publicationStatus'
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        String.class,
                        version.getRuleVersionId()
                )
        );

        UUID auditEventId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT audit_event_id
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_VERSION_PUBLISHED'
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        UUID.class,
                        version.getRuleVersionId()
                );

        assertNotNull(
                auditEventId
        );

        assertEquals(
                "PUBLISH",
                jdbcTemplate.queryForObject(
                        """
                        SELECT action
                        FROM audit.audit_event
                        WHERE audit_event_id = ?
                        """,
                        String.class,
                        auditEventId
                )
        );

        assertEquals(
                "RULE_ENGINE",
                jdbcTemplate.queryForObject(
                        """
                        SELECT source_component
                        FROM audit.audit_event
                        WHERE audit_event_id = ?
                        """,
                        String.class,
                        auditEventId
                )
        );

        assertEquals(
                "SUCCESS",
                jdbcTemplate.queryForObject(
                        """
                        SELECT event_result
                        FROM audit.audit_event
                        WHERE audit_event_id = ?
                        """,
                        String.class,
                        auditEventId
                )
        );

        assertEquals(
                userId,
                jdbcTemplate.queryForObject(
                        """
                        SELECT user_id
                        FROM audit.audit_event
                        WHERE audit_event_id = ?
                        """,
                        UUID.class,
                        auditEventId
                )
        );

        assertEquals(
                correlationId,
                jdbcTemplate.queryForObject(
                        """
                        SELECT correlation_id
                        FROM audit.audit_event
                        WHERE audit_event_id = ?
                        """,
                        UUID.class,
                        auditEventId
                )
        );

        assertEquals(
                Integer.valueOf(1),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation = 'UPDATE'
                        """,
                        Integer.class,
                        auditEventId,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                "DRAFT",
                jdbcTemplate.queryForObject(
                        """
                        SELECT previous_value ->> 'publicationStatus'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        String.class,
                        auditEventId,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                "PUBLISHED",
                jdbcTemplate.queryForObject(
                        """
                        SELECT current_value ->> 'publicationStatus'
                        FROM audit.audit_entity_change
                        WHERE audit_event_id = ?
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        String.class,
                        auditEventId,
                        version.getRuleVersionId()
                )
        );
    }

    @Test
    void shouldRejectPublicationWithoutCompletedControlledTest() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-002"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        assertThrows(
                ValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                ruleId,
                                version.getRuleVersionId(),
                                buildPublishRequest(
                                        UUID.randomUUID()
                                )
                        )
        );

        entityManager.flush();

        assertPublicationWasNotPersisted(
                version.getRuleVersionId()
        );
    }

    @Test
    void shouldRejectGenericCompletedSimulationAsPublicationEvidence() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-003"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        createGenericCompletedTest(
                version.getRuleVersionId()
        );

        assertThrows(
                ValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                ruleId,
                                version.getRuleVersionId(),
                                buildPublishRequest(
                                        UUID.randomUUID()
                                )
                        )
        );

        entityManager.flush();

        assertPublicationWasNotPersisted(
                version.getRuleVersionId()
        );
    }

    @Test
    void shouldRejectControlledTestWithoutCompletionEvidence() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-004"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        createControlledTestWithoutCompletion(
                version.getRuleVersionId()
        );

        assertThrows(
                ValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                ruleId,
                                version.getRuleVersionId(),
                                buildPublishRequest(
                                        UUID.randomUUID()
                                )
                        )
        );

        entityManager.flush();

        assertPublicationWasNotPersisted(
                version.getRuleVersionId()
        );
    }

    @Test
    void shouldRejectRuleVersionRepublication() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-005"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        createCompletedControlledTest(
                version.getRuleVersionId()
        );

        RuleVersionPublishRequest request =
                buildPublishRequest(
                        UUID.randomUUID()
                );

        ruleVersionService.publishRuleVersion(
                ruleId,
                version.getRuleVersionId(),
                request
        );

        assertThrows(
                ValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                ruleId,
                                version.getRuleVersionId(),
                                request
                        )
        );

        entityManager.flush();

        assertEquals(
                Integer.valueOf(1),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND operation_type = 'PUBLICATION'
                        """,
                        Integer.class,
                        version.getRuleVersionId()
                )
        );

        assertEquals(
                Integer.valueOf(1),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_VERSION_PUBLISHED'
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        Integer.class,
                        version.getRuleVersionId()
                )
        );
    }

    @Test
    void shouldRejectPublicationWithoutActor() {

        UUID ruleId =
                createRule(
                        "RULE-UC026-PUBLISH-006"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ruleId,
                        2
                );

        createCompletedControlledTest(
                version.getRuleVersionId()
        );

        RuleVersionPublishRequest request =
                new RuleVersionPublishRequest();

        request.setChangeReason(
                "Missing publication actor"
        );

        request.setCorrelationId(
                UUID.randomUUID()
        );

        assertThrows(
                RequestValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                ruleId,
                                version.getRuleVersionId(),
                                request
                        )
        );

        entityManager.flush();

        assertPublicationWasNotPersisted(
                version.getRuleVersionId()
        );
    }

    @Test
    void shouldRejectPublicationWhenVersionBelongsToAnotherRule() {

        UUID ownerRuleId =
                createRule(
                        "RULE-UC026-PUBLISH-007-A"
                );

        UUID differentRuleId =
                createRule(
                        "RULE-UC026-PUBLISH-007-B"
                );

        RuleVersionResponse version =
                createDraftVersion(
                        ownerRuleId,
                        2
                );

        createCompletedControlledTest(
                version.getRuleVersionId()
        );

        assertThrows(
                ValidationException.class,
                () ->
                        ruleVersionService.publishRuleVersion(
                                differentRuleId,
                                version.getRuleVersionId(),
                                buildPublishRequest(
                                        UUID.randomUUID()
                                )
                        )
        );

        entityManager.flush();

        assertPublicationWasNotPersisted(
                version.getRuleVersionId()
        );
    }

    private UUID createRule(
            String ruleCode) {

        Rule rule =
                new Rule();

        rule.setRuleCode(
                ruleCode
        );

        rule.setRuleName(
                "UC-026 Publication Rule"
        );

        rule.setDescription(
                "Rule used by UC-026 publication integration tests"
        );

        rule.setCategory(
                "TRANSACTION"
        );

        rule.setSeverity(
                "HIGH"
        );

        rule.setPriority(
                (short) 1
        );

        rule.setOwnerTeam(
                "FRAUD_RULES"
        );

        rule.setCurrentVersion(
                1
        );

        rule.setStatus(
                "ACTIVE"
        );

        LocalDateTime now =
                LocalDateTime.now();

        rule.setCreatedAt(
                now
        );

        rule.setUpdatedAt(
                now
        );

        return ruleRepository
                .saveAndFlush(
                        rule
                )
                .getRuleId();
    }

    private RuleVersionResponse createDraftVersion(
            UUID ruleId,
            int versionNumber) {

        RuleVersionRequest request =
                new RuleVersionRequest();

        request.setVersionNumber(
                versionNumber
        );

        request.setRuleName(
                "UC-026 Publication Version"
        );

        request.setDescription(
                "Draft version awaiting controlled publication"
        );

        request.setCategory(
                "TRANSACTION"
        );

        request.setSeverity(
                "HIGH"
        );

        request.setPriority(
                (short) 1
        );

        request.setOwnerTeam(
                "FRAUD_RULES"
        );

        request.setEffectiveFrom(
                null
        );

        request.setEffectiveTo(
                null
        );

        request.setPublicationStatus(
                "DRAFT"
        );

        request.setChangeSummary(
                "UC-026 publication candidate"
        );

        request.setCreatedBy(
                userId
        );

        request.setApprovedBy(
                null
        );

        return ruleVersionService.createRuleVersion(
                ruleId,
                request
        );
    }

    private void createCompletedControlledTest(
            UUID ruleVersionId) {

        RuleSimulationResponse simulation =
                ruleSimulationService.createRuleSimulation(
                        buildSimulationRequest(
                                ruleVersionId
                        )
                );

        entityManager.flush();

        int updated =
                jdbcTemplate.update(
                        """
                        UPDATE rules.rule_simulation
                        SET simulation_source = 'CONTROLLED_RULE_TEST',
                            simulation_status = 'COMPLETED',
                            completed_at = CURRENT_TIMESTAMP
                        WHERE simulation_id = ?
                        """,
                        simulation.getSimulationId()
                );

        assertEquals(
                1,
                updated
        );

        entityManager.clear();
    }

    private void createGenericCompletedTest(
            UUID ruleVersionId) {

        RuleSimulationResponse simulation =
                ruleSimulationService.createRuleSimulation(
                        buildSimulationRequest(
                                ruleVersionId
                        )
                );

        entityManager.flush();

        int updated =
                jdbcTemplate.update(
                        """
                        UPDATE rules.rule_simulation
                        SET completed_at = CURRENT_TIMESTAMP
                        WHERE simulation_id = ?
                        """,
                        simulation.getSimulationId()
                );

        assertEquals(
                1,
                updated
        );

        entityManager.clear();
    }

    private void createControlledTestWithoutCompletion(
            UUID ruleVersionId) {

        RuleSimulationResponse simulation =
                ruleSimulationService.createRuleSimulation(
                        buildSimulationRequest(
                                ruleVersionId
                        )
                );

        entityManager.flush();

        int updated =
                jdbcTemplate.update(
                        """
                        UPDATE rules.rule_simulation
                        SET simulation_source = 'CONTROLLED_RULE_TEST',
                            simulation_status = 'COMPLETED',
                            completed_at = NULL
                        WHERE simulation_id = ?
                        """,
                        simulation.getSimulationId()
                );

        assertEquals(
                1,
                updated
        );

        entityManager.clear();
    }

    private RuleSimulationRequest buildSimulationRequest(
            UUID ruleVersionId) {

        RuleSimulationRequest request =
                new RuleSimulationRequest();

        request.setSimulationName(
                "UC-026 Publication Controlled Test"
        );

        request.setEntityType(
                "RULE_VERSION"
        );

        request.setEntityId(
                ruleVersionId
        );

        request.setDatasetReference(
                "dataset://uc026/publication"
        );

        request.setSampleSize(
                10L
        );

        request.setSimulationStatus(
                "COMPLETED"
        );

        request.setMatchCount(
                2L
        );

        request.setApproveCount(
                6L
        );

        request.setRejectCount(
                2L
        );

        request.setReviewCount(
                2L
        );

        request.setResultSummary(
                null
        );

        request.setExecutedBy(
                userId
        );

        return request;
    }

    private RuleVersionPublishRequest buildPublishRequest(
            UUID correlationId) {

        RuleVersionPublishRequest request =
                new RuleVersionPublishRequest();

        request.setApprovedBy(
                userId
        );

        request.setChangeReason(
                "UC-026 controlled publication"
        );

        request.setCorrelationId(
                correlationId
        );

        return request;
    }

    private void assertPublicationWasNotPersisted(
            UUID ruleVersionId) {

        assertEquals(
                "DRAFT",
                jdbcTemplate.queryForObject(
                        """
                        SELECT publication_status
                        FROM rules.rule_version
                        WHERE rule_version_id = ?
                        """,
                        String.class,
                        ruleVersionId
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
                        """,
                        Integer.class,
                        ruleVersionId
                )
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_VERSION_PUBLISHED'
                          AND entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                        """,
                        Integer.class,
                        ruleVersionId
                )
        );
    }
}