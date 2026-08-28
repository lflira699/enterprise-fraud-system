package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.entity.NetworkAnalysis;
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
class NetworkAnalysisRepositoryIntegrationTest {

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

        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();

        customer.setCustomerNumber(
                "V106-" + UUID.randomUUID()
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
                customerRepository.saveAndFlush(customer);

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                "EFS-V106-TXN-" + UUID.randomUUID()
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
                transactionRepository.saveAndFlush(transaction);

        transactionId =
                savedTransaction.getTransactionId();

        Correlation correlation = new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "V106-CORR-" + UUID.randomUUID()
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
                correlationRepository.saveAndFlush(correlation);

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void shouldSaveAndFindNetworkAnalysisById() {

        NetworkAnalysis analysis =
                createNetworkAnalysis(
                        "COMPLETED",
                        "CUSTOMER_NETWORK",
                        "V106-NETWORK-" + UUID.randomUUID(),
                        LocalDateTime.now()
                );

        NetworkAnalysis saved =
                networkAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getNetworkAnalysisId()
        );

        Optional<NetworkAnalysis> result =
                networkAnalysisRepository
                        .findByNetworkAnalysisId(
                                saved.getNetworkAnalysisId()
                        );

        assertTrue(
                result.isPresent()
        );

        NetworkAnalysis found = result.get();

        assertEquals(
                saved.getNetworkAnalysisId(),
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
    }

    @Test
    void shouldPersistNetworkMetricsAndJsonFields() {

        NetworkAnalysis analysis =
                createNetworkAnalysis(
                        "COMPLETED",
                        "RELATIONSHIP_NETWORK",
                        "V106-METRICS-" + UUID.randomUUID(),
                        LocalDateTime.now()
                );

        analysis.setEntityCount(
                12
        );

        analysis.setRelationshipCount(
                18
        );

        analysis.setNetworkConfidence(
                new BigDecimal("0.8750")
        );

        analysis.setNetworkIndicators(
                Map.of(
                        "sharedDevice", true,
                        "linkedAccounts", 4
                )
        );

        analysis.setAnalysisContext(
                Map.of(
                        "channel", "WEB",
                        "country", "GT"
                )
        );

        NetworkAnalysis saved =
                networkAnalysisRepository.saveAndFlush(analysis);

        NetworkAnalysis found =
                networkAnalysisRepository
                        .findById(saved.getNetworkAnalysisId())
                        .orElseThrow();

        assertEquals(
                12,
                found.getEntityCount()
        );

        assertEquals(
                18,
                found.getRelationshipCount()
        );

        assertEquals(
                new BigDecimal("0.8750"),
                found.getNetworkConfidence()
        );

        assertEquals(
                true,
                found.getNetworkIndicators().get("sharedDevice")
        );

        assertEquals(
                4,
                ((Number) found.getNetworkIndicators()
                        .get("linkedAccounts")).intValue()
        );

        assertEquals(
                "WEB",
                found.getAnalysisContext().get("channel")
        );

        assertEquals(
                "GT",
                found.getAnalysisContext().get("country")
        );

        assertNotNull(
                found.getAnalyzedAt()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CUSTOMER_NETWORK",
                                "V106-CUSTOMER-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(10)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CUSTOMER_NETWORK",
                                "V106-CUSTOMER-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "TRANSACTION_NETWORK",
                                "V106-TXN-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(15)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "TRANSACTION_NETWORK",
                                "V106-TXN-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CORRELATION_NETWORK",
                                "V106-CORR-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(20)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CORRELATION_NETWORK",
                                "V106-CORR-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldFindByNetworkTypeOrderedByAnalyzedAtDescending() {

        String networkType =
                "V106_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                networkType,
                                "V106-TYPE-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(5)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                networkType,
                                "V106-TYPE-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByNetworkTypeOrderByAnalyzedAtDesc(
                                networkType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldFindByAnalysisStatusOrderedByAnalyzedAtDescending() {

        String status =
                "V106_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                status,
                                "CUSTOMER_NETWORK",
                                "V106-STATUS-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(5)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                status,
                                "CUSTOMER_NETWORK",
                                "V106-STATUS-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldFindByNetworkKeyOrderedByAnalyzedAtDescending() {

        String networkKey =
                "V106-KEY-" + UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        NetworkAnalysis older =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CUSTOMER_NETWORK",
                                networkKey,
                                now.minusMinutes(5)
                        )
                );

        NetworkAnalysis newer =
                networkAnalysisRepository.saveAndFlush(
                        createNetworkAnalysis(
                                "COMPLETED",
                                "CUSTOMER_NETWORK",
                                networkKey,
                                now
                        )
                );

        List<NetworkAnalysis> results =
                networkAnalysisRepository
                        .findByNetworkKeyOrderByAnalyzedAtDesc(
                                networkKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getNetworkAnalysisId(),
                results.get(0).getNetworkAnalysisId()
        );

        assertEquals(
                older.getNetworkAnalysisId(),
                results.get(1).getNetworkAnalysisId()
        );
    }

    @Test
    void shouldAllowOptionalReferencesAndNetworkKey() {

        NetworkAnalysis analysis =
                createNetworkAnalysis(
                        "COMPLETED",
                        "STANDALONE_NETWORK",
                        null,
                        LocalDateTime.now()
                );

        analysis.setCustomerId(
                null
        );

        analysis.setTransactionId(
                null
        );

        analysis.setCorrelationId(
                null
        );

        NetworkAnalysis saved =
                networkAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getNetworkAnalysisId()
        );

        assertNull(
                saved.getCustomerId()
        );

        assertNull(
                saved.getTransactionId()
        );

        assertNull(
                saved.getCorrelationId()
        );

        assertNull(
                saved.getNetworkKey()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                networkAnalysisRepository
                        .findByNetworkAnalysisId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByNetworkTypeOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                networkAnalysisRepository
                        .findByNetworkKeyOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private NetworkAnalysis createNetworkAnalysis(
            String analysisStatus,
            String networkType,
            String networkKey,
            LocalDateTime analyzedAt) {

        NetworkAnalysis analysis =
                new NetworkAnalysis();

        analysis.setCustomerId(
                customerId
        );

        analysis.setTransactionId(
                transactionId
        );

        analysis.setCorrelationId(
                correlationId
        );

        analysis.setAnalysisStatus(
                analysisStatus
        );

        analysis.setNetworkType(
                networkType
        );

        analysis.setNetworkKey(
                networkKey
        );

        /*
         * Although V106 defines database defaults for these fields,
         * the entity maps them as non-null values without a Hibernate
         * generated/default annotation. Therefore the integration
         * fixture supplies the explicit values expected by the
         * persistence contract.
         */
        analysis.setEntityCount(
                0
        );

        analysis.setRelationshipCount(
                0
        );

        analysis.setAnalyzedAt(
                analyzedAt
        );

        analysis.setCreatedAt(
                analyzedAt
        );

        return analysis;
    }
}