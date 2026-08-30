package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionChannelRepository;
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
class TransactionChannelServiceIntegrationTest {

    @Autowired
    private TransactionChannelServiceInterface transactionChannelService;

    @Autowired
    private TransactionChannelRepository transactionChannelRepository;

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
                "TC-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Channel"
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
                "TC-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("500.00")
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
    void createChannelShouldPersistAndMapProvidedValues() {

        TransactionChannelRequest request =
                buildRequest(
                        "MOBILE"
                );

        request.setApplicationName(
                "EFS Mobile"
        );

        request.setApplicationVersion(
                "2.5.1"
        );

        request.setSdkVersion(
                "5.4.0"
        );

        request.setApiVersion(
                "v1"
        );

        request.setAuthenticationMethod(
                "MFA"
        );

        request.setSessionDuration(
                1800
        );

        TransactionChannelResponse response =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getChannelTransactionId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "MOBILE",
                response.getChannelType()
        );

        assertEquals(
                "EFS Mobile",
                response.getApplicationName()
        );

        assertEquals(
                "2.5.1",
                response.getApplicationVersion()
        );

        assertEquals(
                "5.4.0",
                response.getSdkVersion()
        );

        assertEquals(
                "v1",
                response.getApiVersion()
        );

        assertEquals(
                "MFA",
                response.getAuthenticationMethod()
        );

        assertEquals(
                Integer.valueOf(1800),
                response.getSessionDuration()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                transactionChannelRepository.existsById(
                        response.getChannelTransactionId()
                )
        );
    }

    @Test
    void createChannelShouldAllowOptionalFieldsToBeNull() {

        TransactionChannelResponse response =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        "WEB"
                                )
                        );

        assertNotNull(
                response.getChannelTransactionId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "WEB",
                response.getChannelType()
        );

        assertNull(
                response.getApplicationName()
        );

        assertNull(
                response.getApplicationVersion()
        );

        assertNull(
                response.getSdkVersion()
        );

        assertNull(
                response.getApiVersion()
        );

        assertNull(
                response.getAuthenticationMethod()
        );

        assertNull(
                response.getSessionDuration()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createChannelShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionChannelService
                        .createChannel(
                                UUID.randomUUID(),
                                buildRequest(
                                        "WEB"
                                )
                        )
        );
    }

    @Test
    void createChannelShouldThrowWhenTransactionIsSoftDeleted() {

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
                () -> transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        "WEB"
                                )
                        )
        );
    }

    @Test
    void getChannelByIdShouldReturnExistingChannel() {

        TransactionChannelResponse created =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        "MOBILE"
                                )
                        );

        TransactionChannelResponse found =
                transactionChannelService
                        .getChannelById(
                                created.getChannelTransactionId()
                        );

        assertEquals(
                created.getChannelTransactionId(),
                found.getChannelTransactionId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                "MOBILE",
                found.getChannelType()
        );
    }

    @Test
    void getChannelByIdShouldThrowWhenChannelDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionChannelService
                        .getChannelById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getChannelsByTransactionIdShouldReturnMatchingChannels() {

        TransactionChannelResponse first =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        "WEB"
                                )
                        );

        TransactionChannelResponse second =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        "MOBILE"
                                )
                        );

        List<TransactionChannelResponse> results =
                transactionChannelService
                        .getChannelsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsChannel(
                        results,
                        first.getChannelTransactionId()
                )
        );

        assertTrue(
                containsChannel(
                        results,
                        second.getChannelTransactionId()
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
    void getChannelsByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionChannelService
                        .getChannelsByTransactionId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getChannelsByTypeShouldReturnMatchingChannels() {

        String channelType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TransactionChannelResponse first =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        channelType
                                )
                        );

        TransactionChannelResponse second =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                buildRequest(
                                        channelType
                                )
                        );

        List<TransactionChannelResponse> results =
                transactionChannelService
                        .getChannelsByType(
                                channelType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsChannel(
                        results,
                        first.getChannelTransactionId()
                )
        );

        assertTrue(
                containsChannel(
                        results,
                        second.getChannelTransactionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                channelType.equals(
                                        result.getChannelType()
                                )
                        )
        );
    }

    @Test
    void getChannelsByApplicationNameShouldReturnMatchingChannels() {

        String applicationName =
                "APP-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        TransactionChannelRequest firstRequest =
                buildRequest(
                        "WEB"
                );

        firstRequest.setApplicationName(
                applicationName
        );

        TransactionChannelRequest secondRequest =
                buildRequest(
                        "MOBILE"
                );

        secondRequest.setApplicationName(
                applicationName
        );

        TransactionChannelResponse first =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                firstRequest
                        );

        TransactionChannelResponse second =
                transactionChannelService
                        .createChannel(
                                transactionId,
                                secondRequest
                        );

        List<TransactionChannelResponse> results =
                transactionChannelService
                        .getChannelsByApplicationName(
                                applicationName
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsChannel(
                        results,
                        first.getChannelTransactionId()
                )
        );

        assertTrue(
                containsChannel(
                        results,
                        second.getChannelTransactionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                applicationName.equals(
                                        result.getApplicationName()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                transactionChannelService
                        .getChannelsByType(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                transactionChannelService
                        .getChannelsByApplicationName(
                                "UNKNOWN_" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private TransactionChannelRequest buildRequest(
            String channelType) {

        TransactionChannelRequest request =
                new TransactionChannelRequest();

        request.setChannelType(
                channelType
        );

        return request;
    }

    private boolean containsChannel(
            List<TransactionChannelResponse> results,
            UUID channelTransactionId) {

        return results.stream()
                .anyMatch(result ->
                        channelTransactionId.equals(
                                result.getChannelTransactionId()
                        )
                );
    }
}