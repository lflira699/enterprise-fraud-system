package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
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
class CorrelationRepositoryIntegrationTest {

    @Autowired
    private CorrelationRepository correlationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID customerId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "V103-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Detection"
        );

        customer.setLastName(
                "Correlation"
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
                "EFS-V103-TXN-" + UUID.randomUUID()
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
                new BigDecimal("300.00")
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
    }

    @Test
    void shouldSaveAndFindCorrelationById() {

        Correlation correlation =
                createCorrelation(
                        "V103-ID-" + UUID.randomUUID(),
                        "TRANSACTION",
                        "OPEN",
                        LocalDateTime.now()
                );

        Correlation saved =
                correlationRepository.saveAndFlush(
                        correlation
                );

        assertNotNull(
                saved.getCorrelationId()
        );

        Optional<Correlation> result =
                correlationRepository.findByCorrelationId(
                        saved.getCorrelationId()
                );

        assertTrue(
                result.isPresent()
        );

        Correlation found =
                result.get();

        assertEquals(
                saved.getCorrelationId(),
                found.getCorrelationId()
        );

        assertEquals(
                correlation.getCorrelationKey(),
                found.getCorrelationKey()
        );

        assertEquals(
                "TRANSACTION",
                found.getCorrelationType()
        );

        assertEquals(
                "OPEN",
                found.getCorrelationStatus()
        );
    }

    @Test
    void shouldPersistCorrelationFieldsAndJsonContext() {

        Correlation correlation =
                createCorrelation(
                        "V103-FIELDS-" + UUID.randomUUID(),
                        "BEHAVIORAL",
                        "ACTIVE",
                        LocalDateTime.now()
                );

        correlation.setEventCount(
                5
        );

        correlation.setMatchedRuleCount(
                (short) 2
        );

        correlation.setConfidence(
                new BigDecimal("0.8750")
        );

        correlation.setCorrelationContext(
                Map.of(
                        "source", "V103_TEST",
                        "channel", "WEB"
                )
        );

        Correlation saved =
                correlationRepository.saveAndFlush(
                        correlation
                );

        Correlation found =
                correlationRepository
                        .findById(
                                saved.getCorrelationId()
                        )
                        .orElseThrow();

        assertEquals(
                customerId,
                found.getCustomerId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                5,
                found.getEventCount()
        );

        assertEquals(
                (short) 2,
                found.getMatchedRuleCount()
        );

        assertEquals(
                0,
                new BigDecimal("0.8750")
                        .compareTo(found.getConfidence())
        );

        assertNotNull(
                found.getCorrelationContext()
        );

        assertEquals(
                "V103_TEST",
                found.getCorrelationContext().get("source")
        );

        assertEquals(
                "WEB",
                found.getCorrelationContext().get("channel")
        );

        assertNotNull(
                found.getWindowStart()
        );

        assertNotNull(
                found.getWindowEnd()
        );

        assertNotNull(
                found.getCreatedAt()
        );

        assertNotNull(
                found.getUpdatedAt()
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByCreatedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation older =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-CUSTOMER-OLD-" + UUID.randomUUID(),
                                "CUSTOMER",
                                "OPEN",
                                now.minusMinutes(5)
                        )
                );

        Correlation newer =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-CUSTOMER-NEW-" + UUID.randomUUID(),
                                "CUSTOMER",
                                "OPEN",
                                now
                        )
                );

        List<Correlation> results =
                correlationRepository
                        .findByCustomerIdOrderByCreatedAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getCorrelationId(),
                results.get(0).getCorrelationId()
        );

        assertEquals(
                older.getCorrelationId(),
                results.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByCreatedAtDescending() {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation older =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-TXN-OLD-" + UUID.randomUUID(),
                                "TRANSACTION",
                                "OPEN",
                                now.minusMinutes(5)
                        )
                );

        Correlation newer =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-TXN-NEW-" + UUID.randomUUID(),
                                "TRANSACTION",
                                "OPEN",
                                now
                        )
                );

        List<Correlation> results =
                correlationRepository
                        .findByTransactionIdOrderByCreatedAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getCorrelationId(),
                results.get(0).getCorrelationId()
        );

        assertEquals(
                older.getCorrelationId(),
                results.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldFindByCorrelationKeyOrderedByCreatedAtDescending() {

        String correlationKey =
                "V103-KEY-" + UUID.randomUUID();

        LocalDateTime now =
                LocalDateTime.now();

        Correlation older =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                correlationKey,
                                "VELOCITY",
                                "OPEN",
                                now.minusMinutes(5)
                        )
                );

        Correlation newer =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                correlationKey,
                                "VELOCITY",
                                "OPEN",
                                now
                        )
                );

        List<Correlation> results =
                correlationRepository
                        .findByCorrelationKeyOrderByCreatedAtDesc(
                                correlationKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getCorrelationId(),
                results.get(0).getCorrelationId()
        );

        assertEquals(
                older.getCorrelationId(),
                results.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldFindByCorrelationTypeOrderedByCreatedAtDescending() {

        String correlationType =
                "V103_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        Correlation older =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-TYPE-OLD-" + UUID.randomUUID(),
                                correlationType,
                                "OPEN",
                                now.minusMinutes(5)
                        )
                );

        Correlation newer =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-TYPE-NEW-" + UUID.randomUUID(),
                                correlationType,
                                "OPEN",
                                now
                        )
                );

        List<Correlation> results =
                correlationRepository
                        .findByCorrelationTypeOrderByCreatedAtDesc(
                                correlationType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getCorrelationId(),
                results.get(0).getCorrelationId()
        );

        assertEquals(
                older.getCorrelationId(),
                results.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldFindByCorrelationStatusOrderedByCreatedAtDescending() {

        String correlationStatus =
                "V103_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        Correlation older =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-STATUS-OLD-" + UUID.randomUUID(),
                                "TRANSACTION",
                                correlationStatus,
                                now.minusMinutes(5)
                        )
                );

        Correlation newer =
                correlationRepository.saveAndFlush(
                        createCorrelation(
                                "V103-STATUS-NEW-" + UUID.randomUUID(),
                                "TRANSACTION",
                                correlationStatus,
                                now
                        )
                );

        List<Correlation> results =
                correlationRepository
                        .findByCorrelationStatusOrderByCreatedAtDesc(
                                correlationStatus
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getCorrelationId(),
                results.get(0).getCorrelationId()
        );

        assertEquals(
                older.getCorrelationId(),
                results.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldAllowCorrelationWithoutOptionalCustomerAndTransaction() {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                null
        );

        correlation.setTransactionId(
                null
        );

        correlation.setCorrelationKey(
                "V103-OPTIONAL-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "GLOBAL"
        );

        correlation.setCorrelationStatus(
                "OPEN"
        );

        correlation.setWindowStart(
                now.minusMinutes(10)
        );

        correlation.setWindowEnd(
                now
        );

        correlation.setEventCount(
                0
        );

        correlation.setMatchedRuleCount(
                (short) 0
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        Correlation saved =
                correlationRepository.saveAndFlush(
                        correlation
                );

        assertNotNull(
                saved.getCorrelationId()
        );

        assertNull(
                saved.getCustomerId()
        );

        assertNull(
                saved.getTransactionId()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                correlationRepository
                        .findByCustomerIdOrderByCreatedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationRepository
                        .findByTransactionIdOrderByCreatedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationRepository
                        .findByCorrelationKeyOrderByCreatedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationRepository
                        .findByCorrelationTypeOrderByCreatedAtDesc(
                                "UNKNOWN"
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationRepository
                        .findByCorrelationStatusOrderByCreatedAtDesc(
                                "UNKNOWN"
                        )
                        .isEmpty()
        );
    }

    private Correlation createCorrelation(
            String correlationKey,
            String correlationType,
            String correlationStatus,
            LocalDateTime createdAt) {

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                correlationKey
        );

        correlation.setCorrelationType(
                correlationType
        );

        correlation.setCorrelationStatus(
                correlationStatus
        );

        correlation.setWindowStart(
                createdAt.minusMinutes(30)
        );

        correlation.setWindowEnd(
                createdAt
        );

        correlation.setEventCount(
                1
        );

        correlation.setMatchedRuleCount(
                (short) 0
        );

        correlation.setConfidence(
                new BigDecimal("0.5000")
        );

        correlation.setCorrelationContext(
                Map.of(
                        "test", "V103"
                )
        );

        correlation.setCreatedAt(
                createdAt
        );

        correlation.setUpdatedAt(
                createdAt
        );

        return correlation;
    }
}