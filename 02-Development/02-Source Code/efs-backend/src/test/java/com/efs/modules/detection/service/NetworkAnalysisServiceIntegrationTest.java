package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.detection.repository.NetworkAnalysisRepository;
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
class NetworkAnalysisServiceIntegrationTest {

    @Autowired
    private NetworkAnalysisServiceInterface networkAnalysisService;

    @Autowired
    private NetworkAnalysisRepository networkAnalysisRepository;

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
                "NA-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Network"
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
                "EFS-NA-SVC-" + UUID.randomUUID()
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
                "NA-CORR-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "TRANSACTION"
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
                new BigDecimal("0.7500")
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
    void createNetworkAnalysisShouldPersistProvidedValuesAndApplyInitialCounts() {

        NetworkAnalysisRequest request =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "CUSTOMER_NETWORK"
                );

        NetworkAnalysisResponse response =
                networkAnalysisService
                        .createNetworkAnalysis(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getNetworkAnalysisId()
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
                "CUSTOMER_NETWORK",
                response.getNetworkType()
        );

        assertEquals(
                request.getNetworkKey(),
                response.getNetworkKey()
        );

        assertEquals(
                0,
                response.getEntityCount()
        );

        assertEquals(
                0,
                response.getRelationshipCount()
        );

        assertNull(
                response.getNetworkConfidence()
        );

        assertNotNull(
                response.getNetworkIndicators()
        );

        assertEquals(
                true,
                response.getNetworkIndicators()
                        .get("sharedDevice")
        );

        assertEquals(
                4,
                ((Number) response.getNetworkIndicators()
                        .get("linkedAccounts"))
                        .intValue()
        );

        assertNotNull(
                response.getAnalysisContext()
        );

        assertEquals(
                "SERVICE_TEST",
                response.getAnalysisContext()
                        .get("source")
        );

