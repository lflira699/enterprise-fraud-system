package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioEvidence;
import com.efs.modules.detection.entity.ScenarioVersion;
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
class ScenarioEvidenceRepositoryIntegrationTest {

    @Autowired
    private ScenarioEvidenceRepository scenarioEvidenceRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Test
    void shouldSaveScenarioEvidence() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-SAVE-" + UUID.randomUUID()
                );

        LocalDateTime observedAt =
                LocalDateTime.now().minusMinutes(5);

        ScenarioEvidence evidence =
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "DEVICE",
                        "INTERNAL_TOOL",
                        observedAt
                );

        evidence.setSourceReference(
                "DEVICE-REFERENCE-001"
        );

        evidence.setEvidenceValue(
                Map.of(
                        "deviceFingerprint", "trusted-device",
                        "historicalMatch", true
                )
        );

        evidence.setEvidenceSummary(
                "Device evidence repository integration test"
        );

        evidence.setConfidence(
                new BigDecimal("0.9500")
        );

        ScenarioEvidence saved =
                scenarioEvidenceRepository
                        .saveAndFlush(evidence);

        assertNotNull(saved.getEvidenceId());

        assertEquals(
                scenarioVersion.getScenarioVersionId(),
                saved.getScenarioVersionId()
        );

        assertEquals(
                "DEVICE",
                saved.getEvidenceType()
        );

        assertEquals(
                "INTERNAL_TOOL",
                saved.getSourceType()
        );

        assertEquals(
                "DEVICE-REFERENCE-001",
                saved.getSourceReference()
        );

        assertEquals(
                "trusted-device",
                saved.getEvidenceValue()
                        .get("deviceFingerprint")
        );

        assertEquals(
                true,
                saved.getEvidenceValue()
                        .get("historicalMatch")
        );

        assertEquals(
                "Device evidence repository integration test",
                saved.getEvidenceSummary()
        );

        assertEquals(
                new BigDecimal("0.9500"),
                saved.getConfidence()
        );

        assertEquals(
                observedAt,
                saved.getObservedAt()
        );

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void shouldFindScenarioEvidenceById() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-ID-" + UUID.randomUUID()
                );

        ScenarioEvidence saved =
                scenarioEvidenceRepository
                        .saveAndFlush(
                                createEvidence(
                                        scenarioVersion.getScenarioVersionId(),
                                        "IP",
                                        "RISK_ENGINE",
                                        LocalDateTime.now()
                                )
                        );

        ScenarioEvidence result =
                scenarioEvidenceRepository
                        .findByEvidenceId(
                                saved.getEvidenceId()
                        )
                        .orElseThrow();

        assertEquals(
                saved.getEvidenceId(),
                result.getEvidenceId()
        );

        assertEquals(
                scenarioVersion.getScenarioVersionId(),
                result.getScenarioVersionId()
        );

        assertEquals(
                "IP",
                result.getEvidenceType()
        );
    }

    @Test
    void shouldFindEvidenceByScenarioVersionOrderedByObservedAtDesc() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-VERSION-" + UUID.randomUUID()
                );

        LocalDateTime now =
                LocalDateTime.now();

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "DEVICE",
                        "INTERNAL_TOOL",
                        now.minusHours(3)
                )
        );

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "IP",
                        "RISK_ENGINE",
                        now.minusHours(1)
                )
        );

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "BEHAVIORAL",
                        "DETECTION_ENGINE",
                        now.minusHours(2)
                )
        );

        scenarioEvidenceRepository.flush();

        List<ScenarioEvidence> result =
                scenarioEvidenceRepository
                        .findByScenarioVersionIdOrderByObservedAtDesc(
                                scenarioVersion.getScenarioVersionId()
                        );

        assertEquals(3, result.size());

        assertEquals(
                "IP",
                result.get(0).getEvidenceType()
        );

        assertEquals(
                "BEHAVIORAL",
                result.get(1).getEvidenceType()
        );

        assertEquals(
                "DEVICE",
                result.get(2).getEvidenceType()
        );
    }

    @Test
    void shouldFindEvidenceByTypeOrderedByObservedAtDesc() {

        String evidenceType =
                "V99_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-TYPE-" + UUID.randomUUID()
                );

        LocalDateTime now =
                LocalDateTime.now();

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        evidenceType,
                        "SOURCE_A",
                        now.minusHours(2)
                )
        );

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        evidenceType,
                        "SOURCE_B",
                        now.minusHours(1)
                )
        );

        scenarioEvidenceRepository.flush();

        List<ScenarioEvidence> result =
                scenarioEvidenceRepository
                        .findByEvidenceTypeOrderByObservedAtDesc(
                                evidenceType
                        );

        assertEquals(2, result.size());

        assertEquals(
                "SOURCE_B",
                result.get(0).getSourceType()
        );

        assertEquals(
                "SOURCE_A",
                result.get(1).getSourceType()
        );

        assertTrue(
                result.get(0)
                        .getObservedAt()
                        .isAfter(
                                result.get(1)
                                        .getObservedAt()
                        )
        );
    }

    @Test
    void shouldFindEvidenceBySourceTypeOrderedByObservedAtDesc() {

        String sourceType =
                "V99_SOURCE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-SOURCE-" + UUID.randomUUID()
                );

        LocalDateTime now =
                LocalDateTime.now();

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "DEVICE",
                        sourceType,
                        now.minusMinutes(30)
                )
        );

        scenarioEvidenceRepository.save(
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "NETWORK",
                        sourceType,
                        now.minusMinutes(10)
                )
        );

        scenarioEvidenceRepository.flush();

        List<ScenarioEvidence> result =
                scenarioEvidenceRepository
                        .findBySourceTypeOrderByObservedAtDesc(
                                sourceType
                        );

        assertEquals(2, result.size());

        assertEquals(
                "NETWORK",
                result.get(0).getEvidenceType()
        );

        assertEquals(
                "DEVICE",
                result.get(1).getEvidenceType()
        );
    }

    @Test
    void shouldPersistJsonbEvidenceValue() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-JSON-" + UUID.randomUUID()
                );

        ScenarioEvidence evidence =
                createEvidence(
                        scenarioVersion.getScenarioVersionId(),
                        "BEHAVIORAL",
                        "DETECTION_ENGINE",
                        LocalDateTime.now()
                );

        evidence.setEvidenceValue(
                Map.of(
                        "velocityCount", 7,
                        "knownDevice", false,
                        "riskIndicator", "HIGH"
                )
        );

        ScenarioEvidence saved =
                scenarioEvidenceRepository
                        .saveAndFlush(evidence);

        ScenarioEvidence result =
                scenarioEvidenceRepository
                        .findByEvidenceId(
                                saved.getEvidenceId()
                        )
                        .orElseThrow();

        assertNotNull(result.getEvidenceValue());

        assertEquals(
                7,
                result.getEvidenceValue()
                        .get("velocityCount")
        );

        assertEquals(
                false,
                result.getEvidenceValue()
                        .get("knownDevice")
        );

        assertEquals(
                "HIGH",
                result.getEvidenceValue()
                        .get("riskIndicator")
        );
    }

    @Test
    void shouldReturnEmptyWhenEvidenceDoesNotExist() {

        assertTrue(
                scenarioEvidenceRepository
                        .findByEvidenceId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioVersionHasNoEvidence() {

        ScenarioVersion scenarioVersion =
                createAndSaveScenarioVersion(
                        "V99-EMPTY-" + UUID.randomUUID()
                );

        List<ScenarioEvidence> result =
                scenarioEvidenceRepository
                        .findByScenarioVersionIdOrderByObservedAtDesc(
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
                "V99 Scenario"
        );

        scenario.setObjective(
                "Scenario Evidence repository integration test"
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
            String scenarioCode) {

        DetectionScenario scenario =
                createAndSaveScenario(
                        scenarioCode
                );

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(
                scenario.getScenarioId()
        );

        version.setVersionNumber(1);
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

    private ScenarioEvidence createEvidence(
            UUID scenarioVersionId,
            String evidenceType,
            String sourceType,
            LocalDateTime observedAt) {

        ScenarioEvidence evidence =
                new ScenarioEvidence();

        evidence.setScenarioVersionId(
                scenarioVersionId
        );

        evidence.setEvidenceType(
                evidenceType
        );

        evidence.setSourceType(
                sourceType
        );

        evidence.setObservedAt(
                observedAt
        );

        evidence.setCreatedAt(
                LocalDateTime.now()
        );

        return evidence;
    }
}