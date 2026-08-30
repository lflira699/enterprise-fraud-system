package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionParticipantRepository;
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
class TransactionParticipantServiceIntegrationTest {

    @Autowired
    private TransactionParticipantServiceInterface transactionParticipantService;

    @Autowired
    private TransactionParticipantRepository transactionParticipantRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                createCustomer(
                        "TP-SVC-"
                );

        customerId =
                customer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "TP-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("900.00")
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
    void createParticipantShouldPersistAndMapProvidedValues() {

        UUID institutionId =
                UUID.randomUUID();

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                customerId
        );

        request.setExternalIdentifier(
                "EXT-" + UUID.randomUUID()
        );

        request.setInstitutionId(
                institutionId
        );

        request.setCountryCode(
                "GT"
        );

        request.setRiskLevel(
                "LOW"
        );

        TransactionParticipantResponse response =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getParticipantId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "SENDER",
                response.getParticipantType()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                request.getExternalIdentifier(),
                response.getExternalIdentifier()
        );

        assertEquals(
                institutionId,
                response.getInstitutionId()
        );

        assertEquals(
                "GT",
                response.getCountryCode()
        );

        assertEquals(
                "LOW",
                response.getRiskLevel()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                transactionParticipantRepository.existsById(
                        response.getParticipantId()
                )
        );
    }

    @Test
    void createParticipantShouldAllowOptionalFieldsToBeNull() {

        TransactionParticipantResponse response =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                buildRequest(
                                        "BENEFICIARY"
                                )
                        );

        assertNotNull(
                response.getParticipantId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "BENEFICIARY",
                response.getParticipantType()
        );

        assertNull(
                response.getCustomerId()
        );

        assertNull(
                response.getExternalIdentifier()
        );

        assertNull(
                response.getInstitutionId()
        );

        assertNull(
                response.getCountryCode()
        );

        assertNull(
                response.getRiskLevel()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createParticipantShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionParticipantService
                        .createParticipant(
                                UUID.randomUUID(),
                                buildRequest(
                                        "SENDER"
                                )
                        )
        );
    }

    @Test
    void createParticipantShouldThrowWhenTransactionIsSoftDeleted() {

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
                () -> transactionParticipantService
                        .createParticipant(
                                transactionId,
                                buildRequest(
                                        "SENDER"
                                )
                        )
        );
    }

    @Test
    void createParticipantShouldThrowWhenCustomerDoesNotExist() {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionParticipantService
                        .createParticipant(
                                transactionId,
                                request
                        )
        );
    }

    @Test
    void getParticipantByIdShouldReturnExistingParticipant() {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                customerId
        );

        TransactionParticipantResponse created =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                request
                        );

        TransactionParticipantResponse found =
                transactionParticipantService
                        .getParticipantById(
                                created.getParticipantId()
                        );

        assertEquals(
                created.getParticipantId(),
                found.getParticipantId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                "SENDER",
                found.getParticipantType()
        );

        assertEquals(
                customerId,
                found.getCustomerId()
        );
    }

    @Test
    void getParticipantByIdShouldThrowWhenParticipantDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionParticipantService
                        .getParticipantById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getParticipantsByTransactionIdShouldReturnMatchingParticipants() {

        TransactionParticipantResponse first =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                buildRequest(
                                        "SENDER"
                                )
                        );

        TransactionParticipantResponse second =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                buildRequest(
                                        "BENEFICIARY"
                                )
                        );

        List<TransactionParticipantResponse> results =
                transactionParticipantService
                        .getParticipantsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsParticipant(
                        results,
                        first.getParticipantId()
                )
        );

        assertTrue(
                containsParticipant(
                        results,
                        second.getParticipantId()
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
    void getParticipantsByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionParticipantService
                        .getParticipantsByTransactionId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getParticipantsByCustomerIdShouldReturnMatchingParticipants() {

        Customer secondCustomer =
                createCustomer(
                        "TP-SVC-B-"
                );

        TransactionParticipantRequest firstRequest =
                buildRequest(
                        "SENDER"
                );

        firstRequest.setCustomerId(
                customerId
        );

        TransactionParticipantRequest secondRequest =
                buildRequest(
                        "BENEFICIARY"
                );

        secondRequest.setCustomerId(
                customerId
        );

        TransactionParticipantRequest otherRequest =
                buildRequest(
                        "BENEFICIARY"
                );

        otherRequest.setCustomerId(
                secondCustomer.getCustomerId()
        );

        TransactionParticipantResponse first =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                firstRequest
                        );

        TransactionParticipantResponse second =
                transactionParticipantService
                        .createParticipant(
                                transactionId,
                                secondRequest
                        );

        transactionParticipantService
                .createParticipant(
                        transactionId,
                        otherRequest
                );

        List<TransactionParticipantResponse> results =
                transactionParticipantService
                        .getParticipantsByCustomerId(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsParticipant(
                        results,
                        first.getParticipantId()
                )
        );

        assertTrue(
                containsParticipant(
                        results,
                        second.getParticipantId()
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
    void getParticipantsByCustomerIdShouldReturnEmptyListWhenCustomerHasNoParticipants() {

        Customer customerWithoutParticipants =
                createCustomer(
                        "TP-SVC-NO-"
                );

        List<TransactionParticipantResponse> results =
                transactionParticipantService
                        .getParticipantsByCustomerId(
                                customerWithoutParticipants
                                        .getCustomerId()
                        );

        assertTrue(
                results.isEmpty()
        );
    }

    @Test
    void getParticipantsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionParticipantService
                        .getParticipantsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    private Customer createCustomer(
            String prefix) {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                prefix + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Participant"
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

    private TransactionParticipantRequest buildRequest(
            String participantType) {

        TransactionParticipantRequest request =
                new TransactionParticipantRequest();

        request.setParticipantType(
                participantType
        );

        return request;
    }

    private boolean containsParticipant(
            List<TransactionParticipantResponse> results,
            UUID participantId) {

        return results.stream()
                .anyMatch(result ->
                        participantId.equals(
                                result.getParticipantId()
                        )
                );
    }
}