package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionPaymentMethodRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TransactionPaymentMethodServiceIntegrationTest {

    @Autowired
    private TransactionPaymentMethodServiceInterface
            transactionPaymentMethodService;

    @Autowired
    private TransactionPaymentMethodRepository
            transactionPaymentMethodRepository;

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
                "TPM-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "PaymentMethod"
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
                "TPM-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("1200.00")
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
    void createPaymentMethodShouldPersistAndMapProvidedValues() {

        LocalDate expirationDate =
                LocalDate.of(
                        2030,
                        12,
                        31
                );

        TransactionPaymentMethodRequest request =
                buildRequest(
                        "CARD"
                );

        request.setNetwork(
                "VISA"
        );

        request.setIssuer(
                "Integration Test Bank"
        );

        request.setMaskedIdentifier(
                "**** **** **** 1234"
        );

        request.setTokenReference(
                "TOKEN-" + UUID.randomUUID()
        );

        request.setExpirationDate(
                expirationDate
        );

        TransactionPaymentMethodResponse response =
                transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getPaymentMethodId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "CARD",
                response.getPaymentType()
        );

        assertEquals(
                "VISA",
                response.getNetwork()
        );

        assertEquals(
                "Integration Test Bank",
                response.getIssuer()
        );

        assertEquals(
                "**** **** **** 1234",
                response.getMaskedIdentifier()
        );

        assertEquals(
                request.getTokenReference(),
                response.getTokenReference()
        );

        assertEquals(
                expirationDate,
                response.getExpirationDate()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                transactionPaymentMethodRepository.existsById(
                        response.getPaymentMethodId()
                )
        );
    }

    @Test
    void createPaymentMethodShouldAllowOptionalFieldsToBeNull() {

        TransactionPaymentMethodResponse response =
                transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                buildRequest(
                                        "BANK_ACCOUNT"
                                )
                        );

        assertNotNull(
                response.getPaymentMethodId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "BANK_ACCOUNT",
                response.getPaymentType()
        );

        assertNull(
                response.getNetwork()
        );

        assertNull(
                response.getIssuer()
        );

        assertNull(
                response.getMaskedIdentifier()
        );

        assertNull(
                response.getTokenReference()
        );

        assertNull(
                response.getExpirationDate()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createPaymentMethodShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionPaymentMethodService
                        .createPaymentMethod(
                                UUID.randomUUID(),
                                buildRequest(
                                        "CARD"
                                )
                        )
        );
    }

    @Test
    void createPaymentMethodShouldThrowWhenTransactionIsSoftDeleted() {

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
                () -> transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                buildRequest(
                                        "CARD"
                                )
                        )
        );
    }

    @Test
    void getPaymentMethodByIdShouldReturnExistingPaymentMethod() {

        TransactionPaymentMethodRequest request =
                buildRequest(
                        "CARD"
                );

        request.setNetwork(
                "MASTERCARD"
        );

        TransactionPaymentMethodResponse created =
                transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                request
                        );

        TransactionPaymentMethodResponse found =
                transactionPaymentMethodService
                        .getPaymentMethodById(
                                created.getPaymentMethodId()
                        );

        assertEquals(
                created.getPaymentMethodId(),
                found.getPaymentMethodId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                "CARD",
                found.getPaymentType()
        );

        assertEquals(
                "MASTERCARD",
                found.getNetwork()
        );
    }

    @Test
    void getPaymentMethodByIdShouldThrowWhenPaymentMethodDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionPaymentMethodService
                        .getPaymentMethodById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getPaymentMethodsByTransactionIdShouldReturnMatchingPaymentMethods() {

        TransactionPaymentMethodResponse first =
                transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                buildRequest(
                                        "CARD"
                                )
                        );

        TransactionPaymentMethodResponse second =
                transactionPaymentMethodService
                        .createPaymentMethod(
                                transactionId,
                                buildRequest(
                                        "BANK_ACCOUNT"
                                )
                        );

        List<TransactionPaymentMethodResponse> results =
                transactionPaymentMethodService
                        .getPaymentMethodsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsPaymentMethod(
                        results,
                        first.getPaymentMethodId()
                )
        );

        assertTrue(
                containsPaymentMethod(
                        results,
                        second.getPaymentMethodId()
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
    void getPaymentMethodsByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionPaymentMethodService
                        .getPaymentMethodsByTransactionId(
                                UUID.randomUUID()
                        )
        );
    }

    private TransactionPaymentMethodRequest buildRequest(
            String paymentType) {

        TransactionPaymentMethodRequest request =
                new TransactionPaymentMethodRequest();

        request.setPaymentType(
                paymentType
        );

        return request;
    }

    private boolean containsPaymentMethod(
            List<TransactionPaymentMethodResponse> results,
            UUID paymentMethodId) {

        return results.stream()
                .anyMatch(result ->
                        paymentMethodId.equals(
                                result.getPaymentMethodId()
                        )
                );
    }
}