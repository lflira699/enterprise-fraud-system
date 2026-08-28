package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DetectionScenarioRepositoryIntegrationTest {

    @Autowired
    private DetectionScenarioRepository repository;

    @Test
    void shouldSaveDetectionScenario() {

        DetectionScenario scenario =
                createScenario(
                        "V96-SAVE-" + UUID.randomUUID(),
                        "ATO Detection Scenario",
                        "Detect account takeover indicators",
                        "ATO",
                        "HIGH",
                        "ACTIVE",
                        "Fraud Operations",
                        1
                );

        scenario.setDescription(
                "Repository integration test scenario"
        );

        scenario.setCorrelationWindowMinutes(30);
        scenario.setMaximumExecutionTimeSeconds(60);
        scenario.setMinimumEvents(2);

        scenario.setMinimumConfidence(
                new BigDecimal("0.8500")
        );

        scenario.setMinimumEvidence(1);

        scenario.setRequiredRules(
                Map.of("rule", "ATO_DEVICE_CHANGE")
        );

        scenario.setRequiredVariables(
                Map.of("variable", "deviceFingerprint")
        );

        scenario.setEvidenceRequirements(
                Map.of("evidence", "DEVICE")
        );

        scenario.setExclusions(
                Map.of("excludeTrustedDevice", true)
        );

        scenario.setExceptions(
                Map.of("manualReview", true)
        );

        scenario.setSuggestedActions(
                Map.of("action", "REVIEW")
        );

        scenario.setRelatedScenarios(
                Map.of("scenario", "ATO")
        );

        scenario.setConfigurationContext(
                Map.of("source", "V96_TEST")
        );

        DetectionScenario saved =
                repository.saveAndFlush(scenario);

        assertNotNull(saved.getScenarioId());

        assertEquals(
                scenario.getScenarioCode(),
                saved.getScenarioCode()
        );

        assertEquals(
                "ATO Detection Scenario",
                saved.getScenarioName()
        );

        assertEquals(
                "ATO",
                saved.getCategory()
        );

        assertEquals(
                "HIGH",
                saved.getCriticality()
        );

        assertEquals(
                "ACTIVE",
                saved.getStatus()
        );

        assertEquals(
                "Fraud Operations",
                saved.getOwner()
        );

        assertEquals(
                1,
                saved.getVersion()
        );

        assertEquals(
                new BigDecimal("0.8500"),
                saved.getMinimumConfidence()
        );

        assertEquals(
                "ATO_DEVICE_CHANGE",
                saved.getRequiredRules().get("rule")
        );

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldFindScenarioByScenarioId() {

        DetectionScenario saved =
                repository.saveAndFlush(
                        createScenario(
                                "V96-ID-" + UUID.randomUUID(),
                                "Scenario By Id",
                                "Validate lookup by scenario identifier",
                                "ATO",
                                "MEDIUM",
                                "ACTIVE",
                                "Detection Team",
                                1
                        )
                );

        DetectionScenario result =
                repository
                        .findByScenarioId(
                                saved.getScenarioId()
                        )
                        .orElseThrow();

        assertEquals(
                saved.getScenarioId(),
                result.getScenarioId()
        );

        assertEquals(
                saved.getScenarioCode(),
                result.getScenarioCode()
        );
    }

    @Test
    void shouldFindScenarioByCodeAndVersion() {

        String scenarioCode =
                "V96-CODE-" + UUID.randomUUID();

        repository.saveAndFlush(
                createScenario(
                        scenarioCode,
                        "Version One",
                        "Scenario version one",
                        "TRANSACTION",
                        "MEDIUM",
                        "ACTIVE",
                        "Risk Team",
                        1
                )
        );

        repository.saveAndFlush(
                createScenario(
                        scenarioCode,
                        "Version Two",
                        "Scenario version two",
                        "TRANSACTION",
                        "HIGH",
                        "ACTIVE",
                        "Risk Team",
                        2
                )
        );

        DetectionScenario result =
                repository
                        .findByScenarioCodeAndVersion(
                                scenarioCode,
                                2
                        )
                        .orElseThrow();

        assertEquals(
                scenarioCode,
                result.getScenarioCode()
        );

        assertEquals(
                2,
                result.getVersion()
        );

        assertEquals(
                "Version Two",
                result.getScenarioName()
        );
    }

    @Test
    void shouldFindScenariosByCodeOrderedByVersionDesc() {

        String scenarioCode =
                "V96-VERSION-" + UUID.randomUUID();

        repository.save(
                createScenario(
                        scenarioCode,
                        "Version One",
                        "First version",
                        "BEHAVIORAL",
                        "LOW",
                        "ACTIVE",
                        "Detection Team",
                        1
                )
        );

        repository.save(
                createScenario(
                        scenarioCode,
                        "Version Three",
                        "Third version",
                        "BEHAVIORAL",
                        "HIGH",
                        "ACTIVE",
                        "Detection Team",
                        3
                )
        );

        repository.save(
                createScenario(
                        scenarioCode,
                        "Version Two",
                        "Second version",
                        "BEHAVIORAL",
                        "MEDIUM",
                        "ACTIVE",
                        "Detection Team",
                        2
                )
        );

        repository.flush();

        List<DetectionScenario> result =
                repository
                        .findByScenarioCodeOrderByVersionDesc(
                                scenarioCode
                        );

        assertEquals(3, result.size());

        assertEquals(
                3,
                result.get(0).getVersion()
        );

        assertEquals(
                2,
                result.get(1).getVersion()
        );

        assertEquals(
                1,
                result.get(2).getVersion()
        );
    }

    @Test
    void shouldFindScenariosByCategoryOrderedByScenarioNameAsc() {

        String category =
                "V96_CATEGORY_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        repository.save(
                createScenario(
                        "V96-CAT-A-" + UUID.randomUUID(),
                        "Zulu Scenario",
                        "Category ordering test",
                        category,
                        "MEDIUM",
                        "ACTIVE",
                        "Detection Team",
                        1
                )
        );

        repository.save(
                createScenario(
                        "V96-CAT-B-" + UUID.randomUUID(),
                        "Alpha Scenario",
                        "Category ordering test",
                        category,
                        "MEDIUM",
                        "ACTIVE",
                        "Detection Team",
                        1
                )
        );

        repository.flush();

        List<DetectionScenario> result =
                repository
                        .findByCategoryOrderByScenarioNameAsc(
                                category
                        );

        assertEquals(2, result.size());

        assertEquals(
                "Alpha Scenario",
                result.get(0).getScenarioName()
        );

        assertEquals(
                "Zulu Scenario",
                result.get(1).getScenarioName()
        );
    }

    @Test
    void shouldFindScenariosByStatusOrderedByScenarioNameAsc() {

        String status =
                "V96_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        repository.save(
                createScenario(
                        "V96-STATUS-A-" + UUID.randomUUID(),
                        "Zulu Status Scenario",
                        "Status ordering test",
                        "ATO",
                        "HIGH",
                        status,
                        "Fraud Team",
                        1
                )
        );

        repository.save(
                createScenario(
                        "V96-STATUS-B-" + UUID.randomUUID(),
                        "Alpha Status Scenario",
                        "Status ordering test",
                        "ATO",
                        "HIGH",
                        status,
                        "Fraud Team",
                        1
                )
        );

        repository.flush();

        List<DetectionScenario> result =
                repository
                        .findByStatusOrderByScenarioNameAsc(
                                status
                        );

        assertEquals(2, result.size());

        assertEquals(
                "Alpha Status Scenario",
                result.get(0).getScenarioName()
        );

        assertEquals(
                "Zulu Status Scenario",
                result.get(1).getScenarioName()
        );
    }

    @Test
    void shouldFindScenariosByCriticalityOrderedByScenarioNameAsc() {

        String criticality =
                "V96_CRIT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        repository.save(
                createScenario(
                        "V96-CRIT-A-" + UUID.randomUUID(),
                        "Zulu Criticality Scenario",
                        "Criticality ordering test",
                        "KYC",
                        criticality,
                        "ACTIVE",
                        "Risk Team",
                        1
                )
        );

        repository.save(
                createScenario(
                        "V96-CRIT-B-" + UUID.randomUUID(),
                        "Alpha Criticality Scenario",
                        "Criticality ordering test",
                        "KYC",
                        criticality,
                        "ACTIVE",
                        "Risk Team",
                        1
                )
        );

        repository.flush();

        List<DetectionScenario> result =
                repository
                        .findByCriticalityOrderByScenarioNameAsc(
                                criticality
                        );

        assertEquals(2, result.size());

        assertEquals(
                "Alpha Criticality Scenario",
                result.get(0).getScenarioName()
        );

        assertEquals(
                "Zulu Criticality Scenario",
                result.get(1).getScenarioName()
        );
    }

    @Test
    void shouldFindScenariosByOwnerOrderedByScenarioNameAsc() {

        String owner =
                "V96_OWNER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        repository.save(
                createScenario(
                        "V96-OWNER-A-" + UUID.randomUUID(),
                        "Zulu Owner Scenario",
                        "Owner ordering test",
                        "DEVICE",
                        "MEDIUM",
                        "ACTIVE",
                        owner,
                        1
                )
        );

        repository.save(
                createScenario(
                        "V96-OWNER-B-" + UUID.randomUUID(),
                        "Alpha Owner Scenario",
                        "Owner ordering test",
                        "DEVICE",
                        "MEDIUM",
                        "ACTIVE",
                        owner,
                        1
                )
        );

        repository.flush();

        List<DetectionScenario> result =
                repository
                        .findByOwnerOrderByScenarioNameAsc(
                                owner
                        );

        assertEquals(2, result.size());

        assertEquals(
                "Alpha Owner Scenario",
                result.get(0).getScenarioName()
        );

        assertEquals(
                "Zulu Owner Scenario",
                result.get(1).getScenarioName()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioCodeAndVersionDoNotExist() {

        assertTrue(
                repository
                        .findByScenarioCodeAndVersion(
                                "V96-NOT-FOUND-" + UUID.randomUUID(),
                                999
                        )
                        .isEmpty()
        );
    }

    private DetectionScenario createScenario(
            String scenarioCode,
            String scenarioName,
            String objective,
            String category,
            String criticality,
            String status,
            String owner,
            Integer version) {

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(scenarioCode);
        scenario.setScenarioName(scenarioName);
        scenario.setObjective(objective);
        scenario.setCategory(category);
        scenario.setCriticality(criticality);
        scenario.setStatus(status);
        scenario.setOwner(owner);
        scenario.setVersion(version);

        LocalDateTime now =
                LocalDateTime.now();

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

        return scenario;
    }
}