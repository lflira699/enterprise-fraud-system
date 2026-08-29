package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerPhoneRequest;
import com.efs.modules.customer.dto.CustomerPhoneResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerPhone;
import com.efs.modules.customer.repository.CustomerPhoneRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerPhoneServiceIntegrationTest {

    @Autowired
    private CustomerPhoneServiceInterface customerPhoneService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerPhoneRepository customerPhoneRepository;

    @Test
    void createPhoneShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest request =
                buildPhoneRequest(uniquePhoneNumber());

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setPrimary(Boolean.TRUE);
        request.setVerified(Boolean.FALSE);
        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerPhoneResponse response =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerPhoneId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals("MOBILE", response.getPhoneType());
        assertEquals("+502", response.getCountryCode());

        assertEquals(
                request.getPhoneNumber(),
                response.getPhoneNumber()
        );

        assertEquals(Boolean.TRUE, response.getPrimary());
        assertEquals(Boolean.FALSE, response.getVerified());
        assertNull(response.getVerifiedAt());

        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(updatedBy, response.getUpdatedBy());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertTrue(
                customerPhoneRepository.existsById(
                        response.getCustomerPhoneId()
                )
        );
    }

    @Test
    void createPhoneShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest request =
                buildPhoneRequest(uniquePhoneNumber());

        request.setPrimary(null);
        request.setVerified(null);

        CustomerPhoneResponse response =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(Boolean.FALSE, response.getPrimary());
        assertEquals(Boolean.FALSE, response.getVerified());
        assertNull(response.getVerifiedAt());
    }

    @Test
    void createPhoneShouldSetVerifiedAtWhenVerifiedIsTrue() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest request =
                buildPhoneRequest(uniquePhoneNumber());

        request.setVerified(Boolean.TRUE);

        CustomerPhoneResponse response =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(Boolean.TRUE, response.getVerified());
        assertNotNull(response.getVerifiedAt());
    }

    @Test
    void createPhoneShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.createPhone(
                        UUID.randomUUID(),
                        buildPhoneRequest(uniquePhoneNumber())
                )
        );
    }

    @Test
    void getPhoneByIdShouldReturnExistingPhone() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        CustomerPhoneResponse found =
                customerPhoneService.getPhoneById(
                        created.getCustomerPhoneId()
                );

        assertEquals(
                created.getCustomerPhoneId(),
                found.getCustomerPhoneId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getPhoneNumber(),
                found.getPhoneNumber()
        );
    }

    @Test
    void getPhoneByIdShouldThrowWhenPhoneDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.getPhoneById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getPhonesByCustomerIdShouldReturnCustomerPhones() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse first =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        CustomerPhoneRequest secondRequest =
                buildPhoneRequest(uniquePhoneNumber());

        secondRequest.setPhoneType("HOME");

        CustomerPhoneResponse second =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerPhoneResponse> phones =
                customerPhoneService.getPhonesByCustomerId(
                        customer.getCustomerId()
                );

        assertEquals(2, phones.size());

        assertTrue(
                phones.stream().anyMatch(phone ->
                        first.getCustomerPhoneId()
                                .equals(phone.getCustomerPhoneId())
                )
        );

        assertTrue(
                phones.stream().anyMatch(phone ->
                        second.getCustomerPhoneId()
                                .equals(phone.getCustomerPhoneId())
                )
        );
    }

    @Test
    void getPhonesByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.getPhonesByCustomerId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void updatePhoneShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        CustomerPhoneRequest updateRequest =
                buildPhoneRequest(uniquePhoneNumber());

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setPhoneType("WORK");
        updateRequest.setCountryCode("+1");
        updateRequest.setPrimary(Boolean.TRUE);
        updateRequest.setVerified(Boolean.FALSE);
        updateRequest.setUpdatedBy(updatedBy);

        CustomerPhoneResponse updated =
                customerPhoneService.updatePhone(
                        created.getCustomerPhoneId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerPhoneId(),
                updated.getCustomerPhoneId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals("WORK", updated.getPhoneType());
        assertEquals("+1", updated.getCountryCode());

        assertEquals(
                updateRequest.getPhoneNumber(),
                updated.getPhoneNumber()
        );

        assertEquals(Boolean.TRUE, updated.getPrimary());
        assertEquals(Boolean.FALSE, updated.getVerified());

        assertEquals(updatedBy, updated.getUpdatedBy());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updatePhoneShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest createRequest =
                buildPhoneRequest(uniquePhoneNumber());

        createRequest.setPrimary(Boolean.TRUE);
        createRequest.setVerified(Boolean.TRUE);

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(Boolean.TRUE, created.getPrimary());
        assertEquals(Boolean.TRUE, created.getVerified());
        assertNotNull(created.getVerifiedAt());

        CustomerPhoneRequest updateRequest =
                buildPhoneRequest(created.getPhoneNumber());

        updateRequest.setPrimary(null);
        updateRequest.setVerified(null);

        CustomerPhoneResponse updated =
                customerPhoneService.updatePhone(
                        created.getCustomerPhoneId(),
                        updateRequest
                );

        assertEquals(Boolean.FALSE, updated.getPrimary());
        assertEquals(Boolean.FALSE, updated.getVerified());
        assertNull(updated.getVerifiedAt());
    }

    @Test
    void updatePhoneShouldSetVerifiedAtWhenChangingFromFalseToTrue() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest createRequest =
                buildPhoneRequest(uniquePhoneNumber());

        createRequest.setVerified(Boolean.FALSE);

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNull(created.getVerifiedAt());

        CustomerPhoneRequest updateRequest =
                buildPhoneRequest(created.getPhoneNumber());

        updateRequest.setVerified(Boolean.TRUE);

        CustomerPhoneResponse updated =
                customerPhoneService.updatePhone(
                        created.getCustomerPhoneId(),
                        updateRequest
                );

        assertEquals(Boolean.TRUE, updated.getVerified());
        assertNotNull(updated.getVerifiedAt());
    }

    @Test
    void updatePhoneShouldPreserveVerifiedAtWhenRemainingVerified() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest createRequest =
                buildPhoneRequest(uniquePhoneNumber());

        createRequest.setVerified(Boolean.TRUE);

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNotNull(created.getVerifiedAt());

        CustomerPhoneRequest updateRequest =
                buildPhoneRequest(created.getPhoneNumber());

        updateRequest.setVerified(Boolean.TRUE);
        updateRequest.setPhoneType("WORK");

        CustomerPhoneResponse updated =
                customerPhoneService.updatePhone(
                        created.getCustomerPhoneId(),
                        updateRequest
                );

        assertEquals(Boolean.TRUE, updated.getVerified());

        assertEquals(
                created.getVerifiedAt(),
                updated.getVerifiedAt()
        );
    }

    @Test
    void updatePhoneShouldClearVerifiedAtWhenChangingToFalse() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest createRequest =
                buildPhoneRequest(uniquePhoneNumber());

        createRequest.setVerified(Boolean.TRUE);

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNotNull(created.getVerifiedAt());

        CustomerPhoneRequest updateRequest =
                buildPhoneRequest(created.getPhoneNumber());

        updateRequest.setVerified(Boolean.FALSE);

        CustomerPhoneResponse updated =
                customerPhoneService.updatePhone(
                        created.getCustomerPhoneId(),
                        updateRequest
                );

        assertEquals(Boolean.FALSE, updated.getVerified());
        assertNull(updated.getVerifiedAt());
    }

    @Test
    void updatePhoneShouldThrowWhenPhoneDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.updatePhone(
                        UUID.randomUUID(),
                        buildPhoneRequest(uniquePhoneNumber())
                )
        );
    }

    @Test
    void deletePhoneShouldSoftDeleteExistingPhone() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        UUID deletedBy = UUID.randomUUID();

        customerPhoneService.deletePhone(
                created.getCustomerPhoneId(),
                deletedBy
        );

        CustomerPhone persisted =
                customerPhoneRepository.findById(
                        created.getCustomerPhoneId()
                ).orElseThrow();

        assertNotNull(persisted.getDeletedAt());

        assertEquals(
                deletedBy,
                persisted.getDeletedBy()
        );

        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerPhoneRepository.existsById(
                        created.getCustomerPhoneId()
                )
        );
    }

    @Test
    void getPhoneByIdShouldNotReturnSoftDeletedPhone() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        customerPhoneService.deletePhone(
                created.getCustomerPhoneId(),
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.getPhoneById(
                        created.getCustomerPhoneId()
                )
        );
    }

    @Test
    void getPhonesByCustomerIdShouldExcludeSoftDeletedPhones() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneResponse activePhone =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        CustomerPhoneResponse deletedPhone =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(uniquePhoneNumber())
                );

        customerPhoneService.deletePhone(
                deletedPhone.getCustomerPhoneId(),
                UUID.randomUUID()
        );

        List<CustomerPhoneResponse> phones =
                customerPhoneService.getPhonesByCustomerId(
                        customer.getCustomerId()
                );

        assertEquals(1, phones.size());

        assertEquals(
                activePhone.getCustomerPhoneId(),
                phones.getFirst().getCustomerPhoneId()
        );

        assertFalse(
                phones.stream().anyMatch(phone ->
                        deletedPhone.getCustomerPhoneId()
                                .equals(phone.getCustomerPhoneId())
                )
        );
    }

    @Test
    void repositoryShouldReturnOnlyPrimaryNonDeletedPhones() {

        CustomerResponse customer = createCustomer();

        CustomerPhoneRequest primaryRequest =
                buildPhoneRequest(uniquePhoneNumber());

        primaryRequest.setPrimary(Boolean.TRUE);

        CustomerPhoneResponse primary =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        primaryRequest
                );

        CustomerPhoneRequest secondaryRequest =
                buildPhoneRequest(uniquePhoneNumber());

        secondaryRequest.setPrimary(Boolean.FALSE);

        customerPhoneService.createPhone(
                customer.getCustomerId(),
                secondaryRequest
        );

        List<CustomerPhone> primaryPhones =
                customerPhoneRepository
                        .findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertEquals(1, primaryPhones.size());

        assertEquals(
                primary.getCustomerPhoneId(),
                primaryPhones.getFirst().getCustomerPhoneId()
        );
    }

    @Test
    void repositoryShouldFindPhoneByCustomerAndPhoneNumber() {

        CustomerResponse customer = createCustomer();

        String phoneNumber = uniquePhoneNumber();

        CustomerPhoneResponse created =
                customerPhoneService.createPhone(
                        customer.getCustomerId(),
                        buildPhoneRequest(phoneNumber)
                );

        CustomerPhone found =
                customerPhoneRepository
                        .findByCustomerIdAndPhoneNumberAndDeletedAtIsNull(
                                customer.getCustomerId(),
                                phoneNumber
                        )
                        .orElseThrow();

        assertEquals(
                created.getCustomerPhoneId(),
                found.getCustomerPhoneId()
        );
    }

    @Test
    void deletePhoneShouldThrowWhenPhoneDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerPhoneService.deletePhone(
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );
    }

    private CustomerResponse createCustomer() {

        CustomerRequest request = new CustomerRequest();

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

    private CustomerPhoneRequest buildPhoneRequest(
            String phoneNumber) {

        CustomerPhoneRequest request =
                new CustomerPhoneRequest();

        request.setPhoneType("MOBILE");
        request.setCountryCode("+502");
        request.setPhoneNumber(phoneNumber);
        request.setPrimary(Boolean.FALSE);
        request.setVerified(Boolean.FALSE);

        return request;
    }

    private String uniquePhoneNumber() {

        long value =
                Math.abs(
                        UUID.randomUUID()
                                .getLeastSignificantBits()
                );

        return "5" +
                String.valueOf(value)
                        .substring(0, 7);
    }
}