package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;
import com.efs.modules.transaction.entity.Transaction;
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
class TransactionEventServiceIntegrationTest {

    @Autowired
    private TransactionEventServiceInterface transactionEventService;

    @Autowired
    private TransactionEventRepository transactionEventRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TEVT-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
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
                "TEVT-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("1000.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "RECEIVED"
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
    void createEventShouldPersistAndMapProvidedValues() {

        LocalDateTime eventTimestamp =
                LocalDateTime.now()
                        .minusMinutes(5)
                        .withNano(0);

        UUID correlationId =
                UUID.randomUUID();

        UUID requestId =
                UUID.randomUUID();

        TransactionEventRequest request =
                buildRequest(
                        "RISK_EVALUATION",
                        "RISK_ENGINE"
                );

        request.setEventTimestamp(
                eventTimestamp
        );

        request.setEventResult(
                "MATCHED"
        );

        request.setSeverity(
                "HIGH"
        );

        request.setCorrelationId(
                correlationId
        );

        request.setRequestId(
                requestId
        );

        request.setEventMessage(
                "Risk evaluation completed"
        );

        request.setExecutionTimeMs(
                125
        );

        TransactionEventResponse response =
                transactionEventService
                        .createEvent(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getEventId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "RISK_EVALUATION",
                response.getEventType()
        );

        assertEquals(
                eventTimestamp,
                response.getEventTimestamp()
        );

        assertEquals(
                "RISK_ENGINE",
                response.getComponentName()
        );

        assertEquals(
                "MATCHED",
                response.getEventResult()
        );

        assertEquals(
                "HIGH",
                response.getSeverity()
        );

        assertEquals(
                correlationId,
                response.getCorrelationId()
        );

        assertEquals(
                requestId,
                response.getRequestId()
        );

        assertEquals(
                "Risk evaluation completed",
                response.getEventMessage()
        );

        assertEquals(
                Integer.valueOf(125),
                response.getExecutionTimeMs()
        );

        assertTrue(
                transactionEventRepository.existsById(
                        response.getEventId()
                )
        );
    }

    @Test
    void createEventShouldApplyEventTimestampWhenNotProvided() {

        TransactionEventRequest request =
                buildRequest(
                        "RECEIVED",
                        "TRANSACTION_ENGINE"
                );

        request.setEventTimestamp(
                null
        );

        TransactionEventResponse response =
                transactionEventService
                        .createEvent(
                                transactionId,
                                request
                        );

        assertNotNull(
                response.getEventId()
        );

        assertNotNull(
                response.getEventTimestamp()
        );

        assertEquals(
                "RECEIVED",
                response.getEventType()
        );

        assertEquals(
                "TRANSACTION_ENGINE",
                response.getComponentName()
        );
    }

