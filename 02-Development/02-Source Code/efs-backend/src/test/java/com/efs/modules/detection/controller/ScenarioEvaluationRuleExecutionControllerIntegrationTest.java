package com.efs.modules.detection.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleExecutionRepository;
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
class ScenarioEvaluationRuleExecutionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScenarioEvaluationRepository scenarioEvaluationRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private RuleExecutionRepository ruleExecutionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID evaluationId;
    private UUID executionId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "SERE-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Scenario"
        );

        customer.setLastName(
                "RuleExecution"
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
                "EFS-SERE-CTRL-" + UUID.randomUUID()
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
                new BigDecimal("250.00")
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
                "SERE-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Rule Execution Controller Test"
        );

        scenario.setObjective(
                "Validate scenario evaluation rule execution controller behavior"
        );

        scenario.setDescription(
                "Scenario used by controller integration test"
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

        ScenarioEvaluation evaluation =
                createScenarioEvaluation(
                        true,
                        now
                );

        ScenarioEvaluation savedEvaluation =
                scenarioEvaluationRepository.saveAndFlush(
                        evaluation
                );

        evaluationId =
                savedEvaluation.getEvaluationId();

        RuleExecution ruleExecution =
                createRuleExecution(
                        true,
                        10,
                        now
                );

        RuleExecution savedRuleExecution =
                ruleExecutionRepository.saveAndFlush(
                        ruleExecution
                );

        executionId =
                savedRuleExecution.getExecutionId();
    }

    @Test
    void shouldCreateScenarioEvaluationRuleExecution()
            throws Exception {

        Map<String, Object> request =
                validRequest(
                        evaluationId,
                        executionId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-evaluation-rule-executions"
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
                        jsonPath("$.evaluationRuleExecutionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.evaluationId")
                                .value(evaluationId.toString())
                )
                .andExpect(
                        jsonPath("$.executionId")
                                .value(executionId.toString())
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
                                "/api/v1/detection/scenario-evaluation-rule-executions"
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
                                "$.validationErrors.evaluationId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.executionId"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioEvaluationRuleExecutionById()
            throws Exception {

        JsonNode created =
                createRelation(
                        evaluationId,
                        executionId
                );

        UUID relationId =
                UUID.fromString(
                        created.get(
                                "evaluationRuleExecutionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluation-rule-executions/{evaluationRuleExecutionId}",
                                relationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.evaluationRuleExecutionId")
                                .value(relationId.toString())
                )
                .andExpect(
                        jsonPath("$.evaluationId")
                                .value(evaluationId.toString())
                )
                .andExpect(
                        jsonPath("$.executionId")
                                .value(executionId.toString())
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownRelation()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluation-rule-executions/{evaluationRuleExecutionId}",
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
    void shouldGetRuleExecutionsByEvaluation()
            throws Exception {

        JsonNode first =
                createRelation(
                        evaluationId,
                        executionId
                );

        RuleExecution secondExecution =
                ruleExecutionRepository.saveAndFlush(
                        createRuleExecution(
                                false,
                                20,
                                LocalDateTime.now()
                        )
                );

        JsonNode second =
                createRelation(
                        evaluationId,
                        secondExecution.getExecutionId()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluation-rule-executions/evaluation/{evaluationId}",
                                evaluationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].evaluationRuleExecutionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationRuleExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationRuleExecutionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationRuleExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationId")
                                .value(
                                        hasItem(
                                                evaluationId.toString()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEvaluationsByRuleExecution()
            throws Exception {

        JsonNode first =
                createRelation(
                        evaluationId,
                        executionId
                );

        ScenarioEvaluation secondEvaluation =
                scenarioEvaluationRepository.saveAndFlush(
                        createScenarioEvaluation(
                                false,
                                LocalDateTime.now()
                        )
                );

        JsonNode second =
                createRelation(
                        secondEvaluation.getEvaluationId(),
                        executionId
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-evaluation-rule-executions/execution/{executionId}",
                                executionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].evaluationRuleExecutionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "evaluationRuleExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationRuleExecutionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "evaluationRuleExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].executionId")
                                .value(
                                        hasItem(
                                                executionId.toString()
                                        )
                                )
                );
    }

    private JsonNode createRelation(
            UUID targetEvaluationId,
            UUID targetExecutionId)
            throws Exception {

        Map<String, Object> request =
                validRequest(
                        targetEvaluationId,
                        targetExecutionId
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-evaluation-rule-executions"
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

    private Map<String, Object> validRequest(
            UUID targetEvaluationId,
            UUID targetExecutionId) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "evaluationId",
                targetEvaluationId
        );

        request.put(
                "executionId",
                targetExecutionId
        );

        return request;
    }

    private ScenarioEvaluation createScenarioEvaluation(
            boolean matched,
            LocalDateTime evaluatedAt) {

        ScenarioEvaluation evaluation =
                new ScenarioEvaluation();

        evaluation.setScenarioId(
                scenarioId
        );

        evaluation.setScenarioVersionId(
                scenarioVersionId
        );

        evaluation.setTransactionId(
                transactionId
        );

        evaluation.setCustomerId(
                customerId
        );

        evaluation.setEvaluationStatus(
                "COMPLETED"
        );

        evaluation.setMatched(
                matched
        );

        evaluation.setRuleCount(
                (short) 1
        );

        evaluation.setMatchedRuleCount(
                matched
                        ? (short) 1
                        : (short) 0
        );

        evaluation.setRequiredEvidenceCount(
                (short) 0
        );

        evaluation.setAvailableEvidenceCount(
                (short) 0
        );

        evaluation.setConfidence(
                matched
                        ? new BigDecimal("0.9000")
                        : new BigDecimal("0.5000")
        );

        evaluation.setRiskContribution(
                matched
                        ? new BigDecimal("0.7000")
                        : new BigDecimal("0.3000")
        );

        evaluation.setEvaluatedAt(
                evaluatedAt
        );

        evaluation.setEvaluationDurationMs(
                matched
                        ? 100L
                        : 50L
        );

        evaluation.setCreatedAt(
                evaluatedAt
        );

        return evaluation;
    }

    private RuleExecution createRuleExecution(
            boolean matched,
            int executionTimeMs,
            LocalDateTime executedAt) {

        RuleExecution execution =
                new RuleExecution();

        execution.setRuleId(
                null
        );

        execution.setRuleVersionId(
                null
        );

        execution.setPolicyId(
                null
        );

        execution.setTransactionId(
                transactionId
        );

        execution.setExecutionStatus(
                "COMPLETED"
        );

        execution.setMatched(
                matched
        );

        execution.setExecutionTimeMs(
                executionTimeMs
        );

        execution.setErrorCode(
                null
        );

        execution.setEngineInstance(
                "EFS-SERE-CTRL"
        );

        execution.setExecutedAt(
                executedAt
        );

        return execution;
    }
}