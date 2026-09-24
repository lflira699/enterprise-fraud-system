package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScenarioEvaluationServiceIntegrationTest {

    @Autowired
    private ScenarioEvaluationServiceInterface scenarioEvaluationService;

    @Autowired
    private ScenarioEvaluationRepository scenarioEvaluationRepository;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID organizationId;
    private UUID tenantId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID customerId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        organizationId =
                UUID.randomUUID();

        tenantId =
                UUID.randomUUID();

        String scopeSuffix =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        entityManager
                .createNativeQuery(
                        """
                        INSERT INTO administration.organization (
                            organization_id,
                            organization_code,
                            legal_name,
                            country_code,
                            timezone,
                            status
                        )
                        VALUES (
                            :organizationId,
                            :organizationCode,
                            :legalName,
                            'GT',
                            'America/Guatemala',
                            'ACTIVE'
                        )
                        """
                )
                .setParameter(
                        "organizationId",
                        organizationId
                )
                .setParameter(
                        "organizationCode",
                        "SE-SVC-ORG-" + scopeSuffix
                )
                .setParameter(
                        "legalName",
                        "Scenario Evaluation Service Organization "
                                + scopeSuffix
                )
                .executeUpdate();

        entityManager
                .createNativeQuery(
                        """
                        INSERT INTO administration.tenant (
                            tenant_id,
                            organization_id,
                            tenant_code,
                            tenant_name,
                            status,
                            environment
                        )
                        VALUES (
                            :tenantId,
                            :organizationId,
                            :tenantCode,
                            :tenantName,
                            'ACTIVE',
                            'TEST'
                        )
                        """
                )
                .setParameter(
                        "tenantId",
                        tenantId
                )
                .setParameter(
                        "organizationId",
                        organizationId
                )
                .setParameter(
                        "tenantCode",
                        "SE-SVC-TEN-" + scopeSuffix
                )
                .setParameter(
                        "tenantName",
                        "Scenario Evaluation Service Tenant "
                                + scopeSuffix
                )
                .executeUpdate();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "SE-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Scenario"
        );

        customer.setLastName(
                "Evaluation"
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

        customer.setTenantId(
                tenantId
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
                "EFS-SE-SVC-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setOrganizationId(
                organizationId
        );

        transaction.setTenantId(
                tenantId
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
                "SE-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Evaluation Service Test"
        );

        scenario.setObjective(
                "Validate scenario evaluation service behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioEvaluationServiceIntegrationTest"
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
    void createScenarioEvaluationShouldPersistAndMapProvidedValues() {

        ScenarioEvaluationRequest request =
                buildRequest(
                        transactionId,
                        customerId,
                        "COMPLETED",
                        true
                );

        ScenarioEvaluationResponse response =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                request,
                                organizationId,
                                tenantId
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getEvaluationId()
        );

        assertEquals(
                scenarioId,
                response.getScenarioId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                "COMPLETED",
                response.getEvaluationStatus()
        );

        assertTrue(
                response.getMatched()
        );

        assertEquals(
                Short.valueOf((short) 5),
                response.getRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 3),
                response.getMatchedRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 2),
                response.getRequiredEvidenceCount()
        );

        assertEquals(
                Short.valueOf((short) 2),
                response.getAvailableEvidenceCount()
        );

        assertEquals(
                0,
                new BigDecimal("0.8750")
                        .compareTo(
                                response.getConfidence()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("0.6500")
                        .compareTo(
                                response.getRiskContribution()
                        )
        );

        assertEquals(
                Long.valueOf(125L),
                response.getEvaluationDurationMs()
        );

        assertNotNull(
                response.getEvaluationContext()
        );

        assertEquals(
                "service-test",
                response.getEvaluationContext()
                        .get("source")
        );

        assertEquals(
                "scenario-engine",
                response.getEvaluationContext()
                        .get("component")
        );

        assertNotNull(
                response.getEvaluatedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                scenarioEvaluationRepository.existsById(
                        response.getEvaluationId()
                )
        );
    }

    @Test
    void createScenarioEvaluationShouldAllowOptionalTransactionAndCustomer() {

        ScenarioEvaluationRequest request =
                buildRequest(
                        null,
                        null,
                        "COMPLETED",
                        false
                );

        ScenarioEvaluationResponse response =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                request,
                                organizationId,
                                tenantId
                        );

        assertNotNull(
                response.getEvaluationId()
        );

        assertEquals(
                scenarioId,
                response.getScenarioId()
        );

        assertEquals(
                scenarioVersionId,
                response.getScenarioVersionId()
        );

        assertNull(
                response.getTransactionId()
        );

        assertNull(
                response.getCustomerId()
        );

        assertFalse(
                response.getMatched()
        );

        assertEquals(
                Short.valueOf((short) 5),
                response.getRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 0),
                response.getMatchedRuleCount()
        );

        assertEquals(
                Short.valueOf((short) 2),
                response.getRequiredEvidenceCount()
        );

        assertEquals(
                Short.valueOf((short) 1),
                response.getAvailableEvidenceCount()
        );

        assertNotNull(
                response.getEvaluatedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getScenarioEvaluationByIdShouldReturnExistingEvaluation() {

        ScenarioEvaluationResponse created =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "COMPLETED",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse found =
                scenarioEvaluationService
                        .getScenarioEvaluationById(
                                created.getEvaluationId(),
                                organizationId,
                                tenantId
                        );

        assertEquals(
                created.getEvaluationId(),
                found.getEvaluationId()
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
                "COMPLETED",
                found.getEvaluationStatus()
        );

        assertTrue(
                found.getMatched()
        );
    }

    @Test
    void getScenarioEvaluationByIdShouldThrowWhenEvaluationDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvaluationService
                        .getScenarioEvaluationById(
                                UUID.randomUUID(),
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void getEvaluationsByScenarioShouldReturnMatchingEvaluations() {

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "COMPLETED",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        null,
                                        null,
                                        "PENDING",
                                        false
                                ),
                                organizationId,
                                tenantId
                        );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByScenario(
                                scenarioId,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                scenarioId.equals(
                                        result.getScenarioId()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByScenarioVersionShouldReturnMatchingEvaluations() {

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "COMPLETED",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        null,
                                        null,
                                        "PENDING",
                                        false
                                ),
                                organizationId,
                                tenantId
                        );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByScenarioVersion(
                                scenarioVersionId,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                scenarioVersionId.equals(
                                        result.getScenarioVersionId()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByTransactionShouldReturnMatchingEvaluations() {

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "COMPLETED",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "PENDING",
                                        false
                                ),
                                organizationId,
                                tenantId
                        );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByTransaction(
                                transactionId,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                transactionId.equals(
                                        result.getTransactionId()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByCustomerShouldReturnMatchingEvaluations() {

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "COMPLETED",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        null,
                                        customerId,
                                        "PENDING",
                                        false
                                ),
                                organizationId,
                                tenantId
                        );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByCustomer(
                                customerId,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                customerId.equals(
                                        result.getCustomerId()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByStatusShouldReturnMatchingEvaluations() {

        String status =
                "SE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        status,
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        null,
                                        null,
                                        status,
                                        false
                                ),
                                organizationId,
                                tenantId
                        );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByStatus(
                                status,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getEvaluationStatus()
                                )
                        )
        );
    }

    @Test
    void getEvaluationsByMatchedShouldReturnMatchingEvaluations() {

        ScenarioEvaluationResponse first =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "MATCHED_TEST",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        ScenarioEvaluationResponse second =
                scenarioEvaluationService
                        .createScenarioEvaluation(
                                buildRequest(
                                        null,
                                        null,
                                        "MATCHED_TEST",
                                        true
                                ),
                                organizationId,
                                tenantId
                        );

        scenarioEvaluationService
                .createScenarioEvaluation(
                        buildRequest(
                                transactionId,
                                customerId,
                                "MATCHED_TEST",
                                false
                        ),
                        organizationId,
                        tenantId
                );

        List<ScenarioEvaluationResponse> results =
                scenarioEvaluationService
                        .getEvaluationsByMatched(
                                true,
                                organizationId,
                                tenantId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvaluation(
                        results,
                        first.getEvaluationId()
                )
        );

        assertTrue(
                containsEvaluation(
                        results,
                        second.getEvaluationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                Boolean.TRUE.equals(
                                        result.getMatched()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                scenarioEvaluationService
                        .getEvaluationsByScenario(
                                UUID.randomUUID(),
                                organizationId,
                                tenantId
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvaluationService
                        .getEvaluationsByScenarioVersion(
                                UUID.randomUUID(),
                                organizationId,
                                tenantId
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvaluationService
                        .getEvaluationsByTransaction(
                                UUID.randomUUID(),
                                organizationId,
                                tenantId
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvaluationService
                        .getEvaluationsByCustomer(
                                UUID.randomUUID(),
                                organizationId,
                                tenantId
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioEvaluationService
                        .getEvaluationsByStatus(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8),
                                organizationId,
                                tenantId
                        )
                        .isEmpty()
        );
    }

    private ScenarioEvaluationRequest buildRequest(
            UUID targetTransactionId,
            UUID targetCustomerId,
            String evaluationStatus,
            boolean matched) {

        ScenarioEvaluationRequest request =
                new ScenarioEvaluationRequest();

        request.setScenarioId(
                scenarioId
        );

        request.setScenarioVersionId(
                scenarioVersionId
        );

        request.setTransactionId(
                targetTransactionId
        );

        request.setCustomerId(
                targetCustomerId
        );

        request.setEvaluationStatus(
                evaluationStatus
        );

        request.setMatched(
                matched
        );

        request.setRuleCount(
                (short) 5
        );

        request.setMatchedRuleCount(
                matched
                        ? (short) 3
                        : (short) 0
        );

        request.setRequiredEvidenceCount(
                (short) 2
        );

        request.setAvailableEvidenceCount(
                matched
                        ? (short) 2
                        : (short) 1
        );

        request.setConfidence(
                new BigDecimal("0.8750")
        );

        request.setRiskContribution(
                new BigDecimal("0.6500")
        );

        request.setEvaluationDurationMs(
                125L
        );

        request.setEvaluationContext(
                Map.of(
                        "source",
                        "service-test",
                        "component",
                        "scenario-engine"
                )
        );

        return request;
    }

    private boolean containsEvaluation(
            List<ScenarioEvaluationResponse> results,
            UUID evaluationId) {

        return results.stream()
                .anyMatch(result ->
                        evaluationId.equals(
                                result.getEvaluationId()
                        )
                );
    }
}
