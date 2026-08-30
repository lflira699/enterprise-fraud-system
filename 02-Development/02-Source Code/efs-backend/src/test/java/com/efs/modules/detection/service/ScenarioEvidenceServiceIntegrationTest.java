package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvidenceRequest;
import com.efs.modules.detection.dto.ScenarioEvidenceResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioEvidenceRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioEvidenceServiceIntegrationTest {

    @Autowired
    private ScenarioEvidenceServiceInterface scenarioEvidenceService;

    @Autowired
    private ScenarioEvidenceRepository scenarioEvidenceRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    private UUID scenarioId;
    private UUID scenarioVersionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SEV-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evidence Service Test"
        );

        scenario.setObjective(
                "Validate scenario evidence service behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioEvidenceServiceIntegrationTest"
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
    void createScenarioEvidenceShouldPersistAndMapProvidedValues() {

        LocalDateTime observedAt =
                LocalDateTime.now().minusMinutes(5);

        ScenarioEvidenceRequest request =
                buildRequest(
                        "DEVICE",
                        "INVESTIGATION_TOOL"
                );

        request.setSourceReference(
                "DEVICE-" + UUID.randomUUID()
        );

        request.setEvidenceValue(
                Map.of(
                        "knownDevice", true,
                        "deviceAgeDays", 240
                )
        );

        request.setEvidenceSummary(
                "Known customer device observed during investigation"
        );

        request.setConfidence(
                new BigDecimal("0.9250")
        );

        request.setObservedAt(
                observedAt
        );

        ScenarioEvidenceResponse response =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getEvidenceId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertEquals(
                "DEVICE",
                response.getEvidenceType()
        );

        assertEquals(
                "INVESTIGATION_TOOL",
                response.getSourceType()
        );

        assertEquals(
                request.getSourceReference(),
                response.getSourceReference()
        );

        assertNotNull(
                response.getEvidenceValue()
        );

        assertEquals(
                true,
                response.getEvidenceValue()
                        .get("knownDevice")
        );

        assertEquals(
                240,
                ((Number) response.getEvidenceValue()
                        .get("deviceAgeDays"))
                        .intValue()
        );

        assertEquals(
                "Known customer device observed during investigation",
                response.getEvidenceSummary()
        );

        assertEquals(
                0,
                new BigDecimal("0.9250")
                        .compareTo(
                                response.getConfidence()
                        )
        );

        assertEquals(
                observedAt,
                response.getObservedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                scenarioEvidenceRepository.existsById(
                        response.getEvidenceId()
                )
        );
    }

    @Test
    void createScenarioEvidenceShouldAllowOptionalFieldsToBeNull() {

        ScenarioEvidenceRequest request =
                buildRequest(
                        "BEHAVIORAL",
                        "MANUAL_REVIEW"
                );

        ScenarioEvidenceResponse response =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                request
                        );

        assertNotNull(
                response.getEvidenceId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertEquals(
                "BEHAVIORAL",
                response.getEvidenceType()
        );

        assertEquals(
                "MANUAL_REVIEW",
                response.getSourceType()
        );

        assertNull(
                response.getSourceReference()
        );

        assertNull(
                response.getEvidenceValue()
        );

        assertNull(
                response.getEvidenceSummary()
        );

        assertNull(
                response.getConfidence()
        );

        assertNull(
                response.getObservedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getScenarioEvidenceByIdShouldReturnExistingEvidence() {

        ScenarioEvidenceResponse created =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        "DEVICE",
                                        "INVESTIGATION_TOOL"
                                )
                        );

        ScenarioEvidenceResponse found =
                scenarioEvidenceService
                        .getScenarioEvidenceById(
                                created.getEvidenceId()
                        );

        assertEquals(
                created.getEvidenceId(),
                found.getEvidenceId()
        );

        assertEquals(
                scenarioVersionId,
                found.getScenarioVersionId()
        );

        assertEquals(
                "DEVICE",
                found.getEvidenceType()
        );

        assertEquals(
                "INVESTIGATION_TOOL",
                found.getSourceType()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void getScenarioEvidenceByIdShouldThrowWhenEvidenceDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvidenceService
                        .getScenarioEvidenceById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getEvidenceByScenarioVersionShouldReturnMatchingEvidence() {

        ScenarioEvidenceResponse first =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        "DEVICE",
                                        "INVESTIGATION_TOOL"
                                )
                        );

        ScenarioEvidenceResponse second =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        "IP",
                                        "MANUAL_REVIEW"
                                )
                        );

        List<ScenarioEvidenceResponse> results =
                scenarioEvidenceService
                        .getEvidenceByScenarioVersion(
                                scenarioVersionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvidence(
                        results,
                        first.getEvidenceId()
                )
        );

        assertTrue(
                containsEvidence(
                        results,
                        second.getEvidenceId()
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
    void getEvidenceByTypeShouldReturnMatchingEvidence() {

        String evidenceType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioEvidenceResponse first =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        evidenceType,
                                        "SOURCE_A"
                                )
                        );

        ScenarioEvidenceResponse second =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        evidenceType,
                                        "SOURCE_B"
                                )
                        );

        List<ScenarioEvidenceResponse> results =
                scenarioEvidenceService
                        .getEvidenceByType(
                                evidenceType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvidence(
                        results,
                        first.getEvidenceId()
                )
        );

        assertTrue(
                containsEvidence(
                        results,
                        second.getEvidenceId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                evidenceType.equals(
                                        result.getEvidenceType()
                                )
                        )
        );
    }

    @Test
    void getEvidenceBySourceTypeShouldReturnMatchingEvidence() {

        String sourceType =
                "SRC_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioEvidenceResponse first =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        "DEVICE",
                                        sourceType
                                )
                        );

        ScenarioEvidenceResponse second =
                scenarioEvidenceService
                        .createScenarioEvidence(
                                buildRequest(
                                        "BEHAVIORAL",
                                        sourceType
                                )
                        );

        List<ScenarioEvidenceResponse> results =
                scenarioEvidenceService
                        .getEvidenceBySourceType(
                                sourceType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvidence(
                        results,
                        first.getEvidenceId()
                )
        );

        assertTrue(
                containsEvidence(
                        results,
                        second.getEvidenceId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                sourceType.equals(
                                        result.getSourceType()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                scenarioEvidenceService
                        .getEvidenceByScenarioVersion(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvidenceService
                        .getEvidenceByType(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvidenceService
                        .getEvidenceBySourceType(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private ScenarioEvidenceRequest buildRequest(
            String evidenceType,
            String sourceType) {

        ScenarioEvidenceRequest request =
                new ScenarioEvidenceRequest();

        request.setScenarioVersionId(
                scenarioVersionId
        );

        request.setEvidenceType(
                evidenceType
        );

        request.setSourceType(
                sourceType
        );

        return request;
    }

    private boolean containsEvidence(
            List<ScenarioEvidenceResponse> results,
            UUID evidenceId) {

        return results.stream()
                .anyMatch(result ->
                        evidenceId.equals(
                                result.getEvidenceId()
                        )
                );
    }
}