package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.DuplicateRecordException;
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
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionServiceInterface transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID customerId;
    private UUID createdBy;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TX-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
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

        createdBy =
                UUID.randomUUID();
    }

    @Test
    void createTransactionShouldPersistAndMapProvidedValues() {

        String reference =
                newReference();

        LocalDateTime transactionDatetime =
                LocalDateTime.now()
                        .minusMinutes(10)
                        .withNano(0);

        LocalDateTime processingDatetime =
                LocalDateTime.now()
                        .minusMinutes(5)
                        .withNano(0);

        UUID organizationId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID requestId =
                UUID.randomUUID();

        UUID sessionId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        TransactionRequest request =
                buildRequest(
                        reference
                );

        request.setExternalReference(
                "EXT-" + UUID.randomUUID()
        );

        request.setOrganizationId(
                organizationId
        );

        request.setTransactionSubtype(
                "ONLINE"
        );

        request.setExchangeRate(
                new BigDecimal("1.25000000")
        );

        request.setTransactionDatetime(
                transactionDatetime
        );

        request.setProcessingDatetime(
                processingDatetime
        );

        request.setTransactionStatus(
                "AUTHORIZED"
        );

        request.setFinalDecision(
                "REVIEW"
        );

        request.setFraudScore(
                new BigDecimal("72.50")
        );

        request.setCorrelationId(
                correlationId
        );

        request.setRequestId(
                requestId
        );

        request.setSessionId(
                sessionId
        );

        request.setUpdatedBy(
                UUID.randomUUID()
        );

        request.setTenantId(
                tenantId
        );

        TransactionResponse response =
                transactionService
                        .createTransaction(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getTransactionId()
        );

        assertEquals(
                reference,
                response.getTransactionReference()
        );

        assertEquals(
                request.getExternalReference(),
                response.getExternalReference()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                organizationId,
                response.getOrganizationId()
        );

        assertEquals(
                "PAYMENT",
                response.getTransactionType()
        );

        assertEquals(
                "ONLINE",
                response.getTransactionSubtype()
        );

        assertEquals(
                0,
                new BigDecimal("1500.00")
                        .compareTo(
                                response.getAmount()
                        )
        );

        assertEquals(
                "GTQ",
                response.getCurrencyCode()
        );

        assertEquals(
                0,
                new BigDecimal("1.25000000")
                        .compareTo(
                                response.getExchangeRate()
                        )
        );

        assertEquals(
                transactionDatetime,
                response.getTransactionDatetime()
        );

        assertEquals(
                processingDatetime,
                response.getProcessingDatetime()
        );

        assertEquals(
                "AUTHORIZED",
                response.getTransactionStatus()
        );

        assertEquals(
                "REVIEW",
                response.getFinalDecision()
        );

        assertEquals(
                0,
                new BigDecimal("72.50")
                        .compareTo(
                                response.getFraudScore()
                        )
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
                sessionId,
                response.getSessionId()
        );

        assertEquals(
                createdBy,
                response.getCreatedBy()
        );

        assertEquals(
                request.getUpdatedBy(),
                response.getUpdatedBy()
        );

        assertEquals(
                tenantId,
                response.getTenantId()
        );

        assertNull(
                response.getDeletedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );

        assertNotNull(
                response.getRecordVersion()
        );

        assertTrue(
                transactionRepository.existsById(
                        response.getTransactionId()
                )
        );
    }

    @Test
    void createTransactionShouldApplyApprovedDefaults() {

        TransactionRequest request =
                buildRequest(
                        newReference()
                );

        request.setTransactionDatetime(
                null
        );

        request.setTransactionStatus(
                null
        );

        request.setFinalDecision(
                ""
        );

        request.setFraudScore(
                null
        );

        TransactionResponse response =
                transactionService
                        .createTransaction(
                                request
                        );

        assertNotNull(
                response.getTransactionDatetime()
        );

        assertEquals(
                "RECEIVED",
                response.getTransactionStatus()
        );

        assertEquals(
                "PENDING",
                response.getFinalDecision()
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        response.getFraudScore()
                )
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertNotNull(
                response.getUpdatedAt()
        );
    }

    @Test
    void createTransactionShouldThrowWhenCustomerDoesNotExist() {

        TransactionRequest request =
                buildRequest(
                        newReference()
                );

        request.setCustomerId(
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .createTransaction(
                                request
                        )
        );
    }

    @Test
    void createTransactionShouldRejectDuplicateActiveReference() {

        String reference =
                newReference();

        transactionService
                .createTransaction(
                        buildRequest(
                                reference
                        )
                );

        assertThrows(
                DuplicateRecordException.class,
                () -> transactionService
                        .createTransaction(
                                buildRequest(
                                        reference
                                )
                        )
        );
    }

    @Test
    void createTransactionShouldRejectReferenceUsedBySoftDeletedTransaction() {

        String reference =
                newReference();

        TransactionResponse created =
                transactionService
                        .createTransaction(
                                buildRequest(
                                        reference
                                )
                        );

        transactionService
                .deleteTransaction(
                        created.getTransactionId()
                );

        assertTrue(
                transactionRepository
                        .findByTransactionReference(
                                reference
                        )
                        .isPresent()
        );

        assertTrue(
                transactionRepository
                        .findByTransactionReferenceAndDeletedAtIsNull(
                                reference
                        )
                        .isEmpty()
        );

        assertThrows(
                DuplicateRecordException.class,
                () -> transactionService
                        .createTransaction(
                                buildRequest(
                                        reference
                                )
                        )
        );
    }

    @Test
    void getTransactionByIdShouldReturnExistingTransaction() {

        TransactionResponse created =
                createTransaction();

        TransactionResponse found =
                transactionService
                        .getTransactionById(
                                created.getTransactionId()
                        );

        assertEquals(
                created.getTransactionId(),
                found.getTransactionId()
        );

        assertEquals(
                created.getTransactionReference(),
                found.getTransactionReference()
        );

        assertEquals(
                customerId,
                found.getCustomerId()
        );
    }

    @Test
    void getTransactionByIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .getTransactionById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getTransactionByReferenceShouldReturnExistingTransaction() {

        TransactionResponse created =
                createTransaction();

        TransactionResponse found =
                transactionService
                        .getTransactionByReference(
                                created.getTransactionReference()
                        );

        assertEquals(
                created.getTransactionId(),
                found.getTransactionId()
        );

        assertEquals(
                created.getTransactionReference(),
                found.getTransactionReference()
        );
    }

    @Test
    void getTransactionByReferenceShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .getTransactionByReference(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
        );
    }

    @Test
    void getTransactionsByCustomerIdShouldReturnOnlyActiveTransactions() {

        TransactionResponse active =
                createTransaction();

        TransactionResponse deleted =
                createTransaction();

        transactionService
                .deleteTransaction(
                        deleted.getTransactionId()
                );

        List<TransactionResponse> results =
                transactionService
                        .getTransactionsByCustomerId(
                                customerId
                        );

        assertTrue(
                containsTransaction(
                        results,
                        active.getTransactionId()
                )
        );

        assertFalse(
                containsTransaction(
                        results,
                        deleted.getTransactionId()
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
    void getTransactionsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .getTransactionsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updateTransactionShouldPersistProvidedValues() {

        TransactionResponse created =
                createTransaction();

        LocalDateTime transactionDatetime =
                LocalDateTime.now()
                        .minusMinutes(20)
                        .withNano(0);

        UUID updatedBy =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        TransactionRequest updateRequest =
                buildRequest(
                        newReference()
                );

        updateRequest.setExternalReference(
                "UPDATED-EXT-" + UUID.randomUUID()
        );

        updateRequest.setTransactionType(
                "TRANSFER"
        );

        updateRequest.setTransactionSubtype(
                "INTERNAL"
        );

        updateRequest.setAmount(
                new BigDecimal("2750.00")
        );

        updateRequest.setCurrencyCode(
                "USD"
        );

        updateRequest.setExchangeRate(
                new BigDecimal("7.65000000")
        );

        updateRequest.setTransactionDatetime(
                transactionDatetime
        );

        updateRequest.setProcessingDatetime(
                LocalDateTime.now()
                        .withNano(0)
        );

        updateRequest.setTransactionStatus(
                "PROCESSED"
        );

        updateRequest.setFinalDecision(
                "APPROVED"
        );

        updateRequest.setFraudScore(
                new BigDecimal("12.25")
        );

        updateRequest.setCorrelationId(
                UUID.randomUUID()
        );

        updateRequest.setRequestId(
                UUID.randomUUID()
        );

        updateRequest.setSessionId(
                UUID.randomUUID()
        );

        updateRequest.setUpdatedBy(
                updatedBy
        );

        updateRequest.setTenantId(
                tenantId
        );

        TransactionResponse updated =
                transactionService
                        .updateTransaction(
                                created.getTransactionId(),
                                updateRequest
                        );

        assertEquals(
                created.getTransactionId(),
                updated.getTransactionId()
        );

        assertEquals(
                updateRequest.getTransactionReference(),
                updated.getTransactionReference()
        );

        assertEquals(
                updateRequest.getExternalReference(),
                updated.getExternalReference()
        );

        assertEquals(
                "TRANSFER",
                updated.getTransactionType()
        );

        assertEquals(
                "INTERNAL",
                updated.getTransactionSubtype()
        );

        assertEquals(
                0,
                new BigDecimal("2750.00")
                        .compareTo(
                                updated.getAmount()
                        )
        );

        assertEquals(
                "USD",
                updated.getCurrencyCode()
        );

        assertEquals(
                "PROCESSED",
                updated.getTransactionStatus()
        );

        assertEquals(
                "APPROVED",
                updated.getFinalDecision()
        );

        assertEquals(
                0,
                new BigDecimal("12.25")
                        .compareTo(
                                updated.getFraudScore()
                        )
        );

        assertEquals(
                createdBy,
                updated.getCreatedBy()
        );

        assertEquals(
                updatedBy,
                updated.getUpdatedBy()
        );

        assertEquals(
                tenantId,
                updated.getTenantId()
        );

        assertEquals(
                created.getCreatedAt(),
                updated.getCreatedAt()
        );

        assertNotNull(
                updated.getUpdatedAt()
        );
    }

    @Test
    void updateTransactionShouldAllowSameReferenceAndApplyDefaults() {

        TransactionResponse created =
                createTransaction();

        assertNotNull(
                created.getCorrelationId()
        );

        TransactionRequest request =
                buildRequest(
                        created.getTransactionReference()
                );

        request.setTransactionDatetime(
                null
        );

        request.setTransactionStatus(
                " "
        );

        request.setFinalDecision(
                null
        );

        request.setFraudScore(
                null
        );

        TransactionResponse updated =
                transactionService
                        .updateTransaction(
                                created.getTransactionId(),
                                request
                        );

        assertEquals(
                created.getTransactionReference(),
                updated.getTransactionReference()
        );

        assertEquals(
                created.getCorrelationId(),
                updated.getCorrelationId()
        );

        assertNotNull(
                updated.getTransactionDatetime()
        );

        assertEquals(
                "RECEIVED",
                updated.getTransactionStatus()
        );

        assertEquals(
                "PENDING",
                updated.getFinalDecision()
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        updated.getFraudScore()
                )
        );
    }

    @Test
    void updateTransactionShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .updateTransaction(
                                UUID.randomUUID(),
                                buildRequest(
                                        newReference()
                                )
                        )
        );
    }

    @Test
    void updateTransactionShouldThrowWhenCustomerDoesNotExist() {

        TransactionResponse created =
                createTransaction();

        TransactionRequest request =
                buildRequest(
                        created.getTransactionReference()
                );

        request.setCustomerId(
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .updateTransaction(
                                created.getTransactionId(),
                                request
                        )
        );
    }

    @Test
    void updateTransactionShouldRejectReferenceUsedByAnotherActiveTransaction() {

        TransactionResponse first =
                createTransaction();

        TransactionResponse second =
                createTransaction();

        TransactionRequest request =
                buildRequest(
                        first.getTransactionReference()
                );

        assertThrows(
                DuplicateRecordException.class,
                () -> transactionService
                        .updateTransaction(
                                second.getTransactionId(),
                                request
                        )
        );
    }

    @Test
    void updateTransactionShouldRejectReferenceUsedBySoftDeletedTransaction() {

        TransactionResponse deleted =
                createTransaction();

        String reservedReference =
                deleted.getTransactionReference();

        transactionService
                .deleteTransaction(
                        deleted.getTransactionId()
                );

        TransactionResponse active =
                createTransaction();

        TransactionRequest request =
                buildRequest(
                        reservedReference
                );

        assertThrows(
                DuplicateRecordException.class,
                () -> transactionService
                        .updateTransaction(
                                active.getTransactionId(),
                                request
                        )
        );
    }

    @Test
    void deleteTransactionShouldSoftDeleteAndHideTransaction() {

        TransactionResponse created =
                createTransaction();

        transactionService
                .deleteTransaction(
                        created.getTransactionId()
                );

        assertTrue(
                transactionRepository
                        .findById(
                                created.getTransactionId()
                        )
                        .isPresent()
        );

        assertNotNull(
                transactionRepository
                        .findById(
                                created.getTransactionId()
                        )
                        .orElseThrow()
                        .getDeletedAt()
        );

        assertTrue(
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                created.getTransactionId()
                        )
                        .isEmpty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .getTransactionById(
                                created.getTransactionId()
                        )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .getTransactionByReference(
                                created.getTransactionReference()
                        )
        );
    }

    @Test
    void deleteTransactionShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService
                        .deleteTransaction(
                                UUID.randomUUID()
                        )
        );
    }

    private TransactionResponse createTransaction() {

        return transactionService
                .createTransaction(
                        buildRequest(
                                newReference()
                        )
                );
    }

    private TransactionRequest buildRequest(
            String transactionReference) {

        TransactionRequest request =
                new TransactionRequest();

        request.setTransactionReference(
                transactionReference
        );

        request.setCustomerId(
                customerId
        );

        request.setOrganizationId(
                UUID.randomUUID()
        );

        request.setTransactionType(
                "PAYMENT"
        );

        request.setAmount(
                new BigDecimal("1500.00")
        );

        request.setCurrencyCode(
                "GTQ"
        );

        request.setCreatedBy(
                createdBy
        );

        return request;
    }

    private String newReference() {

        return "TX-SVC-" + UUID.randomUUID();
    }

    private boolean containsTransaction(
            List<TransactionResponse> results,
            UUID transactionId) {

        return results.stream()
                .anyMatch(result ->
                        transactionId.equals(
                                result.getTransactionId()
                        )
                );
    }
}
