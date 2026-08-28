package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.entity.TimelineEvent;
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
class TimelineEventRepositoryIntegrationTest {

    @Autowired
    private TimelineEventRepository timelineEventRepository;

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
                "V109-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Timeline"
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
                customerRepository.saveAndFlush(customer);

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                "EFS-V109-TXN-" + UUID.randomUUID()
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
                new BigDecimal("1090.00")
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
                "V109-CORR-" + UUID.randomUUID()
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
    void shouldSaveAndFindTimelineEventById() {

        TimelineEvent event =
                createTimelineEvent(
                        "TRANSACTION_EVENT",
                        "DETECTION",
                        1,
                        LocalDateTime.now()
                );

        TimelineEvent saved =
                timelineEventRepository.saveAndFlush(event);

        assertNotNull(
                saved.getTimelineEventId()
        );

        Optional<TimelineEvent> result =
                timelineEventRepository.findByTimelineEventId(
                        saved.getTimelineEventId()
                );

        assertTrue(
                result.isPresent()
        );

        TimelineEvent found =
                result.get();

        assertEquals(
                saved.getTimelineEventId(),
                found.getTimelineEventId()
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
                "TRANSACTION_EVENT",
                found.getEventType()
        );
    }

    @Test
    void shouldPersistOptionalFieldsAndJsonData() {

        TimelineEvent event =
                createTimelineEvent(
                        "RISK_EVENT",
                        "RISK",
                        2,
                        LocalDateTime.now()
                );

        UUID referenceId =
                UUID.randomUUID();

        event.setEventReferenceId(
                referenceId
        );

        event.setEventSummary(
                "V109 timeline integration event"
        );

        event.setEventData(
                Map.of(
                        "riskLevel", "HIGH",
                        "score", 87,
                        "reviewRequired", true
                )
        );

        TimelineEvent saved =
                timelineEventRepository.saveAndFlush(event);

        TimelineEvent found =
                timelineEventRepository
                        .findById(saved.getTimelineEventId())
                        .orElseThrow();

        assertEquals(
                referenceId,
                found.getEventReferenceId()
        );

        assertEquals(
                "V109 timeline integration event",
                found.getEventSummary()
        );

        assertEquals(
                "HIGH",
                found.getEventData().get("riskLevel")
        );

        assertEquals(
                87,
                ((Number) found.getEventData().get("score")).intValue()
        );

        assertEquals(
                true,
                found.getEventData().get("reviewRequired")
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByEventTimestampAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent later =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "CUSTOMER_EVENT",
                                "CUSTOMER",
                                2,
                                now
                        )
                );

        TimelineEvent earlier =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "CUSTOMER_EVENT",
                                "CUSTOMER",
                                1,
                                now.minusMinutes(10)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByCustomerIdOrderByEventTimestampAsc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                earlier.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                later.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByEventTimestampAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent later =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "TRANSACTION_EVENT",
                                "TRANSACTION",
                                2,
                                now
                        )
                );

        TimelineEvent earlier =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "TRANSACTION_EVENT",
                                "TRANSACTION",
                                1,
                                now.minusMinutes(10)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByTransactionIdOrderByEventTimestampAsc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                earlier.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                later.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByEventTimestampAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent later =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "CORRELATION_EVENT",
                                "DETECTION",
                                2,
                                now
                        )
                );

        TimelineEvent earlier =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "CORRELATION_EVENT",
                                "DETECTION",
                                1,
                                now.minusMinutes(10)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByCorrelationIdOrderByEventTimestampAsc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                earlier.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                later.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedBySequenceNumberAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent sequenceThree =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "SEQUENCE_EVENT",
                                "DETECTION",
                                3,
                                now.minusMinutes(10)
                        )
                );

        TimelineEvent sequenceOne =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "SEQUENCE_EVENT",
                                "DETECTION",
                                1,
                                now
                        )
                );

        TimelineEvent sequenceTwo =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "SEQUENCE_EVENT",
                                "DETECTION",
                                2,
                                now.minusMinutes(5)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByCorrelationIdOrderBySequenceNumberAsc(
                                correlationId
                        );

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                sequenceOne.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                sequenceTwo.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );

        assertEquals(
                sequenceThree.getTimelineEventId(),
                results.get(2).getTimelineEventId()
        );
    }

    @Test
    void shouldFindByEventTypeOrderedByEventTimestampAscending() {

        String eventType =
                "V109_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent later =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                eventType,
                                "DETECTION",
                                2,
                                now
                        )
                );

        TimelineEvent earlier =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                eventType,
                                "DETECTION",
                                1,
                                now.minusMinutes(5)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByEventTypeOrderByEventTimestampAsc(
                                eventType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                earlier.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                later.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );
    }

    @Test
    void shouldFindByEventSourceOrderedByEventTimestampAscending() {

        String eventSource =
                "V109_SOURCE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now =
                LocalDateTime.now();

        TimelineEvent later =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "SOURCE_EVENT",
                                eventSource,
                                2,
                                now
                        )
                );

        TimelineEvent earlier =
                timelineEventRepository.saveAndFlush(
                        createTimelineEvent(
                                "SOURCE_EVENT",
                                eventSource,
                                1,
                                now.minusMinutes(5)
                        )
                );

        List<TimelineEvent> results =
                timelineEventRepository
                        .findByEventSourceOrderByEventTimestampAsc(
                                eventSource
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                earlier.getTimelineEventId(),
                results.get(0).getTimelineEventId()
        );

        assertEquals(
                later.getTimelineEventId(),
                results.get(1).getTimelineEventId()
        );
    }

    @Test
    void shouldAllowOptionalReferencesAndOptionalEventFields() {

        TimelineEvent event =
                new TimelineEvent();

        event.setEventType(
                "STANDALONE_EVENT"
        );

        event.setEventTimestamp(
                LocalDateTime.now()
        );

        event.setCreatedAt(
                LocalDateTime.now()
        );

        TimelineEvent saved =
                timelineEventRepository.saveAndFlush(event);

        assertNotNull(
                saved.getTimelineEventId()
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
                saved.getEventSource()
        );

        assertNull(
                saved.getEventReferenceId()
        );

        assertNull(
                saved.getSequenceNumber()
        );

        assertNull(
                saved.getEventSummary()
        );

        assertNull(
                saved.getEventData()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                timelineEventRepository
                        .findByTimelineEventId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByCustomerIdOrderByEventTimestampAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByTransactionIdOrderByEventTimestampAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByCorrelationIdOrderByEventTimestampAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByCorrelationIdOrderBySequenceNumberAsc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByEventTypeOrderByEventTimestampAsc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventRepository
                        .findByEventSourceOrderByEventTimestampAsc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private TimelineEvent createTimelineEvent(
            String eventType,
            String eventSource,
            Integer sequenceNumber,
            LocalDateTime eventTimestamp) {

        TimelineEvent event =
                new TimelineEvent();

        event.setCustomerId(
                customerId
        );

        event.setTransactionId(
                transactionId
        );

        event.setCorrelationId(
                correlationId
        );

        event.setEventType(
                eventType
        );

        event.setEventSource(
                eventSource
        );

        event.setSequenceNumber(
                sequenceNumber
        );

        event.setEventTimestamp(
                eventTimestamp
        );

        event.setCreatedAt(
                eventTimestamp
        );

        return event;
    }
}