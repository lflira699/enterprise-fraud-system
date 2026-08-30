package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionDeviceRepository;
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
class TransactionDeviceServiceIntegrationTest {

    @Autowired
    private TransactionDeviceServiceInterface transactionDeviceService;

    @Autowired
    private TransactionDeviceRepository transactionDeviceRepository;

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
                "TD-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Device"
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
                "TD-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("750.00")
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
    void createDeviceShouldPersistAndMapProvidedValues() {

        UUID deviceId =
                UUID.randomUUID();

        String fingerprint =
                "FP-" + UUID.randomUUID();

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceId(
                deviceId
        );

        request.setDeviceFingerprint(
                fingerprint
        );

        request.setDeviceType(
                "MOBILE"
        );

        request.setOperatingSystem(
                "Android"
        );

        request.setOsVersion(
                "16"
        );

        request.setBrowser(
                "Chrome"
        );

        request.setBrowserVersion(
                "152"
        );

        request.setScreenResolution(
                "1080x2400"
        );

        request.setLanguage(
                "es-GT"
        );

        request.setTimezone(
                "America/Guatemala"
        );

        request.setTrustScore(
                new BigDecimal("92.50")
        );

        TransactionDeviceResponse response =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getDeviceTransactionId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                deviceId,
                response.getDeviceId()
        );

        assertEquals(
                fingerprint,
                response.getDeviceFingerprint()
        );

        assertEquals(
                "MOBILE",
                response.getDeviceType()
        );

        assertEquals(
                "Android",
                response.getOperatingSystem()
        );

        assertEquals(
                "16",
                response.getOsVersion()
        );

        assertEquals(
                "Chrome",
                response.getBrowser()
        );

        assertEquals(
                "152",
                response.getBrowserVersion()
        );

        assertEquals(
                "1080x2400",
                response.getScreenResolution()
        );

        assertEquals(
                "es-GT",
                response.getLanguage()
        );

        assertEquals(
                "America/Guatemala",
                response.getTimezone()
        );

        assertEquals(
                0,
                new BigDecimal("92.50")
                        .compareTo(
                                response.getTrustScore()
                        )
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                transactionDeviceRepository.existsById(
                        response.getDeviceTransactionId()
                )
        );
    }

    @Test
    void createDeviceShouldAllowOptionalFieldsToBeNull() {

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        TransactionDeviceResponse response =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                request
                        );

        assertNotNull(
                response.getDeviceTransactionId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertNull(
                response.getDeviceId()
        );

        assertNull(
                response.getDeviceFingerprint()
        );

        assertNull(
                response.getDeviceType()
        );

        assertNull(
                response.getOperatingSystem()
        );

        assertNull(
                response.getOsVersion()
        );

        assertNull(
                response.getBrowser()
        );

        assertNull(
                response.getBrowserVersion()
        );

        assertNull(
                response.getScreenResolution()
        );

        assertNull(
                response.getLanguage()
        );

        assertNull(
                response.getTimezone()
        );

        assertNull(
                response.getTrustScore()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createDeviceShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionDeviceService
                        .createDevice(
                                UUID.randomUUID(),
                                new TransactionDeviceRequest()
                        )
        );
    }

    @Test
    void createDeviceShouldThrowWhenTransactionIsSoftDeleted() {

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
                () -> transactionDeviceService
                        .createDevice(
                                transactionId,
                                new TransactionDeviceRequest()
                        )
        );
    }

    @Test
    void getDeviceByIdShouldReturnExistingDevice() {

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceFingerprint(
                "FP-" + UUID.randomUUID()
        );

        request.setDeviceType(
                "DESKTOP"
        );

        TransactionDeviceResponse created =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                request
                        );

        TransactionDeviceResponse found =
                transactionDeviceService
                        .getDeviceById(
                                created.getDeviceTransactionId()
                        );

        assertEquals(
                created.getDeviceTransactionId(),
                found.getDeviceTransactionId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                request.getDeviceFingerprint(),
                found.getDeviceFingerprint()
        );

        assertEquals(
                "DESKTOP",
                found.getDeviceType()
        );
    }

    @Test
    void getDeviceByIdShouldThrowWhenDeviceDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionDeviceService
                        .getDeviceById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getDevicesByTransactionIdShouldReturnMatchingDevices() {

        TransactionDeviceRequest firstRequest =
                new TransactionDeviceRequest();

        firstRequest.setDeviceFingerprint(
                "FP-A-" + UUID.randomUUID()
        );

        TransactionDeviceRequest secondRequest =
                new TransactionDeviceRequest();

        secondRequest.setDeviceFingerprint(
                "FP-B-" + UUID.randomUUID()
        );

        TransactionDeviceResponse first =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                firstRequest
                        );

        TransactionDeviceResponse second =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                secondRequest
                        );

        List<TransactionDeviceResponse> results =
                transactionDeviceService
                        .getDevicesByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsDevice(
                        results,
                        first.getDeviceTransactionId()
                )
        );

        assertTrue(
                containsDevice(
                        results,
                        second.getDeviceTransactionId()
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
    void getDevicesByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionDeviceService
                        .getDevicesByTransactionId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getDevicesByFingerprintShouldReturnMatchingDevices() {

        String fingerprint =
                "FP-SHARED-" + UUID.randomUUID();

        TransactionDeviceRequest firstRequest =
                new TransactionDeviceRequest();

        firstRequest.setDeviceFingerprint(
                fingerprint
        );

        firstRequest.setDeviceType(
                "MOBILE"
        );

        TransactionDeviceRequest secondRequest =
                new TransactionDeviceRequest();

        secondRequest.setDeviceFingerprint(
                fingerprint
        );

        secondRequest.setDeviceType(
                "TABLET"
        );

        TransactionDeviceResponse first =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                firstRequest
                        );

        TransactionDeviceResponse second =
                transactionDeviceService
                        .createDevice(
                                transactionId,
                                secondRequest
                        );

        List<TransactionDeviceResponse> results =
                transactionDeviceService
                        .getDevicesByFingerprint(
                                fingerprint
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsDevice(
                        results,
                        first.getDeviceTransactionId()
                )
        );

        assertTrue(
                containsDevice(
                        results,
                        second.getDeviceTransactionId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                fingerprint.equals(
                                        result.getDeviceFingerprint()
                                )
                        )
        );
    }

    @Test
    void getDevicesByFingerprintShouldReturnEmptyListForUnknownFingerprint() {

        assertTrue(
                transactionDeviceService
                        .getDevicesByFingerprint(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private boolean containsDevice(
            List<TransactionDeviceResponse> results,
            UUID deviceTransactionId) {

        return results.stream()
                .anyMatch(result ->
                        deviceTransactionId.equals(
                                result.getDeviceTransactionId()
                        )
                );
    }
}