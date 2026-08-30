package com.efs.modules.detection.controller;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
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
class ScenarioVersionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                "SV-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Version Controller Test"
        );

        scenario.setObjective(
                "Validate scenario version controller behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioVersionControllerIntegrationTest"
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
    void shouldCreateScenarioVersionWithFullPayload()
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        1,
                        "ACTIVE",
                        "AUTOMATIC"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-versions"
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
                        jsonPath("$.scenarioVersionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.versionStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.correlationWindowSeconds")
                                .value(1800)
                )
                .andExpect(
                        jsonPath("$.maximumProcessingTimeMs")
                                .value(2500)
                )
                .andExpect(
                        jsonPath("$.minimumEvents")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.minimumConfidence")
                                .value(0.75)
                )
                .andExpect(
                        jsonPath("$.activationMode")
                                .value("AUTOMATIC")
                )
                .andExpect(
                        jsonPath("$.configuration.strategy")
                                .value("CORRELATION")
                )
                .andExpect(
                        jsonPath("$.configuration.enabled")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.effectiveFrom")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.effectiveTo")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateScenarioVersionWithOnlyRequiredFields()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        1,
                        "DRAFT",
                        "MANUAL"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-versions"
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
                        jsonPath("$.scenarioVersionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.versionStatus")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$.correlationWindowSeconds")
                                .value(1800)
                )
                .andExpect(
                        jsonPath("$.activationMode")
                                .value("MANUAL")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-versions"
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
                                "$.validationErrors.scenarioId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.versionNumber"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.versionStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.correlationWindowSeconds"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.activationMode"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenSizedFieldsExceedMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        1,
                        "S".repeat(31),
                        "M".repeat(31)
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-versions"
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
                                "$.validationErrors.versionStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.activationMode"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioVersionById()
            throws Exception {

        JsonNode created =
                createVersion(
                        1,
                        "ACTIVE",
                        "AUTOMATIC"
                );

        UUID scenarioVersionId =
                UUID.fromString(
                        created.get(
                                "scenarioVersionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.versionStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.activationMode")
                                .value("AUTOMATIC")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioVersion()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/{scenarioVersionId}",
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
    void shouldGetScenarioVersionsByScenario()
            throws Exception {

        JsonNode first =
                createVersion(
                        1,
                        "ACTIVE",
                        "AUTOMATIC"
                );

        JsonNode second =
                createVersion(
                        2,
                        "DRAFT",
                        "MANUAL"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/scenario/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetScenarioVersionByNumber()
            throws Exception {

        createVersion(
                1,
                "ACTIVE",
                "AUTOMATIC"
        );

        JsonNode expected =
                createVersion(
                        2,
                        "DRAFT",
                        "MANUAL"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/scenario/{scenarioId}/version/{versionNumber}",
                                scenarioId,
                                2
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        expected.get(
                                                "scenarioVersionId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.versionStatus")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$.activationMode")
                                .value("MANUAL")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioVersionNumber()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/scenario/{scenarioId}/version/{versionNumber}",
                                scenarioId,
                                999
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
    void shouldGetScenarioVersionsByStatus()
            throws Exception {

        String versionStatus =
                "STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createVersion(
                        1,
                        versionStatus,
                        "AUTOMATIC"
                );

        JsonNode second =
                createVersion(
                        2,
                        versionStatus,
                        "MANUAL"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/status/{versionStatus}",
                                versionStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].versionStatus")
                                .value(
                                        hasItem(
                                                versionStatus
                                        )
                                )
                );
    }

    @Test
    void shouldGetScenarioVersionsByActivationMode()
            throws Exception {

        String activationMode =
                "MODE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createVersion(
                        1,
                        "ACTIVE",
                        activationMode
                );

        JsonNode second =
                createVersion(
                        2,
                        "DRAFT",
                        activationMode
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-versions/activation-mode/{activationMode}",
                                activationMode
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioVersionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioVersionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationMode")
                                .value(
                                        hasItem(
                                                activationMode
                                        )
                                )
                );
    }

    private JsonNode createVersion(
            Integer versionNumber,
            String versionStatus,
            String activationMode)
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        versionNumber,
                        versionStatus,
                        activationMode
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-versions"
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
            Integer versionNumber,
            String versionStatus,
            String activationMode) {

        Map<String, Object> request =
                requiredRequest(
                        versionNumber,
                        versionStatus,
                        activationMode
                );

        request.put(
                "maximumProcessingTimeMs",
                2500
        );

        request.put(
                "minimumEvents",
                3
        );

        request.put(
                "minimumConfidence",
                new BigDecimal("0.7500")
        );

        request.put(
                "configuration",
                Map.of(
                        "strategy",
                        "CORRELATION",
                        "enabled",
                        true
                )
        );

        request.put(
                "effectiveFrom",
                LocalDateTime.now()
                        .minusHours(1)
        );

        request.put(
                "effectiveTo",
                LocalDateTime.now()
                        .plusDays(30)
        );

        return request;
    }

    private Map<String, Object> requiredRequest(
            Integer versionNumber,
            String versionStatus,
            String activationMode) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioId",
                scenarioId
        );

        request.put(
                "versionNumber",
                versionNumber
        );

        request.put(
                "versionStatus",
                versionStatus
        );

        request.put(
                "correlationWindowSeconds",
                1800L
        );

        request.put(
                "activationMode",
                activationMode
        );

        return request;
    }
}