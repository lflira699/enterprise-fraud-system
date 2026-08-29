package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void createCustomerShouldPersistCustomerWithProvidedValues() {

        CustomerRequest request = buildRequest(uniqueCustomerNumber());

        request.setRiskLevel("MEDIUM");
        request.setRiskScore(new BigDecimal("42.50"));
        request.setCustomerStatus("ACTIVE");

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertNotNull(response.getCustomerId());

        assertEquals(
                request.getCustomerNumber(),
                response.getCustomerNumber()
        );

        assertEquals("INDIVIDUAL", response.getCustomerType());
        assertEquals("Fernando", response.getFirstName());
        assertEquals("Lira", response.getLastName());
        assertEquals("MEDIUM", response.getRiskLevel());

        assertEquals(
                0,
                new BigDecimal("42.50")
                        .compareTo(response.getRiskScore())
        );

        assertEquals("ACTIVE", response.getCustomerStatus());
        assertEquals("ACTIVE", response.getRecordStatus());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertTrue(
                customerRepository.existsById(
                        response.getCustomerId()
                )
        );
    }

    @Test
    void createCustomerShouldApplyDefaultsWhenRiskFieldsAreNull() {

        CustomerRequest request = buildRequest(uniqueCustomerNumber());

        request.setRiskLevel(null);
        request.setRiskScore(null);
        request.setCustomerStatus(null);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals("LOW", response.getRiskLevel());

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        response.getRiskScore()
                )
        );

        assertEquals("ACTIVE", response.getCustomerStatus());
        assertEquals("ACTIVE", response.getRecordStatus());
    }

    @Test
    void createCustomerShouldRejectDuplicateCustomerNumber() {

        String customerNumber = uniqueCustomerNumber();

        CustomerRequest firstRequest =
                buildRequest(customerNumber);

        CustomerRequest duplicateRequest =
                buildRequest(customerNumber);

        customerService.createCustomer(firstRequest);

        assertThrows(
                DuplicateRecordException.class,
                () -> customerService.createCustomer(
                        duplicateRequest
                )
        );
    }

    @Test
    void getCustomerByIdShouldReturnExistingCustomer() {

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        CustomerResponse found =
                customerService.getCustomerById(
                        created.getCustomerId()
                );

        assertEquals(
                created.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getCustomerNumber(),
                found.getCustomerNumber()
        );
    }

    @Test
    void getCustomerByNumberShouldReturnExistingCustomer() {

        String customerNumber = uniqueCustomerNumber();

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(customerNumber)
                );

        CustomerResponse found =
                customerService.getCustomerByNumber(
                        customerNumber
                );

        assertEquals(
                created.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                customerNumber,
                found.getCustomerNumber()
        );
    }

    @Test
    void getAllCustomersShouldIncludeCreatedCustomer() {

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        List<CustomerResponse> customers =
                customerService.getAllCustomers();

        assertNotNull(customers);

        assertTrue(
                customers.stream()
                        .anyMatch(customer ->
                                created.getCustomerId()
                                        .equals(
                                                customer.getCustomerId()
                                        )
                        )
        );
    }

    @Test
    void updateCustomerShouldPersistUpdatedValues() {

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        CustomerRequest updateRequest =
                buildRequest(uniqueCustomerNumber());

        updateRequest.setFirstName("Luis");
        updateRequest.setMiddleName("Fernando");
        updateRequest.setLastName("Lira");
        updateRequest.setRiskLevel("HIGH");
        updateRequest.setRiskScore(
                new BigDecimal("88.75")
        );
        updateRequest.setCustomerStatus("REVIEW");

        CustomerResponse updated =
                customerService.updateCustomer(
                        created.getCustomerId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                updateRequest.getCustomerNumber(),
                updated.getCustomerNumber()
        );

        assertEquals("Luis", updated.getFirstName());
        assertEquals("Fernando", updated.getMiddleName());
        assertEquals("HIGH", updated.getRiskLevel());

        assertEquals(
                0,
                new BigDecimal("88.75")
                        .compareTo(updated.getRiskScore())
        );

        assertEquals(
                "REVIEW",
                updated.getCustomerStatus()
        );

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateCustomerShouldApplyDefaultsWhenNullableValuesAreRemoved() {

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        CustomerRequest updateRequest =
                buildRequest(created.getCustomerNumber());

        updateRequest.setRiskLevel(null);
        updateRequest.setRiskScore(null);
        updateRequest.setCustomerStatus(null);

        CustomerResponse updated =
                customerService.updateCustomer(
                        created.getCustomerId(),
                        updateRequest
                );

        assertEquals("LOW", updated.getRiskLevel());

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        updated.getRiskScore()
                )
        );

        assertEquals(
                "ACTIVE",
                updated.getCustomerStatus()
        );
    }

    @Test
    void updateCustomerShouldRejectCustomerNumberAlreadyUsedByAnotherCustomer() {

        CustomerResponse first =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        CustomerResponse second =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        CustomerRequest updateRequest =
                buildRequest(second.getCustomerNumber());

        assertThrows(
                DuplicateRecordException.class,
                () -> customerService.updateCustomer(
                        first.getCustomerId(),
                        updateRequest
                )
        );
    }

    @Test
    void deleteCustomerShouldSoftDeleteExistingCustomer() {

        CustomerResponse created =
                customerService.createCustomer(
                        buildRequest(uniqueCustomerNumber())
                );

        customerService.deleteCustomer(
                created.getCustomerId()
        );

        Customer persisted =
                customerRepository
                        .findById(created.getCustomerId())
                        .orElseThrow();

        assertEquals(
                "DELETED",
                persisted.getRecordStatus()
        );

        assertNotNull(persisted.getDeletedAt());
        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerRepository.existsById(
                        created.getCustomerId()
                )
        );
    }

    @Test
    void getCustomerByIdShouldThrowWhenCustomerDoesNotExist() {

        UUID missingId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(
                        missingId
                )
        );
    }

    @Test
    void getCustomerByNumberShouldThrowWhenCustomerDoesNotExist() {

        String missingNumber =
                uniqueCustomerNumber();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerByNumber(
                        missingNumber
                )
        );
    }

    @Test
    void updateCustomerShouldThrowWhenCustomerDoesNotExist() {

        UUID missingId = UUID.randomUUID();

        CustomerRequest request =
                buildRequest(uniqueCustomerNumber());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(
                        missingId,
                        request
                )
        );
    }

    @Test
    void deleteCustomerShouldThrowWhenCustomerDoesNotExist() {

        UUID missingId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(
                        missingId
                )
        );
    }

    private CustomerRequest buildRequest(
            String customerNumber) {

        CustomerRequest request =
                new CustomerRequest();

        request.setCustomerNumber(customerNumber);
        request.setCustomerType("INDIVIDUAL");

        request.setFirstName("Fernando");
        request.setMiddleName("Test");
        request.setLastName("Lira");
        request.setSecondLastName("Integration");

        request.setDateOfBirth(
                LocalDate.of(1985, 1, 15)
        );

        request.setRiskLevel("LOW");
        request.setRiskScore(
                new BigDecimal("10.00")
        );
        request.setCustomerStatus("ACTIVE");

        return request;
    }

    private String uniqueCustomerNumber() {

        return "CUST-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 12);
    }
}