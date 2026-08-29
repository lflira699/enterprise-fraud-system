package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBiometricRequest;
import com.efs.modules.customer.dto.CustomerBiometricResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerBiometric;
import com.efs.modules.customer.repository.CustomerBiometricRepository;
import com.efs.shared.exception.ResourceNotFoundException;
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
class CustomerBiometricServiceIntegrationTest {

    @Autowired
    private CustomerBiometricServiceInterface customerBiometricService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerBiometricRepository customerBiometricRepository;

    @Test
    void createBiometricShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest request =
                buildBiometricRequest();

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);
        request.setActive(Boolean.TRUE);

        CustomerBiometricResponse response =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getBiometricId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                "FACE",
                response.getBiometricType()
        );

        assertEquals(
                "VERIFIED",
                response.getVerificationStatus()
        );

        assertBigDecimalEquals(
                "98.75",
                response.getVerificationScore()
        );

        assertEquals(
                "PROVIDER-REF-001",
                response.getProviderReference()
        );

        assertEquals(
                LocalDateTime.of(
                        2026, 8, 1, 10, 0, 0
                ),
                response.getEnrolledAt()
        );

        assertEquals(
                LocalDateTime.of(
                        2026, 8, 29, 8, 0, 0
                ),
                response.getLastVerifiedAt()
        );

        assertEquals(
                Boolean.TRUE,
                response.getActive()
        );

        assertEquals(
                createdBy,
                response.getCreatedBy()
        );

        assertEquals(
                updatedBy,
                response.getUpdatedBy()
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertNull(response.getDeletedAt());

        assertTrue(
                customerBiometricRepository.existsById(
                        response.getBiometricId()
                )
        );
    }

    @Test
    void createBiometricShouldDefaultActiveToTrueWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest request =
                buildBiometricRequest();

        request.setActive(null);

        CustomerBiometricResponse response =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.TRUE,
                response.getActive()
        );
    }

    @Test
    void createBiometricShouldPreserveFalseActiveValue() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest request =
                buildBiometricRequest();

        request.setActive(Boolean.FALSE);

        CustomerBiometricResponse response =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getActive()
        );
    }

    @Test
    void createBiometricShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService.createBiometric(
                        UUID.randomUUID(),
                        buildBiometricRequest()
                )
        );
    }

    @Test
    void getBiometricByIdShouldReturnExistingBiometric() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        CustomerBiometricResponse found =
                customerBiometricService.getBiometricById(
                        created.getBiometricId()
                );

        assertEquals(
                created.getBiometricId(),
                found.getBiometricId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getBiometricType(),
                found.getBiometricType()
        );

        assertEquals(
                created.getVerificationStatus(),
                found.getVerificationStatus()
        );
    }

    @Test
    void getBiometricByIdShouldThrowWhenBiometricDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService.getBiometricById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getBiometricsByCustomerIdShouldReturnCustomerBiometrics() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse first =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        CustomerBiometricRequest secondRequest =
                buildBiometricRequest();

        secondRequest.setBiometricType("FINGERPRINT");
        secondRequest.setProviderReference(
                "PROVIDER-REF-002"
        );

        CustomerBiometricResponse second =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerBiometricResponse> biometrics =
                customerBiometricService
                        .getBiometricsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, biometrics.size());

        assertTrue(
                biometrics.stream()
                        .anyMatch(biometric ->
                                first.getBiometricId()
                                        .equals(
                                                biometric.getBiometricId()
                                        )
                        )
        );

        assertTrue(
                biometrics.stream()
                        .anyMatch(biometric ->
                                second.getBiometricId()
                                        .equals(
                                                biometric.getBiometricId()
                                        )
                        )
        );
    }

    @Test
    void getBiometricsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService
                        .getBiometricsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updateBiometricShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest createRequest =
                buildBiometricRequest();

        UUID createdBy = UUID.randomUUID();

        createRequest.setCreatedBy(createdBy);

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        createRequest
                );

        CustomerBiometricRequest updateRequest =
                new CustomerBiometricRequest();

        UUID updatedBy = UUID.randomUUID();

        LocalDateTime enrolledAt =
                LocalDateTime.of(
                        2026, 7, 15, 9, 30, 0
                );

        LocalDateTime lastVerifiedAt =
                LocalDateTime.of(
                        2026, 8, 29, 8, 30, 0
                );

        updateRequest.setBiometricType(
                "FINGERPRINT"
        );

        updateRequest.setVerificationStatus(
                "PENDING"
        );

        updateRequest.setVerificationScore(
                new BigDecimal("72.25")
        );

        updateRequest.setProviderReference(
                "PROVIDER-REF-UPDATED"
        );

        updateRequest.setEnrolledAt(enrolledAt);
        updateRequest.setLastVerifiedAt(lastVerifiedAt);
        updateRequest.setActive(Boolean.FALSE);
        updateRequest.setUpdatedBy(updatedBy);

        CustomerBiometricResponse updated =
                customerBiometricService.updateBiometric(
                        created.getBiometricId(),
                        updateRequest
                );

        assertEquals(
                created.getBiometricId(),
                updated.getBiometricId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                "FINGERPRINT",
                updated.getBiometricType()
        );

        assertEquals(
                "PENDING",
                updated.getVerificationStatus()
        );

        assertBigDecimalEquals(
                "72.25",
                updated.getVerificationScore()
        );

        assertEquals(
                "PROVIDER-REF-UPDATED",
                updated.getProviderReference()
        );

        assertEquals(
                enrolledAt,
                updated.getEnrolledAt()
        );

        assertEquals(
                lastVerifiedAt,
                updated.getLastVerifiedAt()
        );

        assertEquals(
                Boolean.FALSE,
                updated.getActive()
        );

        assertEquals(
                createdBy,
                updated.getCreatedBy()
        );

        assertEquals(
                updatedBy,
                updated.getUpdatedBy()
        );

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateBiometricShouldDefaultActiveToTrueWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest createRequest =
                buildBiometricRequest();

        createRequest.setActive(Boolean.FALSE);

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(
                Boolean.FALSE,
                created.getActive()
        );

        CustomerBiometricRequest updateRequest =
                buildBiometricRequest();

        updateRequest.setActive(null);

        CustomerBiometricResponse updated =
                customerBiometricService.updateBiometric(
                        created.getBiometricId(),
                        updateRequest
                );

        assertEquals(
                Boolean.TRUE,
                updated.getActive()
        );
    }

    @Test
    void updateBiometricShouldThrowWhenBiometricDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService.updateBiometric(
                        UUID.randomUUID(),
                        buildBiometricRequest()
                )
        );
    }

    @Test
    void deleteBiometricShouldSoftDeleteAndDeactivateExistingBiometric() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        customerBiometricService.deleteBiometric(
                created.getBiometricId()
        );

        CustomerBiometric persisted =
                customerBiometricRepository.findById(
                        created.getBiometricId()
                ).orElseThrow();

        assertNotNull(
                persisted.getDeletedAt()
        );

        assertEquals(
                Boolean.FALSE,
                persisted.getActive()
        );

        assertNotNull(
                persisted.getUpdatedAt()
        );

        assertTrue(
                customerBiometricRepository.existsById(
                        created.getBiometricId()
                )
        );
    }

    @Test
    void getBiometricByIdShouldNotReturnSoftDeletedBiometric() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        customerBiometricService.deleteBiometric(
                created.getBiometricId()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService.getBiometricById(
                        created.getBiometricId()
                )
        );
    }

    @Test
    void getBiometricsByCustomerIdShouldExcludeSoftDeletedBiometrics() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse active =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        CustomerBiometricRequest deletedRequest =
                buildBiometricRequest();

        deletedRequest.setBiometricType(
                "FINGERPRINT"
        );

        CustomerBiometricResponse deleted =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        deletedRequest
                );

        customerBiometricService.deleteBiometric(
                deleted.getBiometricId()
        );

        List<CustomerBiometricResponse> biometrics =
                customerBiometricService
                        .getBiometricsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(1, biometrics.size());

        assertEquals(
                active.getBiometricId(),
                biometrics.getFirst()
                        .getBiometricId()
        );

        assertFalse(
                biometrics.stream()
                        .anyMatch(biometric ->
                                deleted.getBiometricId()
                                        .equals(
                                                biometric.getBiometricId()
                                        )
                        )
        );
    }

    @Test
    void repositoryShouldFindNonDeletedBiometricsByType() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricRequest faceRequest =
                buildBiometricRequest();

        CustomerBiometricResponse face =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        faceRequest
                );

        CustomerBiometricRequest fingerprintRequest =
                buildBiometricRequest();

        fingerprintRequest.setBiometricType(
                "FINGERPRINT"
        );

        customerBiometricService.createBiometric(
                customer.getCustomerId(),
                fingerprintRequest
        );

        List<CustomerBiometric> faceBiometrics =
                customerBiometricRepository
                        .findByCustomerIdAndBiometricTypeAndDeletedAtIsNull(
                                customer.getCustomerId(),
                                "FACE"
                        );

        assertEquals(
                1,
                faceBiometrics.size()
        );

        assertEquals(
                face.getBiometricId(),
                faceBiometrics.getFirst()
                        .getBiometricId()
        );
    }

    @Test
    void repositoryShouldExcludeDeletedBiometricFromTypeLookup() {

        CustomerResponse customer = createCustomer();

        CustomerBiometricResponse created =
                customerBiometricService.createBiometric(
                        customer.getCustomerId(),
                        buildBiometricRequest()
                );

        customerBiometricService.deleteBiometric(
                created.getBiometricId()
        );

        List<CustomerBiometric> biometrics =
                customerBiometricRepository
                        .findByCustomerIdAndBiometricTypeAndDeletedAtIsNull(
                                customer.getCustomerId(),
                                "FACE"
                        );

        assertTrue(
                biometrics.isEmpty()
        );
    }

    @Test
    void deleteBiometricShouldThrowWhenBiometricDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBiometricService.deleteBiometric(
                        UUID.randomUUID()
                )
        );
    }

    private CustomerResponse createCustomer() {

        CustomerRequest request =
                new CustomerRequest();

        request.setCustomerNumber(
                "CUST-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 12)
        );

        request.setCustomerType("INDIVIDUAL");
        request.setFirstName("Integration");
        request.setLastName("Test");

        return customerService.createCustomer(request);
    }

    private CustomerBiometricRequest buildBiometricRequest() {

        CustomerBiometricRequest request =
                new CustomerBiometricRequest();

        request.setBiometricType("FACE");

        request.setVerificationStatus(
                "VERIFIED"
        );

        request.setVerificationScore(
                new BigDecimal("98.75")
        );

        request.setProviderReference(
                "PROVIDER-REF-001"
        );

        request.setEnrolledAt(
                LocalDateTime.of(
                        2026, 8, 1, 10, 0, 0
                )
        );

        request.setLastVerifiedAt(
                LocalDateTime.of(
                        2026, 8, 29, 8, 0, 0
                )
        );

        request.setActive(Boolean.TRUE);

        return request;
    }

    private void assertBigDecimalEquals(
            String expected,
            BigDecimal actual) {

        assertNotNull(actual);

        assertEquals(
                0,
                actual.compareTo(
                        new BigDecimal(expected)
                )
        );
    }
}