package com.efs.modules.detection.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
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
class ScenarioEvaluationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID customerId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "SE-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Scenario"
        );

        customer.setLastName(
                "Evaluation Controller"
        );

        customer.setRiskLevel(
                "LOW"
        );

        customer.setRiskScore(
                BigDecimal.ZERO
        );

        customer.setCustomerStatus(
                "ACTIVE"
        );

        customer.setCreatedAt(
                now
        );

        customer.setUpdatedAt(
                now
        );

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(
                0
        );

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-SE-CTRL-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setOrganizationId(
                UUID.randomUUID()
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setAmount(
                new BigDecimal("500.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "PENDING"
        );

        transaction.setFinalDecision(
                "PENDING"
        );

        transaction.setFraudScore(
                BigDecimal.ZERO
        );

        transaction.setCreatedAt(
                now
        );

        transaction.setUpdatedAt(
                now
        );

        transaction.setCreatedBy(
                UUID.randomUUID()
        );

        transaction.setRecordVersion(
                0
        );

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SE-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Controller Test"
        );

        scenario.setObjective(
                "Validate scenario evaluation controller behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioEvaluationControllerIntegrationTest"
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
    void shouldCreateScenarioEvaluationWithFullPayload()
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        "COMPLETED",
                        true
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evaluations"
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
                        jsonPath("$.evaluationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.evaluationStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.matched")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.ruleCount")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.matchedRuleCount")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.requiredEvidenceCount")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.availableEvidenceCount")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.confidence")
                                .value(0.875)
                )
                .andExpect(
                        jsonPath("$.riskContribution")
                                .value(0.65)
                )
                .andExpect(
                        jsonPath("$.evaluationDurationMs")
                                .value(125)
                )
                .andExpect(
                        jsonPath("$.evaluationContext.source")
                                .value("controller-test")
                )
                .andExpect(
                        jsonPath("$.evaluationContext.component")
                                .value("scenario-engine")
                )
                .andExpect(
                        jsonPath("$.evaluatedAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateScenarioEvaluationWithoutOptionalReferences()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "COMPLETED",
                        false
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evaluations"
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
                        jsonPath("$.evaluationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.evaluationStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.matched")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.ruleCount")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.matchedRuleCount")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.requiredEvidenceCount")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.availableEvidenceCount")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.evaluatedAt")
                                .exists()
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
                                "/api/v1/detection/scenario-evaluations"
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
                                "$.validationErrors.scenarioVersionId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.evaluationStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.matched"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.ruleCount"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.matchedRuleCount"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.requiredEvidenceCount"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.availableEvidenceCount"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenEvaluationStatusExceedsMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "1234567890123456789012345678901",
                        true
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evaluations"
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
                                "$.validationErrors.evaluationStatus"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioEvaluationById()
            throws Exception {

        JsonNode created =
                createEvaluation(
                        "COMPLETED",
                        true
                );

        UUID evaluationId =
                UUID.fromString(
                        created.get(
                                "evaluationId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/{evaluationId}",
                                evaluationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.evaluationId")
                                .value(evaluationId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(
                                        scenarioVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.evaluationStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.matched")
                                .value(true)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioEvaluation()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/{evaluationId}",
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
    void shouldGetEvaluationsByScenario()
            throws Exception {

        JsonNode first =
                createEvaluation(
                        "COMPLETED",
                        true
                );

        JsonNode second =
                createEvaluation(
                        "PENDING",
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/scenario/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByScenarioVersion()
            throws Exception {

        JsonNode first =
                createEvaluation(
                        "COMPLETED",
                        true
                );

        JsonNode second =
                createEvaluation(
                        "PENDING",
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/scenario-version/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByTransaction()
            throws Exception {

        JsonNode first =
                createEvaluation(
                        "COMPLETED",
                        true
                );

        JsonNode second =
                createEvaluation(
                        "PENDING",
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByCustomer()
            throws Exception {

        JsonNode first =
                createEvaluation(
                        "COMPLETED",
                        true
                );

        JsonNode second =
                createEvaluation(
                        "PENDING",
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByStatus()
            throws Exception {

        String evaluationStatus =
                "SE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createEvaluation(
                        evaluationStatus,
                        true
                );

        JsonNode second =
                createEvaluation(
                        evaluationStatus,
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/status/{evaluationStatus}",
                                evaluationStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationStatus")
                                .value(
                                        hasItem(
                                                evaluationStatus
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByMatched()
            throws Exception {

        JsonNode first =
                createEvaluation(
                        "MATCHED_TEST",
                        true
                );

        JsonNode second =
                createEvaluation(
                        "MATCHED_TEST",
                        true
                );

        createEvaluation(
                "MATCHED_TEST",
                false
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluations/matched/{matched}",
                                true
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].matched")
                                .value(
                                        hasItem(true)
                                )
                );
    }

    private JsonNode createEvaluation(
            String evaluationStatus,
            boolean matched)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        evaluationStatus,
                        matched
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-evaluations"
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
            String evaluationStatus,
            boolean matched) {

        Map<String, Object> request =
                requiredRequest(
                        evaluationStatus,
                        matched
                );

        request.put(
                "transactionId",
                transactionId
        );

        request.put(
                "customerId",
                customerId
        );

        request.put(
                "confidence",
                new BigDecimal("0.8750")
        );

        request.put(
                "riskContribution",
                new BigDecimal("0.6500")
        );

        request.put(
                "evaluationDurationMs",
                125L
        );

        request.put(
                "evaluationContext",
                Map.of(
                        "source",
                        "controller-test",
                        "component",
                        "scenario-engine"
                )
        );

        return request;
    }

    private Map<String, Object> requiredRequest(
            String evaluationStatus,
            boolean matched) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioId",
                scenarioId
        );

        request.put(
                "scenarioVersionId",
                scenarioVersionId
        );

        request.put(
                "evaluationStatus",
                evaluationStatus
        );

        request.put(
                "matched",
                matched
        );

        request.put(
                "ruleCount",
                5
        );

        request.put(
                "matchedRuleCount",
                matched
                        ? 3
                        : 0
        );

        request.put(
                "requiredEvidenceCount",
                2
        );

        request.put(
                "availableEvidenceCount",
                matched
                        ? 2
                        : 1
        );

        return request;
    }
}