package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DetectionScenarioServiceIntegrationTest {

    @Autowired
    private DetectionScenarioServiceInterface detectionScenarioService;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Test
    void createScenarioShouldPersistAndMapProvidedValues() {

        String scenarioCode =
                "DS-SVC-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioRequest request =
                buildRequest(
                        scenarioCode,
                        "ATO Service Scenario",
                        3
                );

        request.setDescription(
                "Detection scenario service integration test"
        );

        request.setCorrelationWindowMinutes(
                30
        );

        request.setMaximumExecutionTimeSeconds(
                60
        );

        request.setMinimumEvents(
                2
        );

        request.setMinimumConfidence(
                new BigDecimal("0.8500")
        );

        request.setMinimumEvidence(
                1
        );

        request.setRequiredRules(
                Map.of(
                        "rule",
                        "ATO_DEVICE_CHANGE"
                )
        );

        request.setRequiredVariables(
                Map.of(
                        "variable",
                        "deviceFingerprint"
                )
        );

        request.setEvidenceRequirements(
                Map.of(
                        "evidence",
                        "DEVICE"
                )
        );

        request.setExclusions(
                Map.of(
                        "excludeTrustedDevice",
                        true
                )
        );

        request.setExceptions(
                Map.of(
                        "manualReview",
                        true
                )
        );

        request.setSuggestedActions(
                Map.of(
                        "action",
                        "REVIEW"
                )
        );

        request.setRelatedScenarios(
                Map.of(
                        "scenario",
                        "ATO"
                )
        );

        request.setConfigurationContext(
                Map.of(
                        "source",
                        "SERVICE_TEST"
                )
        );

        DetectionScenarioResponse response =
                detectionScenarioService
                        .createScenario(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getScenarioId()
        );

        assertEquals(
                scenarioCode,
                response.getScenarioCode()
        );

        assertEquals(
                "ATO Service Scenario",
                response.getScenarioName()
        );

        assertEquals(
                "Detect account takeover indicators",
                response.getObjective()
        );

        assertEquals(
                "Detection scenario service integration test",
                response.getDescription()
        );

        assertEquals(
                "ATO",
                response.getCategory()
        );

        assertEquals(
                "HIGH",
                response.getCriticality()
        );

        assertEquals(
                "ACTIVE",
                response.getStatus()
        );

        assertEquals(
                "Detection Team",
                response.getOwner()
        );

        assertEquals(
                3,
                response.getVersion()
        );

        assertEquals(
                30,
                response.getCorrelationWindowMinutes()
        );

        assertEquals(
                60,
                response.getMaximumExecutionTimeSeconds()
        );

        assertEquals(
                2,
                response.getMinimumEvents()
        );

        assertEquals(
                new BigDecimal("0.8500"),
                response.getMinimumConfidence()
        );

        assertEquals(
                1,
                response.getMinimumEvidence()
        );

        assertEquals(
                "ATO_DEVICE_CHANGE",
                response.getRequiredRules()
                        .get("rule")
        );

        assertEquals(
                "deviceFingerprint",
                response.getRequiredVariables()
                        .get("variable")
        );

        assertEquals(
                "DEVICE",
                response.getEvidenceRequirements()
                        .get("evidence")
        );

        assertEquals(
                true,
                response.getExclusions()
                        .get("excludeTrustedDevice")
        );

        assertEquals(
                true,
                response.getExceptions()
                        .get("manualReview")
        );

        assertEquals(
                "REVIEW",
                response.getSuggestedActions()
                        .get("action")
        );

        assertEquals(
                "ATO",
                response.getRelatedScenarios()
                        .get("scenario")
        );

        assertEquals(
                "SERVICE_TEST",
                response.getConfigurationContext()
                        .get("source")
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );

        assertTrue(
                detectionScenarioRepository.existsById(
                        response.getScenarioId()
                )
        );
    }

    @Test
    void createScenarioShouldDefaultVersionToOneWhenNotProvided() {

        DetectionScenarioRequest request =
                buildRequest(
                        "DS-DEFAULT-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Default Version Scenario",
                        null
                );

        DetectionScenarioResponse response =
                detectionScenarioService
                        .createScenario(
                                request
                        );

        assertNotNull(
                response.getScenarioId()
        );

        assertEquals(
                1,
                response.getVersion()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );
    }

    @Test
    void getScenarioByIdShouldReturnExistingScenario() {

        DetectionScenarioResponse created =
                detectionScenarioService
                        .createScenario(
                                buildRequest(
                                        "DS-ID-" +
                                                UUID.randomUUID()
                                                        .toString()
                                                        .substring(0, 8),
                                        "Scenario By Id",
                                        1
                                )
                        );

        DetectionScenarioResponse found =
                detectionScenarioService
                        .getScenarioById(
                                created.getScenarioId()
                        );

        assertEquals(
                created.getScenarioId(),
                found.getScenarioId()
        );

        assertEquals(
                created.getScenarioCode(),
                found.getScenarioCode()
        );

        assertEquals(
                created.getScenarioName(),
                found.getScenarioName()
        );

        assertEquals(
                1,
                found.getVersion()
        );
    }

    @Test
    void getScenarioByIdShouldThrowWhenScenarioDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> detectionScenarioService
                        .getScenarioById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getScenarioByCodeAndVersionShouldReturnMatchingScenario() {

        String scenarioCode =
                "DS-CODE-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        detectionScenarioService.createScenario(
                buildRequest(
                        scenarioCode,
                        "Version One",
                        1
                )
        );

        DetectionScenarioResponse versionTwo =
                detectionScenarioService
                        .createScenario(
                                buildRequest(
                                        scenarioCode,
                                        "Version Two",
                                        2
                                )
                        );

        DetectionScenarioResponse found =
                detectionScenarioService
                        .getScenarioByCodeAndVersion(
                                scenarioCode,
                                2
                        );

        assertEquals(
                versionTwo.getScenarioId(),
                found.getScenarioId()
        );

        assertEquals(
                scenarioCode,
                found.getScenarioCode()
        );

        assertEquals(
                2,
                found.getVersion()
        );

        assertEquals(
                "Version Two",
                found.getScenarioName()
        );
    }

    @Test
    void getScenarioByCodeAndVersionShouldThrowWhenScenarioDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> detectionScenarioService
                        .getScenarioByCodeAndVersion(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8),
                                999
                        )
        );
    }

    @Test
    void getScenariosByCodeShouldReturnMatchingScenarios() {

        String scenarioCode =
                "DS-VERS-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioResponse first =
                detectionScenarioService
                        .createScenario(
                                buildRequest(
                                        scenarioCode,
                                        "Version One",
                                        1
                                )
                        );

        DetectionScenarioResponse second =
                detectionScenarioService
                        .createScenario(
                                buildRequest(
                                        scenarioCode,
                                        "Version Two",
                                        2
                                )
                        );

        List<DetectionScenarioResponse> results =
                detectionScenarioService
                        .getScenariosByCode(
                                scenarioCode
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenario(
                        results,
                        first.getScenarioId()
                )
        );

        assertTrue(
                containsScenario(
                        results,
                        second.getScenarioId()
                )
        );
    }

    @Test
    void getScenariosByCategoryShouldReturnMatchingScenarios() {

        String category =
                "CAT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioRequest firstRequest =
                buildRequest(
                        "DS-CAT-A-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Category Scenario A",
                        1
                );

        firstRequest.setCategory(
                category
        );

        DetectionScenarioResponse first =
                detectionScenarioService
                        .createScenario(
                                firstRequest
                        );

        DetectionScenarioRequest secondRequest =
                buildRequest(
                        "DS-CAT-B-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Category Scenario B",
                        1
                );

        secondRequest.setCategory(
                category
        );

        DetectionScenarioResponse second =
                detectionScenarioService
                        .createScenario(
                                secondRequest
                        );

        List<DetectionScenarioResponse> results =
                detectionScenarioService
                        .getScenariosByCategory(
                                category
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenario(
                        results,
                        first.getScenarioId()
                )
        );

        assertTrue(
                containsScenario(
                        results,
                        second.getScenarioId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                category.equals(
                                        result.getCategory()
                                )
                        )
        );
    }

    @Test
    void getScenariosByStatusShouldReturnMatchingScenarios() {

        String status =
                "ST_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioRequest firstRequest =
                buildRequest(
                        "DS-ST-A-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Status Scenario A",
                        1
                );

        firstRequest.setStatus(
                status
        );

        DetectionScenarioResponse first =
                detectionScenarioService
                        .createScenario(
                                firstRequest
                        );

        DetectionScenarioRequest secondRequest =
                buildRequest(
                        "DS-ST-B-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Status Scenario B",
                        1
                );

        secondRequest.setStatus(
                status
        );

        DetectionScenarioResponse second =
                detectionScenarioService
                        .createScenario(
                                secondRequest
                        );

        List<DetectionScenarioResponse> results =
                detectionScenarioService
                        .getScenariosByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenario(
                        results,
                        first.getScenarioId()
                )
        );

        assertTrue(
                containsScenario(
                        results,
                        second.getScenarioId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getStatus()
                                )
                        )
        );
    }

    @Test
    void getScenariosByCriticalityShouldReturnMatchingScenarios() {

        String criticality =
                "CRIT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioRequest firstRequest =
                buildRequest(
                        "DS-CR-A-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Criticality Scenario A",
                        1
                );

        firstRequest.setCriticality(
                criticality
        );

        DetectionScenarioResponse first =
                detectionScenarioService
                        .createScenario(
                                firstRequest
                        );

        DetectionScenarioRequest secondRequest =
                buildRequest(
                        "DS-CR-B-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Criticality Scenario B",
                        1
                );

        secondRequest.setCriticality(
                criticality
        );

        DetectionScenarioResponse second =
                detectionScenarioService
                        .createScenario(
                                secondRequest
                        );

        List<DetectionScenarioResponse> results =
                detectionScenarioService
                        .getScenariosByCriticality(
                                criticality
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenario(
                        results,
                        first.getScenarioId()
                )
        );

        assertTrue(
                containsScenario(
                        results,
                        second.getScenarioId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                criticality.equals(
                                        result.getCriticality()
                                )
                        )
        );
    }

    @Test
    void getScenariosByOwnerShouldReturnMatchingScenarios() {

        String owner =
                "OWNER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DetectionScenarioRequest firstRequest =
                buildRequest(
                        "DS-OWN-A-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Owner Scenario A",
                        1
                );

        firstRequest.setOwner(
                owner
        );

        DetectionScenarioResponse first =
                detectionScenarioService
                        .createScenario(
                                firstRequest
                        );

        DetectionScenarioRequest secondRequest =
                buildRequest(
                        "DS-OWN-B-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8),
                        "Owner Scenario B",
                        1
                );

        secondRequest.setOwner(
                owner
        );

        DetectionScenarioResponse second =
                detectionScenarioService
                        .createScenario(
                                secondRequest
                        );

        List<DetectionScenarioResponse> results =
                detectionScenarioService
                        .getScenariosByOwner(
                                owner
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsScenario(
                        results,
                        first.getScenarioId()
                )
        );

        assertTrue(
                containsScenario(
                        results,
                        second.getScenarioId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                owner.equals(
                                        result.getOwner()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                detectionScenarioService
                        .getScenariosByCode(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                detectionScenarioService
                        .getScenariosByCategory(
                                "UNKNOWN_CATEGORY_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                detectionScenarioService
                        .getScenariosByStatus(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                detectionScenarioService
                        .getScenariosByCriticality(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                detectionScenarioService
                        .getScenariosByOwner(
                                "UNKNOWN_OWNER_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private DetectionScenarioRequest buildRequest(
            String scenarioCode,
            String scenarioName,
            Integer version) {

        DetectionScenarioRequest request =
                new DetectionScenarioRequest();

        request.setScenarioCode(
                scenarioCode
        );

        request.setScenarioName(
                scenarioName
        );

        request.setObjective(
                "Detect account takeover indicators"
        );

        request.setCategory(
                "ATO"
        );

        request.setCriticality(
                "HIGH"
        );

        request.setStatus(
                "ACTIVE"
        );

        request.setOwner(
                "Detection Team"
        );

        request.setVersion(
                version
        );

        return request;
    }

    private boolean containsScenario(
            List<DetectionScenarioResponse> results,
            UUID scenarioId) {

        return results.stream()
                .anyMatch(result ->
                        scenarioId.equals(
                                result.getScenarioId()
                        )
                );
    }
}