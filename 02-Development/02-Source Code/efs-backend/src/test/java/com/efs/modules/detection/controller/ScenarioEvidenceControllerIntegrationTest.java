package com.efs.modules.detection.controller;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScenarioEvidenceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    private UUID scenarioVersionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SEV-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evidence Controller Test"
        );

        scenario.setObjective(
                "Validate scenario evidence controller behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioEvidenceControllerIntegrationTest"
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

        ScenarioVersion scenarioVersion =
                new ScenarioVersion();

        scenarioVersion.setScenarioId(
                savedScenario.getScenarioId()
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
    void shouldCreateScenarioEvidenceWithFullPayload()
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        "DEVICE",
                        "INVESTIGATION_TOOL"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evidence"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.evidenceId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.evidenceType")
                                .value("DEVICE")
                )
                .andExpect(
                        jsonPath("$.sourceType")
                                .value("INVESTIGATION_TOOL")
                )
                .andExpect(
                        jsonPath("$.sourceReference")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.evidenceValue.knownDevice")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.evidenceValue.deviceAgeDays")
                                .value(240)
                )
                .andExpect(
                        jsonPath("$.evidenceSummary")
                                .value(
                                        "Known customer device observed during investigation"
                                )
                )
                .andExpect(
                        jsonPath("$.confidence")
                                .value(0.925)
                )
                .andExpect(
                        jsonPath("$.observedAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateScenarioEvidenceWithOnlyRequiredFields()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "BEHAVIORAL",
                        "MANUAL_REVIEW"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evidence"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.evidenceId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.evidenceType")
                                .value("BEHAVIORAL")
                )
                .andExpect(
                        jsonPath("$.sourceType")
                                .value("MANUAL_REVIEW")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evidence"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.scenarioVersionId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.evidenceType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.sourceType"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenSizedFieldsExceedMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "E".repeat(41),
                        "S".repeat(51)
                );

        request.put(
                "sourceReference",
                "R".repeat(251)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evidence"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.evidenceType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.sourceType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.sourceReference"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioEvidenceById()
            throws Exception {

        JsonNode created =
                createEvidence(
                        "DEVICE",
                        "INVESTIGATION_TOOL"
                );

        UUID evidenceId =
                UUID.fromString(
                        created.get(
                                "evidenceId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evidence/{evidenceId}",
                                evidenceId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.evidenceId")
                                .value(evidenceId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.evidenceType")
                                .value("DEVICE")
                )
                .andExpect(
                        jsonPath("$.sourceType")
                                .value("INVESTIGATION_TOOL")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioEvidence()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evidence/{evidenceId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "CUSTOMER_RESOURCE_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldGetEvidenceByScenarioVersion()
            throws Exception {

        JsonNode first =
                createEvidence(
                        "DEVICE",
                        "INVESTIGATION_TOOL"
                );

        JsonNode second =
                createEvidence(
                        "IP",
                        "MANUAL_REVIEW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evidence/scenario-version/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvidenceByType()
            throws Exception {

        String evidenceType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createEvidence(
                        evidenceType,
                        "SOURCE_A"
                );

        JsonNode second =
                createEvidence(
                        evidenceType,
                        "SOURCE_B"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evidence/type/{evidenceType}",
                                evidenceType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evidenceType")
                                .value(
                                        hasItem(
                                                evidenceType
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvidenceBySourceType()
            throws Exception {

        String sourceType =
                "SRC_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createEvidence(
                        "DEVICE",
                        sourceType
                );

        JsonNode second =
                createEvidence(
                        "BEHAVIORAL",
                        sourceType
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evidence/source-type/{sourceType}",
                                sourceType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evidenceId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evidenceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].sourceType")
                                .value(
                                        hasItem(
                                                sourceType
                                        )
                                )
                );
    }

    private JsonNode createEvidence(
            String evidenceType,
            String sourceType)
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        evidenceType,
                        sourceType
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-evidence"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                request
                                                        )
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> fullRequest(
            String evidenceType,
            String sourceType) {

        Map<String, Object> request =
                requiredRequest(
                        evidenceType,
                        sourceType
                );

        request.put(
                "sourceReference",
                "DEVICE-" + UUID.randomUUID()
        );

        request.put(
                "evidenceValue",
                Map.of(
                        "knownDevice", true,
                        "deviceAgeDays", 240
                )
        );

        request.put(
                "evidenceSummary",
                "Known customer device observed during investigation"
        );

        request.put(
                "confidence",
                new BigDecimal("0.9250")
        );

        request.put(
                "observedAt",
                LocalDateTime.now()
                        .minusMinutes(5)
        );

        return request;
    }

    private Map<String, Object> requiredRequest(
            String evidenceType,
            String sourceType) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioVersionId",
                scenarioVersionId
        );

        request.put(
                "evidenceType",
                evidenceType
        );

        request.put(
                "sourceType",
                sourceType
        );

        return request;
    }
}