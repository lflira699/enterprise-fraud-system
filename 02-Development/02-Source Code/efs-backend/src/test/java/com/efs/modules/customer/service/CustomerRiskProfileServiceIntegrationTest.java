package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerRiskProfileServiceIntegrationTest {

    @Autowired
    private CustomerRiskProfileServiceInterface customerRiskProfileService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerRiskProfileRepository customerRiskProfileRepository;

    @Test
    void createRiskProfileShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileRequest request =
                buildRiskProfileRequest();

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerRiskProfileResponse response =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getProfileId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertBigDecimalEquals(
                "75.50",
                response.getCurrentRiskScore()
        );

        assertEquals(
                "HIGH",
                response.getRiskLevel()
        );

        assertBigDecimalEquals(
                "20.00",
                response.getBehaviorScore()
        );

        assertBigDecimalEquals(
                "80.00",
                response.getFraudScore()
        );

        assertBigDecimalEquals(
                "25.00",
                response.getAmlScore()
        );

        assertBigDecimalEquals(
                "10.00",
                response.getKycScore()
        );

        assertBigDecimalEquals(
                "60.00",
                response.getDeviceScore()
        );

        assertBigDecimalEquals(
                "5.00",
                response.getSanctionsScore()
        );

        assertBigDecimalEquals(
                "2.00",
                response.getPepScore()
        );

        assertBigDecimalEquals(
                "15.00",
                response.getWatchlistScore()
        );

        assertNotNull(response.getLastCalculation());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertEquals(
                createdBy,
                response.getCreatedBy()
        );

        assertEquals(
                updatedBy,
                response.getUpdatedBy()
        );

        assertNull(response.getDeletedAt());

        assertTrue(
                customerRiskProfileRepository.existsById(
                        response.getProfileId()
                )
        );
    }

    @Test
    void createRiskProfileShouldDefaultAllScoresToZeroWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileRequest request =
                new CustomerRiskProfileRequest();

        request.setRiskLevel("LOW");

        CustomerRiskProfileResponse response =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        request
                );

        assertZero(response.getCurrentRiskScore());
        assertZero(response.getBehaviorScore());
        assertZero(response.getFraudScore());
        assertZero(response.getAmlScore());
        assertZero(response.getKycScore());
        assertZero(response.getDeviceScore());
        assertZero(response.getSanctionsScore());
        assertZero(response.getPepScore());
        assertZero(response.getWatchlistScore());

        assertEquals(
                "LOW",
                response.getRiskLevel()
        );

        assertNotNull(response.getLastCalculation());
    }

    @Test
    void createRiskProfileShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService.createRiskProfile(
                        UUID.randomUUID(),
                        buildRiskProfileRequest()
                )
        );
    }

    @Test
    void createRiskProfileShouldRejectDuplicateActiveProfile() {

        CustomerResponse customer = createCustomer();

        customerRiskProfileService.createRiskProfile(
                customer.getCustomerId(),
                buildRiskProfileRequest()
        );

        assertThrows(
                DuplicateRecordException.class,
                () -> customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                )
        );
    }

    @Test
    void getRiskProfileByCustomerIdShouldReturnExistingProfile() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileResponse created =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                );

        CustomerRiskProfileResponse found =
                customerRiskProfileService
                        .getRiskProfileByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(
                created.getProfileId(),
                found.getProfileId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getRiskLevel(),
                found.getRiskLevel()
        );

        assertBigDecimalEquals(
                created.getCurrentRiskScore(),
                found.getCurrentRiskScore()
        );
    }

    @Test
    void getRiskProfileByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService
                        .getRiskProfileByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getRiskProfileByCustomerIdShouldThrowWhenProfileDoesNotExist() {

        CustomerResponse customer = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService
                        .getRiskProfileByCustomerId(
                                customer.getCustomerId()
                        )
        );
    }

    @Test
    void updateRiskProfileShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileRequest createRequest =
                buildRiskProfileRequest();

        UUID createdBy = UUID.randomUUID();

        createRequest.setCreatedBy(createdBy);

        CustomerRiskProfileResponse created =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        createRequest
                );

        CustomerRiskProfileRequest updateRequest =
                new CustomerRiskProfileRequest();

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setCurrentRiskScore(
                new BigDecimal("91.25")
        );
        updateRequest.setRiskLevel("CRITICAL");
        updateRequest.setBehaviorScore(
                new BigDecimal("65.00")
        );
        updateRequest.setFraudScore(
                new BigDecimal("95.00")
        );
        updateRequest.setAmlScore(
                new BigDecimal("55.00")
        );
        updateRequest.setKycScore(
                new BigDecimal("35.00")
        );
        updateRequest.setDeviceScore(
                new BigDecimal("88.00")
        );
        updateRequest.setSanctionsScore(
                new BigDecimal("12.00")
        );
        updateRequest.setPepScore(
                new BigDecimal("8.00")
        );
        updateRequest.setWatchlistScore(
                new BigDecimal("40.00")
        );
        updateRequest.setUpdatedBy(updatedBy);

        CustomerRiskProfileResponse updated =
                customerRiskProfileService.updateRiskProfile(
                        customer.getCustomerId(),
                        updateRequest
                );

        assertEquals(
                created.getProfileId(),
                updated.getProfileId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                "CRITICAL",
                updated.getRiskLevel()
        );

        assertBigDecimalEquals(
                "91.25",
                updated.getCurrentRiskScore()
        );

        assertBigDecimalEquals(
                "65.00",
                updated.getBehaviorScore()
        );

        assertBigDecimalEquals(
                "95.00",
                updated.getFraudScore()
        );

        assertBigDecimalEquals(
                "55.00",
                updated.getAmlScore()
        );

        assertBigDecimalEquals(
                "35.00",
                updated.getKycScore()
        );

        assertBigDecimalEquals(
                "88.00",
                updated.getDeviceScore()
        );

        assertBigDecimalEquals(
                "12.00",
                updated.getSanctionsScore()
        );

        assertBigDecimalEquals(
                "8.00",
                updated.getPepScore()
        );

        assertBigDecimalEquals(
                "40.00",
                updated.getWatchlistScore()
        );

        assertEquals(
                createdBy,
                updated.getCreatedBy()
        );

        assertEquals(
                updatedBy,
                updated.getUpdatedBy()
        );

        assertNotNull(updated.getLastCalculation());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateRiskProfileShouldDefaultAllScoresToZeroWhenNull() {

        CustomerResponse customer = createCustomer();

        customerRiskProfileService.createRiskProfile(
                customer.getCustomerId(),
                buildRiskProfileRequest()
        );

        CustomerRiskProfileRequest updateRequest =
                new CustomerRiskProfileRequest();

        updateRequest.setRiskLevel("LOW");

        CustomerRiskProfileResponse updated =
                customerRiskProfileService.updateRiskProfile(
                        customer.getCustomerId(),
                        updateRequest
                );

        assertZero(updated.getCurrentRiskScore());
        assertZero(updated.getBehaviorScore());
        assertZero(updated.getFraudScore());
        assertZero(updated.getAmlScore());
        assertZero(updated.getKycScore());
        assertZero(updated.getDeviceScore());
        assertZero(updated.getSanctionsScore());
        assertZero(updated.getPepScore());
        assertZero(updated.getWatchlistScore());

        assertEquals(
                "LOW",
                updated.getRiskLevel()
        );

        assertNotNull(updated.getLastCalculation());
    }

    @Test
    void updateRiskProfileShouldThrowWhenProfileDoesNotExist() {

        CustomerResponse customer = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService.updateRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                )
        );
    }

    @Test
    void deleteRiskProfileShouldSoftDeleteExistingProfile() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileResponse created =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                );

        customerRiskProfileService.deleteRiskProfile(
                customer.getCustomerId()
        );

        CustomerRiskProfile persisted =
                customerRiskProfileRepository.findById(
                        created.getProfileId()
                ).orElseThrow();

        assertNotNull(
                persisted.getDeletedAt()
        );

        assertNotNull(
                persisted.getUpdatedAt()
        );

        assertTrue(
                customerRiskProfileRepository.existsById(
                        created.getProfileId()
                )
        );
    }

    @Test
    void getRiskProfileShouldNotReturnSoftDeletedProfile() {

        CustomerResponse customer = createCustomer();

        customerRiskProfileService.createRiskProfile(
                customer.getCustomerId(),
                buildRiskProfileRequest()
        );

        customerRiskProfileService.deleteRiskProfile(
                customer.getCustomerId()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService
                        .getRiskProfileByCustomerId(
                                customer.getCustomerId()
                        )
        );
    }

    @Test
    void repositoryShouldExcludeSoftDeletedProfileFromActiveLookup() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileResponse created =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                );

        assertTrue(
                customerRiskProfileRepository
                        .existsByCustomerIdAndDeletedAtIsNull(
                                customer.getCustomerId()
                        )
        );

        customerRiskProfileService.deleteRiskProfile(
                customer.getCustomerId()
        );

        assertFalse(
                customerRiskProfileRepository
                        .existsByCustomerIdAndDeletedAtIsNull(
                                customer.getCustomerId()
                        )
        );

        assertTrue(
                customerRiskProfileRepository.findById(
                        created.getProfileId()
                ).isPresent()
        );
    }

    @Test
    void repositoryShouldFindActiveProfileByProfileId() {

        CustomerResponse customer = createCustomer();

        CustomerRiskProfileResponse created =
                customerRiskProfileService.createRiskProfile(
                        customer.getCustomerId(),
                        buildRiskProfileRequest()
                );

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByProfileIdAndDeletedAtIsNull(
                                created.getProfileId()
                        )
                        .orElseThrow();

        assertEquals(
                created.getProfileId(),
                profile.getProfileId()
        );

        assertEquals(
                customer.getCustomerId(),
                profile.getCustomerId()
        );
    }

    @Test
    void deleteRiskProfileShouldThrowWhenProfileDoesNotExist() {

        CustomerResponse customer = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRiskProfileService.deleteRiskProfile(
                        customer.getCustomerId()
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

    private CustomerRiskProfileRequest buildRiskProfileRequest() {

        CustomerRiskProfileRequest request =
                new CustomerRiskProfileRequest();

        request.setCurrentRiskScore(
                new BigDecimal("75.50")
        );

        request.setRiskLevel("HIGH");

        request.setBehaviorScore(
                new BigDecimal("20.00")
        );

        request.setFraudScore(
                new BigDecimal("80.00")
        );

        request.setAmlScore(
                new BigDecimal("25.00")
        );

        request.setKycScore(
                new BigDecimal("10.00")
        );

        request.setDeviceScore(
                new BigDecimal("60.00")
        );

        request.setSanctionsScore(
                new BigDecimal("5.00")
        );

        request.setPepScore(
                new BigDecimal("2.00")
        );

        request.setWatchlistScore(
                new BigDecimal("15.00")
        );

        return request;
    }

    private void assertZero(BigDecimal actual) {

        assertNotNull(actual);

        assertEquals(
                0,
                actual.compareTo(BigDecimal.ZERO)
        );
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

    private void assertBigDecimalEquals(
            BigDecimal expected,
            BigDecimal actual) {

        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(
                0,
                actual.compareTo(expected)
        );
    }
}