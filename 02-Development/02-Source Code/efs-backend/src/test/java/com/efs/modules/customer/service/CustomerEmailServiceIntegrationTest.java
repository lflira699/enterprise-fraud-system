package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerEmailRequest;
import com.efs.modules.customer.dto.CustomerEmailResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerEmail;
import com.efs.modules.customer.repository.CustomerEmailRepository;
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
class CustomerEmailServiceIntegrationTest {

    @Autowired
    private CustomerEmailServiceInterface customerEmailService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerEmailRepository customerEmailRepository;

    @Test
    void createEmailShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest request =
                buildEmailRequest(uniqueEmailAddress());

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setPrimary(Boolean.TRUE);
        request.setVerified(Boolean.FALSE);
        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerEmailResponse response =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerEmailId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals("PERSONAL", response.getEmailType());

        assertEquals(
                request.getEmailAddress(),
                response.getEmailAddress()
        );

        assertEquals(Boolean.TRUE, response.getPrimary());
        assertEquals(Boolean.FALSE, response.getVerified());
        assertNull(response.getVerifiedAt());

        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(updatedBy, response.getUpdatedBy());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertTrue(
                customerEmailRepository.existsById(
                        response.getCustomerEmailId()
                )
        );
    }

    @Test
    void createEmailShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest request =
                buildEmailRequest(uniqueEmailAddress());

        request.setPrimary(null);
        request.setVerified(null);

        CustomerEmailResponse response =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(Boolean.FALSE, response.getPrimary());
        assertEquals(Boolean.FALSE, response.getVerified());
        assertNull(response.getVerifiedAt());
    }

    @Test
    void createEmailShouldSetVerifiedAtWhenVerifiedIsTrue() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest request =
                buildEmailRequest(uniqueEmailAddress());

        request.setVerified(Boolean.TRUE);

        CustomerEmailResponse response =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(Boolean.TRUE, response.getVerified());
        assertNotNull(response.getVerifiedAt());
    }

    @Test
    void createEmailShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.createEmail(
                        UUID.randomUUID(),
                        buildEmailRequest(uniqueEmailAddress())
                )
        );
    }

    @Test
    void getEmailByIdShouldReturnExistingEmail() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        CustomerEmailResponse found =
                customerEmailService.getEmailById(
                        created.getCustomerEmailId()
                );

        assertEquals(
                created.getCustomerEmailId(),
                found.getCustomerEmailId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getEmailAddress(),
                found.getEmailAddress()
        );
    }

    @Test
    void getEmailByIdShouldThrowWhenEmailDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.getEmailById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getEmailsByCustomerIdShouldReturnCustomerEmails() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse first =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        CustomerEmailRequest secondRequest =
                buildEmailRequest(uniqueEmailAddress());

        secondRequest.setEmailType("WORK");

        CustomerEmailResponse second =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerEmailResponse> emails =
                customerEmailService.getEmailsByCustomerId(
                        customer.getCustomerId()
                );

        assertEquals(2, emails.size());

        assertTrue(
                emails.stream().anyMatch(email ->
                        first.getCustomerEmailId()
                                .equals(email.getCustomerEmailId())
                )
        );

        assertTrue(
                emails.stream().anyMatch(email ->
                        second.getCustomerEmailId()
                                .equals(email.getCustomerEmailId())
                )
        );
    }

    @Test
    void getEmailsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.getEmailsByCustomerId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void updateEmailShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        CustomerEmailRequest updateRequest =
                buildEmailRequest(uniqueEmailAddress());

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setEmailType("WORK");
        updateRequest.setPrimary(Boolean.TRUE);
        updateRequest.setVerified(Boolean.FALSE);
        updateRequest.setUpdatedBy(updatedBy);

        CustomerEmailResponse updated =
                customerEmailService.updateEmail(
                        created.getCustomerEmailId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerEmailId(),
                updated.getCustomerEmailId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals("WORK", updated.getEmailType());

        assertEquals(
                updateRequest.getEmailAddress(),
                updated.getEmailAddress()
        );

        assertEquals(Boolean.TRUE, updated.getPrimary());
        assertEquals(Boolean.FALSE, updated.getVerified());

        assertEquals(updatedBy, updated.getUpdatedBy());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateEmailShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest createRequest =
                buildEmailRequest(uniqueEmailAddress());

        createRequest.setPrimary(Boolean.TRUE);
        createRequest.setVerified(Boolean.TRUE);

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(Boolean.TRUE, created.getPrimary());
        assertEquals(Boolean.TRUE, created.getVerified());
        assertNotNull(created.getVerifiedAt());

        CustomerEmailRequest updateRequest =
                buildEmailRequest(created.getEmailAddress());

        updateRequest.setPrimary(null);
        updateRequest.setVerified(null);

        CustomerEmailResponse updated =
                customerEmailService.updateEmail(
                        created.getCustomerEmailId(),
                        updateRequest
                );

        assertEquals(Boolean.FALSE, updated.getPrimary());
        assertEquals(Boolean.FALSE, updated.getVerified());
        assertNull(updated.getVerifiedAt());
    }

    @Test
    void updateEmailShouldSetVerifiedAtWhenChangingFromFalseToTrue() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest createRequest =
                buildEmailRequest(uniqueEmailAddress());

        createRequest.setVerified(Boolean.FALSE);

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNull(created.getVerifiedAt());

        CustomerEmailRequest updateRequest =
                buildEmailRequest(created.getEmailAddress());

        updateRequest.setVerified(Boolean.TRUE);

        CustomerEmailResponse updated =
                customerEmailService.updateEmail(
                        created.getCustomerEmailId(),
                        updateRequest
                );

        assertEquals(Boolean.TRUE, updated.getVerified());
        assertNotNull(updated.getVerifiedAt());
    }

    @Test
    void updateEmailShouldPreserveVerifiedAtWhenRemainingVerified() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest createRequest =
                buildEmailRequest(uniqueEmailAddress());

        createRequest.setVerified(Boolean.TRUE);

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNotNull(created.getVerifiedAt());

        CustomerEmailRequest updateRequest =
                buildEmailRequest(created.getEmailAddress());

        updateRequest.setVerified(Boolean.TRUE);
        updateRequest.setEmailType("WORK");

        CustomerEmailResponse updated =
                customerEmailService.updateEmail(
                        created.getCustomerEmailId(),
                        updateRequest
                );

        assertEquals(Boolean.TRUE, updated.getVerified());

        assertEquals(
                created.getVerifiedAt(),
                updated.getVerifiedAt()
        );
    }

    @Test
    void updateEmailShouldClearVerifiedAtWhenChangingToFalse() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest createRequest =
                buildEmailRequest(uniqueEmailAddress());

        createRequest.setVerified(Boolean.TRUE);

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNotNull(created.getVerifiedAt());

        CustomerEmailRequest updateRequest =
                buildEmailRequest(created.getEmailAddress());

        updateRequest.setVerified(Boolean.FALSE);

        CustomerEmailResponse updated =
                customerEmailService.updateEmail(
                        created.getCustomerEmailId(),
                        updateRequest
                );

        assertEquals(Boolean.FALSE, updated.getVerified());
        assertNull(updated.getVerifiedAt());
    }

    @Test
    void updateEmailShouldThrowWhenEmailDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.updateEmail(
                        UUID.randomUUID(),
                        buildEmailRequest(uniqueEmailAddress())
                )
        );
    }

    @Test
    void deleteEmailShouldSoftDeleteExistingEmail() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        UUID deletedBy = UUID.randomUUID();

        customerEmailService.deleteEmail(
                created.getCustomerEmailId(),
                deletedBy
        );

        CustomerEmail persisted =
                customerEmailRepository.findById(
                        created.getCustomerEmailId()
                ).orElseThrow();

        assertNotNull(persisted.getDeletedAt());

        assertEquals(
                deletedBy,
                persisted.getDeletedBy()
        );

        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerEmailRepository.existsById(
                        created.getCustomerEmailId()
                )
        );
    }

    @Test
    void getEmailByIdShouldNotReturnSoftDeletedEmail() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        customerEmailService.deleteEmail(
                created.getCustomerEmailId(),
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.getEmailById(
                        created.getCustomerEmailId()
                )
        );
    }

    @Test
    void getEmailsByCustomerIdShouldExcludeSoftDeletedEmails() {

        CustomerResponse customer = createCustomer();

        CustomerEmailResponse activeEmail =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        CustomerEmailResponse deletedEmail =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(uniqueEmailAddress())
                );

        customerEmailService.deleteEmail(
                deletedEmail.getCustomerEmailId(),
                UUID.randomUUID()
        );

        List<CustomerEmailResponse> emails =
                customerEmailService.getEmailsByCustomerId(
                        customer.getCustomerId()
                );

        assertEquals(1, emails.size());

        assertEquals(
                activeEmail.getCustomerEmailId(),
                emails.getFirst().getCustomerEmailId()
        );

        assertFalse(
                emails.stream().anyMatch(email ->
                        deletedEmail.getCustomerEmailId()
                                .equals(email.getCustomerEmailId())
                )
        );
    }

    @Test
    void repositoryShouldReturnOnlyPrimaryNonDeletedEmails() {

        CustomerResponse customer = createCustomer();

        CustomerEmailRequest primaryRequest =
                buildEmailRequest(uniqueEmailAddress());

        primaryRequest.setPrimary(Boolean.TRUE);

        CustomerEmailResponse primary =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        primaryRequest
                );

        CustomerEmailRequest secondaryRequest =
                buildEmailRequest(uniqueEmailAddress());

        secondaryRequest.setPrimary(Boolean.FALSE);

        customerEmailService.createEmail(
                customer.getCustomerId(),
                secondaryRequest
        );

        List<CustomerEmail> primaryEmails =
                customerEmailRepository
                        .findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertEquals(1, primaryEmails.size());

        assertEquals(
                primary.getCustomerEmailId(),
                primaryEmails.getFirst().getCustomerEmailId()
        );
    }

    @Test
    void repositoryShouldFindEmailByCustomerAndEmailAddress() {

        CustomerResponse customer = createCustomer();

        String emailAddress = uniqueEmailAddress();

        CustomerEmailResponse created =
                customerEmailService.createEmail(
                        customer.getCustomerId(),
                        buildEmailRequest(emailAddress)
                );

        CustomerEmail found =
                customerEmailRepository
                        .findByCustomerIdAndEmailAddressAndDeletedAtIsNull(
                                customer.getCustomerId(),
                                emailAddress
                        )
                        .orElseThrow();

        assertEquals(
                created.getCustomerEmailId(),
                found.getCustomerEmailId()
        );
    }

    @Test
    void deleteEmailShouldThrowWhenEmailDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerEmailService.deleteEmail(
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

    private CustomerEmailRequest buildEmailRequest(
            String emailAddress) {

        CustomerEmailRequest request =
                new CustomerEmailRequest();

        request.setEmailType("PERSONAL");
        request.setEmailAddress(emailAddress);
        request.setPrimary(Boolean.FALSE);
        request.setVerified(Boolean.FALSE);

        return request;
    }

    private String uniqueEmailAddress() {

        return "test-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 12)
                + "@efs.test";
    }
}