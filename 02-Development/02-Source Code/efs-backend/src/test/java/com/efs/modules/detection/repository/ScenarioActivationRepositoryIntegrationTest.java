package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioActivation;
import com.efs.modules.detection.entity.ScenarioVersion;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioActivationRepositoryIntegrationTest {

    @Autowired
    private ScenarioActivationRepository scenarioActivationRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

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
                "SACT-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Scenario"
        );

        customer.setLastName(
                "Activation"
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

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(0);

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-SACT-TXN-" + UUID.randomUUID()
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

        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        transaction.setCreatedBy(
                UUID.randomUUID()
        );

        transaction.setRecordVersion(0);

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SACT-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Activation Integration Test"
        );

        scenario.setObjective(
                "Scenario Activation repository integration test"
        );

        scenario.setDescription(
                "Scenario used by ScenarioActivation repository integration tests"
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

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

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
    void shouldSaveAndFindByActivationId() {

        ScenarioActivation activation =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        LocalDateTime.now()
                );

        ScenarioActivation saved =
                scenarioActivationRepository.saveAndFlush(
                        activation
                );

        Optional<ScenarioActivation> result =
                scenarioActivationRepository.findByActivationId(
                        saved.getActivationId()
                );

        assertTrue(result.isPresent());

        ScenarioActivation found =
                result.get();

        assertEquals(
                saved.getActivationId(),
                found.getActivationId()
        );

        assertEquals(
                scenarioId,
                found.getScenarioId()
        );

        assertEquals(
                scenarioVersionId,
                found.getScenarioVersionId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                customerId,
                found.getCustomerId()
        );

        assertEquals(
                "TRIGGERED",
                found.getActivationStatus()
        );

        assertEquals(
                "HIGH",
                found.getSeverity()
        );

        assertEquals(
                0,
                new BigDecimal("0.9500")
                        .compareTo(
                                found.getConfidence()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("87.5000")
                        .compareTo(
                                found.getRiskScore()
                        )
        );

        assertEquals(
                "Scenario activation integration test",
                found.getActivationReason()
        );

        assertNotNull(
                found.getDecisionContext()
        );

        assertEquals(
                "repository-test",
                found.getDecisionContext()
                        .get("source")
        );

        assertEquals(
                true,
                found.getDecisionContext()
                        .get("validated")
        );

        assertNotNull(
                found.getTriggeredAt()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void shouldFindByScenarioIdOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "MEDIUM",
                        base.minusMinutes(10)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findByScenarioIdOrderByTriggeredAtDesc(
                                scenarioId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );
    }

    @Test
    void shouldFindByScenarioVersionIdOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "LOW",
                        base.minusMinutes(5)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findByScenarioVersionIdOrderByTriggeredAtDesc(
                                scenarioVersionId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "MEDIUM",
                        base.minusMinutes(3)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findByTransactionIdOrderByTriggeredAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "LOW",
                        base.minusMinutes(2)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findByCustomerIdOrderByTriggeredAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );
    }

    @Test
    void shouldFindByActivationStatusOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "MEDIUM",
                        base.minusMinutes(4)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        ScenarioActivation differentStatus =
                createActivation(
                        "RESOLVED",
                        "LOW",
                        base.plusMinutes(1)
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.save(differentStatus);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findByActivationStatusOrderByTriggeredAtDesc(
                                "TRIGGERED"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                activation ->
                                        "TRIGGERED".equals(
                                                activation.getActivationStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldFindBySeverityOrderedByTriggeredAtDescending() {

        LocalDateTime base =
                LocalDateTime.now();

        ScenarioActivation older =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base.minusMinutes(6)
                );

        ScenarioActivation newer =
                createActivation(
                        "TRIGGERED",
                        "HIGH",
                        base
                );

        ScenarioActivation differentSeverity =
                createActivation(
                        "TRIGGERED",
                        "LOW",
                        base.plusMinutes(1)
                );

        scenarioActivationRepository.save(older);
        scenarioActivationRepository.save(newer);
        scenarioActivationRepository.save(differentSeverity);
        scenarioActivationRepository.flush();

        List<ScenarioActivation> result =
                scenarioActivationRepository
                        .findBySeverityOrderByTriggeredAtDesc(
                                "HIGH"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                newer.getActivationId(),
                result.get(0).getActivationId()
        );

        assertEquals(
                older.getActivationId(),
                result.get(1).getActivationId()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                activation ->
                                        "HIGH".equals(
                                                activation.getSeverity()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyWhenActivationDoesNotExist() {

        Optional<ScenarioActivation> result =
                scenarioActivationRepository.findByActivationId(
                        UUID.randomUUID()
                );

        assertTrue(
                result.isEmpty()
        );
    }

    private ScenarioActivation createActivation(
            String activationStatus,
            String severity,
            LocalDateTime triggeredAt) {

        ScenarioActivation activation =
                new ScenarioActivation();

        activation.setScenarioId(
                scenarioId
        );

        activation.setScenarioVersionId(
                scenarioVersionId
        );

        activation.setTransactionId(
                transactionId
        );

        activation.setCustomerId(
                customerId
        );

        activation.setActivationStatus(
                activationStatus
        );

        activation.setSeverity(
                severity
        );

        activation.setConfidence(
                new BigDecimal("0.9500")
        );

        activation.setRiskScore(
                new BigDecimal("87.5000")
        );

        activation.setTriggeredAt(
                triggeredAt
        );

        activation.setActivationReason(
                "Scenario activation integration test"
        );

        activation.setDecisionContext(
                Map.of(
                        "source",
                        "repository-test",
                        "validated",
                        true
                )
        );

        activation.setCreatedAt(
                triggeredAt
        );

        return activation;
    }
}