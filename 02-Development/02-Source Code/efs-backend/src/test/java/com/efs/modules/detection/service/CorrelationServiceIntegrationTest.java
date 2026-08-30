package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.CorrelationRequest;
import com.efs.modules.detection.dto.CorrelationResponse;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
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
class CorrelationServiceIntegrationTest {

    @Autowired
    private CorrelationServiceInterface correlationService;

    @Autowired
    private CorrelationRepository correlationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void createCorrelationShouldPersistProvidedValuesAndDefaultConfidenceToZero() {

        Customer customer = createCustomer();

        Transaction transaction =
                createTransaction(customer.getCustomerId());

        CorrelationRequest request =
                buildCorrelationRequest(
                        customer.getCustomerId(),
                        transaction.getTransactionId()
                );

        CorrelationResponse response =
                correlationService.createCorrelation(request);

        assertNotNull(response);
        assertNotNull(response.getCorrelationId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                transaction.getTransactionId(),
                response.getTransactionId()
        );

        assertEquals(
                request.getCorrelationKey(),
                response.getCorrelationKey()
        );

        assertEquals(
                "TRANSACTION",
                response.getCorrelationType()
        );

        assertEquals(
                "OPEN",
                response.getCorrelationStatus()
        );

        assertEquals(
                request.getWindowStart(),
                response.getWindowStart()
        );

        assertEquals(
                request.getWindowEnd(),
                response.getWindowEnd()
        );

        assertEquals(
                3,
                response.getEventCount()
        );

        assertEquals(
                (short) 1,
                response.getMatchedRuleCount()
        );

        assertBigDecimalEquals(
                BigDecimal.ZERO,
                response.getConfidence()
        );

        assertNotNull(
                response.getCorrelationContext()
        );

        assertEquals(
                "SERVICE_TEST",
                response.getCorrelationContext().get("source")
        );

        assertEquals(
                "WEB",
                response.getCorrelationContext().get("channel")
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertTrue(
                correlationRepository.existsById(
                        response.getCorrelationId()
                )
        );
    }

    @Test
    void createCorrelationShouldAllowNullCustomerAndTransaction() {

        CorrelationRequest request =
                buildCorrelationRequest(
                        null,
                        null
                );

        request.setCorrelationType("GLOBAL");

        CorrelationResponse response =
                correlationService.createCorrelation(request);

        assertNotNull(
                response.getCorrelationId()
        );

        assertNull(
                response.getCustomerId()
        );

        assertNull(
                response.getTransactionId()
        );

        assertEquals(
                "GLOBAL",
                response.getCorrelationType()
        );

        assertBigDecimalEquals(
                BigDecimal.ZERO,
                response.getConfidence()
        );
    }

    @Test
    void createCorrelationShouldPersistNullCorrelationContext() {

        CorrelationRequest request =
                buildCorrelationRequest(
                        null,
                        null
                );

        request.setCorrelationContext(null);

        CorrelationResponse response =
                correlationService.createCorrelation(request);

        assertNotNull(
                response.getCorrelationId()
        );

        assertNull(
                response.getCorrelationContext()
        );
    }

    @Test
    void getCorrelationByIdShouldReturnExistingCorrelation() {

        CorrelationResponse created =
                correlationService.createCorrelation(
                        buildCorrelationRequest(
                                null,
                                null
                        )
                );

        CorrelationResponse found =
                correlationService.getCorrelationById(
                        created.getCorrelationId()
                );

        assertEquals(
                created.getCorrelationId(),
                found.getCorrelationId()
        );

        assertEquals(
                created.getCorrelationKey(),
                found.getCorrelationKey()
        );

        assertEquals(
                created.getCorrelationType(),
                found.getCorrelationType()
        );

        assertEquals(
                created.getCorrelationStatus(),
                found.getCorrelationStatus()
        );

        assertBigDecimalEquals(
                created.getConfidence(),
                found.getConfidence()
        );
    }

    @Test
    void getCorrelationByIdShouldThrowWhenCorrelationDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> correlationService.getCorrelationById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getCorrelationsByCustomerShouldReturnMatchingCorrelations() {

        Customer customer =
                createCustomer();

        CorrelationRequest firstRequest =
                buildCorrelationRequest(
                        customer.getCustomerId(),
                        null
                );

        firstRequest.setCorrelationKey(
                uniqueKey("CUSTOMER-1")
        );

        CorrelationResponse first =
                correlationService.createCorrelation(
                        firstRequest
                );

        CorrelationRequest secondRequest =
                buildCorrelationRequest(
                        customer.getCustomerId(),
                        null
                );

        secondRequest.setCorrelationKey(
                uniqueKey("CUSTOMER-2")
        );

        CorrelationResponse second =
                correlationService.createCorrelation(
                        secondRequest
                );

        List<CorrelationResponse> results =
                correlationService.getCorrelationsByCustomer(
                        customer.getCustomerId()
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .anyMatch(correlation ->
                                first.getCorrelationId()
                                        .equals(
                                                correlation.getCorrelationId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(correlation ->
                                second.getCorrelationId()
                                        .equals(
                                                correlation.getCorrelationId()
                                        )
                        )
        );
    }

    @Test
    void getCorrelationsByTransactionShouldReturnMatchingCorrelations() {

        Customer customer =
                createCustomer();

        Transaction transaction =
                createTransaction(
                        customer.getCustomerId()
                );

        CorrelationResponse created =
                correlationService.createCorrelation(
                        buildCorrelationRequest(
                                customer.getCustomerId(),
                                transaction.getTransactionId()
                        )
                );

        List<CorrelationResponse> results =
                correlationService.getCorrelationsByTransaction(
                        transaction.getTransactionId()
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getCorrelationId(),
                results.getFirst().getCorrelationId()
        );

        assertEquals(
                transaction.getTransactionId(),
                results.getFirst().getTransactionId()
        );
    }

    @Test
    void getCorrelationsByKeyShouldReturnMatchingCorrelations() {

        String correlationKey =
                uniqueKey("KEY");

        CorrelationRequest request =
                buildCorrelationRequest(
                        null,
                        null
                );

        request.setCorrelationKey(
                correlationKey
        );

        CorrelationResponse created =
                correlationService.createCorrelation(
                        request
                );

        List<CorrelationResponse> results =
                correlationService.getCorrelationsByKey(
                        correlationKey
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getCorrelationId(),
                results.getFirst().getCorrelationId()
        );

        assertEquals(
                correlationKey,
                results.getFirst().getCorrelationKey()
        );
    }

    @Test
    void getCorrelationsByTypeShouldReturnMatchingCorrelations() {

        String correlationType =
                "SERVICE_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        CorrelationRequest request =
                buildCorrelationRequest(
                        null,
                        null
                );

        request.setCorrelationType(
                correlationType
        );

        CorrelationResponse created =
                correlationService.createCorrelation(
                        request
                );

        List<CorrelationResponse> results =
                correlationService.getCorrelationsByType(
                        correlationType
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getCorrelationId(),
                results.getFirst().getCorrelationId()
        );

        assertEquals(
                correlationType,
                results.getFirst().getCorrelationType()
        );
    }

    @Test
    void getCorrelationsByStatusShouldReturnMatchingCorrelations() {

        String correlationStatus =
                "SERVICE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        CorrelationRequest request =
                buildCorrelationRequest(
                        null,
                        null
                );

        request.setCorrelationStatus(
                correlationStatus
        );

        CorrelationResponse created =
                correlationService.createCorrelation(
                        request
                );

        List<CorrelationResponse> results =
                correlationService.getCorrelationsByStatus(
                        correlationStatus
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getCorrelationId(),
                results.getFirst().getCorrelationId()
        );

        assertEquals(
                correlationStatus,
                results.getFirst().getCorrelationStatus()
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                correlationService.getCorrelationsByCustomer(
                        UUID.randomUUID()
                ).isEmpty()
        );

        assertTrue(
                correlationService.getCorrelationsByTransaction(
                        UUID.randomUUID()
                ).isEmpty()
        );

        assertTrue(
                correlationService.getCorrelationsByKey(
                        "UNKNOWN-" + UUID.randomUUID()
                ).isEmpty()
        );

        assertTrue(
                correlationService.getCorrelationsByType(
                        "UNKNOWN-" + UUID.randomUUID()
                ).isEmpty()
        );

        assertTrue(
                correlationService.getCorrelationsByStatus(
                        "UNKNOWN-" + UUID.randomUUID()
                ).isEmpty()
        );
    }

    private CorrelationRequest buildCorrelationRequest(
            UUID customerId,
            UUID transactionId) {

        LocalDateTime now =
                LocalDateTime.now();

        CorrelationRequest request =
                new CorrelationRequest();

        request.setCustomerId(
                customerId
        );

        request.setTransactionId(
                transactionId
        );

        request.setCorrelationKey(
                uniqueKey("CORR")
        );

        request.setCorrelationType(
                "TRANSACTION"
        );

        request.setCorrelationStatus(
                "OPEN"
        );

        request.setWindowStart(
                now.minusMinutes(30)
        );

        request.setWindowEnd(
                now
        );

        request.setEventCount(
                3
        );

        request.setMatchedRuleCount(
                (short) 1
        );

        request.setCorrelationContext(
                Map.of(
                        "source", "SERVICE_TEST",
                        "channel", "WEB"
                )
        );

        return request;
    }

    private Customer createCustomer() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "CORR-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Correlation"
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

        return customerRepository.saveAndFlush(
                customer
        );
    }

    private Transaction createTransaction(
            UUID customerId) {

        LocalDateTime now =
                LocalDateTime.now();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-CORR-SVC-" + UUID.randomUUID()
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
                new BigDecimal("450.00")
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

        return transactionRepository.saveAndFlush(
                transaction
        );
    }

    private String uniqueKey(
            String prefix) {

        return "SVC-" +
                prefix +
                "-" +
                UUID.randomUUID();
    }

    private void assertBigDecimalEquals(
            BigDecimal expected,
            BigDecimal actual) {

        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(
                0,
                expected.compareTo(actual)
        );
    }
}