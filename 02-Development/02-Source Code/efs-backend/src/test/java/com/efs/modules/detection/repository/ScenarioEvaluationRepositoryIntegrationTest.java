package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioEvaluationRepositoryIntegrationTest {

    @Autowired
    private ScenarioEvaluationRepository scenarioEvaluationRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    private UUID scenarioId;
    private UUID scenarioVersionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "V101-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Integration Test"
        );

        scenario.setObjective(
                "Scenario Evaluation repository integration test"
        );

        scenario.setDescription(
                "Scenario used by V101 repository integration tests"
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

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

        DetectionScenario savedScenario =
                detectionScenarioRepository.saveAndFlush(
                        scenario
                );

        scenarioId =
                savedScenario.getScenarioId();

        ScenarioVersion scenarioVersion =
                new ScenarioVersion();

        scenarioVersion.setScenarioId(
                scenarioId
        );

        scenarioVersion.setVersionNumber(
                1
        );

        scenarioVersion.setVersionStatus(
                "ACTIVE"
        );

        scenarioVersion.setCorrelationWindowSeconds(
                1800L
        );

        scenarioVersion.setActivationMode(
                "AUTOMATIC"
        );

        scenarioVersion.setEffectiveFrom(
                now
        );

        scenarioVersion.setCreatedAt(
                now
        );

        scenarioVersion.setUpdatedAt(
                now
        );

        ScenarioVersion savedScenarioVersion =
                scenarioVersionRepository.saveAndFlush(
                        scenarioVersion
                );

        scenarioVersionId =
                savedScenarioVersion.getScenarioVersionId();
    }

    @Test
    void shouldSaveAndFindScenarioEvaluationById() {

        ScenarioEvaluation evaluation =
                createEvaluation(
                        "COMPLETED",
                        true,
                        LocalDateTime.now()
                );

        ScenarioEvaluation saved =
                scenarioEvaluationRepository.saveAndFlush(
                        evaluation
                );

        assertNotNull(
                saved.getEvaluationId()
        );

        Optional<ScenarioEvaluation> result =
                scenarioEvaluationRepository.findByEvaluationId(
                        saved.getEvaluationId()
                );

        assertTrue(
                result.isPresent()
        );

        ScenarioEvaluation found =
                result.get();

        assertEquals(
                scenarioId,
                found.getScenarioId()
        );

        assertEquals(
                scenarioVersionId,
                found.getScenarioVersionId()
        );

        assertEquals(
                "COMPLETED",
                found.getEvaluationStatus()
        );

        assertTrue(
                found.getMatched()
        );

        assertEquals(
                Short.valueOf((short) 5),
                found.getRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 3),
                found.getMatchedRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 2),
                found.getRequiredEvidenceCount()
        );

        assertEquals(
                Short.valueOf((short) 2),
                found.getAvailableEvidenceCount()
        );

        assertEquals(
                0,
                new BigDecimal("0.8750")
                        .compareTo(
                                found.getConfidence()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("0.6500")
                        .compareTo(
                                found.getRiskContribution()
                        )
        );

        assertEquals(
                Long.valueOf(125L),
                found.getEvaluationDurationMs()
        );

        assertNotNull(
                found.getEvaluationContext()
        );

        assertEquals(
                "integration-test",
                found.getEvaluationContext()
                        .get("source")
        );

        assertNotNull(
                found.getEvaluatedAt()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void shouldFindEvaluationsByScenarioOrderedByEvaluatedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluation older =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "COMPLETED",
                                false,
                                now.minusMinutes(10)
                        )
                );

        ScenarioEvaluation newer =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "COMPLETED",
                                true,
                                now
                        )
                );

        List<ScenarioEvaluation> results =
                scenarioEvaluationRepository
                        .findByScenarioIdOrderByEvaluatedAtDesc(
                                scenarioId
                        );

        int newerIndex =
                indexOfEvaluation(
                        results,
                        newer.getEvaluationId()
                );

        int olderIndex =
                indexOfEvaluation(
                        results,
                        older.getEvaluationId()
                );

        assertTrue(
                newerIndex >= 0
        );

        assertTrue(
                olderIndex >= 0
        );

        assertTrue(
                newerIndex < olderIndex
        );
    }

    @Test
    void shouldFindEvaluationsByScenarioVersionOrderedByEvaluatedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluation older =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "COMPLETED",
                                false,
                                now.minusMinutes(5)
                        )
                );

        ScenarioEvaluation newer =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "COMPLETED",
                                true,
                                now
                        )
                );

        List<ScenarioEvaluation> results =
                scenarioEvaluationRepository
                        .findByScenarioVersionIdOrderByEvaluatedAtDesc(
                                scenarioVersionId
                        );

        int newerIndex =
                indexOfEvaluation(
                        results,
                        newer.getEvaluationId()
                );

        int olderIndex =
                indexOfEvaluation(
                        results,
                        older.getEvaluationId()
                );

        assertTrue(
                newerIndex >= 0
        );

        assertTrue(
                olderIndex >= 0
        );

        assertTrue(
                newerIndex < olderIndex
        );
    }

    @Test
    void shouldFindEvaluationsByStatusOrderedByEvaluatedAtDescending() {

        String status =
                "TEST_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluation older =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                status,
                                false,
                                now.minusMinutes(3)
                        )
                );

        ScenarioEvaluation newer =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                status,
                                true,
                                now
                        )
                );

        List<ScenarioEvaluation> results =
                scenarioEvaluationRepository
                        .findByEvaluationStatusOrderByEvaluatedAtDesc(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getEvaluationId(),
                results.get(0)
                        .getEvaluationId()
        );

        assertEquals(
                older.getEvaluationId(),
                results.get(1)
                        .getEvaluationId()
        );
    }

    @Test
    void shouldFindMatchedEvaluationsOrderedByEvaluatedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluation older =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "MATCH_TEST",
                                true,
                                now.minusMinutes(2)
                        )
                );

        ScenarioEvaluation newer =
                scenarioEvaluationRepository.saveAndFlush(
                        createEvaluation(
                                "MATCH_TEST",
                                true,
                                now
                        )
                );

        List<ScenarioEvaluation> results =
                scenarioEvaluationRepository
                        .findByMatchedOrderByEvaluatedAtDesc(
                                true
                        );

        int newerIndex =
                indexOfEvaluation(
                        results,
                        newer.getEvaluationId()
                );

        int olderIndex =
                indexOfEvaluation(
                        results,
                        older.getEvaluationId()
                );

        assertTrue(
                newerIndex >= 0
        );

        assertTrue(
                olderIndex >= 0
        );

        assertTrue(
                newerIndex < olderIndex
        );
    }

    @Test
    void shouldPersistJsonEvaluationContext() {

        ScenarioEvaluation evaluation =
                createEvaluation(
                        "COMPLETED",
                        true,
                        LocalDateTime.now()
                );

        evaluation.setEvaluationContext(
                Map.of(
                        "source",
                        "scenario-engine",
                        "channel",
                        "integration-test",
                        "attempt",
                        1
                )
        );

        ScenarioEvaluation saved =
                scenarioEvaluationRepository.saveAndFlush(
                        evaluation
                );

        ScenarioEvaluation found =
                scenarioEvaluationRepository
                        .findByEvaluationId(
                                saved.getEvaluationId()
                        )
                        .orElseThrow();

        assertNotNull(
                found.getEvaluationContext()
        );

        assertEquals(
                "scenario-engine",
                found.getEvaluationContext()
                        .get("source")
        );

        assertEquals(
                "integration-test",
                found.getEvaluationContext()
                        .get("channel")
        );

        assertEquals(
                1,
                ((Number) found
                        .getEvaluationContext()
                        .get("attempt"))
                        .intValue()
        );
    }

    @Test
    void shouldAllowNullTransactionAndCustomer() {

        ScenarioEvaluation evaluation =
                createEvaluation(
                        "COMPLETED",
                        false,
                        LocalDateTime.now()
                );

        evaluation.setTransactionId(
                null
        );

        evaluation.setCustomerId(
                null
        );

        ScenarioEvaluation saved =
                scenarioEvaluationRepository.saveAndFlush(
                        evaluation
                );

        ScenarioEvaluation found =
                scenarioEvaluationRepository
                        .findByEvaluationId(
                                saved.getEvaluationId()
                        )
                        .orElseThrow();

        assertNull(
                found.getTransactionId()
        );

        assertNull(
                found.getCustomerId()
        );
    }

    private ScenarioEvaluation createEvaluation(
            String status,
            boolean matched,
            LocalDateTime evaluatedAt) {

        ScenarioEvaluation evaluation =
                new ScenarioEvaluation();

        evaluation.setScenarioId(
                scenarioId
        );

        evaluation.setScenarioVersionId(
                scenarioVersionId
        );

        evaluation.setTransactionId(
                null
        );

        evaluation.setCustomerId(
                null
        );

        evaluation.setEvaluationStatus(
                status
        );

        evaluation.setMatched(
                matched
        );

        evaluation.setRuleCount(
                (short) 5
        );

        evaluation.setMatchedRuleCount(
                matched
                        ? (short) 3
                        : (short) 0
        );

        evaluation.setRequiredEvidenceCount(
                (short) 2
        );

        evaluation.setAvailableEvidenceCount(
                matched
                        ? (short) 2
                        : (short) 1
        );

        evaluation.setConfidence(
                new BigDecimal("0.8750")
        );

        evaluation.setRiskContribution(
                new BigDecimal("0.6500")
        );

        evaluation.setEvaluatedAt(
                evaluatedAt
        );

        evaluation.setEvaluationDurationMs(
                125L
        );

        evaluation.setEvaluationContext(
                Map.of(
                        "source",
                        "integration-test"
                )
        );

        evaluation.setCreatedAt(
                LocalDateTime.now()
        );

        return evaluation;
    }

    private int indexOfEvaluation(
            List<ScenarioEvaluation> evaluations,
            UUID evaluationId) {

        for (int i = 0;
             i < evaluations.size();
             i++) {

            if (evaluationId.equals(
                    evaluations
                            .get(i)
                            .getEvaluationId())) {

                return i;
            }
        }

        return -1;
    }
}