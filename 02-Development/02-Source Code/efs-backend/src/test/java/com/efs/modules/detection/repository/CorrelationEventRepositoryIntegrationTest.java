package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.entity.CorrelationEvent;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionEvent;
import com.efs.modules.transaction.repository.TransactionEventRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CorrelationEventRepositoryIntegrationTest {

    @Autowired
    private CorrelationEventRepository correlationEventRepository;

    @Autowired
    private CorrelationRepository correlationRepository;

    @Autowired
    private TransactionEventRepository transactionEventRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID correlationId;
    private UUID transactionId;
    private UUID eventId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "V104-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Correlation"
        );

        customer.setLastName(
                "Event"
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
                "EFS-V104-TXN-" + UUID.randomUUID()
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
                new BigDecimal("350.00")
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
                savedCustomer.getCustomerId()
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "V104-CORR-" + UUID.randomUUID()
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

        TransactionEvent transactionEvent =
                new TransactionEvent();

        transactionEvent.setTransactionId(
                transactionId
        );

        transactionEvent.setEventType(
                "TRANSACTION_RECEIVED"
        );

        transactionEvent.setEventTimestamp(
                now
        );

        transactionEvent.setComponentName(
                "V104_INTEGRATION_TEST"
        );

        transactionEvent.setEventResult(
                "SUCCESS"
        );

        transactionEvent.setSeverity(
                "INFO"
        );

        transactionEvent.setEventMessage(
                "Transaction event for V104 correlation test"
        );

        transactionEvent.setExecutionTimeMs(
                10
        );

        TransactionEvent savedEvent =
                transactionEventRepository.saveAndFlush(
                        transactionEvent
                );

        eventId =
                savedEvent.getEventId();
    }

    @Test
    void shouldSaveAndFindCorrelationEventById() {

        CorrelationEvent correlationEvent =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "PRIMARY",
                        LocalDateTime.now()
                );

        CorrelationEvent saved =
                correlationEventRepository.saveAndFlush(
                        correlationEvent
                );

        assertNotNull(
                saved.getCorrelationEventId()
        );

        Optional<CorrelationEvent> result =
                correlationEventRepository
                        .findByCorrelationEventId(
                                saved.getCorrelationEventId()
                        );

        assertTrue(
                result.isPresent()
        );

        CorrelationEvent found =
                result.get();

        assertEquals(
                saved.getCorrelationEventId(),
                found.getCorrelationEventId()
        );

        assertEquals(
                correlationId,
                found.getCorrelationId()
        );

        assertEquals(
                eventId,
                found.getEventId()
        );

        assertEquals(
                "PRIMARY",
                found.getEventRole()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void shouldPersistCorrelationEventFields() {

        LocalDateTime createdAt =
                LocalDateTime.now();

        CorrelationEvent correlationEvent =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "TRIGGER",
                        createdAt
                );

        CorrelationEvent saved =
                correlationEventRepository.saveAndFlush(
                        correlationEvent
                );

        CorrelationEvent found =
                correlationEventRepository
                        .findById(
                                saved.getCorrelationEventId()
                        )
                        .orElseThrow();

        assertEquals(
                correlationId,
                found.getCorrelationId()
        );

        assertEquals(
                eventId,
                found.getEventId()
        );

        assertEquals(
                "TRIGGER",
                found.getEventRole()
        );

        assertEquals(
                createdAt,
                found.getCreatedAt()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByCreatedAtAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        TransactionEvent secondTransactionEvent =
                createAndSaveTransactionEvent(
                        "SECOND_CORRELATION_EVENT",
                        now.plusSeconds(1)
                );

        CorrelationEvent newer =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                correlationId,
                                secondTransactionEvent.getEventId(),
                                "SECONDARY",
                                now.plusMinutes(5)
                        )
                );

        CorrelationEvent older =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                correlationId,
                                eventId,
                                "PRIMARY",
                                now
                        )
                );

        List<CorrelationEvent> results =
                correlationEventRepository
                        .findByCorrelationIdOrderByCreatedAtAsc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                older.getCorrelationEventId(),
                results.get(0).getCorrelationEventId()
        );

        assertEquals(
                newer.getCorrelationEventId(),
                results.get(1).getCorrelationEventId()
        );
    }

    @Test
    void shouldFindByEventIdOrderedByCreatedAtAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation secondCorrelation =
                createAndSaveCorrelation(
                        "V104-SECOND-CORR-" + UUID.randomUUID(),
                        now
                );

        CorrelationEvent newer =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                secondCorrelation.getCorrelationId(),
                                eventId,
                                "RELATED",
                                now.plusMinutes(5)
                        )
                );

        CorrelationEvent older =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                correlationId,
                                eventId,
                                "PRIMARY",
                                now
                        )
                );

        List<CorrelationEvent> results =
                correlationEventRepository
                        .findByEventIdOrderByCreatedAtAsc(
                                eventId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                older.getCorrelationEventId(),
                results.get(0).getCorrelationEventId()
        );

        assertEquals(
                newer.getCorrelationEventId(),
                results.get(1).getCorrelationEventId()
        );
    }

    @Test
    void shouldFindByEventRoleOrderedByCreatedAtAscending() {

        String role =
                "V104_ROLE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        TransactionEvent secondTransactionEvent =
                createAndSaveTransactionEvent(
                        "ROLE_EVENT",
                        now.plusSeconds(1)
                );

        CorrelationEvent newer =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                correlationId,
                                secondTransactionEvent.getEventId(),
                                role,
                                now.plusMinutes(5)
                        )
                );

        CorrelationEvent older =
                correlationEventRepository.saveAndFlush(
                        createCorrelationEvent(
                                correlationId,
                                eventId,
                                role,
                                now
                        )
                );

        List<CorrelationEvent> results =
                correlationEventRepository
                        .findByEventRoleOrderByCreatedAtAsc(
                                role
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                older.getCorrelationEventId(),
                results.get(0).getCorrelationEventId()
        );

        assertEquals(
                newer.getCorrelationEventId(),
                results.get(1).getCorrelationEventId()
        );
    }

    @Test
    void shouldAllowNullEventRole() {

        CorrelationEvent correlationEvent =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        null,
                        LocalDateTime.now()
                );

        CorrelationEvent saved =
                correlationEventRepository.saveAndFlush(
                        correlationEvent
                );

        assertNotNull(
                saved.getCorrelationEventId()
        );

        assertNull(
                saved.getEventRole()
        );
    }

    @Test
    void shouldRejectDuplicateCorrelationAndEventCombination() {

        CorrelationEvent first =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "PRIMARY",
                        LocalDateTime.now()
                );

        correlationEventRepository.saveAndFlush(
                first
        );

        CorrelationEvent duplicate =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "DUPLICATE",
                        LocalDateTime.now().plusSeconds(1)
                );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> correlationEventRepository.saveAndFlush(
                        duplicate
                )
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                correlationEventRepository
                        .findByCorrelationEventId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationEventRepository
                        .findByCorrelationIdOrderByCreatedAtAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationEventRepository
                        .findByEventIdOrderByCreatedAtAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationEventRepository
                        .findByEventRoleOrderByCreatedAtAsc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private CorrelationEvent createCorrelationEvent(
            UUID targetCorrelationId,
            UUID targetEventId,
            String eventRole,
            LocalDateTime createdAt) {

        CorrelationEvent correlationEvent =
                new CorrelationEvent();

        correlationEvent.setCorrelationId(
                targetCorrelationId
        );

        correlationEvent.setEventId(
                targetEventId
        );

        correlationEvent.setEventRole(
                eventRole
        );

        correlationEvent.setCreatedAt(
                createdAt
        );

        return correlationEvent;
    }

    private TransactionEvent createAndSaveTransactionEvent(
            String eventType,
            LocalDateTime eventTimestamp) {

        TransactionEvent transactionEvent =
                new TransactionEvent();

        transactionEvent.setTransactionId(
                transactionId
        );

        transactionEvent.setEventType(
                eventType
        );

        transactionEvent.setEventTimestamp(
                eventTimestamp
        );

        transactionEvent.setComponentName(
                "V104_INTEGRATION_TEST"
        );

        transactionEvent.setEventResult(
                "SUCCESS"
        );

        transactionEvent.setSeverity(
                "INFO"
        );

        return transactionEventRepository.saveAndFlush(
                transactionEvent
        );
    }

    private Correlation createAndSaveCorrelation(
            String correlationKey,
            LocalDateTime createdAt) {

        Correlation correlation =
                new Correlation();

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                correlationKey
        );

        correlation.setCorrelationType(
                "TRANSACTION"
        );

        correlation.setCorrelationStatus(
                "OPEN"
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

        correlation.setCreatedAt(
                createdAt
        );

        correlation.setUpdatedAt(
                createdAt
        );

        return correlationRepository.saveAndFlush(
                correlation
        );
    }
}