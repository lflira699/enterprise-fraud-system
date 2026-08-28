package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioEvaluationRuleExecutionRepositoryIntegrationTest {

    @Autowired
    private ScenarioEvaluationRuleExecutionRepository repository;

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
    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "V102-" + UUID.randomUUID()
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

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-V102-TXN-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                savedCustomer.getCustomerId()
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
                "V102-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Rule Execution Integration Test"
        );

        scenario.setObjective(
                "Validate scenario evaluation and rule execution relationship"
        );

        scenario.setDescription(
                "Scenario used by V102 repository integration tests"
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

        ScenarioEvaluation evaluation =
                new ScenarioEvaluation();

        evaluation.setScenarioId(
                savedScenario.getScenarioId()
        );

        evaluation.setScenarioVersionId(
                savedScenarioVersion.getScenarioVersionId()
        );

        evaluation.setTransactionId(
                transactionId
        );

        evaluation.setCustomerId(
                savedCustomer.getCustomerId()
        );

        evaluation.setEvaluationStatus(
                "COMPLETED"
        );

        evaluation.setMatched(
                true
        );

        evaluation.setRuleCount(
                (short) 1
        );

        evaluation.setMatchedRuleCount(
                (short) 1
        );

        evaluation.setRequiredEvidenceCount(
                (short) 0
        );

        evaluation.setAvailableEvidenceCount(
                (short) 0
        );

        evaluation.setConfidence(
                new BigDecimal("0.9000")
        );

        evaluation.setRiskContribution(
                new BigDecimal("0.7000")
        );

        evaluation.setEvaluatedAt(
                now
        );

        evaluation.setEvaluationDurationMs(
                100L
        );

        evaluation.setCreatedAt(
                now
        );

        ScenarioEvaluation savedEvaluation =
                scenarioEvaluationRepository.saveAndFlush(
                        evaluation
                );

        evaluationId =
                savedEvaluation.getEvaluationId();

        RuleExecution ruleExecution =
                createRuleExecutionEntity(
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
    void shouldSaveAndFindRelationById() {

        ScenarioEvaluationRuleExecution relation =
                createRelation(
                        evaluationId,
                        executionId,
                        LocalDateTime.now()
                );

        ScenarioEvaluationRuleExecution saved =
                repository.saveAndFlush(
                        relation
                );

        assertNotNull(
                saved.getEvaluationRuleExecutionId()
        );

        Optional<ScenarioEvaluationRuleExecution> result =
                repository.findByEvaluationRuleExecutionId(
                        saved.getEvaluationRuleExecutionId()
                );

        assertTrue(
                result.isPresent()
        );

        ScenarioEvaluationRuleExecution found =
                result.get();

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
    void shouldFindRelationsByEvaluationOrderedByCreatedAtAscending() {

        RuleExecution secondExecution =
                createRuleExecution();

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluationRuleExecution older =
                repository.saveAndFlush(
                        createRelation(
                                evaluationId,
                                executionId,
                                now.minusMinutes(2)
                        )
                );

        ScenarioEvaluationRuleExecution newer =
                repository.saveAndFlush(
                        createRelation(
                                evaluationId,
                                secondExecution.getExecutionId(),
                                now
                        )
                );

        List<ScenarioEvaluationRuleExecution> results =
                repository
                        .findByEvaluationIdOrderByCreatedAtAsc(
                                evaluationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                older.getEvaluationRuleExecutionId(),
                results.get(0)
                        .getEvaluationRuleExecutionId()
        );

        assertEquals(
                newer.getEvaluationRuleExecutionId(),
                results.get(1)
                        .getEvaluationRuleExecutionId()
        );
    }

    @Test
    void shouldFindRelationsByExecutionOrderedByCreatedAtAscending() {

        ScenarioEvaluation secondEvaluation =
                createScenarioEvaluation();

        LocalDateTime now =
                LocalDateTime.now();

        ScenarioEvaluationRuleExecution older =
                repository.saveAndFlush(
                        createRelation(
                                evaluationId,
                                executionId,
                                now.minusMinutes(2)
                        )
                );

        ScenarioEvaluationRuleExecution newer =
                repository.saveAndFlush(
                        createRelation(
                                secondEvaluation.getEvaluationId(),
                                executionId,
                                now
                        )
                );

        List<ScenarioEvaluationRuleExecution> results =
                repository
                        .findByExecutionIdOrderByCreatedAtAsc(
                                executionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                older.getEvaluationRuleExecutionId(),
                results.get(0)
                        .getEvaluationRuleExecutionId()
        );

        assertEquals(
                newer.getEvaluationRuleExecutionId(),
                results.get(1)
                        .getEvaluationRuleExecutionId()
        );
    }

    @Test
    void shouldPersistEvaluationAndExecutionReferences() {

        ScenarioEvaluationRuleExecution saved =
                repository.saveAndFlush(
                        createRelation(
                                evaluationId,
                                executionId,
                                LocalDateTime.now()
                        )
                );

        ScenarioEvaluationRuleExecution found =
                repository
                        .findById(
                                saved.getEvaluationRuleExecutionId()
                        )
                        .orElseThrow();

        assertEquals(
                evaluationId,
                found.getEvaluationId()
        );

        assertEquals(
                executionId,
                found.getExecutionId()
        );
    }

    @Test
    void shouldEnforceUniqueEvaluationAndExecutionCombination() {

        ScenarioEvaluationRuleExecution first =
                createRelation(
                        evaluationId,
                        executionId,
                        LocalDateTime.now()
                );

        repository.saveAndFlush(
                first
        );

        ScenarioEvaluationRuleExecution duplicate =
                createRelation(
                        evaluationId,
                        executionId,
                        LocalDateTime.now()
                );

        assertThrows(
                RuntimeException.class,
                () -> repository.saveAndFlush(
                        duplicate
                )
        );
    }

    @Test
    void shouldReturnEmptyListForUnknownEvaluationId() {

        List<ScenarioEvaluationRuleExecution> results =
                repository
                        .findByEvaluationIdOrderByCreatedAtAsc(
                                UUID.randomUUID()
                        );

        assertTrue(
                results.isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyListForUnknownExecutionId() {

        List<ScenarioEvaluationRuleExecution> results =
                repository
                        .findByExecutionIdOrderByCreatedAtAsc(
                                UUID.randomUUID()
                        );

        assertTrue(
                results.isEmpty()
        );
    }

    private ScenarioEvaluationRuleExecution createRelation(
            UUID relationEvaluationId,
            UUID relationExecutionId,
            LocalDateTime createdAt) {

        ScenarioEvaluationRuleExecution relation =
                new ScenarioEvaluationRuleExecution();

        relation.setEvaluationId(
                relationEvaluationId
        );

        relation.setExecutionId(
                relationExecutionId
        );

        relation.setCreatedAt(
                createdAt
        );

        return relation;
    }

    private RuleExecution createRuleExecution() {

        RuleExecution execution =
                createRuleExecutionEntity(
                        false,
                        20,
                        LocalDateTime.now()
                );

        return ruleExecutionRepository.saveAndFlush(
                execution
        );
    }

    private RuleExecution createRuleExecutionEntity(
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
                "EFS-V102-TEST"
        );

        execution.setExecutedAt(
                executedAt
        );

        return execution;
    }

    private ScenarioEvaluation createScenarioEvaluation() {

        ScenarioEvaluation existing =
                scenarioEvaluationRepository
                        .findByEvaluationId(
                                evaluationId
                        )
                        .orElseThrow();

        ScenarioEvaluation evaluation =
                new ScenarioEvaluation();

        evaluation.setScenarioId(
                existing.getScenarioId()
        );

        evaluation.setScenarioVersionId(
                existing.getScenarioVersionId()
        );

        evaluation.setTransactionId(
                transactionId
        );

        evaluation.setCustomerId(
                existing.getCustomerId()
        );

        evaluation.setEvaluationStatus(
                "COMPLETED"
        );

        evaluation.setMatched(
                false
        );

        evaluation.setRuleCount(
                (short) 1
        );

        evaluation.setMatchedRuleCount(
                (short) 0
        );

        evaluation.setRequiredEvidenceCount(
                (short) 0
        );

        evaluation.setAvailableEvidenceCount(
                (short) 0
        );

        evaluation.setConfidence(
                new BigDecimal("0.5000")
        );

        evaluation.setRiskContribution(
                new BigDecimal("0.3000")
        );

        evaluation.setEvaluatedAt(
                LocalDateTime.now()
        );

        evaluation.setEvaluationDurationMs(
                50L
        );

        evaluation.setCreatedAt(
                LocalDateTime.now()
        );

        return scenarioEvaluationRepository.saveAndFlush(
                evaluation
        );
    }
}