        assertEquals(
                "WEB",
                response.getAnalysisContext()
                        .get("channel")
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                networkAnalysisRepository.existsById(
                        response.getNetworkAnalysisId()
                )
        );
    }

    @Test
    void createNetworkAnalysisShouldAllowOptionalReferencesAndNetworkKey() {

        NetworkAnalysisRequest request =
                new NetworkAnalysisRequest();

        request.setAnalysisStatus(
                "COMPLETED"
        );

        request.setNetworkType(
                "STANDALONE_NETWORK"
        );

        NetworkAnalysisResponse response =
                networkAnalysisService
                        .createNetworkAnalysis(
                                request
                        );

        assertNotNull(
                response.getNetworkAnalysisId()
        );

        assertNull(
                response.getCustomerId()
        );

        assertNull(
                response.getTransactionId()
        );

        assertNull(
                response.getCorrelationId()
        );

        assertNull(
                response.getNetworkKey()
        );

        assertEquals(
                "COMPLETED",
                response.getAnalysisStatus()
        );

        assertEquals(
                "STANDALONE_NETWORK",
                response.getNetworkType()
        );

        assertEquals(
                0,
                response.getEntityCount()
        );

        assertEquals(
                0,
                response.getRelationshipCount()
        );

        assertNull(
                response.getNetworkIndicators()
        );

        assertNull(
                response.getAnalysisContext()
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getNetworkAnalysisByIdShouldReturnExistingAnalysis() {

        NetworkAnalysisResponse created =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CUSTOMER_NETWORK"
                                )
                        );

        NetworkAnalysisResponse found =
                networkAnalysisService
                        .getNetworkAnalysisById(
                                created.getNetworkAnalysisId()
                        );

        assertEquals(
                created.getNetworkAnalysisId(),
                found.getNetworkAnalysisId()
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

        assertEquals(
                "CUSTOMER_NETWORK",
                found.getNetworkType()
        );

        assertEquals(
                0,
                found.getEntityCount()
        );

        assertEquals(
                0,
                found.getRelationshipCount()
        );
    }

    @Test
    void getNetworkAnalysisByIdShouldThrowWhenAnalysisDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> networkAnalysisService
                        .getNetworkAnalysisById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getAnalysesByCustomerShouldReturnMatchingAnalyses() {

        NetworkAnalysisResponse first =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CUSTOMER_NETWORK"
                                )
                        );

        NetworkAnalysisResponse second =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        "PENDING",
                                        "CUSTOMER_NETWORK"
                                )
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByCustomer(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getNetworkAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getNetworkAnalysisId()
                )
        );
    }

    @Test
    void getAnalysesByTransactionShouldReturnMatchingAnalysis() {

        NetworkAnalysisResponse created =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "TRANSACTION_NETWORK"
                                )
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByTransaction(
                                transactionId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getNetworkAnalysisId(),
                results.getFirst()
                        .getNetworkAnalysisId()
        );

        assertEquals(
                transactionId,
                results.getFirst()
                        .getTransactionId()
        );
    }

    @Test
    void getAnalysesByCorrelationShouldReturnMatchingAnalysis() {

        NetworkAnalysisResponse created =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CORRELATION_NETWORK"
                                )
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByCorrelation(
                                correlationId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getNetworkAnalysisId(),
                results.getFirst()
                        .getNetworkAnalysisId()
        );

        assertEquals(
                correlationId,
                results.getFirst()
                        .getCorrelationId()
        );
    }

    @Test
    void getAnalysesByTypeShouldReturnMatchingAnalyses() {

        String networkType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        NetworkAnalysisResponse first =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        networkType
                                )
                        );

        NetworkAnalysisResponse second =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        "PENDING",
                                        networkType
                                )
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByType(
                                networkType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getNetworkAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getNetworkAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                networkType.equals(
                                        result.getNetworkType()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByStatusShouldReturnMatchingAnalyses() {

        String status =
                "NA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        NetworkAnalysisResponse first =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        status,
                                        "CUSTOMER_NETWORK"
                                )
                        );

        NetworkAnalysisResponse second =
                networkAnalysisService
                        .createNetworkAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        status,
                                        "STANDALONE_NETWORK"
                                )
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getNetworkAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getNetworkAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getAnalysisStatus()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByKeyShouldReturnMatchingAnalyses() {

        String networkKey =
                "NA-KEY-" + UUID.randomUUID();

        NetworkAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "CUSTOMER_NETWORK"
                );

        firstRequest.setNetworkKey(
                networkKey
        );

        NetworkAnalysisResponse first =
                networkAnalysisService
                        .createNetworkAnalysis(
                                firstRequest
                        );

        NetworkAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "TRANSACTION_NETWORK"
                );

        secondRequest.setNetworkKey(
                networkKey
        );

        NetworkAnalysisResponse second =
                networkAnalysisService
                        .createNetworkAnalysis(
                                secondRequest
                        );

        List<NetworkAnalysisResponse> results =
                networkAnalysisService
                        .getAnalysesByKey(
                                networkKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getNetworkAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getNetworkAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                networkKey.equals(
                                        result.getNetworkKey()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                networkAnalysisService
                        .getAnalysesByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisService
                        .getAnalysesByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisService
                        .getAnalysesByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisService
                        .getAnalysesByType(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisService
                        .getAnalysesByStatus(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisService
                        .getAnalysesByKey(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private NetworkAnalysisRequest buildRequest(
            UUID targetCustomerId,
            UUID targetTransactionId,
            UUID targetCorrelationId,
            String analysisStatus,
            String networkType) {

        NetworkAnalysisRequest request =
                new NetworkAnalysisRequest();

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

        request.setNetworkType(
                networkType
        );

        request.setNetworkKey(
                "NA-KEY-" + UUID.randomUUID()
        );

        request.setNetworkIndicators(
                Map.of(
                        "sharedDevice", true,
                        "linkedAccounts", 4
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

    private boolean containsAnalysis(
            List<NetworkAnalysisResponse> results,
            UUID networkAnalysisId) {

        return results.stream()
                .anyMatch(result ->
                        networkAnalysisId.equals(
                                result.getNetworkAnalysisId()
                        )
                );
    }
}