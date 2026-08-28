package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.entity.RelationshipAnalysis;
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
class RelationshipAnalysisRepositoryIntegrationTest {

    @Autowired
    private RelationshipAnalysisRepository relationshipAnalysisRepository;

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
                "V108-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Relationship"
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
                "EFS-V108-TXN-" + UUID.randomUUID()
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
                new BigDecimal("1100.00")
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
                "V108-CORR-" + UUID.randomUUID()
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
    void shouldSaveAndFindRelationshipAnalysisById() {

        RelationshipAnalysis analysis =
                createRelationshipAnalysis(
                        "COMPLETED",
                        "CUSTOMER_TO_BENEFICIARY",
                        "CUSTOMER",
                        "CUS-" + UUID.randomUUID(),
                        "BENEFICIARY",
                        "BEN-" + UUID.randomUUID(),
                        LocalDateTime.now()
                );

        RelationshipAnalysis saved =
                relationshipAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getRelationshipAnalysisId()
        );

        Optional<RelationshipAnalysis> result =
                relationshipAnalysisRepository
                        .findByRelationshipAnalysisId(
                                saved.getRelationshipAnalysisId()
                        );

        assertTrue(
                result.isPresent()
        );

        RelationshipAnalysis found =
                result.get();

        assertEquals(
                saved.getRelationshipAnalysisId(),
                found.getRelationshipAnalysisId()
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
                "CUSTOMER_TO_BENEFICIARY",
                found.getRelationshipType()
        );
    }

    @Test
    void shouldPersistRelationshipFieldsMetricsAndJson() {

        RelationshipAnalysis analysis =
                createRelationshipAnalysis(
                        "COMPLETED",
                        "SHARED_DEVICE",
                        "CUSTOMER",
                        "CUS-" + UUID.randomUUID(),
                        "DEVICE",
                        "DEV-" + UUID.randomUUID(),
                        LocalDateTime.now()
                );

        analysis.setRelationshipStrength(
                new BigDecimal("0.8750")
        );

        analysis.setEntityCount(
                4
        );

        analysis.setRelationshipCount(
                6
        );

        analysis.setRelationshipIndicators(
                Map.of(
                        "sharedDevice", true,
                        "linkedEntities", 4
                )
        );

        analysis.setAnalysisContext(
                Map.of(
                        "channel", "WEB",
                        "source", "V108_TEST"
                )
        );

        RelationshipAnalysis saved =
                relationshipAnalysisRepository.saveAndFlush(analysis);

        RelationshipAnalysis found =
                relationshipAnalysisRepository
                        .findById(
                                saved.getRelationshipAnalysisId()
                        )
                        .orElseThrow();

        assertEquals(
                "CUSTOMER",
                found.getSourceEntityType()
        );

        assertEquals(
                "DEVICE",
                found.getTargetEntityType()
        );

        assertEquals(
                new BigDecimal("0.8750"),
                found.getRelationshipStrength()
        );

        assertEquals(
                4,
                found.getEntityCount()
        );

        assertEquals(
                6,
                found.getRelationshipCount()
        );

        assertEquals(
                true,
                found.getRelationshipIndicators()
                        .get("sharedDevice")
        );

        assertEquals(
                4,
                ((Number) found.getRelationshipIndicators()
                        .get("linkedEntities")).intValue()
        );

        assertEquals(
                "WEB",
                found.getAnalysisContext().get("channel")
        );

        assertEquals(
                "V108_TEST",
                found.getAnalysisContext().get("source")
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "CUSTOMER_LINK",
                                "CUSTOMER",
                                "CUS-OLD-" + UUID.randomUUID(),
                                "ACCOUNT",
                                "ACC-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(10)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "CUSTOMER_LINK",
                                "CUSTOMER",
                                "CUS-NEW-" + UUID.randomUUID(),
                                "ACCOUNT",
                                "ACC-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "TRANSACTION_LINK",
                                "TRANSACTION",
                                "TXN-OLD-" + UUID.randomUUID(),
                                "BENEFICIARY",
                                "BEN-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(10)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "TRANSACTION_LINK",
                                "TRANSACTION",
                                "TXN-NEW-" + UUID.randomUUID(),
                                "BENEFICIARY",
                                "BEN-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "CORRELATION_LINK",
                                "CUSTOMER",
                                "CUS-CORR-OLD-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-CORR-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(10)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "CORRELATION_LINK",
                                "CUSTOMER",
                                "CUS-CORR-NEW-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-CORR-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindByRelationshipTypeOrderedByAnalyzedAtDescending() {

        String relationshipType =
                "V108_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                relationshipType,
                                "CUSTOMER",
                                "CUS-TYPE-OLD-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-TYPE-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(5)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                relationshipType,
                                "CUSTOMER",
                                "CUS-TYPE-NEW-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-TYPE-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByRelationshipTypeOrderByAnalyzedAtDesc(
                                relationshipType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindBySourceEntityKeyOrderedByAnalyzedAtDescending() {

        String sourceEntityKey =
                "V108-SOURCE-" + UUID.randomUUID();

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "SOURCE_LINK",
                                "CUSTOMER",
                                sourceEntityKey,
                                "DEVICE",
                                "DEV-SOURCE-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(5)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "SOURCE_LINK",
                                "CUSTOMER",
                                sourceEntityKey,
                                "DEVICE",
                                "DEV-SOURCE-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findBySourceEntityKeyOrderByAnalyzedAtDesc(
                                sourceEntityKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindByTargetEntityKeyOrderedByAnalyzedAtDescending() {

        String targetEntityKey =
                "V108-TARGET-" + UUID.randomUUID();

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "TARGET_LINK",
                                "CUSTOMER",
                                "CUS-TARGET-OLD-" + UUID.randomUUID(),
                                "DEVICE",
                                targetEntityKey,
                                now.minusMinutes(5)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                "COMPLETED",
                                "TARGET_LINK",
                                "CUSTOMER",
                                "CUS-TARGET-NEW-" + UUID.randomUUID(),
                                "DEVICE",
                                targetEntityKey,
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByTargetEntityKeyOrderByAnalyzedAtDesc(
                                targetEntityKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldFindByAnalysisStatusOrderedByAnalyzedAtDescending() {

        String status =
                "V108_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        RelationshipAnalysis older =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                status,
                                "STATUS_LINK",
                                "CUSTOMER",
                                "CUS-STATUS-OLD-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-STATUS-OLD-" + UUID.randomUUID(),
                                now.minusMinutes(5)
                        )
                );

        RelationshipAnalysis newer =
                relationshipAnalysisRepository.saveAndFlush(
                        createRelationshipAnalysis(
                                status,
                                "STATUS_LINK",
                                "CUSTOMER",
                                "CUS-STATUS-NEW-" + UUID.randomUUID(),
                                "DEVICE",
                                "DEV-STATUS-NEW-" + UUID.randomUUID(),
                                now
                        )
                );

        List<RelationshipAnalysis> results =
                relationshipAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getRelationshipAnalysisId(),
                results.get(0).getRelationshipAnalysisId()
        );

        assertEquals(
                older.getRelationshipAnalysisId(),
                results.get(1).getRelationshipAnalysisId()
        );
    }

    @Test
    void shouldAllowOptionalReferencesAndOptionalAnalysisFields() {

        RelationshipAnalysis analysis =
                createRelationshipAnalysis(
                        "COMPLETED",
                        "STANDALONE_LINK",
                        "CUSTOMER",
                        "CUS-" + UUID.randomUUID(),
                        "DEVICE",
                        "DEV-" + UUID.randomUUID(),
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

        analysis.setRelationshipStrength(
                null
        );

        analysis.setRelationshipIndicators(
                null
        );

        analysis.setAnalysisContext(
                null
        );

        RelationshipAnalysis saved =
                relationshipAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getRelationshipAnalysisId()
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
                saved.getRelationshipStrength()
        );

        assertNull(
                saved.getRelationshipIndicators()
        );

        assertNull(
                saved.getAnalysisContext()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                relationshipAnalysisRepository
                        .findByRelationshipAnalysisId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByRelationshipTypeOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findBySourceEntityKeyOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByTargetEntityKeyOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private RelationshipAnalysis createRelationshipAnalysis(
            String analysisStatus,
            String relationshipType,
            String sourceEntityType,
            String sourceEntityKey,
            String targetEntityType,
            String targetEntityKey,
            LocalDateTime analyzedAt) {

        RelationshipAnalysis analysis =
                new RelationshipAnalysis();

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

        analysis.setRelationshipType(
                relationshipType
        );

        analysis.setSourceEntityType(
                sourceEntityType
        );

        analysis.setSourceEntityKey(
                sourceEntityKey
        );

        analysis.setTargetEntityType(
                targetEntityType
        );

        analysis.setTargetEntityKey(
                targetEntityKey
        );

        /*
         * V108 defines database defaults for these columns.
         * The entity maps them as non-null fields, therefore
         * the integration fixture provides explicit values.
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