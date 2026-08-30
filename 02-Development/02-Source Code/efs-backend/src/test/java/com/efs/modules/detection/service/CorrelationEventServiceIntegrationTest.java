package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.CorrelationEventRequest;
import com.efs.modules.detection.dto.CorrelationEventResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationEventRepository;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionEvent;
import com.efs.modules.transaction.repository.TransactionEventRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CorrelationEventServiceIntegrationTest {

    @Autowired
    private CorrelationEventServiceInterface correlationEventService;

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

    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private UUID eventId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "CE-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Correlation"
        );

        customer.setLastName(
                "Event Service"
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
                "EFS-CORR-EVENT-" + UUID.randomUUID()
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
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();

        Correlation correlation =
                createAndSaveCorrelation(
                        "BASE-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                );

        correlationId =
                correlation.getCorrelationId();

        TransactionEvent transactionEvent =
                createAndSaveTransactionEvent(
                        "TRANSACTION_RECEIVED"
                );

        eventId =
                transactionEvent.getEventId();
    }

    @Test
    void createCorrelationEventShouldPersistProvidedValuesAndGenerateCreatedAt() {

        CorrelationEventRequest request =
                buildRequest(
                        correlationId,
                        eventId,
                        "PRIMARY"
                );

        CorrelationEventResponse response =
                correlationEventService
                        .createCorrelationEvent(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getCorrelationEventId()
        );

        assertEquals(
                correlationId,
                response.getCorrelationId()
        );

        assertEquals(
                eventId,
                response.getEventId()
        );

        assertEquals(
                "PRIMARY",
                response.getEventRole()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                correlationEventRepository.existsById(
                        response.getCorrelationEventId()
                )
        );
    }

    @Test
    void createCorrelationEventShouldAllowNullEventRole() {

        CorrelationEventRequest request =
                buildRequest(
                        correlationId,
                        eventId,
                        null
                );

        CorrelationEventResponse response =
                correlationEventService
                        .createCorrelationEvent(
                                request
                        );

        assertNotNull(
                response.getCorrelationEventId()
        );

        assertEquals(
                correlationId,
                response.getCorrelationId()
        );

        assertEquals(
                eventId,
                response.getEventId()
        );

        assertNull(
                response.getEventRole()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getCorrelationEventByIdShouldReturnExistingCorrelationEvent() {

        CorrelationEventResponse created =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        eventId,
                                        "TRIGGER"
                                )
                        );

        CorrelationEventResponse found =
                correlationEventService
                        .getCorrelationEventById(
                                created.getCorrelationEventId()
                        );

        assertEquals(
                created.getCorrelationEventId(),
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
                "TRIGGER",
                found.getEventRole()
        );

        assertNotNull(
                found.getCreatedAt()
        );
    }

    @Test
    void getCorrelationEventByIdShouldThrowWhenCorrelationEventDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> correlationEventService
                        .getCorrelationEventById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getEventsByCorrelationShouldReturnMatchingEvents() {

        TransactionEvent secondEvent =
                createAndSaveTransactionEvent(
                        "SECOND_EVENT"
                );

        CorrelationEventResponse first =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        eventId,
                                        "PRIMARY"
                                )
                        );

        CorrelationEventResponse second =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        secondEvent.getEventId(),
                                        "SECONDARY"
                                )
                        );

        List<CorrelationEventResponse> results =
                correlationEventService
                        .getEventsByCorrelation(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                first.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                second.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );
    }

    @Test
    void getCorrelationsByEventShouldReturnMatchingCorrelations() {

        Correlation secondCorrelation =
                createAndSaveCorrelation(
                        "SECOND-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                );

        CorrelationEventResponse first =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        eventId,
                                        "PRIMARY"
                                )
                        );

        CorrelationEventResponse second =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        secondCorrelation
                                                .getCorrelationId(),
                                        eventId,
                                        "RELATED"
                                )
                        );

        List<CorrelationEventResponse> results =
                correlationEventService
                        .getCorrelationsByEvent(
                                eventId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                first.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                second.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );
    }

    @Test
    void getEventsByRoleShouldReturnMatchingEvents() {

        String eventRole =
                "ROLE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TransactionEvent secondEvent =
                createAndSaveTransactionEvent(
                        "ROLE_EVENT"
                );

        CorrelationEventResponse first =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        eventId,
                                        eventRole
                                )
                        );

        CorrelationEventResponse second =
                correlationEventService
                        .createCorrelationEvent(
                                buildRequest(
                                        correlationId,
                                        secondEvent.getEventId(),
                                        eventRole
                                )
                        );

        List<CorrelationEventResponse> results =
                correlationEventService
                        .getEventsByRole(
                                eventRole
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                eventRole.equals(
                                        result.getEventRole()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                first.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                second.getCorrelationEventId()
                                        .equals(
                                                result.getCorrelationEventId()
                                        )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                correlationEventService
                        .getEventsByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationEventService
                        .getCorrelationsByEvent(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                correlationEventService
                        .getEventsByRole(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private CorrelationEventRequest buildRequest(
            UUID targetCorrelationId,
            UUID targetEventId,
            String eventRole) {

        CorrelationEventRequest request =
                new CorrelationEventRequest();

        request.setCorrelationId(
                targetCorrelationId
        );

        request.setEventId(
                targetEventId
        );

        request.setEventRole(
                eventRole
        );

        return request;
    }

    private Correlation createAndSaveCorrelation(
            String keySuffix) {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "CORR-EVENT-SVC-" + keySuffix
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
                BigDecimal.ZERO
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        return correlationRepository.saveAndFlush(
                correlation
        );
    }

    private TransactionEvent createAndSaveTransactionEvent(
            String eventType) {

        LocalDateTime now =
                LocalDateTime.now();

        TransactionEvent transactionEvent =
                new TransactionEvent();

        transactionEvent.setTransactionId(
                transactionId
        );

        transactionEvent.setEventType(
                eventType
        );

        transactionEvent.setEventTimestamp(
                now
        );

        transactionEvent.setComponentName(
                "CORRELATION_EVENT_SERVICE_TEST"
        );

        transactionEvent.setEventResult(
                "SUCCESS"
        );

        transactionEvent.setSeverity(
                "INFO"
        );

        transactionEvent.setEventMessage(
                "Correlation event service integration test"
        );

        transactionEvent.setExecutionTimeMs(
                10
        );

        return transactionEventRepository.saveAndFlush(
                transactionEvent
        );
    }
}