    @Test
    void createEventShouldAllowOptionalFieldsToBeNull() {

        TransactionEventResponse response =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "VALIDATION",
                                        "VALIDATION_ENGINE"
                                )
                        );

        assertNotNull(
                response.getEventId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertNotNull(
                response.getEventTimestamp()
        );

        assertNull(
                response.getEventResult()
        );

        assertNull(
                response.getSeverity()
        );

        assertNull(
                response.getCorrelationId()
        );

        assertNull(
                response.getRequestId()
        );

        assertNull(
                response.getEventMessage()
        );

        assertNull(
                response.getExecutionTimeMs()
        );
    }

    @Test
    void createEventShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionEventService
                        .createEvent(
                                UUID.randomUUID(),
                                buildRequest(
                                        "RECEIVED",
                                        "TRANSACTION_ENGINE"
                                )
                        )
        );
    }

    @Test
    void createEventShouldThrowWhenTransactionIsSoftDeleted() {

        Transaction transaction =
                transactionRepository
                        .findById(
                                transactionId
                        )
                        .orElseThrow();

        transaction.setDeletedAt(
                LocalDateTime.now()
        );

        transactionRepository.saveAndFlush(
                transaction
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "RECEIVED",
                                        "TRANSACTION_ENGINE"
                                )
                        )
        );
    }

    @Test
    void getEventByIdShouldReturnExistingEvent() {

        TransactionEventResponse created =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "RISK_EVALUATION",
                                        "RISK_ENGINE"
                                )
                        );

        TransactionEventResponse found =
                transactionEventService
                        .getEventById(
                                created.getEventId()
                        );

        assertEquals(
                created.getEventId(),
                found.getEventId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                "RISK_EVALUATION",
                found.getEventType()
        );

        assertEquals(
                "RISK_ENGINE",
                found.getComponentName()
        );
    }

    @Test
    void getEventByIdShouldThrowWhenEventDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionEventService
                        .getEventById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getEventsByTransactionIdShouldReturnMatchingEvents() {

        TransactionEventResponse first =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "RECEIVED",
                                        "TRANSACTION_ENGINE"
                                )
                        );

        TransactionEventResponse second =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "RISK_EVALUATION",
                                        "RISK_ENGINE"
                                )
                        );

        List<TransactionEventResponse> results =
                transactionEventService
                        .getEventsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getEventId()
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
    void getEventsByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionEventService
                        .getEventsByTransactionId(
                                UUID.randomUUID()
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

        TransactionEventResponse first =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        eventType,
                                        "COMPONENT_A"
                                )
                        );

        TransactionEventResponse second =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        eventType,
                                        "COMPONENT_B"
                                )
                        );

        List<TransactionEventResponse> results =
                transactionEventService
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
                        first.getEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getEventId()
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
    void getEventsByComponentNameShouldReturnMatchingEvents() {

        String componentName =
                "COMP_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TransactionEventResponse first =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "EVENT_A",
                                        componentName
                                )
                        );

        TransactionEventResponse second =
                transactionEventService
                        .createEvent(
                                transactionId,
                                buildRequest(
                                        "EVENT_B",
                                        componentName
                                )
                        );

        List<TransactionEventResponse> results =
                transactionEventService
                        .getEventsByComponentName(
                                componentName
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getEventId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                componentName.equals(
                                        result.getComponentName()
                                )
                        )
        );
    }

    @Test
    void getEventsByCorrelationIdShouldReturnMatchingEvents() {

        UUID correlationId =
                UUID.randomUUID();

        TransactionEventRequest firstRequest =
                buildRequest(
                        "EVENT_A",
                        "RISK_ENGINE"
                );

        firstRequest.setCorrelationId(
                correlationId
        );

        TransactionEventRequest secondRequest =
                buildRequest(
                        "EVENT_B",
                        "RULE_ENGINE"
                );

        secondRequest.setCorrelationId(
                correlationId
        );

        TransactionEventResponse first =
                transactionEventService
                        .createEvent(
                                transactionId,
                                firstRequest
                        );

        TransactionEventResponse second =
                transactionEventService
                        .createEvent(
                                transactionId,
                                secondRequest
                        );

        List<TransactionEventResponse> results =
                transactionEventService
                        .getEventsByCorrelationId(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsEvent(
                        results,
                        first.getEventId()
                )
        );

        assertTrue(
                containsEvent(
                        results,
                        second.getEventId()
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
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                transactionEventService
                        .getEventsByType(
                                "UNKNOWN_" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                transactionEventService
                        .getEventsByComponentName(
                                "UNKNOWN_" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                transactionEventService
                        .getEventsByCorrelationId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private TransactionEventRequest buildRequest(
            String eventType,
            String componentName) {

        TransactionEventRequest request =
                new TransactionEventRequest();

        request.setEventType(
                eventType
        );

        request.setComponentName(
                componentName
        );

        return request;
    }

    private boolean containsEvent(
            List<TransactionEventResponse> results,
            UUID eventId) {

        return results.stream()
                .anyMatch(result ->
                        eventId.equals(
                                result.getEventId()
                        )
                );
    }
}