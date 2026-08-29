package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerAddressRequest;
import com.efs.modules.customer.dto.CustomerAddressResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerAddress;
import com.efs.modules.customer.repository.CustomerAddressRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerAddressServiceIntegrationTest {

    @Autowired
    private CustomerAddressServiceInterface customerAddressService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Test
    void createAddressShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest request = buildAddressRequest();

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);
        request.setPrimary(Boolean.TRUE);

        CustomerAddressResponse response =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerAddressId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals("HOME", response.getAddressType());
        assertEquals(
                "10 Avenida 10-10 Zona 10",
                response.getAddressLine1()
        );
        assertEquals(
                "Apartamento 5A",
                response.getAddressLine2()
        );
        assertEquals(
                "Guatemala City",
                response.getCity()
        );
        assertEquals(
                "Guatemala",
                response.getState()
        );
        assertEquals(
                "01010",
                response.getPostalCode()
        );
        assertEquals(
                "GTM",
                response.getCountryCode()
        );
        assertEquals(
                Boolean.TRUE,
                response.getPrimary()
        );

        assertEquals(
                LocalDate.of(2026, 1, 1),
                response.getEffectiveDate()
        );

        assertEquals(
                LocalDate.of(2030, 12, 31),
                response.getExpirationDate()
        );

        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(updatedBy, response.getUpdatedBy());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertNull(response.getDeletedAt());
        assertNull(response.getDeletedBy());

        assertTrue(
                customerAddressRepository.existsById(
                        response.getCustomerAddressId()
                )
        );
    }

    @Test
    void createAddressShouldDefaultPrimaryToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest request = buildAddressRequest();
        request.setPrimary(null);

        CustomerAddressResponse response =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getPrimary()
        );
    }

    @Test
    void createAddressShouldPreservePrimaryTrue() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest request = buildAddressRequest();
        request.setPrimary(Boolean.TRUE);

        CustomerAddressResponse response =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.TRUE,
                response.getPrimary()
        );
    }

    @Test
    void createAddressShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService.createAddress(
                        missingCustomerId,
                        buildAddressRequest()
                )
        );
    }

    @Test
    void getAddressByIdShouldReturnExistingAddress() {

        CustomerResponse customer = createCustomer();

        CustomerAddressResponse created =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        buildAddressRequest()
                );

        CustomerAddressResponse found =
                customerAddressService.getAddressById(
                        created.getCustomerAddressId()
                );

        assertEquals(
                created.getCustomerAddressId(),
                found.getCustomerAddressId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getAddressLine1(),
                found.getAddressLine1()
        );
    }

    @Test
    void getAddressByIdShouldThrowWhenAddressDoesNotExist() {

        UUID missingAddressId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService.getAddressById(
                        missingAddressId
                )
        );
    }

    @Test
    void getAddressesByCustomerIdShouldReturnActiveAddresses() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest firstRequest =
                buildAddressRequest();

        CustomerAddressResponse first =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        firstRequest
                );

        CustomerAddressRequest secondRequest =
                buildAddressRequest();

        secondRequest.setAddressType("WORK");
        secondRequest.setAddressLine1(
                "20 Calle 5-50 Zona 4"
        );
        secondRequest.setPrimary(Boolean.FALSE);

        CustomerAddressResponse second =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerAddressResponse> addresses =
                customerAddressService
                        .getAddressesByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, addresses.size());

        assertTrue(
                addresses.stream()
                        .anyMatch(address ->
                                first.getCustomerAddressId()
                                        .equals(
                                                address.getCustomerAddressId()
                                        )
                        )
        );

        assertTrue(
                addresses.stream()
                        .anyMatch(address ->
                                second.getCustomerAddressId()
                                        .equals(
                                                address.getCustomerAddressId()
                                        )
                        )
        );
    }

    @Test
    void getAddressesByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService
                        .getAddressesByCustomerId(
                                missingCustomerId
                        )
        );
    }

    @Test
    void updateAddressShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerAddressResponse created =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        buildAddressRequest()
                );

        CustomerAddressRequest updateRequest =
                buildAddressRequest();

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setAddressType("WORK");
        updateRequest.setAddressLine1(
                "Avenida Reforma 12-34"
        );
        updateRequest.setAddressLine2("Office 900");
        updateRequest.setCity("Guatemala");
        updateRequest.setState("Guatemala");
        updateRequest.setPostalCode("01009");
        updateRequest.setCountryCode("GTM");
        updateRequest.setPrimary(Boolean.TRUE);
        updateRequest.setUpdatedBy(updatedBy);

        CustomerAddressResponse updated =
                customerAddressService.updateAddress(
                        created.getCustomerAddressId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerAddressId(),
                updated.getCustomerAddressId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals("WORK", updated.getAddressType());

        assertEquals(
                "Avenida Reforma 12-34",
                updated.getAddressLine1()
        );

        assertEquals(
                "Office 900",
                updated.getAddressLine2()
        );

        assertEquals(
                Boolean.TRUE,
                updated.getPrimary()
        );

        assertEquals(
                updatedBy,
                updated.getUpdatedBy()
        );

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateAddressShouldDefaultPrimaryToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest createRequest =
                buildAddressRequest();

        createRequest.setPrimary(Boolean.TRUE);

        CustomerAddressResponse created =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(
                Boolean.TRUE,
                created.getPrimary()
        );

        CustomerAddressRequest updateRequest =
                buildAddressRequest();

        updateRequest.setPrimary(null);

        CustomerAddressResponse updated =
                customerAddressService.updateAddress(
                        created.getCustomerAddressId(),
                        updateRequest
                );

        assertEquals(
                Boolean.FALSE,
                updated.getPrimary()
        );
    }

    @Test
    void updateAddressShouldThrowWhenAddressDoesNotExist() {

        UUID missingAddressId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService.updateAddress(
                        missingAddressId,
                        buildAddressRequest()
                )
        );
    }

    @Test
    void deleteAddressShouldSoftDeleteExistingAddress() {

        CustomerResponse customer = createCustomer();

        CustomerAddressResponse created =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        buildAddressRequest()
                );

        UUID deletedBy = UUID.randomUUID();

        customerAddressService.deleteAddress(
                created.getCustomerAddressId(),
                deletedBy
        );

        CustomerAddress persisted =
                customerAddressRepository.findById(
                        created.getCustomerAddressId()
                ).orElseThrow();

        assertNotNull(persisted.getDeletedAt());

        assertEquals(
                deletedBy,
                persisted.getDeletedBy()
        );

        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerAddressRepository.existsById(
                        created.getCustomerAddressId()
                )
        );
    }

    @Test
    void getAddressByIdShouldNotReturnSoftDeletedAddress() {

        CustomerResponse customer = createCustomer();

        CustomerAddressResponse created =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        buildAddressRequest()
                );

        customerAddressService.deleteAddress(
                created.getCustomerAddressId(),
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService.getAddressById(
                        created.getCustomerAddressId()
                )
        );
    }

    @Test
    void getAddressesByCustomerIdShouldExcludeSoftDeletedAddresses() {

        CustomerResponse customer = createCustomer();

        CustomerAddressResponse activeAddress =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        buildAddressRequest()
                );

        CustomerAddressRequest deletedRequest =
                buildAddressRequest();

        deletedRequest.setAddressType("WORK");
        deletedRequest.setAddressLine1(
                "Address to delete"
        );

        CustomerAddressResponse deletedAddress =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        deletedRequest
                );

        customerAddressService.deleteAddress(
                deletedAddress.getCustomerAddressId(),
                UUID.randomUUID()
        );

        List<CustomerAddressResponse> addresses =
                customerAddressService
                        .getAddressesByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(1, addresses.size());

        assertEquals(
                activeAddress.getCustomerAddressId(),
                addresses.getFirst()
                        .getCustomerAddressId()
        );

        assertFalse(
                addresses.stream()
                        .anyMatch(address ->
                                deletedAddress
                                        .getCustomerAddressId()
                                        .equals(
                                                address.getCustomerAddressId()
                                        )
                        )
        );
    }

    @Test
    void repositoryShouldReturnOnlyPrimaryNonDeletedAddresses() {

        CustomerResponse customer = createCustomer();

        CustomerAddressRequest primaryRequest =
                buildAddressRequest();

        primaryRequest.setPrimary(Boolean.TRUE);

        CustomerAddressResponse primaryAddress =
                customerAddressService.createAddress(
                        customer.getCustomerId(),
                        primaryRequest
                );

        CustomerAddressRequest secondaryRequest =
                buildAddressRequest();

        secondaryRequest.setAddressType("WORK");
        secondaryRequest.setAddressLine1(
                "Secondary Address"
        );
        secondaryRequest.setPrimary(Boolean.FALSE);

        customerAddressService.createAddress(
                customer.getCustomerId(),
                secondaryRequest
        );

        List<CustomerAddress> primaryAddresses =
                customerAddressRepository
                        .findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertEquals(1, primaryAddresses.size());

        assertEquals(
                primaryAddress.getCustomerAddressId(),
                primaryAddresses.getFirst()
                        .getCustomerAddressId()
        );
    }

    @Test
    void deleteAddressShouldThrowWhenAddressDoesNotExist() {

        UUID missingAddressId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerAddressService.deleteAddress(
                        missingAddressId,
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

    private CustomerAddressRequest buildAddressRequest() {

        CustomerAddressRequest request =
                new CustomerAddressRequest();

        request.setAddressType("HOME");

        request.setAddressLine1(
                "10 Avenida 10-10 Zona 10"
        );

        request.setAddressLine2(
                "Apartamento 5A"
        );

        request.setCity("Guatemala City");
        request.setState("Guatemala");
        request.setPostalCode("01010");
        request.setCountryCode("GTM");

        request.setPrimary(Boolean.FALSE);

        request.setEffectiveDate(
                LocalDate.of(2026, 1, 1)
        );

        request.setExpirationDate(
                LocalDate.of(2030, 12, 31)
        );

        return request;
    }
}