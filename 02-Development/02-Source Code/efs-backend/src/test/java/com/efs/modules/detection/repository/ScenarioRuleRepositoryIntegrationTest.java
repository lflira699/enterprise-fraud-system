package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioRule;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ScenarioRuleRepositoryIntegrationTest {

    @Autowired
    private ScenarioRuleRepository scenarioRuleRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    void shouldSaveScenarioRule() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-SAVE-" + UUID.randomUUID(),
                        1
                );

        Rule rule =
                createAndSaveRule(
                        "V98-RULE-SAVE-" + UUID.randomUUID(),
                        (short) 1
                );

        ScenarioRule scenarioRule =
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        rule.getRuleId(),
                        "PRIMARY",
                        true,
                        (short) 1
                );

        ScenarioRule saved =
                scenarioRuleRepository
                        .saveAndFlush(scenarioRule);

        assertNotNull(saved.getScenarioRuleId());

        assertEquals(
                scenarioVersion.getScenarioVersionId(),
                saved.getScenarioVersionId()
        );

        assertEquals(
                rule.getRuleId(),
                saved.getRuleId()
        );

        assertEquals(
                "PRIMARY",
                saved.getRuleRole()
        );

        assertTrue(saved.getRequired());

        assertEquals(
                (short) 1,
                saved.getEvaluationOrder()
        );

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void shouldFindScenarioRuleById() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-ID-" + UUID.randomUUID(),
                        1
                );

        Rule rule =
                createAndSaveRule(
                        "V98-RULE-ID-" + UUID.randomUUID(),
                        (short) 1
                );

        ScenarioRule saved =
                scenarioRuleRepository
                        .saveAndFlush(
                                createScenarioRule(
                                        scenarioVersion.getScenarioVersionId(),
                                        rule.getRuleId(),
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        ScenarioRule result =
                scenarioRuleRepository
                        .findByScenarioRuleId(
                                saved.getScenarioRuleId()
                        )
                        .orElseThrow();

        assertEquals(
                saved.getScenarioRuleId(),
                result.getScenarioRuleId()
        );

        assertEquals(
                scenarioVersion.getScenarioVersionId(),
                result.getScenarioVersionId()
        );

        assertEquals(
                rule.getRuleId(),
                result.getRuleId()
        );
    }

    @Test
    void shouldFindScenarioRulesByScenarioVersionOrderedByEvaluationOrderAsc() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-ORDER-" + UUID.randomUUID(),
                        1
                );

        Rule ruleOne =
                createAndSaveRule(
                        "V98-ORDER-R1-" + UUID.randomUUID(),
                        (short) 1
                );

        Rule ruleTwo =
                createAndSaveRule(
                        "V98-ORDER-R2-" + UUID.randomUUID(),
                        (short) 2
                );

        Rule ruleThree =
                createAndSaveRule(
                        "V98-ORDER-R3-" + UUID.randomUUID(),
                        (short) 3
                );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleOne.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 3
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleTwo.getRuleId(),
                        "PRIMARY",
                        true,
                        (short) 1
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleThree.getRuleId(),
                        "SUPPORTING",
                        true,
                        (short) 2
                )
        );

        scenarioRuleRepository.flush();

        List<ScenarioRule> result =
                scenarioRuleRepository
                        .findByScenarioVersionIdOrderByEvaluationOrderAsc(
                                scenarioVersion.getScenarioVersionId()
                        );

        assertEquals(3, result.size());

        assertEquals(
                (short) 1,
                result.get(0).getEvaluationOrder()
        );

        assertEquals(
                (short) 2,
                result.get(1).getEvaluationOrder()
        );

        assertEquals(
                (short) 3,
                result.get(2).getEvaluationOrder()
        );
    }

    @Test
    void shouldFindScenarioRulesByRuleOrderedByScenarioVersionIdAsc() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V98-RULE-ORDER-" + UUID.randomUUID()
                );

        ScenarioVersion versionOne =
                createAndSaveScenarioVersion(
                        scenario,
                        1
                );

        ScenarioVersion versionTwo =
                createAndSaveScenarioVersion(
                        scenario,
                        2
                );

        Rule rule =
                createAndSaveRule(
                        "V98-SHARED-RULE-" + UUID.randomUUID(),
                        (short) 1
                );

        scenarioRuleRepository.save(
                createScenarioRule(
                        versionOne.getScenarioVersionId(),
                        rule.getRuleId(),
                        "PRIMARY",
                        true,
                        (short) 1
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        versionTwo.getScenarioVersionId(),
                        rule.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 2
                )
        );

        scenarioRuleRepository.flush();

        List<ScenarioRule> result =
                scenarioRuleRepository
                        .findByRuleIdOrderByScenarioVersionIdAsc(
                                rule.getRuleId()
                        );

        assertEquals(2, result.size());

        assertTrue(
                result.get(0)
                        .getScenarioVersionId()
                        .compareTo(
                                result.get(1)
                                        .getScenarioVersionId()
                        ) <= 0
        );

        assertEquals(
                rule.getRuleId(),
                result.get(0).getRuleId()
        );

        assertEquals(
                rule.getRuleId(),
                result.get(1).getRuleId()
        );
    }

    @Test
    void shouldFindRequiredScenarioRulesOrderedByEvaluationOrderAsc() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-REQUIRED-" + UUID.randomUUID(),
                        1
                );

        Rule ruleOne =
                createAndSaveRule(
                        "V98-REQ-R1-" + UUID.randomUUID(),
                        (short) 1
                );

        Rule ruleTwo =
                createAndSaveRule(
                        "V98-REQ-R2-" + UUID.randomUUID(),
                        (short) 2
                );

        Rule ruleThree =
                createAndSaveRule(
                        "V98-REQ-R3-" + UUID.randomUUID(),
                        (short) 3
                );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleOne.getRuleId(),
                        "PRIMARY",
                        true,
                        (short) 2
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleTwo.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 1
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleThree.getRuleId(),
                        "SUPPORTING",
                        true,
                        (short) 3
                )
        );

        scenarioRuleRepository.flush();

        List<ScenarioRule> result =
                scenarioRuleRepository
                        .findByScenarioVersionIdAndRequiredOrderByEvaluationOrderAsc(
                                scenarioVersion.getScenarioVersionId(),
                                true
                        );

        assertEquals(2, result.size());

        assertTrue(
                result.get(0).getRequired()
        );

        assertTrue(
                result.get(1).getRequired()
        );

        assertEquals(
                (short) 2,
                result.get(0).getEvaluationOrder()
        );

        assertEquals(
                (short) 3,
                result.get(1).getEvaluationOrder()
        );
    }

    @Test
    void shouldFindOptionalScenarioRulesOrderedByEvaluationOrderAsc() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-OPTIONAL-" + UUID.randomUUID(),
                        1
                );

        Rule ruleOne =
                createAndSaveRule(
                        "V98-OPT-R1-" + UUID.randomUUID(),
                        (short) 1
                );

        Rule ruleTwo =
                createAndSaveRule(
                        "V98-OPT-R2-" + UUID.randomUUID(),
                        (short) 2
                );

        Rule ruleThree =
                createAndSaveRule(
                        "V98-OPT-R3-" + UUID.randomUUID(),
                        (short) 3
                );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleOne.getRuleId(),
                        "PRIMARY",
                        true,
                        (short) 1
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleTwo.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 3
                )
        );

        scenarioRuleRepository.save(
                createScenarioRule(
                        scenarioVersion.getScenarioVersionId(),
                        ruleThree.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 2
                )
        );

        scenarioRuleRepository.flush();

        List<ScenarioRule> result =
                scenarioRuleRepository
                        .findByScenarioVersionIdAndRequiredOrderByEvaluationOrderAsc(
                                scenarioVersion.getScenarioVersionId(),
                                false
                        );

        assertEquals(2, result.size());

        assertFalse(
                result.get(0).getRequired()
        );

        assertFalse(
                result.get(1).getRequired()
        );

        assertEquals(
                (short) 2,
                result.get(0).getEvaluationOrder()
        );

        assertEquals(
                (short) 3,
                result.get(1).getEvaluationOrder()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioRuleDoesNotExist() {

        assertTrue(
                scenarioRuleRepository
                        .findByScenarioRuleId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioVersionHasNoRules() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V98-EMPTY-" + UUID.randomUUID(),
                        1
                );

        List<ScenarioRule> result =
                scenarioRuleRepository
                        .findByScenarioVersionIdOrderByEvaluationOrderAsc(
                                scenarioVersion.getScenarioVersionId()
                        );

        assertTrue(result.isEmpty());
    }

    private DetectionScenario createAndSaveScenario(
            String scenarioCode) {

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(scenarioCode);
        scenario.setScenarioName(
                "V98 Scenario"
        );

        scenario.setObjective(
                "Scenario Rule repository integration test"
        );

        scenario.setCategory(
                "DETECTION"
        );

        scenario.setCriticality(
                "MEDIUM"
        );

        scenario.setStatus(
                "ACTIVE"
        );

        scenario.setOwner(
                "Detection Team"
        );

        scenario.setVersion(1);

        LocalDateTime now =
                LocalDateTime.now();

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

        return detectionScenarioRepository
                .saveAndFlush(scenario);
    }

    private ScenarioVersion createAndSaveScenarioVersion(
            String scenarioCode,
            Integer versionNumber) {

        DetectionScenario scenario =
                createAndSaveScenario(
                        scenarioCode
                );

        return createAndSaveScenarioVersion(
                scenario,
                versionNumber
        );
    }

    private ScenarioVersion createAndSaveScenarioVersion(
            DetectionScenario scenario,
            Integer versionNumber) {

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(
                scenario.getScenarioId()
        );

        version.setVersionNumber(
                versionNumber
        );

        version.setVersionStatus(
                "ACTIVE"
        );

        version.setCorrelationWindowSeconds(
                1800L
        );

        version.setActivationMode(
                "AUTOMATIC"
        );

        LocalDateTime now =
                LocalDateTime.now();

        version.setCreatedAt(now);
        version.setUpdatedAt(now);

        return scenarioVersionRepository
                .saveAndFlush(version);
    }

    private Rule createAndSaveRule(
            String ruleCode,
            Short priority) {

        Rule rule =
                new Rule();

        rule.setRuleCode(ruleCode);

        rule.setRuleName(
                "V98 Scenario Rule Test"
        );

        rule.setDescription(
                "Rule used by V98 repository integration tests"
        );

        rule.setCategory(
                "TRANSACTION"
        );

        rule.setSeverity(
                "HIGH"
        );

        rule.setPriority(priority);

        rule.setOwnerTeam(
                "FRAUD_RULES"
        );

        rule.setCurrentVersion(1);

        rule.setStatus(
                "ACTIVE"
        );

        LocalDateTime now =
                LocalDateTime.now();

        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        return ruleRepository
                .saveAndFlush(rule);
    }

    private ScenarioRule createScenarioRule(
            UUID scenarioVersionId,
            UUID ruleId,
            String ruleRole,
            Boolean required,
            Short evaluationOrder) {

        ScenarioRule scenarioRule =
                new ScenarioRule();

        scenarioRule.setScenarioVersionId(
                scenarioVersionId
        );

        scenarioRule.setRuleId(
                ruleId
        );

        scenarioRule.setRuleRole(
                ruleRole
        );

        scenarioRule.setRequired(
                required
        );

        scenarioRule.setEvaluationOrder(
                evaluationOrder
        );

        scenarioRule.setCreatedAt(
                LocalDateTime.now()
        );

        return scenarioRule;
    }
}