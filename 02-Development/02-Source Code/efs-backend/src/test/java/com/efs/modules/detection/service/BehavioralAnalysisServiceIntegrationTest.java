package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.BehavioralAnalysisRequest;
import com.efs.modules.detection.dto.BehavioralAnalysisResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.BehavioralAnalysisRepository;
import com.efs.modules.detection.repository.CorrelationRepository;
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
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BehavioralAnalysisServiceIntegrationTest {

    @Autowired
    private BehavioralAnalysisServiceInterface behavioralAnalysisService;

    @Autowired
    private BehavioralAnalysisRepository behavioralAnalysisRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CorrelationRepository correlationRepository;

    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "BA-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Behavioral"
        );

        customer.setLastName(
                "Analysis"
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
                "EFS-BA-SVC-" + UUID.randomUUID()
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
                new BigDecimal("750.00")
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

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "BA-CORR-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "BEHAVIORAL"
        );

        correlation.setCorrelationStatus(
                "OPEN"
        );

        correlation.setWindowStart(
                now.minusMinutes(30)
        );

        correlation.setWindowEnd(
                now
        );

        correlation.setEventCount(
                1
        );

        correlation.setMatchedRuleCount(
                (short) 0
        );

        correlation.setConfidence(
                BigDecimal.ZERO
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        Correlation savedCorrelation =
                correlationRepository.saveAndFlush(
                        correlation
                );

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void createBehavioralAnalysisShouldPersistProvidedValuesAndGenerateTimestamps() {

        BehavioralAnalysisRequest request =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        BehavioralAnalysisResponse response =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getBehavioralAnalysisId()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                correlationId,
                response.getCorrelationId()
        );

        assertEquals(
                "COMPLETED",
                response.getAnalysisStatus()
        );

        assertEquals(
                30,
                response.getBaselineWindowDays()
        );

        assertEquals(
                request.getObservedWindowStart(),
                response.getObservedWindowStart()
        );

        assertEquals(
                request.getObservedWindowEnd(),
                response.getObservedWindowEnd()
        );

        assertNotNull(
                response.getBehavioralIndicators()
        );

        assertEquals(
                "HIGH",
                response.getBehavioralIndicators()
                        .get("velocityRisk")
        );

        assertNotNull(
                response.getAnalysisContext()
        );

        assertEquals(
                "SERVICE_TEST",
                response.getAnalysisContext()
                        .get("source")
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                behavioralAnalysisRepository.existsById(
                        response.getBehavioralAnalysisId()
                )
        );
    }

    @Test
    void createBehavioralAnalysisShouldAllowNullTransactionAndCorrelation() {

        BehavioralAnalysisRequest request =
                buildRequest(
                        customerId,
                        null,
                        null,
                        "PENDING"
                );

        BehavioralAnalysisResponse response =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                request
                        );

        assertNotNull(
                response.getBehavioralAnalysisId()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertNull(
                response.getTransactionId()
        );

        assertNull(
                response.getCorrelationId()
        );

        assertEquals(
                "PENDING",
                response.getAnalysisStatus()
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getBehavioralAnalysisByIdShouldReturnExistingAnalysis() {

        BehavioralAnalysisResponse created =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED"
                                )
                        );

        BehavioralAnalysisResponse found =
                behavioralAnalysisService
                        .getBehavioralAnalysisById(
                                created.getBehavioralAnalysisId()
                        );

        assertEquals(
                created.getBehavioralAnalysisId(),
                found.getBehavioralAnalysisId()
        );

        assertEquals(
                customerId,
                found.getCustomerId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                correlationId,
                found.getCorrelationId()
        );

        assertEquals(
                "COMPLETED",
                found.getAnalysisStatus()
        );
    }

    @Test
    void getBehavioralAnalysisByIdShouldThrowWhenAnalysisDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> behavioralAnalysisService
                        .getBehavioralAnalysisById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getAnalysesByCustomerShouldReturnMatchingAnalyses() {

        BehavioralAnalysisResponse first =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED"
                                )
                        );

        BehavioralAnalysisResponse second =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        "PENDING"
                                )
                        );

        List<BehavioralAnalysisResponse> results =
                behavioralAnalysisService
                        .getAnalysesByCustomer(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                first.getBehavioralAnalysisId()
                                        .equals(
                                                result.getBehavioralAnalysisId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                second.getBehavioralAnalysisId()
                                        .equals(
                                                result.getBehavioralAnalysisId()
                                        )
                        )
        );
    }

    @Test
    void getAnalysesByTransactionShouldReturnMatchingAnalysis() {

        BehavioralAnalysisResponse created =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED"
                                )
                        );

        List<BehavioralAnalysisResponse> results =
                behavioralAnalysisService
                        .getAnalysesByTransaction(
                                transactionId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getBehavioralAnalysisId(),
                results.getFirst()
                        .getBehavioralAnalysisId()
        );

        assertEquals(
                transactionId,
                results.getFirst()
                        .getTransactionId()
        );
    }

    @Test
    void getAnalysesByCorrelationShouldReturnMatchingAnalysis() {

        BehavioralAnalysisResponse created =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED"
                                )
                        );

        List<BehavioralAnalysisResponse> results =
                behavioralAnalysisService
                        .getAnalysesByCorrelation(
                                correlationId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getBehavioralAnalysisId(),
                results.getFirst()
                        .getBehavioralAnalysisId()
        );

        assertEquals(
                correlationId,
                results.getFirst()
                        .getCorrelationId()
        );
    }

    @Test
    void getAnalysesByStatusShouldReturnMatchingAnalyses() {

        String status =
                "BA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        BehavioralAnalysisResponse first =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        status
                                )
                        );

        BehavioralAnalysisResponse second =
                behavioralAnalysisService
                        .createBehavioralAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        status
                                )
                        );

        List<BehavioralAnalysisResponse> results =
                behavioralAnalysisService
                        .getAnalysesByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getAnalysisStatus()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                first.getBehavioralAnalysisId()
                                        .equals(
                                                result.getBehavioralAnalysisId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                second.getBehavioralAnalysisId()
                                        .equals(
                                                result.getBehavioralAnalysisId()
                                        )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                behavioralAnalysisService
                        .getAnalysesByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisService
                        .getAnalysesByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisService
                        .getAnalysesByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisService
                        .getAnalysesByStatus(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private BehavioralAnalysisRequest buildRequest(
            UUID targetCustomerId,
            UUID targetTransactionId,
            UUID targetCorrelationId,
            String analysisStatus) {

        LocalDateTime now =
                LocalDateTime.now();

        BehavioralAnalysisRequest request =
                new BehavioralAnalysisRequest();

        request.setCustomerId(
                targetCustomerId
        );

        request.setTransactionId(
                targetTransactionId
        );

        request.setCorrelationId(
                targetCorrelationId
        );

        request.setAnalysisStatus(
                analysisStatus
        );

        request.setBaselineWindowDays(
                30
        );

        request.setObservedWindowStart(
                now.minusHours(1)
        );

        request.setObservedWindowEnd(
                now
        );

        request.setBehavioralIndicators(
                Map.of(
                        "velocityRisk", "HIGH",
                        "channelChange", true
                )
        );

        request.setAnalysisContext(
                Map.of(
                        "source", "SERVICE_TEST",
                        "channel", "WEB"
                )
        );

        return request;
    }
}