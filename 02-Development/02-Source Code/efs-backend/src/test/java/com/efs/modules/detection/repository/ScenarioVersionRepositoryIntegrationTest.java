package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
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
class ScenarioVersionRepositoryIntegrationTest {

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Test
    void shouldSaveScenarioVersion() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-SAVE-" + UUID.randomUUID()
                );

        LocalDateTime effectiveFrom =
                LocalDateTime.now()
                        .minusMinutes(5);

        LocalDateTime effectiveTo =
                effectiveFrom.plusDays(30);

        ScenarioVersion version =
                createScenarioVersion(
                        scenario.getScenarioId(),
                        1,
                        "ACTIVE",
                        1800L,
                        "AUTOMATIC"
                );

        version.setMaximumProcessingTimeMs(5000);
        version.setMinimumEvents(2);

        version.setMinimumConfidence(
                new BigDecimal("0.8500")
        );

        version.setConfiguration(
                Map.of(
                        "source", "V97_TEST",
                        "enabled", true
                )
        );

        version.setEffectiveFrom(effectiveFrom);
        version.setEffectiveTo(effectiveTo);

        ScenarioVersion saved =
                scenarioVersionRepository
                        .saveAndFlush(version);

        assertNotNull(saved.getScenarioVersionId());

        assertEquals(
                scenario.getScenarioId(),
                saved.getScenarioId()
        );

        assertEquals(
                1,
                saved.getVersionNumber()
        );

        assertEquals(
                "ACTIVE",
                saved.getVersionStatus()
        );

        assertEquals(
                1800L,
                saved.getCorrelationWindowSeconds()
        );

        assertEquals(
                5000,
                saved.getMaximumProcessingTimeMs()
        );

        assertEquals(
                2,
                saved.getMinimumEvents()
        );

        assertEquals(
                new BigDecimal("0.8500"),
                saved.getMinimumConfidence()
        );

        assertEquals(
                "AUTOMATIC",
                saved.getActivationMode()
        );

        assertEquals(
                "V97_TEST",
                saved.getConfiguration().get("source")
        );

        assertEquals(
                true,
                saved.getConfiguration().get("enabled")
        );

        assertEquals(
                effectiveFrom,
                saved.getEffectiveFrom()
        );

        assertEquals(
                effectiveTo,
                saved.getEffectiveTo()
        );

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldFindScenarioVersionById() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-ID-" + UUID.randomUUID()
                );

        ScenarioVersion saved =
                scenarioVersionRepository
                        .saveAndFlush(
                                createScenarioVersion(
                                        scenario.getScenarioId(),
                                        1,
                                        "ACTIVE",
                                        900L,
                                        "AUTOMATIC"
                                )
                        );

        ScenarioVersion result =
                scenarioVersionRepository
                        .findByScenarioVersionId(
                                saved.getScenarioVersionId()
                        )
                        .orElseThrow();

        assertEquals(
                saved.getScenarioVersionId(),
                result.getScenarioVersionId()
        );

        assertEquals(
                scenario.getScenarioId(),
                result.getScenarioId()
        );

        assertEquals(
                1,
                result.getVersionNumber()
        );
    }

    @Test
    void shouldFindScenarioVersionsByScenarioOrderedByVersionNumberDesc() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-ORDER-" + UUID.randomUUID()
                );

        scenarioVersionRepository.save(
                createScenarioVersion(
                        scenario.getScenarioId(),
                        1,
                        "INACTIVE",
                        900L,
                        "MANUAL"
                )
        );

        scenarioVersionRepository.save(
                createScenarioVersion(
                        scenario.getScenarioId(),
                        3,
                        "ACTIVE",
                        1800L,
                        "AUTOMATIC"
                )
        );

        scenarioVersionRepository.save(
                createScenarioVersion(
                        scenario.getScenarioId(),
                        2,
                        "INACTIVE",
                        1200L,
                        "MANUAL"
                )
        );

        scenarioVersionRepository.flush();

        List<ScenarioVersion> result =
                scenarioVersionRepository
                        .findByScenarioIdOrderByVersionNumberDesc(
                                scenario.getScenarioId()
                        );

        assertEquals(3, result.size());

        assertEquals(
                3,
                result.get(0).getVersionNumber()
        );

        assertEquals(
                2,
                result.get(1).getVersionNumber()
        );

        assertEquals(
                1,
                result.get(2).getVersionNumber()
        );
    }

    @Test
    void shouldFindScenarioVersionByScenarioAndVersionNumber() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-NUMBER-" + UUID.randomUUID()
                );

        scenarioVersionRepository.save(
                createScenarioVersion(
                        scenario.getScenarioId(),
                        1,
                        "INACTIVE",
                        900L,
                        "MANUAL"
                )
        );

        scenarioVersionRepository.save(
                createScenarioVersion(
                        scenario.getScenarioId(),
                        2,
                        "ACTIVE",
                        1800L,
                        "AUTOMATIC"
                )
        );

        scenarioVersionRepository.flush();

        ScenarioVersion result =
                scenarioVersionRepository
                        .findByScenarioIdAndVersionNumber(
                                scenario.getScenarioId(),
                                2
                        )
                        .orElseThrow();

        assertEquals(
                scenario.getScenarioId(),
                result.getScenarioId()
        );

        assertEquals(
                2,
                result.getVersionNumber()
        );

        assertEquals(
                "ACTIVE",
                result.getVersionStatus()
        );

        assertEquals(
                "AUTOMATIC",
                result.getActivationMode()
        );
    }

    @Test
    void shouldFindScenarioVersionsByStatusOrderedByCreatedAtDesc() {

        String status =
                "V97_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-STATUS-" + UUID.randomUUID()
                );

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioVersion older =
                createScenarioVersion(
                        scenario.getScenarioId(),
                        1,
                        status,
                        900L,
                        "MANUAL"
                );

        older.setCreatedAt(
                now.minusHours(2)
        );

        older.setUpdatedAt(
                now.minusHours(2)
        );

        ScenarioVersion newer =
                createScenarioVersion(
                        scenario.getScenarioId(),
                        2,
                        status,
                        1800L,
                        "AUTOMATIC"
                );

        newer.setCreatedAt(
                now.minusHours(1)
        );

        newer.setUpdatedAt(
                now.minusHours(1)
        );

        scenarioVersionRepository.save(older);
        scenarioVersionRepository.save(newer);
        scenarioVersionRepository.flush();

        List<ScenarioVersion> result =
                scenarioVersionRepository
                        .findByVersionStatusOrderByCreatedAtDesc(
                                status
                        );

        assertEquals(2, result.size());

        assertEquals(
                2,
                result.get(0).getVersionNumber()
        );

        assertEquals(
                1,
                result.get(1).getVersionNumber()
        );
    }

    @Test
    void shouldFindScenarioVersionsByActivationModeOrderedByCreatedAtDesc() {

        String activationMode =
                "V97_MODE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-MODE-" + UUID.randomUUID()
                );

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioVersion older =
                createScenarioVersion(
                        scenario.getScenarioId(),
                        1,
                        "ACTIVE",
                        900L,
                        activationMode
                );

        older.setCreatedAt(
                now.minusHours(2)
        );

        older.setUpdatedAt(
                now.minusHours(2)
        );

        ScenarioVersion newer =
                createScenarioVersion(
                        scenario.getScenarioId(),
                        2,
                        "ACTIVE",
                        1800L,
                        activationMode
                );

        newer.setCreatedAt(
                now.minusHours(1)
        );

        newer.setUpdatedAt(
                now.minusHours(1)
        );

        scenarioVersionRepository.save(older);
        scenarioVersionRepository.save(newer);
        scenarioVersionRepository.flush();

        List<ScenarioVersion> result =
                scenarioVersionRepository
                        .findByActivationModeOrderByCreatedAtDesc(
                                activationMode
                        );

        assertEquals(2, result.size());

        assertEquals(
                2,
                result.get(0).getVersionNumber()
        );

        assertEquals(
                1,
                result.get(1).getVersionNumber()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioVersionDoesNotExist() {

        assertTrue(
                scenarioVersionRepository
                        .findByScenarioVersionId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyWhenScenarioAndVersionNumberDoNotExist() {

        DetectionScenario scenario =
                createAndSaveScenario(
                        "V97-EMPTY-" + UUID.randomUUID()
                );

        assertTrue(
                scenarioVersionRepository
                        .findByScenarioIdAndVersionNumber(
                                scenario.getScenarioId(),
                                999
                        )
                        .isEmpty()
        );
    }

    private DetectionScenario createAndSaveScenario(
            String scenarioCode) {

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(scenarioCode);
        scenario.setScenarioName(
                "V97 Scenario"
        );

        scenario.setObjective(
                "Scenario Version repository integration test"
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

    private ScenarioVersion createScenarioVersion(
            UUID scenarioId,
            Integer versionNumber,
            String versionStatus,
            Long correlationWindowSeconds,
            String activationMode) {

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(scenarioId);
        version.setVersionNumber(versionNumber);
        version.setVersionStatus(versionStatus);
        version.setCorrelationWindowSeconds(
                correlationWindowSeconds
        );

        version.setActivationMode(
                activationMode
        );

        LocalDateTime now =
                LocalDateTime.now();

        version.setCreatedAt(now);
        version.setUpdatedAt(now);

        return version;
    }
}