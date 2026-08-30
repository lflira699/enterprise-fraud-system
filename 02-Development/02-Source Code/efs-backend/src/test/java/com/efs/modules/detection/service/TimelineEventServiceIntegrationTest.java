package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.TimelineEventRequest;
import com.efs.modules.detection.dto.TimelineEventResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.detection.repository.TimelineEventRepository;
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
class TimelineEventServiceIntegrationTest {

    @Autowired
    private TimelineEventServiceInterface timelineEventService;

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

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TE-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Timeline"
        );

        customer.setLastName(
                "Service"
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
                "EFS-TE-SVC-" + UUID.randomUUID()
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
                new BigDecimal("1500.00")
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
                "TE-SVC-CORR-" + UUID.randomUUID()
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
    void createTimelineEventShouldPersistAndMapProvidedValues() {

        LocalDateTime eventTimestamp =
                LocalDateTime.now()
                        .minusMinutes(5)
                        .withNano(0);

        UUID eventReferenceId =
                UUID.randomUUID();

        TimelineEventRequest request =
                buildRequest(
                        "TRANSACTION_EVENT",
                        eventTimestamp
                );

        request.setCustomerId(
                customerId
        );

        request.setTransactionId(
                transactionId
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventSource(
                "DETECTION"
        );

        request.setEventReferenceId(
                eventReferenceId
        );

        request.setSequenceNumber(
                1
        );

        request.setEventSummary(
                "Timeline event created by service integration test"
        );

        request.setEventData(
                Map.of(
                        "riskLevel", "HIGH",
                        "score", 87,
                        "reviewRequired", true
                )
        );

        TimelineEventResponse response =
                timelineEventService
                        .createTimelineEvent(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getTimelineEventId()
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
                "TRANSACTION_EVENT",
                response.getEventType()
        );

        assertEquals(
                "DETECTION",
                response.getEventSource()
        );

        assertEquals(
                eventReferenceId,
                response.getEventReferenceId()
        );

        assertEquals(
                eventTimestamp,
                response.getEventTimestamp()
        );

        assertEquals(
                Integer.valueOf(1),
                response.getSequenceNumber()
        );

        assertEquals(
                "Timeline event created by service integration test",
                response.getEventSummary()
        );

        assertNotNull(
                response.getEventData()
        );

        assertEquals(
                "HIGH",
                response.getEventData()
                        .get("riskLevel")
        );

        assertEquals(
                87,
                ((Number) response.getEventData()
                        .get("score"))
                        .intValue()
        );

        assertEquals(
                true,
                response.getEventData()
                        .get("reviewRequired")
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                timelineEventRepository.existsById(
                        response.getTimelineEventId()
                )
        );
    }

    @Test
    void createTimelineEventShouldAllowOptionalFieldsToBeNull() {

        LocalDateTime eventTimestamp =
                LocalDateTime.now()
                        .withNano(0);

        TimelineEventRequest request =
                buildRequest(
                        "STANDALONE_EVENT",
                        eventTimestamp
                );

        TimelineEventResponse response =
                timelineEventService
                        .createTimelineEvent(
                                request
                        );

        assertNotNull(
                response.getTimelineEventId()
        );

        assertEquals(
                "STANDALONE_EVENT",
                response.getEventType()
        );

        assertEquals(
                eventTimestamp,
                response.getEventTimestamp()
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
                response.getEventSource()
        );

        assertNull(
                response.getEventReferenceId()
        );

        assertNull(
                response.getSequenceNumber()
        );

        assertNull(
                response.getEventSummary()
        );

        assertNull(
                response.getEventData()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getTimelineEventByIdShouldReturnExistingEvent() {

        TimelineEventRequest request =
                buildRequest(
                        "RISK_EVENT",
                        LocalDateTime.now()
                                .withNano(0)
                );

        request.setCustomerId(
                customerId
        );

        request.setTransactionId(
                transactionId
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventSource(
                "RISK"
        );

        TimelineEventResponse created =
                timelineEventService
                        .createTimelineEvent(
                                request
                        );

        TimelineEventResponse found =
                timelineEventService
                        .getTimelineEventById(
                                created.getTimelineEventId()
                        );

        assertEquals(
                created.getTimelineEventId(),
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
                "RISK_EVENT",
                found.getEventType()
        );

        assertEquals(
                "RISK",
                found.getEventSource()
        );
    }

    @Test
    void getTimelineEventByIdShouldThrowWhenEventDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> timelineEventService
                        .getTimelineEventById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getEventsByCustomerShouldReturnMatchingEvents() {

        TimelineEventResponse first =
                createReferencedEvent(
                        "CUSTOMER_EVENT",
                        "CUSTOMER",
                        1
                );

        TimelineEventResponse second =
                createReferencedEvent(
                        "RISK_EVENT",
                        "RISK",
                        2
                );

        List<TimelineEventResponse> results =
                timelineEventService
                        .getEventsByCustomer(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getTimelineEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getTimelineEventId()
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
    void getEventsByTransactionShouldReturnMatchingEvents() {

        TimelineEventResponse first =
                createReferencedEvent(
                        "TRANSACTION_EVENT",
                        "TRANSACTION",
                        1
                );

        TimelineEventResponse second =
                createReferencedEvent(
                        "RISK_EVENT",
                        "RISK",
                        2
                );

        List<TimelineEventResponse> results =
                timelineEventService
                        .getEventsByTransaction(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getTimelineEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getTimelineEventId()
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
    void getEventsByCorrelationShouldReturnMatchingEvents() {

        TimelineEventResponse first =
                createReferencedEvent(
                        "CORRELATION_EVENT",
                        "DETECTION",
                        1
                );

        TimelineEventResponse second =
                createReferencedEvent(
                        "CORRELATION_EVENT",
                        "DETECTION",
                        2
                );

        List<TimelineEventResponse> results =
                timelineEventService
                        .getEventsByCorrelation(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getTimelineEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getTimelineEventId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                correlationId.equals(
                                        result.getCorrelationId()
                                )
                        )
        );
    }

    @Test
    void getEventsByTypeShouldReturnMatchingEvents() {

        String eventType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TimelineEventResponse first =
                createReferencedEvent(
                        eventType,
                        "SOURCE_A",
                        1
                );

        TimelineEventResponse second =
                createReferencedEvent(
                        eventType,
                        "SOURCE_B",
                        2
                );

        List<TimelineEventResponse> results =
                timelineEventService
                        .getEventsByType(
                                eventType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getTimelineEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getTimelineEventId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                eventType.equals(
                                        result.getEventType()
                                )
                        )
        );
    }

    @Test
    void getEventsBySourceShouldReturnMatchingEvents() {

        String eventSource =
                "SRC_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TimelineEventResponse first =
                createReferencedEvent(
                        "TRANSACTION_EVENT",
                        eventSource,
                        1
                );

        TimelineEventResponse second =
                createReferencedEvent(
                        "RISK_EVENT",
                        eventSource,
                        2
                );

        List<TimelineEventResponse> results =
                timelineEventService
                        .getEventsBySource(
                                eventSource
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getTimelineEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getTimelineEventId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                eventSource.equals(
                                        result.getEventSource()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                timelineEventService
                        .getEventsByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventService
                        .getEventsByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventService
                        .getEventsByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventService
                        .getEventsByType(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                timelineEventService
                        .getEventsBySource(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private TimelineEventRequest buildRequest(
            String eventType,
            LocalDateTime eventTimestamp) {

        TimelineEventRequest request =
                new TimelineEventRequest();

        request.setEventType(
                eventType
        );

        request.setEventTimestamp(
                eventTimestamp
        );

        return request;
    }

    private TimelineEventResponse createReferencedEvent(
            String eventType,
            String eventSource,
            Integer sequenceNumber) {

        TimelineEventRequest request =
                buildRequest(
                        eventType,
                        LocalDateTime.now()
                                .plusSeconds(sequenceNumber)
                                .withNano(0)
                );

        request.setCustomerId(
                customerId
        );

        request.setTransactionId(
                transactionId
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventSource(
                eventSource
        );

        request.setSequenceNumber(
                sequenceNumber
        );

        return timelineEventService
                .createTimelineEvent(
                        request
                );
    }

    private boolean containsEvent(
            List<TimelineEventResponse> results,
            UUID timelineEventId) {

        return results.stream()
                .anyMatch(result ->
                        timelineEventId.equals(
                                result.getTimelineEventId()
                        )
                );
    }
}