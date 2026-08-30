package com.efs.modules.detection.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

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
class DetectionScenarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateScenarioWithFullPayload()
            throws Exception {

        String scenarioCode =
                uniqueValue("DS-FULL");

        Map<String, Object> request =
                validScenarioRequest(
                        scenarioCode,
                        "ATO Controller Scenario",
                        3
                );

        request.put(
                "description",
                "Detection scenario controller integration test"
        );

        request.put(
                "correlationWindowMinutes",
                30
        );

        request.put(
                "maximumExecutionTimeSeconds",
                60
        );

        request.put(
                "minimumEvents",
                2
        );

        request.put(
                "minimumConfidence",
                0.8500
        );

        request.put(
                "minimumEvidence",
                1
        );

        request.put(
                "requiredRules",
                Map.of(
                        "rule",
                        "ATO_DEVICE_CHANGE"
                )
        );

        request.put(
                "requiredVariables",
                Map.of(
                        "variable",
                        "deviceFingerprint"
                )
        );

        request.put(
                "evidenceRequirements",
                Map.of(
                        "evidence",
                        "DEVICE"
                )
        );

        request.put(
                "exclusions",
                Map.of(
                        "excludeTrustedDevice",
                        true
                )
        );

        request.put(
                "exceptions",
                Map.of(
                        "manualReview",
                        true
                )
        );

        request.put(
                "suggestedActions",
                Map.of(
                        "action",
                        "REVIEW"
                )
        );

        request.put(
                "relatedScenarios",
                Map.of(
                        "scenario",
                        "ATO"
                )
        );

        request.put(
                "configurationContext",
                Map.of(
                        "source",
                        "CONTROLLER_TEST"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenarios"
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
                        jsonPath("$.scenarioId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioCode")
                                .value(scenarioCode)
                )
                .andExpect(
                        jsonPath("$.scenarioName")
                                .value("ATO Controller Scenario")
                )
                .andExpect(
                        jsonPath("$.objective")
                                .value(
                                        "Detect account takeover indicators"
                                )
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Detection scenario controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.category")
                                .value("ATO")
                )
                .andExpect(
                        jsonPath("$.criticality")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.owner")
                                .value("DetectionTeam")
                )
                .andExpect(
                        jsonPath("$.version")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.correlationWindowMinutes")
                                .value(30)
                )
                .andExpect(
                        jsonPath("$.maximumExecutionTimeSeconds")
                                .value(60)
                )
                .andExpect(
                        jsonPath("$.minimumEvents")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.minimumConfidence")
                                .value(0.85)
                )
                .andExpect(
                        jsonPath("$.minimumEvidence")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.requiredRules.rule")
                                .value("ATO_DEVICE_CHANGE")
                )
                .andExpect(
                        jsonPath("$.requiredVariables.variable")
                                .value("deviceFingerprint")
                )
                .andExpect(
                        jsonPath(
                                "$.evidenceRequirements.evidence"
                        )
                                .value("DEVICE")
                )
                .andExpect(
                        jsonPath(
                                "$.exclusions.excludeTrustedDevice"
                        )
                                .value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.exceptions.manualReview"
                        )
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.suggestedActions.action")
                                .value("REVIEW")
                )
                .andExpect(
                        jsonPath("$.relatedScenarios.scenario")
                                .value("ATO")
                )
                .andExpect(
                        jsonPath("$.configurationContext.source")
                                .value("CONTROLLER_TEST")
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
    void shouldDefaultVersionToOneWhenNotProvided()
            throws Exception {

        String scenarioCode =
                uniqueValue("DS-DEFAULT");

        Map<String, Object> request =
                validScenarioRequest(
                        scenarioCode,
                        "Default Version Scenario",
                        null
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenarios"
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
                        jsonPath("$.scenarioId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioCode")
                                .value(scenarioCode)
                )
                .andExpect(
                        jsonPath("$.version")
                                .value(1)
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

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioCode",
                ""
        );

        request.put(
                "scenarioName",
                ""
        );

        request.put(
                "objective",
                ""
        );

        request.put(
                "category",
                ""
        );

        request.put(
                "status",
                ""
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenarios"
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
                                "$.validationErrors.scenarioCode"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.scenarioName"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.objective"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.category"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.status"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioById()
            throws Exception {

        String scenarioCode =
                uniqueValue("DS-ID");

        JsonNode created =
                createScenario(
                        scenarioCode,
                        "Scenario By Id",
                        1
                );

        UUID scenarioId =
                UUID.fromString(
                        created.get(
                                "scenarioId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioCode")
                                .value(scenarioCode)
                )
                .andExpect(
                        jsonPath("$.scenarioName")
                                .value("Scenario By Id")
                )
                .andExpect(
                        jsonPath("$.version")
                                .value(1)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenario()
            throws Exception {

        UUID unknownScenarioId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/{scenarioId}",
                                unknownScenarioId
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
    void shouldGetScenarioByCodeAndVersion()
            throws Exception {

        String scenarioCode =
                uniqueValue("DS-VERSION");

        createScenario(
                scenarioCode,
                "Version One",
                1
        );

        JsonNode versionTwo =
                createScenario(
                        scenarioCode,
                        "Version Two",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/code/{scenarioCode}/version/{version}",
                                scenarioCode,
                                2
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(
                                        versionTwo.get(
                                                "scenarioId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.scenarioCode")
                                .value(scenarioCode)
                )
                .andExpect(
                        jsonPath("$.scenarioName")
                                .value("Version Two")
                )
                .andExpect(
                        jsonPath("$.version")
                                .value(2)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCodeAndVersion()
            throws Exception {

        String scenarioCode =
                uniqueValue("UNKNOWN");

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/code/{scenarioCode}/version/{version}",
                                scenarioCode,
                                999
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void shouldGetScenariosByCode()
            throws Exception {

        String scenarioCode =
                uniqueValue("DS-CODE");

        JsonNode first =
                createScenario(
                        scenarioCode,
                        "Version One",
                        1
                );

        JsonNode second =
                createScenario(
                        scenarioCode,
                        "Version Two",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/code/{scenarioCode}",
                                scenarioCode
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetScenariosByCategory()
            throws Exception {

        String category =
                uniqueValue("CAT");

        JsonNode first =
                createScenario(
                        uniqueValue("DS-CAT-A"),
                        "Category Scenario A",
                        1,
                        category,
                        "ACTIVE",
                        "HIGH",
                        "DetectionTeam"
                );

        JsonNode second =
                createScenario(
                        uniqueValue("DS-CAT-B"),
                        "Category Scenario B",
                        1,
                        category,
                        "ACTIVE",
                        "HIGH",
                        "DetectionTeam"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/category/{category}",
                                category
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].category")
                                .value(
                                        hasItem(category)
                                )
                );
    }

    @Test
    void shouldGetScenariosByStatus()
            throws Exception {

        String statusValue =
                uniqueValue("STATUS");

        JsonNode first =
                createScenario(
                        uniqueValue("DS-ST-A"),
                        "Status Scenario A",
                        1,
                        "ATO",
                        statusValue,
                        "HIGH",
                        "DetectionTeam"
                );

        JsonNode second =
                createScenario(
                        uniqueValue("DS-ST-B"),
                        "Status Scenario B",
                        1,
                        "ATO",
                        statusValue,
                        "HIGH",
                        "DetectionTeam"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/status/{status}",
                                statusValue
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].status")
                                .value(
                                        hasItem(statusValue)
                                )
                );
    }

    @Test
    void shouldGetScenariosByCriticality()
            throws Exception {

        String criticality =
                uniqueValue("CRIT");

        JsonNode first =
                createScenario(
                        uniqueValue("DS-CR-A"),
                        "Criticality Scenario A",
                        1,
                        "ATO",
                        "ACTIVE",
                        criticality,
                        "DetectionTeam"
                );

        JsonNode second =
                createScenario(
                        uniqueValue("DS-CR-B"),
                        "Criticality Scenario B",
                        1,
                        "ATO",
                        "ACTIVE",
                        criticality,
                        "DetectionTeam"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/criticality/{criticality}",
                                criticality
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].criticality")
                                .value(
                                        hasItem(criticality)
                                )
                );
    }

    @Test
    void shouldGetScenariosByOwner()
            throws Exception {

        String owner =
                uniqueValue("OWNER");

        JsonNode first =
                createScenario(
                        uniqueValue("DS-OWN-A"),
                        "Owner Scenario A",
                        1,
                        "ATO",
                        "ACTIVE",
                        "HIGH",
                        owner
                );

        JsonNode second =
                createScenario(
                        uniqueValue("DS-OWN-B"),
                        "Owner Scenario B",
                        1,
                        "ATO",
                        "ACTIVE",
                        "HIGH",
                        owner
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/owner/{owner}",
                                owner
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].owner")
                                .value(
                                        hasItem(owner)
                                )
                );
    }

    private JsonNode createScenario(
            String scenarioCode,
            String scenarioName,
            Integer version)
            throws Exception {

        return createScenario(
                scenarioCode,
                scenarioName,
                version,
                "ATO",
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );
    }

    private JsonNode createScenario(
            String scenarioCode,
            String scenarioName,
            Integer version,
            String category,
            String statusValue,
            String criticality,
            String owner)
            throws Exception {

        Map<String, Object> request =
                validScenarioRequest(
                        scenarioCode,
                        scenarioName,
                        version
                );

        request.put(
                "category",
                category
        );

        request.put(
                "status",
                statusValue
        );

        request.put(
                "criticality",
                criticality
        );

        request.put(
                "owner",
                owner
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenarios"
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

    private Map<String, Object> validScenarioRequest(
            String scenarioCode,
            String scenarioName,
            Integer version) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioCode",
                scenarioCode
        );

        request.put(
                "scenarioName",
                scenarioName
        );

        request.put(
                "objective",
                "Detect account takeover indicators"
        );

        request.put(
                "category",
                "ATO"
        );

        request.put(
                "criticality",
                "HIGH"
        );

        request.put(
                "status",
                "ACTIVE"
        );

        request.put(
                "owner",
                "DetectionTeam"
        );

        if (version != null) {
            request.put(
                    "version",
                    version
            );
        }

        return request;
    }

    private String uniqueValue(
            String prefix) {

        return prefix +
                "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(
                                0,
                                8
                        );
    }
}