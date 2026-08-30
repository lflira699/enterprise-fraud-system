package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionLocationRepository;
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
class TransactionLocationServiceIntegrationTest {

    @Autowired
    private TransactionLocationServiceInterface transactionLocationService;

    @Autowired
    private TransactionLocationRepository transactionLocationRepository;

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
                "TL-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Location"
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
                "TL-SVC-TXN-" + UUID.randomUUID()
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
                new BigDecimal("850.00")
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
    void createLocationShouldPersistAndMapProvidedValues() {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                "203.0.113.25"
        );

        request.setCountryCode(
                "GT"
        );

        request.setState(
                "Guatemala"
        );

        request.setCity(
                "Guatemala City"
        );

        request.setPostalCode(
                "01010"
        );

        request.setLatitude(
                new BigDecimal("14.6349150")
        );

        request.setLongitude(
                new BigDecimal("-90.5068820")
        );

        request.setAsn(
                64512L
        );

        request.setInternetProvider(
                "Integration Test ISP"
        );

        request.setVpnDetected(
                true
        );

        request.setProxyDetected(
                false
        );

        request.setTorDetected(
                true
        );

        TransactionLocationResponse response =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getLocationId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                "203.0.113.25",
                response.getIpAddress()
        );

        assertEquals(
                "GT",
                response.getCountryCode()
        );

        assertEquals(
                "Guatemala",
                response.getState()
        );

        assertEquals(
                "Guatemala City",
                response.getCity()
        );

        assertEquals(
                "01010",
                response.getPostalCode()
        );

        assertEquals(
                0,
                new BigDecimal("14.6349150")
                        .compareTo(
                                response.getLatitude()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("-90.5068820")
                        .compareTo(
                                response.getLongitude()
                        )
        );

        assertEquals(
                Long.valueOf(64512L),
                response.getAsn()
        );

        assertEquals(
                "Integration Test ISP",
                response.getInternetProvider()
        );

        assertTrue(
                response.getVpnDetected()
        );

        assertFalse(
                response.getProxyDetected()
        );

        assertTrue(
                response.getTorDetected()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                transactionLocationRepository.existsById(
                        response.getLocationId()
                )
        );
    }

    @Test
    void createLocationShouldApplyFalseDefaultsAndAllowOptionalFields() {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        TransactionLocationResponse response =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                request
                        );

        assertNotNull(
                response.getLocationId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertNull(
                response.getIpAddress()
        );

        assertNull(
                response.getCountryCode()
        );

        assertNull(
                response.getState()
        );

        assertNull(
                response.getCity()
        );

        assertNull(
                response.getPostalCode()
        );

        assertNull(
                response.getLatitude()
        );

        assertNull(
                response.getLongitude()
        );

        assertNull(
                response.getAsn()
        );

        assertNull(
                response.getInternetProvider()
        );

        assertFalse(
                response.getVpnDetected()
        );

        assertFalse(
                response.getProxyDetected()
        );

        assertFalse(
                response.getTorDetected()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createLocationShouldRejectInvalidIpAddress() {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                "999.999.999.999"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionLocationService
                        .createLocation(
                                transactionId,
                                request
                        )
        );
    }

    @Test
    void createLocationShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionLocationService
                        .createLocation(
                                UUID.randomUUID(),
                                new TransactionLocationRequest()
                        )
        );
    }

    @Test
    void createLocationShouldThrowWhenTransactionIsSoftDeleted() {

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
                () -> transactionLocationService
                        .createLocation(
                                transactionId,
                                new TransactionLocationRequest()
                        )
        );
    }

    @Test
    void getLocationByIdShouldReturnExistingLocation() {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                "203.0.113.30"
        );

        request.setCountryCode(
                "GT"
        );

        TransactionLocationResponse created =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                request
                        );

        TransactionLocationResponse found =
                transactionLocationService
                        .getLocationById(
                                created.getLocationId()
                        );

        assertEquals(
                created.getLocationId(),
                found.getLocationId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                "203.0.113.30",
                found.getIpAddress()
        );

        assertEquals(
                "GT",
                found.getCountryCode()
        );
    }

    @Test
    void getLocationByIdShouldThrowWhenLocationDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionLocationService
                        .getLocationById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getLocationsByTransactionIdShouldReturnMatchingLocations() {

        TransactionLocationResponse first =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.40",
                                        "GT",
                                        64520L
                                )
                        );

        TransactionLocationResponse second =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.41",
                                        "US",
                                        64521L
                                )
                        );

        List<TransactionLocationResponse> results =
                transactionLocationService
                        .getLocationsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsLocation(
                        results,
                        first.getLocationId()
                )
        );

        assertTrue(
                containsLocation(
                        results,
                        second.getLocationId()
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
    void getLocationsByTransactionIdShouldThrowWhenTransactionDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionLocationService
                        .getLocationsByTransactionId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getLocationsByIpAddressShouldReturnMatchingLocations() {

        String ipAddress =
                "203.0.113.50";

        TransactionLocationResponse first =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        ipAddress,
                                        "GT",
                                        64530L
                                )
                        );

        TransactionLocationResponse second =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        ipAddress,
                                        "GT",
                                        64531L
                                )
                        );

        List<TransactionLocationResponse> results =
                transactionLocationService
                        .getLocationsByIpAddress(
                                ipAddress
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsLocation(
                        results,
                        first.getLocationId()
                )
        );

        assertTrue(
                containsLocation(
                        results,
                        second.getLocationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                ipAddress.equals(
                                        result.getIpAddress()
                                )
                        )
        );
    }

    @Test
    void getLocationsByIpAddressShouldRejectInvalidAddress() {

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionLocationService
                        .getLocationsByIpAddress(
                                "999.999.999.999"
                        )
        );
    }

    @Test
    void getLocationsByCountryCodeShouldReturnMatchingLocations() {

        String countryCode =
                "GT";

        TransactionLocationResponse first =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.60",
                                        countryCode,
                                        64540L
                                )
                        );

        TransactionLocationResponse second =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.61",
                                        countryCode,
                                        64541L
                                )
                        );

        List<TransactionLocationResponse> results =
                transactionLocationService
                        .getLocationsByCountryCode(
                                countryCode
                        );

        assertTrue(
                containsLocation(
                        results,
                        first.getLocationId()
                )
        );

        assertTrue(
                containsLocation(
                        results,
                        second.getLocationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                countryCode.equals(
                                        result.getCountryCode()
                                )
                        )
        );
    }

    @Test
    void getLocationsByAsnShouldReturnMatchingLocations() {

        Long asn =
                64550L;

        TransactionLocationResponse first =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.70",
                                        "GT",
                                        asn
                                )
                        );

        TransactionLocationResponse second =
                transactionLocationService
                        .createLocation(
                                transactionId,
                                buildRequest(
                                        "203.0.113.71",
                                        "US",
                                        asn
                                )
                        );

        List<TransactionLocationResponse> results =
                transactionLocationService
                        .getLocationsByAsn(
                                asn
                        );

        assertTrue(
                containsLocation(
                        results,
                        first.getLocationId()
                )
        );

        assertTrue(
                containsLocation(
                        results,
                        second.getLocationId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                asn.equals(
                                        result.getAsn()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                transactionLocationService
                        .getLocationsByIpAddress(
                                "203.0.113.200"
                        )
                        .isEmpty()
        );

        assertTrue(
                transactionLocationService
                        .getLocationsByCountryCode(
                                "ZZ"
                        )
                        .isEmpty()
        );

        assertTrue(
                transactionLocationService
                        .getLocationsByAsn(
                                4294967295L
                        )
                        .isEmpty()
        );
    }

    private TransactionLocationRequest buildRequest(
            String ipAddress,
            String countryCode,
            Long asn) {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                ipAddress
        );

        request.setCountryCode(
                countryCode
        );

        request.setAsn(
                asn
        );

        return request;
    }

    private boolean containsLocation(
            List<TransactionLocationResponse> results,
            UUID locationId) {

        return results.stream()
                .anyMatch(result ->
                        locationId.equals(
                                result.getLocationId()
                        )
                );
    }
}