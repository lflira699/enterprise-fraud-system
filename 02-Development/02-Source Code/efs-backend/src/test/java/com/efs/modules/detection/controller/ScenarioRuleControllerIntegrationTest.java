package com.efs.modules.detection.controller;

import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.rules.entity.Rule;
import com.efs.modules.rules.repository.RuleRepository;
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
class ScenarioRuleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private RuleRepository ruleRepository;

    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID ruleId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SR-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Rule Controller Test"
        );

        scenario.setObjective(
                "Validate scenario rule controller behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioRuleControllerIntegrationTest"
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
                createScenarioVersion(
                        1
                );

        ScenarioVersion savedScenarioVersion =
                scenarioVersionRepository.saveAndFlush(
                        scenarioVersion
                );

        scenarioVersionId =
                savedScenarioVersion.getScenarioVersionId();

        Rule rule =
                createRule(
                        "SR-CTRL-RULE-" + UUID.randomUUID(),
                        (short) 1
                );

        Rule savedRule =
                ruleRepository.saveAndFlush(
                        rule
                );

        ruleId =
                savedRule.getRuleId();
    }

    @Test
    void shouldCreateScenarioRuleWithFullPayload()
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-rules"
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
                        jsonPath("$.scenarioRuleId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleId")
                                .value(ruleId.toString())
                )
                .andExpect(
                        jsonPath("$.ruleRole")
                                .value("PRIMARY")
                )
                .andExpect(
                        jsonPath("$.required")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.evaluationOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateScenarioRuleWithoutOptionalFields()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        scenarioVersionId,
                        ruleId,
                        false
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-rules"
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
                        jsonPath("$.scenarioRuleId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleId")
                                .value(ruleId.toString())
                )
                .andExpect(
                        jsonPath("$.required")
                                .value(false)
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
                                "/api/v1/detection/scenario-rules"
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
                                "$.validationErrors.ruleId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.required"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRuleRoleExceedsMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        scenarioVersionId,
                        ruleId,
                        true
                );

        request.put(
                "ruleRole",
                "R".repeat(31)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-rules"
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
                                "$.validationErrors.ruleRole"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioRuleById()
            throws Exception {

        JsonNode created =
                createScenarioRule(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        UUID scenarioRuleId =
                UUID.fromString(
                        created.get(
                                "scenarioRuleId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-rules/{scenarioRuleId}",
                                scenarioRuleId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scenarioRuleId")
                                .value(
                                        scenarioRuleId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleId")
                                .value(ruleId.toString())
                )
                .andExpect(
                        jsonPath("$.ruleRole")
                                .value("PRIMARY")
                )
                .andExpect(
                        jsonPath("$.required")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.evaluationOrder")
                                .value(1)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioRule()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-rules/{scenarioRuleId}",
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
    void shouldGetScenarioRulesByScenarioVersion()
            throws Exception {

        Rule secondRule =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-CTRL-RULE2-" + UUID.randomUUID(),
                                (short) 2
                        )
                );

        JsonNode first =
                createScenarioRule(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        JsonNode second =
                createScenarioRule(
                        scenarioVersionId,
                        secondRule.getRuleId(),
                        "SUPPORTING",
                        false,
                        (short) 2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-rules/scenario-version/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetScenarioRulesByRuleAcrossVersions()
            throws Exception {

        ScenarioVersion secondVersion =
                scenarioVersionRepository.saveAndFlush(
                        createScenarioVersion(
                                2
                        )
                );

        JsonNode first =
                createScenarioRule(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        JsonNode second =
                createScenarioRule(
                        secondVersion.getScenarioVersionId(),
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-rules/rule/{ruleId}",
                                ruleId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ruleId")
                                .value(
                                        hasItem(
                                                ruleId.toString()
                                        )
                                )
                );
    }

    @Test
    void shouldGetOnlyRequiredScenarioRules()
            throws Exception {

        Rule secondRequiredRule =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-CTRL-REQ2-" + UUID.randomUUID(),
                                (short) 2
                        )
                );

        Rule optionalRule =
                ruleRepository.saveAndFlush(
                        createRule(
                                "SR-CTRL-OPT-" + UUID.randomUUID(),
                                (short) 3
                        )
                );

        JsonNode firstRequired =
                createScenarioRule(
                        scenarioVersionId,
                        ruleId,
                        "PRIMARY",
                        true,
                        (short) 1
                );

        JsonNode secondRequired =
                createScenarioRule(
                        scenarioVersionId,
                        secondRequiredRule.getRuleId(),
                        "SUPPORTING",
                        true,
                        (short) 2
                );

        createScenarioRule(
                scenarioVersionId,
                optionalRule.getRuleId(),
                "OPTIONAL",
                false,
                (short) 3
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-rules/scenario-version/{scenarioVersionId}/required",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                firstRequired.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scenarioRuleId")
                                .value(
                                        hasItem(
                                                secondRequired.get(
                                                        "scenarioRuleId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].required")
                                .value(
                                        hasItem(true)
                                )
                );
    }

    private JsonNode createScenarioRule(
            UUID targetScenarioVersionId,
            UUID targetRuleId,
            String ruleRole,
            Boolean required,
            Short evaluationOrder)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        targetScenarioVersionId,
                        targetRuleId,
                        ruleRole,
                        required,
                        evaluationOrder
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-rules"
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
            UUID targetScenarioVersionId,
            UUID targetRuleId,
            String ruleRole,
            Boolean required,
            Short evaluationOrder) {

        Map<String, Object> request =
                requiredRequest(
                        targetScenarioVersionId,
                        targetRuleId,
                        required
                );

        if (ruleRole != null) {
            request.put(
                    "ruleRole",
                    ruleRole
            );
        }

        if (evaluationOrder != null) {
            request.put(
                    "evaluationOrder",
                    evaluationOrder
            );
        }

        return request;
    }

    private Map<String, Object> requiredRequest(
            UUID targetScenarioVersionId,
            UUID targetRuleId,
            Boolean required) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioVersionId",
                targetScenarioVersionId
        );

        request.put(
                "ruleId",
                targetRuleId
        );

        request.put(
                "required",
                required
        );

        return request;
    }

    private ScenarioVersion createScenarioVersion(
            Integer versionNumber) {

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(
                scenarioId
        );

        version.setVersionNumber(
                versionNumber
        );

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

        version.setCreatedAt(
                now
        );

        version.setUpdatedAt(
                now
        );

        return version;
    }

    private Rule createRule(
            String ruleCode,
            Short priority) {

        Rule rule =
                new Rule();

        rule.setRuleCode(
                ruleCode
        );

        rule.setRuleName(
                "Scenario Rule Controller Test"
        );

        rule.setDescription(
                "Rule used by ScenarioRuleControllerIntegrationTest"
        );

        rule.setCategory(
                "TRANSACTION"
        );

        rule.setSeverity(
                "HIGH"
        );

        rule.setPriority(
                priority
        );

        rule.setOwnerTeam(
                "FRAUD_RULES"
        );

        rule.setCurrentVersion(
                1
        );

        rule.setStatus(
                "ACTIVE"
        );

        LocalDateTime now =
                LocalDateTime.now();

        rule.setCreatedAt(
                now
        );

        rule.setUpdatedAt(
                now
        );

        return rule;
    }
}