package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioActivationRepository;
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
class ScenarioActivationServiceIntegrationTest {

    @Autowired
    private ScenarioActivationServiceInterface scenarioActivationService;

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

    @Autowired
    private EntityManager entityManager;

    private UUID organizationId;
    private UUID tenantId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        organizationId =
                UUID.randomUUID();

        tenantId =
                UUID.randomUUID();

        String suffix =
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
                        "SA-ORG-" + suffix
                )
                .setParameter(
                        "legalName",
                        "Scenario Activation Test Organization " + suffix
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
                        "SA-TEN-" + suffix
                )
                .setParameter(
                        "tenantName",
                        "Scenario Activation Test Tenant " + suffix
                )
                .executeUpdate();
        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "SA-SVC-" + UUID.randomUUID()
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
                "EFS-SA-SVC-" + UUID.randomUUID()
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
                "SA-SVC-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Activation Service Test"
        );

        scenario.setObjective(
                "Validate scenario activation service behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioActivationServiceIntegrationTest"
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
    void createScenarioActivationShouldPersistProvidedValuesAndGenerateTimestamps() {

        ScenarioActivationRequest request =
                buildRequest(
                        transactionId,
                        customerId,
                        "TRIGGERED",
                        "HIGH"
                );

        ScenarioActivationResponse response =
                scenarioActivationService
                        .createScenarioActivation(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getActivationId()
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
                "TRIGGERED",
                response.getActivationStatus()
        );

        assertEquals(
                "HIGH",
                response.getSeverity()
        );

        assertEquals(
                0,
                new BigDecimal("0.9500")
                        .compareTo(
                                response.getConfidence()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("87.5000")
                        .compareTo(
                                response.getRiskScore()
                        )
        );

        assertEquals(
                "Scenario activation service integration test",
                response.getActivationReason()
        );

        assertNotNull(
                response.getDecisionContext()
        );

        assertEquals(
                "service-test",
                response.getDecisionContext()
                        .get("source")
        );

        assertEquals(
                true,
                response.getDecisionContext()
                        .get("validated")
        );

        assertNotNull(
                response.getTriggeredAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNull(
                response.getResolvedAt()
        );

        assertTrue(
                scenarioActivationRepository.existsById(
                        response.getActivationId()
                )
        );
    }

    @Test
    void createScenarioActivationShouldRejectWhenOwnershipCannotBeResolved() {

        ScenarioActivationRequest request =
                buildRequest(
                        null,
                        null,
                        "TRIGGERED",
                        "MEDIUM"
                );

        assertThrows(
                IllegalStateException.class,
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request
                        )
        );
    }

    @Test
    void getScenarioActivationByIdShouldReturnExistingActivation() {

        ScenarioActivationResponse created =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse found =
                scenarioActivationService
                        .getScenarioActivationById(
                                created.getActivationId()
                        );

        assertEquals(
                created.getActivationId(),
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
    }

    @Test
    void getScenarioActivationByIdShouldThrowWhenActivationDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioActivationService
                        .getScenarioActivationById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getActivationsByScenarioShouldReturnMatchingActivations() {

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        null,
                                        customerId,
                                        "PENDING",
                                        "MEDIUM"
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsByScenario(
                                scenarioId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
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
    void getActivationsByScenarioVersionShouldReturnMatchingActivations() {

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        null,
                                        customerId,
                                        "TRIGGERED",
                                        "LOW"
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsByScenarioVersion(
                                scenarioVersionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
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
    void getActivationsByTransactionShouldReturnMatchingActivations() {

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "PENDING",
                                        "MEDIUM"
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsByTransaction(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
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
    void getActivationsByCustomerShouldReturnMatchingActivations() {

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        null,
                                        customerId,
                                        "PENDING",
                                        "LOW"
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsByCustomer(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
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
    void getActivationsByStatusShouldReturnMatchingActivations() {

        String status =
                "SA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        status,
                                        "HIGH"
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        null,
                                        customerId,
                                        status,
                                        "MEDIUM"
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getActivationStatus()
                                )
                        )
        );
    }

    @Test
    void getActivationsBySeverityShouldReturnMatchingActivations() {

        String severity =
                "SV_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        ScenarioActivationResponse first =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        transactionId,
                                        customerId,
                                        "TRIGGERED",
                                        severity
                                )
                        );

        ScenarioActivationResponse second =
                scenarioActivationService
                        .createScenarioActivation(
                                buildRequest(
                                        null,
                                        customerId,
                                        "PENDING",
                                        severity
                                )
                        );

        List<ScenarioActivationResponse> results =
                scenarioActivationService
                        .getActivationsBySeverity(
                                severity
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsActivation(
                        results,
                        first.getActivationId()
                )
        );

        assertTrue(
                containsActivation(
                        results,
                        second.getActivationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                severity.equals(
                                        result.getSeverity()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                scenarioActivationService
                        .getActivationsByScenario(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioActivationService
                        .getActivationsByScenarioVersion(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioActivationService
                        .getActivationsByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioActivationService
                        .getActivationsByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioActivationService
                        .getActivationsByStatus(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                scenarioActivationService
                        .getActivationsBySeverity(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private ScenarioActivationRequest buildRequest(
            UUID targetTransactionId,
            UUID targetCustomerId,
            String activationStatus,
            String severity) {

        ScenarioActivationRequest request =
                new ScenarioActivationRequest();

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

        request.setActivationStatus(
                activationStatus
        );

        request.setSeverity(
                severity
        );

        request.setConfidence(
                new BigDecimal("0.9500")
        );

        request.setRiskScore(
                new BigDecimal("87.5000")
        );

        request.setActivationReason(
                "Scenario activation service integration test"
        );

        request.setDecisionContext(
                Map.of(
                        "source",
                        "service-test",
                        "validated",
                        true
                )
        );

        return request;
    }

    private boolean containsActivation(
            List<ScenarioActivationResponse> results,
            UUID activationId) {

        return results.stream()
                .anyMatch(result ->
                        activationId.equals(
                                result.getActivationId()
                        )
                );
    }
}