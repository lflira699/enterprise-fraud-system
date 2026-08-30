package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioRuleRequest;
import com.efs.modules.detection.dto.ScenarioRuleResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioRuleRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioRuleServiceIntegrationTest {

    @Autowired
    private ScenarioRuleServiceInterface scenarioRuleService;

    @Autowired
    private ScenarioRuleRepository scenarioRuleRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private RuleRepository ruleRepository;

    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID ruleId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SR-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Rule Service Test"
        );

        scenario.setObjective(
                "Validate scenario rule service behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioRuleServiceIntegrationTest"
        );

        scenario.setCategory(
                "DETECTION"
        );

        scenario.setCriticality(
                "HIGH"
        );

        scenario.setStatus(
                "ACTIVE"
        );

        scenario.setOwner(
                "Detection Team"
        );

        scenario.setVersion(
                1
        );

        scenario.setCreatedAt(
                now
        );

        scenario.setUpdatedAt(
                now
        );

        DetectionScenario savedScenario =
                detectionScenarioRepository.saveAndFlush(
                        scenario
                );

        scenarioId =
                savedScenario.getScenarioId();

        ScenarioVersion scenarioVersion =
                createScenarioVersion(
                        1
                );

        ScenarioVersion savedScenarioVersion =
                scenarioVersionRepository.saveAndFlush(
                        scenarioVersion
                );

        scenarioVersionId =
                savedScenarioVersion.getScenarioVersionId();

        Rule rule =
                createRule(
                        "SR-SVC-RULE-" + UUID.randomUUID(),
                        (short) 1
                );

        Rule savedRule =
                ruleRepository.saveAndFlush(
                        rule
                );

        ruleId =
                savedRule.getRuleId();
    }

    @Test
    void createScenarioRuleShouldPersistAndMapProvidedValues() {

        ScenarioRuleRequest request =
                buildRequest(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        ScenarioRuleResponse response =
                scenarioRuleService
                        .createScenarioRule(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getScenarioRuleId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertEquals(
                ruleId,
                response.getRuleId()
        );

        assertEquals(
                "PRIMARY",
                response.getRuleRole()
        );

        assertTrue(
                response.getRequired()
        );

        assertEquals(
                Short.valueOf((short) 1),
                response.getEvaluationOrder()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                scenarioRuleRepository.existsById(
                        response.getScenarioRuleId()
                )
        );
    }

    @Test
    void createScenarioRuleShouldAllowOptionalRoleAndEvaluationOrder() {

        ScenarioRuleRequest request =
                buildRequest(
                        scenarioVersionId,
                        ruleId,
                        null,
                        false,
                        null
                );

        ScenarioRuleResponse response =
                scenarioRuleService
                        .createScenarioRule(
                                request
                        );

        assertNotNull(
                response.getScenarioRuleId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertEquals(
                ruleId,
                response.getRuleId()
        );

        assertNull(
                response.getRuleRole()
        );

        assertFalse(
                response.getRequired()
        );

        assertNull(
                response.getEvaluationOrder()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getScenarioRuleByIdShouldReturnExistingScenarioRule() {

        ScenarioRuleResponse created =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        ruleId,
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        ScenarioRuleResponse found =
                scenarioRuleService
                        .getScenarioRuleById(
                                created.getScenarioRuleId()
                        );

        assertEquals(
                created.getScenarioRuleId(),
                found.getScenarioRuleId()
        );

        assertEquals(
                scenarioVersionId,
                found.getScenarioVersionId()
        );

        assertEquals(
                ruleId,
                found.getRuleId()
        );

        assertEquals(
                "PRIMARY",
                found.getRuleRole()
        );

        assertTrue(
                found.getRequired()
        );
    }

    @Test
    void getScenarioRuleByIdShouldThrowWhenScenarioRuleDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioRuleService
                        .getScenarioRuleById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getScenarioRulesByScenarioVersionShouldReturnMatchingRules() {

        Rule secondRule =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-SVC-RULE2-" + UUID.randomUUID(),
                                (short) 2
                        )
                );

        ScenarioRuleResponse first =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        ruleId,
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        ScenarioRuleResponse second =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        secondRule.getRuleId(),
                                        "SUPPORTING",
                                        false,
                                        (short) 2
                                )
                        );

        List<ScenarioRuleResponse> results =
                scenarioRuleService
                        .getScenarioRulesByScenarioVersion(
                                scenarioVersionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        first.getScenarioRuleId()
                )
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        second.getScenarioRuleId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                scenarioVersionId.equals(
                                        result.getScenarioVersionId()
                                )
                        )
        );
    }

    @Test
    void getScenarioRulesByRuleShouldReturnMatchingRulesAcrossVersions() {

        ScenarioVersion secondVersion =
                scenarioVersionRepository.saveAndFlush(
                        createScenarioVersion(
                                2
                        )
                );

        ScenarioRuleResponse first =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        ruleId,
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        ScenarioRuleResponse second =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        secondVersion.getScenarioVersionId(),
                                        ruleId,
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        List<ScenarioRuleResponse> results =
                scenarioRuleService
                        .getScenarioRulesByRule(
                                ruleId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        first.getScenarioRuleId()
                )
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        second.getScenarioRuleId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                ruleId.equals(
                                        result.getRuleId()
                                )
                        )
        );
    }

    @Test
    void getRequiredScenarioRulesShouldReturnOnlyRequiredRules() {

        Rule requiredRuleTwo =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-SVC-REQ2-" + UUID.randomUUID(),
                                (short) 2
                        )
                );

        Rule optionalRule =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-SVC-OPT-" + UUID.randomUUID(),
                                (short) 3
                        )
                );

        ScenarioRuleResponse firstRequired =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        ruleId,
                                        "PRIMARY",
                                        true,
                                        (short) 1
                                )
                        );

        ScenarioRuleResponse secondRequired =
                scenarioRuleService
                        .createScenarioRule(
                                buildRequest(
                                        scenarioVersionId,
                                        requiredRuleTwo.getRuleId(),
                                        "SUPPORTING",
                                        true,
                                        (short) 2
                                )
                        );

        scenarioRuleService
                .createScenarioRule(
                        buildRequest(
                                scenarioVersionId,
                                optionalRule.getRuleId(),
                                "OPTIONAL",
                                false,
                                (short) 3
                        )
                );

        List<ScenarioRuleResponse> results =
                scenarioRuleService
                        .getRequiredScenarioRules(
                                scenarioVersionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        firstRequired.getScenarioRuleId()
                )
        );

        assertTrue(
                containsScenarioRule(
                        results,
                        secondRequired.getScenarioRuleId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                Boolean.TRUE.equals(
                                        result.getRequired()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                scenarioRuleService
                        .getScenarioRulesByScenarioVersion(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioRuleService
                        .getScenarioRulesByRule(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioRuleService
                        .getRequiredScenarioRules(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private ScenarioRuleRequest buildRequest(
            UUID targetScenarioVersionId,
            UUID targetRuleId,
            String ruleRole,
            Boolean required,
            Short evaluationOrder) {

        ScenarioRuleRequest request =
                new ScenarioRuleRequest();

        request.setScenarioVersionId(
                targetScenarioVersionId
        );

        request.setRuleId(
                targetRuleId
        );

        request.setRuleRole(
                ruleRole
        );

        request.setRequired(
                required
        );

        request.setEvaluationOrder(
                evaluationOrder
        );

        return request;
    }

    private ScenarioVersion createScenarioVersion(
            Integer versionNumber) {

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(
                scenarioId
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

        version.setCreatedAt(
                now
        );

        version.setUpdatedAt(
                now
        );

        return version;
    }

    private Rule createRule(
            String ruleCode,
            Short priority) {

        Rule rule =
                new Rule();

        rule.setRuleCode(
                ruleCode
        );

        rule.setRuleName(
                "Scenario Rule Service Test"
        );

        rule.setDescription(
                "Rule used by ScenarioRuleServiceIntegrationTest"
        );

        rule.setCategory(
                "TRANSACTION"
        );

        rule.setSeverity(
                "HIGH"
        );

        rule.setPriority(
                priority
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

        return rule;
    }

    private boolean containsScenarioRule(
            List<ScenarioRuleResponse> results,
            UUID scenarioRuleId) {

        return results.stream()
                .anyMatch(result ->
                        scenarioRuleId.equals(
                                result.getScenarioRuleId()
                        )
                );
    }
}