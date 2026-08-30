package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioVersionRequest;
import com.efs.modules.detection.dto.ScenarioVersionResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
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
class ScenarioVersionServiceIntegrationTest {

    @Autowired
    private ScenarioVersionServiceInterface scenarioVersionService;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    private UUID scenarioId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SV-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Version Service Test"
        );

        scenario.setObjective(
                "Validate scenario version service behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioVersionServiceIntegrationTest"
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
    }

    @Test
    void createScenarioVersionShouldPersistAndMapProvidedValues() {

        LocalDateTime effectiveFrom =
                LocalDateTime.now().minusHours(1);

        LocalDateTime effectiveTo =
                LocalDateTime.now().plusDays(30);

        ScenarioVersionRequest request =
                buildRequest(
                        1,
                        "ACTIVE",
                        "AUTOMATIC"
                );

        request.setMaximumProcessingTimeMs(
                2500
        );

        request.setMinimumEvents(
                3
        );

        request.setMinimumConfidence(
                new BigDecimal("0.7500")
        );

        request.setConfiguration(
                Map.of(
                        "strategy",
                        "CORRELATION",
                        "enabled",
                        true
                )
        );

        request.setEffectiveFrom(
                effectiveFrom
        );

        request.setEffectiveTo(
                effectiveTo
        );

        ScenarioVersionResponse response =
                scenarioVersionService
                        .createScenarioVersion(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getScenarioVersionId()
        );

        assertEquals(
                scenarioId,
                response.getScenarioId()
        );

        assertEquals(
                Integer.valueOf(1),
                response.getVersionNumber()
        );

        assertEquals(
                "ACTIVE",
                response.getVersionStatus()
        );

        assertEquals(
                Long.valueOf(1800L),
                response.getCorrelationWindowSeconds()
        );

        assertEquals(
                Integer.valueOf(2500),
                response.getMaximumProcessingTimeMs()
        );

        assertEquals(
                Integer.valueOf(3),
                response.getMinimumEvents()
        );

        assertEquals(
                0,
                new BigDecimal("0.7500")
                        .compareTo(
                                response.getMinimumConfidence()
                        )
        );

        assertEquals(
                "AUTOMATIC",
                response.getActivationMode()
        );

        assertNotNull(
                response.getConfiguration()
        );

        assertEquals(
                "CORRELATION",
                response.getConfiguration()
                        .get("strategy")
        );

        assertEquals(
                true,
                response.getConfiguration()
                        .get("enabled")
        );

        assertEquals(
                effectiveFrom,
                response.getEffectiveFrom()
        );

        assertEquals(
                effectiveTo,
                response.getEffectiveTo()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );

        assertTrue(
                scenarioVersionRepository.existsById(
                        response.getScenarioVersionId()
                )
        );
    }

    @Test
    void createScenarioVersionShouldAllowOptionalFieldsToBeNull() {

        ScenarioVersionRequest request =
                buildRequest(
                        1,
                        "DRAFT",
                        "MANUAL"
                );

        ScenarioVersionResponse response =
                scenarioVersionService
                        .createScenarioVersion(
                                request
                        );

        assertNotNull(
                response.getScenarioVersionId()
        );

        assertEquals(
                scenarioId,
                response.getScenarioId()
        );

        assertEquals(
                Integer.valueOf(1),
                response.getVersionNumber()
        );

        assertEquals(
                "DRAFT",
                response.getVersionStatus()
        );

        assertEquals(
                Long.valueOf(1800L),
                response.getCorrelationWindowSeconds()
        );

        assertEquals(
                "MANUAL",
                response.getActivationMode()
        );

        assertNull(
                response.getMaximumProcessingTimeMs()
        );

        assertNull(
                response.getMinimumEvents()
        );

        assertNull(
                response.getMinimumConfidence()
        );

        assertNull(
                response.getConfiguration()
        );

        assertNull(
                response.getEffectiveFrom()
        );

        assertNull(
                response.getEffectiveTo()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );
    }

    @Test
    void getScenarioVersionByIdShouldReturnExistingVersion() {

        ScenarioVersionResponse created =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        1,
                                        "ACTIVE",
                                        "AUTOMATIC"
                                )
                        );

        ScenarioVersionResponse found =
                scenarioVersionService
                        .getScenarioVersionById(
                                created.getScenarioVersionId()
                        );

        assertEquals(
                created.getScenarioVersionId(),
                found.getScenarioVersionId()
        );

        assertEquals(
                scenarioId,
                found.getScenarioId()
        );

        assertEquals(
                Integer.valueOf(1),
                found.getVersionNumber()
        );

        assertEquals(
                "ACTIVE",
                found.getVersionStatus()
        );

        assertEquals(
                "AUTOMATIC",
                found.getActivationMode()
        );
    }

    @Test
    void getScenarioVersionByIdShouldThrowWhenVersionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioVersionService
                        .getScenarioVersionById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getScenarioVersionsByScenarioShouldReturnMatchingVersions() {

        ScenarioVersionResponse first =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        1,
                                        "ACTIVE",
                                        "AUTOMATIC"
                                )
                        );

        ScenarioVersionResponse second =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        2,
                                        "DRAFT",
                                        "MANUAL"
                                )
                        );

        List<ScenarioVersionResponse> results =
                scenarioVersionService
                        .getScenarioVersionsByScenario(
                                scenarioId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsVersion(
                        results,
                        first.getScenarioVersionId()
                )
        );

        assertTrue(
                containsVersion(
                        results,
                        second.getScenarioVersionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                scenarioId.equals(
                                        result.getScenarioId()
                                )
                        )
        );
    }

    @Test
    void getScenarioVersionByNumberShouldReturnMatchingVersion() {

        scenarioVersionService
                .createScenarioVersion(
                        buildRequest(
                                1,
                                "ACTIVE",
                                "AUTOMATIC"
                        )
                );

        ScenarioVersionResponse expected =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        2,
                                        "DRAFT",
                                        "MANUAL"
                                )
                        );

        ScenarioVersionResponse found =
                scenarioVersionService
                        .getScenarioVersionByNumber(
                                scenarioId,
                                2
                        );

        assertEquals(
                expected.getScenarioVersionId(),
                found.getScenarioVersionId()
        );

        assertEquals(
                scenarioId,
                found.getScenarioId()
        );

        assertEquals(
                Integer.valueOf(2),
                found.getVersionNumber()
        );

        assertEquals(
                "DRAFT",
                found.getVersionStatus()
        );
    }

    @Test
    void getScenarioVersionByNumberShouldThrowWhenVersionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioVersionService
                        .getScenarioVersionByNumber(
                                scenarioId,
                                999
                        )
        );
    }

    @Test
    void getScenarioVersionsByStatusShouldReturnMatchingVersions() {

        String status =
                "STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioVersionResponse first =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        1,
                                        status,
                                        "AUTOMATIC"
                                )
                        );

        ScenarioVersionResponse second =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        2,
                                        status,
                                        "MANUAL"
                                )
                        );

        List<ScenarioVersionResponse> results =
                scenarioVersionService
                        .getScenarioVersionsByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsVersion(
                        results,
                        first.getScenarioVersionId()
                )
        );

        assertTrue(
                containsVersion(
                        results,
                        second.getScenarioVersionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getVersionStatus()
                                )
                        )
        );
    }

    @Test
    void getScenarioVersionsByActivationModeShouldReturnMatchingVersions() {

        String activationMode =
                "MODE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioVersionResponse first =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        1,
                                        "ACTIVE",
                                        activationMode
                                )
                        );

        ScenarioVersionResponse second =
                scenarioVersionService
                        .createScenarioVersion(
                                buildRequest(
                                        2,
                                        "DRAFT",
                                        activationMode
                                )
                        );

        List<ScenarioVersionResponse> results =
                scenarioVersionService
                        .getScenarioVersionsByActivationMode(
                                activationMode
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsVersion(
                        results,
                        first.getScenarioVersionId()
                )
        );

        assertTrue(
                containsVersion(
                        results,
                        second.getScenarioVersionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                activationMode.equals(
                                        result.getActivationMode()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                scenarioVersionService
                        .getScenarioVersionsByScenario(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioVersionService
                        .getScenarioVersionsByStatus(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioVersionService
                        .getScenarioVersionsByActivationMode(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private ScenarioVersionRequest buildRequest(
            Integer versionNumber,
            String versionStatus,
            String activationMode) {

        ScenarioVersionRequest request =
                new ScenarioVersionRequest();

        request.setScenarioId(
                scenarioId
        );

        request.setVersionNumber(
                versionNumber
        );

        request.setVersionStatus(
                versionStatus
        );

        request.setCorrelationWindowSeconds(
                1800L
        );

        request.setActivationMode(
                activationMode
        );

        return request;
    }

    private boolean containsVersion(
            List<ScenarioVersionResponse> results,
            UUID scenarioVersionId) {

        return results.stream()
                .anyMatch(result ->
                        scenarioVersionId.equals(
                                result.getScenarioVersionId()
                        )
                );
    }
}