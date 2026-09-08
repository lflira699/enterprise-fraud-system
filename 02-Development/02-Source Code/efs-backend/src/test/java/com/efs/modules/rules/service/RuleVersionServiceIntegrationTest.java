package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleVersionServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    @Autowired
    private RuleVersionServiceInterface service;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

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
                "EFS-RULE-VERSION-TEST-ORG",
                "EFS Rule Version Test Organization",
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
                "efs.rule.version.test",
                "EFS Rule Version Test User",
                "efs.rule.version.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateAndRetrieveRuleVersionById() {

        UUID ruleId =
                createRule(
                        "RULE-VERSION-001"
                );

        RuleVersionResponse created =
                service.createRuleVersion(
                        ruleId,
                        buildRequest(
                                1,
                                "DRAFT"
                        )
                );

        assertNotNull(
                created.getRuleVersionId()
        );

        assertEquals(
                ruleId,
                created.getRuleId()
        );

        assertEquals(
                Integer.valueOf(1),
                created.getVersionNumber()
        );

        assertEquals(
                "Rule Version Snapshot",
                created.getRuleName()
        );

        assertEquals(
                "Versioned rule snapshot",
                created.getDescription()
        );

        assertEquals(
                "TRANSACTION",
                created.getCategory()
        );

        assertEquals(
                "HIGH",
                created.getSeverity()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getPriority()
        );

        assertEquals(
                "FRAUD_RULES",
                created.getOwnerTeam()
        );

        assertEquals(
                "DRAFT",
                created.getPublicationStatus()
        );

        assertEquals(
                "Initial rule version",
                created.getChangeSummary()
        );

        assertEquals(
                CREATED_BY,
                created.getCreatedBy()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        RuleVersionResponse retrieved =
                service.getRuleVersionById(
                        created.getRuleVersionId()
                );

        assertEquals(
                created.getRuleVersionId(),
                retrieved.getRuleVersionId()
        );

        assertEquals(
                "Rule Version Snapshot",
                retrieved.getRuleName()
        );

        assertEquals(
                "Versioned rule snapshot",
                retrieved.getDescription()
        );

        assertEquals(
                "TRANSACTION",
                retrieved.getCategory()
        );

        assertEquals(
                "HIGH",
                retrieved.getSeverity()
        );

        assertEquals(
                Short.valueOf((short) 1),
                retrieved.getPriority()
        );

        assertEquals(
                "FRAUD_RULES",
                retrieved.getOwnerTeam()
        );
    }

    @Test
    void shouldRetrieveRuleVersionByRuleAndVersionNumber() {

        UUID ruleId =
                createRule(
                        "RULE-VERSION-002"
                );

        service.createRuleVersion(
                ruleId,
                buildRequest(
                        1,
                        "DRAFT"
                )
        );

        RuleVersionResponse retrieved =
                service.getRuleVersionByRuleIdAndVersionNumber(
                        ruleId,
                        1
                );

        assertEquals(
                ruleId,
                retrieved.getRuleId()
        );

        assertEquals(
                Integer.valueOf(1),
                retrieved.getVersionNumber()
        );

        assertEquals(
                "Rule Version Snapshot",
                retrieved.getRuleName()
        );

        assertEquals(
                "TRANSACTION",
                retrieved.getCategory()
        );

        assertEquals(
                "HIGH",
                retrieved.getSeverity()
        );

        assertEquals(
                Short.valueOf((short) 1),
                retrieved.getPriority()
        );
    }

    @Test
    void shouldReturnRuleVersionsOrderedByVersionNumberDescending() {

        UUID ruleId =
                createRule(
                        "RULE-VERSION-003"
                );

        service.createRuleVersion(
                ruleId,
                buildRequest(
                        1,
                        "DRAFT"
                )
        );


        service.createRuleVersion(
                ruleId,
                buildRequest(
                        2,
                        "DRAFT"
                )
        );

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE rules.rule_version
                SET publication_status = 'PUBLISHED'
                WHERE rule_id = ?
                  AND version_number = ?
                """,
                ruleId,
                2
        );

        entityManager.clear();

        List<RuleVersionResponse> versions =
                service.getRuleVersionsByRuleId(
                        ruleId
                );

        assertEquals(
                2,
                versions.size()
        );

        assertEquals(
                Integer.valueOf(2),
                versions.get(0).getVersionNumber()
        );

        assertEquals(
                Integer.valueOf(1),
                versions.get(1).getVersionNumber()
        );

        assertEquals(
                "Rule Version Snapshot",
                versions.get(0).getRuleName()
        );

        assertEquals(
                "Rule Version Snapshot",
                versions.get(1).getRuleName()
        );
    }

    @Test
    void shouldReturnRuleVersionsByPublicationStatus() {

        UUID firstRuleId =
                createRule(
                        "RULE-VERSION-004"
                );

        UUID secondRuleId =
                createRule(
                        "RULE-VERSION-005"
                );


        service.createRuleVersion(
                firstRuleId,
                buildRequest(
                        1,
                        "DRAFT"
                )
        );

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE rules.rule_version
                SET publication_status = 'PUBLISHED'
                WHERE rule_id = ?
                  AND version_number = ?
                """,
                firstRuleId,
                1
        );

        entityManager.clear();


        service.createRuleVersion(
                secondRuleId,
                buildRequest(
                        1,
                        "DRAFT"
                )
        );

        entityManager.flush();

        jdbcTemplate.update(
                """
                UPDATE rules.rule_version
                SET publication_status = 'PUBLISHED'
                WHERE rule_id = ?
                  AND version_number = ?
                """,
                secondRuleId,
                1
        );

        entityManager.clear();

        List<RuleVersionResponse> versions =
                service.getRuleVersionsByPublicationStatus(
                        "PUBLISHED"
                );

        assertEquals(
                2,
                versions.size()
        );

        assertEquals(
                "PUBLISHED",
                versions.get(0).getPublicationStatus()
        );

        assertEquals(
                "PUBLISHED",
                versions.get(1).getPublicationStatus()
        );

        assertEquals(
                "Rule Version Snapshot",
                versions.get(0).getRuleName()
        );

        assertEquals(
                "Rule Version Snapshot",
                versions.get(1).getRuleName()
        );
    }

    @Test
    void shouldRejectDirectPublishedRuleVersionCreation() {

        UUID ruleId =
                createRule(
                        "RULE-VERSION-PUBLISHED-REJECTED"
                );

        RuleVersionRequest request =
                buildRequest(
                        2,
                        "PUBLISHED"
                );

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> service.createRuleVersion(
                                ruleId,
                                request
                        )
                );

        assertEquals(
                "Rule version cannot be created as PUBLISHED; use publication workflow",
                exception.getMessage()
        );

        assertEquals(
                Integer.valueOf(0),
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_version
                        WHERE rule_id = ?
                          AND version_number = ?
                        """,
                        Integer.class,
                        ruleId,
                        2
                )
        );
    }

    @Test
    void shouldRejectCreationForUnknownRule() {

        UUID unknownRuleId =
                UUID.randomUUID();

        RuleVersionRequest request =
                buildRequest(
                        1,
                        "DRAFT"
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createRuleVersion(
                        unknownRuleId,
                        request
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleVersionId() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleVersionById(
                        unknownRuleVersionId
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleWhenListingVersions() {

        UUID unknownRuleId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleVersionsByRuleId(
                        unknownRuleId
                )
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
                "Rule Version Integration Test"
        );

        rule.setDescription(
                "Rule used by V50 integration tests"
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
                .saveAndFlush(rule)
                .getRuleId();
    }

    private RuleVersionRequest buildRequest(
            int versionNumber,
            String publicationStatus) {

        RuleVersionRequest request =
                new RuleVersionRequest();

        request.setVersionNumber(
                versionNumber
        );

        request.setRuleName(
                "Rule Version Snapshot"
        );

        request.setDescription(
                "Versioned rule snapshot"
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
                LocalDateTime.now()
        );

        request.setEffectiveTo(
                null
        );

        request.setPublicationStatus(
                publicationStatus
        );

        request.setChangeSummary(
                "Initial rule version"
        );

        request.setCreatedBy(
                CREATED_BY
        );

        request.setApprovedBy(
                null
        );

        return request;
    }
}