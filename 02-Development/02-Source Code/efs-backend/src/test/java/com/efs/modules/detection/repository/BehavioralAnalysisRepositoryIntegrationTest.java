package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.BehavioralAnalysis;
import com.efs.modules.detection.entity.Correlation;
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
class BehavioralAnalysisRepositoryIntegrationTest {

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

        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();

        customer.setCustomerNumber(
                "V105-" + UUID.randomUUID()
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
                customerRepository.saveAndFlush(customer);

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-V105-TXN-" + UUID.randomUUID()
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
                transactionRepository.saveAndFlush(transaction);

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
                "V105-CORR-" + UUID.randomUUID()
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
    void shouldSaveAndFindBehavioralAnalysisById() {

        BehavioralAnalysis analysis =
                createBehavioralAnalysis(
                        "COMPLETED",
                        LocalDateTime.now()
                );

        BehavioralAnalysis saved =
                behavioralAnalysisRepository.saveAndFlush(
                        analysis
                );

        assertNotNull(
                saved.getBehavioralAnalysisId()
        );

        Optional<BehavioralAnalysis> result =
                behavioralAnalysisRepository
                        .findByBehavioralAnalysisId(
                                saved.getBehavioralAnalysisId()
                        );

        assertTrue(
                result.isPresent()
        );

        BehavioralAnalysis found =
                result.get();

        assertEquals(
                saved.getBehavioralAnalysisId(),
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
    void shouldPersistBehavioralMetricsAndJsonFields() {

        BehavioralAnalysis analysis =
                createBehavioralAnalysis(
                        "COMPLETED",
                        LocalDateTime.now()
                );

        analysis.setBaselineWindowDays(
                30
        );

        analysis.setObservedWindowStart(
                LocalDateTime.now().minusDays(1)
        );

        analysis.setObservedWindowEnd(
                LocalDateTime.now()
        );

        analysis.setAmountDeviation(
                new BigDecimal("1.2500")
        );

        analysis.setFrequencyDeviation(
                new BigDecimal("2.5000")
        );

        analysis.setVelocityDeviation(
                new BigDecimal("3.7500")
        );

        analysis.setChannelDeviation(
                new BigDecimal("0.5000")
        );

        analysis.setGeographicDeviation(
                new BigDecimal("4.2500")
        );

        analysis.setTemporalDeviation(
                new BigDecimal("1.7500")
        );

        analysis.setBehavioralConfidence(
                new BigDecimal("0.8750")
        );

        analysis.setBehavioralIndicators(
                Map.of(
                        "amountPattern", "DEVIATION",
                        "velocityPattern", "ELEVATED"
                )
        );

        analysis.setAnalysisContext(
                Map.of(
                        "channel", "WEB",
                        "country", "GT"
                )
        );

        BehavioralAnalysis saved =
                behavioralAnalysisRepository.saveAndFlush(
                        analysis
                );

        BehavioralAnalysis found =
                behavioralAnalysisRepository
                        .findById(
                                saved.getBehavioralAnalysisId()
                        )
                        .orElseThrow();

        assertEquals(
                30,
                found.getBaselineWindowDays()
        );

        assertEquals(
                new BigDecimal("1.2500"),
                found.getAmountDeviation()
        );

        assertEquals(
                new BigDecimal("2.5000"),
                found.getFrequencyDeviation()
        );

        assertEquals(
                new BigDecimal("3.7500"),
                found.getVelocityDeviation()
        );

        assertEquals(
                new BigDecimal("0.5000"),
                found.getChannelDeviation()
        );

        assertEquals(
                new BigDecimal("4.2500"),
                found.getGeographicDeviation()
        );

        assertEquals(
                new BigDecimal("1.7500"),
                found.getTemporalDeviation()
        );

        assertEquals(
                new BigDecimal("0.8750"),
                found.getBehavioralConfidence()
        );

        assertEquals(
                "DEVIATION",
                found.getBehavioralIndicators()
                        .get("amountPattern")
        );

        assertEquals(
                "ELEVATED",
                found.getBehavioralIndicators()
                        .get("velocityPattern")
        );

        assertEquals(
                "WEB",
                found.getAnalysisContext()
                        .get("channel")
        );

        assertEquals(
                "GT",
                found.getAnalysisContext()
                        .get("country")
        );

        assertNotNull(
                found.getObservedWindowStart()
        );

        assertNotNull(
                found.getObservedWindowEnd()
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        BehavioralAnalysis older =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now.minusMinutes(10)
                        )
                );

        BehavioralAnalysis newer =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now
                        )
                );

        List<BehavioralAnalysis> results =
                behavioralAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getBehavioralAnalysisId(),
                results.get(0).getBehavioralAnalysisId()
        );

        assertEquals(
                older.getBehavioralAnalysisId(),
                results.get(1).getBehavioralAnalysisId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        BehavioralAnalysis older =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now.minusMinutes(15)
                        )
                );

        BehavioralAnalysis newer =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now
                        )
                );

        List<BehavioralAnalysis> results =
                behavioralAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getBehavioralAnalysisId(),
                results.get(0).getBehavioralAnalysisId()
        );

        assertEquals(
                older.getBehavioralAnalysisId(),
                results.get(1).getBehavioralAnalysisId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        BehavioralAnalysis older =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now.minusMinutes(20)
                        )
                );

        BehavioralAnalysis newer =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                "COMPLETED",
                                now
                        )
                );

        List<BehavioralAnalysis> results =
                behavioralAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getBehavioralAnalysisId(),
                results.get(0).getBehavioralAnalysisId()
        );

        assertEquals(
                older.getBehavioralAnalysisId(),
                results.get(1).getBehavioralAnalysisId()
        );
    }

    @Test
    void shouldFindByAnalysisStatusOrderedByAnalyzedAtDescending() {

        String status =
                "V105_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        BehavioralAnalysis older =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                status,
                                now.minusMinutes(5)
                        )
                );

        BehavioralAnalysis newer =
                behavioralAnalysisRepository.saveAndFlush(
                        createBehavioralAnalysis(
                                status,
                                now
                        )
                );

        List<BehavioralAnalysis> results =
                behavioralAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getBehavioralAnalysisId(),
                results.get(0).getBehavioralAnalysisId()
        );

        assertEquals(
                older.getBehavioralAnalysisId(),
                results.get(1).getBehavioralAnalysisId()
        );
    }

    @Test
    void shouldAllowOptionalTransactionAndCorrelation() {

        BehavioralAnalysis analysis =
                createBehavioralAnalysis(
                        "COMPLETED",
                        LocalDateTime.now()
                );

        analysis.setTransactionId(
                null
        );

        analysis.setCorrelationId(
                null
        );

        BehavioralAnalysis saved =
                behavioralAnalysisRepository.saveAndFlush(
                        analysis
                );

        assertNotNull(
                saved.getBehavioralAnalysisId()
        );

        assertNull(
                saved.getTransactionId()
        );

        assertNull(
                saved.getCorrelationId()
        );

        assertEquals(
                customerId,
                saved.getCustomerId()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                behavioralAnalysisRepository
                        .findByBehavioralAnalysisId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                behavioralAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private BehavioralAnalysis createBehavioralAnalysis(
            String analysisStatus,
            LocalDateTime analyzedAt) {

        BehavioralAnalysis analysis =
                new BehavioralAnalysis();

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

        analysis.setAnalyzedAt(
                analyzedAt
        );

        analysis.setCreatedAt(
                analyzedAt
        );

        return analysis;
    }
}