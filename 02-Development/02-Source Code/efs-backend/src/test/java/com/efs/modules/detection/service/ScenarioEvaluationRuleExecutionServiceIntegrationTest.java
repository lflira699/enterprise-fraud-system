package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRuleExecutionRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioEvaluationRuleExecutionServiceIntegrationTest {

    @Autowired
    private ScenarioEvaluationRuleExecutionServiceInterface service;

    @Autowired
    private ScenarioEvaluationRuleExecutionRepository relationRepository;

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
                "SERE-SVC-" + UUID.randomUUID()
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
                "EFS-SERE-SVC-" + UUID.randomUUID()
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
                "SERE-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Rule Execution Service Test"
        );

        scenario.setObjective(
                "Validate scenario evaluation and rule execution service relationship"
        );

        scenario.setDescription(
                "Scenario used by ScenarioEvaluationRuleExecutionServiceIntegrationTest"
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
    void createScenarioEvaluationRuleExecutionShouldPersistAndMapReferences() {

        ScenarioEvaluationRuleExecutionRequest request =
                buildRequest(
                        evaluationId,
                        executionId
                );

        ScenarioEvaluationRuleExecutionResponse response =
                service.createScenarioEvaluationRuleExecution(
                        request
                );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getEvaluationRuleExecutionId()
        );

        assertEquals(
                evaluationId,
                response.getEvaluationId()
        );

        assertEquals(
                executionId,
                response.getExecutionId()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                relationRepository.existsById(
                        response.getEvaluationRuleExecutionId()
                )
        );
    }

    @Test
    void getScenarioEvaluationRuleExecutionByIdShouldReturnExistingRelation() {

        ScenarioEvaluationRuleExecutionResponse created =
                service.createScenarioEvaluationRuleExecution(
                        buildRequest(
                                evaluationId,
                                executionId
                        )
                );

        ScenarioEvaluationRuleExecutionResponse found =
                service.getScenarioEvaluationRuleExecutionById(
                        created.getEvaluationRuleExecutionId()
                );

        assertEquals(
                created.getEvaluationRuleExecutionId(),
                found.getEvaluationRuleExecutionId()
        );

        assertEquals(
                evaluationId,
                found.getEvaluationId()
        );

        assertEquals(
                executionId,
                found.getExecutionId()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void getScenarioEvaluationRuleExecutionByIdShouldThrowWhenRelationDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> service
                        .getScenarioEvaluationRuleExecutionById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getRuleExecutionsByEvaluationShouldReturnMatchingRelations() {

        ScenarioEvaluationRuleExecutionResponse first =
                service.createScenarioEvaluationRuleExecution(
                        buildRequest(
                                evaluationId,
                                executionId
                        )
                );

        RuleExecution secondExecution =
                ruleExecutionRepository.saveAndFlush(
                        createRuleExecution(
                                false,
                                20,
                                LocalDateTime.now()
                        )
                );

        ScenarioEvaluationRuleExecutionResponse second =
                service.createScenarioEvaluationRuleExecution(
                        buildRequest(
                                evaluationId,
                                secondExecution.getExecutionId()
                        )
                );

        List<ScenarioEvaluationRuleExecutionResponse> results =
                service.getRuleExecutionsByEvaluation(
                        evaluationId
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsRelation(
                        results,
                        first.getEvaluationRuleExecutionId()
                )
        );

        assertTrue(
                containsRelation(
                        results,
                        second.getEvaluationRuleExecutionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                evaluationId.equals(
                                        result.getEvaluationId()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByRuleExecutionShouldReturnMatchingRelations() {

        ScenarioEvaluationRuleExecutionResponse first =
                service.createScenarioEvaluationRuleExecution(
                        buildRequest(
                                evaluationId,
                                executionId
                        )
                );

        ScenarioEvaluation secondEvaluation =
                scenarioEvaluationRepository.saveAndFlush(
                        createScenarioEvaluation(
                                false,
                                LocalDateTime.now()
                        )
                );

        ScenarioEvaluationRuleExecutionResponse second =
                service.createScenarioEvaluationRuleExecution(
                        buildRequest(
                                secondEvaluation.getEvaluationId(),
                                executionId
                        )
                );

        List<ScenarioEvaluationRuleExecutionResponse> results =
                service.getEvaluationsByRuleExecution(
                        executionId
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsRelation(
                        results,
                        first.getEvaluationRuleExecutionId()
                )
        );

        assertTrue(
                containsRelation(
                        results,
                        second.getEvaluationRuleExecutionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                executionId.equals(
                                        result.getExecutionId()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                service.getRuleExecutionsByEvaluation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                service.getEvaluationsByRuleExecution(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private ScenarioEvaluationRuleExecutionRequest buildRequest(
            UUID targetEvaluationId,
            UUID targetExecutionId) {

        ScenarioEvaluationRuleExecutionRequest request =
                new ScenarioEvaluationRuleExecutionRequest();

        request.setEvaluationId(
                targetEvaluationId
        );

        request.setExecutionId(
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
                "EFS-SERE-SVC"
        );

        execution.setExecutedAt(
                executedAt
        );

        return execution;
    }

    private boolean containsRelation(
            List<ScenarioEvaluationRuleExecutionResponse> results,
            UUID relationId) {

        return results.stream()
                .anyMatch(result ->
                        relationId.equals(
                                result.getEvaluationRuleExecutionId()
                        )
                );
    }
}