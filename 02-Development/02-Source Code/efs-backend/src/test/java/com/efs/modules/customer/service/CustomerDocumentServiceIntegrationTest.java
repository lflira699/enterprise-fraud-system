package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDocumentRequest;
import com.efs.modules.customer.dto.CustomerDocumentResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.shared.exception.DuplicateRecordException;
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
class CustomerDocumentServiceIntegrationTest {

    @Autowired
    private CustomerDocumentServiceInterface customerDocumentService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Test
    void createDocumentShouldPersistDocument() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest request =
                buildDocumentRequest(uniqueDocumentNumber());

        CustomerDocumentResponse response =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getDocumentId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                request.getDocumentType(),
                response.getDocumentType()
        );

        assertEquals(
                request.getDocumentNumber(),
                response.getDocumentNumber()
        );

        assertEquals(
                request.getIssuingCountry(),
                response.getIssuingCountry()
        );

        assertEquals(
                request.getIssueDate(),
                response.getIssueDate()
        );

        assertEquals(
                request.getExpirationDate(),
                response.getExpirationDate()
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void createDocumentShouldSetVerifiedAtWhenVerificationDataIsProvided() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest request =
                buildDocumentRequest(uniqueDocumentNumber());

        UUID verifiedBy = UUID.randomUUID();

        request.setVerificationStatus("VERIFIED");
        request.setVerifiedBy(verifiedBy);

        CustomerDocumentResponse response =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                "VERIFIED",
                response.getVerificationStatus()
        );

        assertEquals(
                verifiedBy,
                response.getVerifiedBy()
        );

        assertNotNull(response.getVerifiedAt());
    }

    @Test
    void createDocumentShouldNotSetVerifiedAtWithoutVerifiedBy() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest request =
                buildDocumentRequest(uniqueDocumentNumber());

        request.setVerificationStatus("PENDING");
        request.setVerifiedBy(null);

        CustomerDocumentResponse response =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                "PENDING",
                response.getVerificationStatus()
        );

        assertNull(response.getVerifiedAt());
    }

    @Test
    void createDocumentShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        CustomerDocumentRequest request =
                buildDocumentRequest(uniqueDocumentNumber());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService.createDocument(
                        missingCustomerId,
                        request
                )
        );
    }

    @Test
    void createDocumentShouldRejectDuplicateDocumentForSameCustomer() {

        CustomerResponse customer = createCustomer();

        String documentNumber = uniqueDocumentNumber();

        CustomerDocumentRequest firstRequest =
                buildDocumentRequest(documentNumber);

        CustomerDocumentRequest duplicateRequest =
                buildDocumentRequest(documentNumber);

        customerDocumentService.createDocument(
                customer.getCustomerId(),
                firstRequest
        );

        assertThrows(
                DuplicateRecordException.class,
                () -> customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        duplicateRequest
                )
        );
    }

    @Test
    void sameDocumentIdentityShouldBeAllowedForDifferentCustomers() {

        CustomerResponse firstCustomer = createCustomer();
        CustomerResponse secondCustomer = createCustomer();

        String documentNumber = uniqueDocumentNumber();

        CustomerDocumentRequest firstRequest =
                buildDocumentRequest(documentNumber);

        CustomerDocumentRequest secondRequest =
                buildDocumentRequest(documentNumber);

        CustomerDocumentResponse first =
                customerDocumentService.createDocument(
                        firstCustomer.getCustomerId(),
                        firstRequest
                );

        CustomerDocumentResponse second =
                customerDocumentService.createDocument(
                        secondCustomer.getCustomerId(),
                        secondRequest
                );

        assertNotNull(first.getDocumentId());
        assertNotNull(second.getDocumentId());

        assertNotEquals(
                first.getCustomerId(),
                second.getCustomerId()
        );
    }

    @Test
    void getDocumentByIdShouldReturnExistingDocument() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentResponse created =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        buildDocumentRequest(
                                uniqueDocumentNumber()
                        )
                );

        CustomerDocumentResponse found =
                customerDocumentService.getDocumentById(
                        created.getDocumentId()
                );

        assertEquals(
                created.getDocumentId(),
                found.getDocumentId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );
    }

    @Test
    void getDocumentByIdShouldThrowWhenDocumentDoesNotExist() {

        UUID missingDocumentId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService.getDocumentById(
                        missingDocumentId
                )
        );
    }

    @Test
    void getDocumentsByCustomerIdShouldReturnCustomerDocuments() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentResponse first =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        buildDocumentRequest(
                                uniqueDocumentNumber()
                        )
                );

        CustomerDocumentRequest secondRequest =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        secondRequest.setDocumentType("PASSPORT");

        CustomerDocumentResponse second =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerDocumentResponse> documents =
                customerDocumentService
                        .getDocumentsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, documents.size());

        assertTrue(
                documents.stream()
                        .anyMatch(document ->
                                first.getDocumentId()
                                        .equals(
                                                document.getDocumentId()
                                        )
                        )
        );

        assertTrue(
                documents.stream()
                        .anyMatch(document ->
                                second.getDocumentId()
                                        .equals(
                                                document.getDocumentId()
                                        )
                        )
        );
    }

    @Test
    void getDocumentsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService
                        .getDocumentsByCustomerId(
                                missingCustomerId
                        )
        );
    }

    @Test
    void updateDocumentShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentResponse created =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        buildDocumentRequest(
                                uniqueDocumentNumber()
                        )
                );

        CustomerDocumentRequest updateRequest =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        updateRequest.setDocumentType("PASSPORT");
        updateRequest.setIssuingCountry("USA");

        CustomerDocumentResponse updated =
                customerDocumentService.updateDocument(
                        created.getDocumentId(),
                        updateRequest
                );

        assertEquals(
                created.getDocumentId(),
                updated.getDocumentId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                "PASSPORT",
                updated.getDocumentType()
        );

        assertEquals(
                updateRequest.getDocumentNumber(),
                updated.getDocumentNumber()
        );

        assertEquals(
                "USA",
                updated.getIssuingCountry()
        );

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateDocumentShouldSetVerifiedAtWhenVerificationIsAdded() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest createRequest =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        createRequest.setVerificationStatus(null);
        createRequest.setVerifiedBy(null);

        CustomerDocumentResponse created =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        createRequest
                );

        assertNull(created.getVerifiedAt());

        CustomerDocumentRequest updateRequest =
                buildDocumentRequest(
                        created.getDocumentNumber()
                );

        UUID verifiedBy = UUID.randomUUID();

        updateRequest.setVerificationStatus("VERIFIED");
        updateRequest.setVerifiedBy(verifiedBy);

        CustomerDocumentResponse updated =
                customerDocumentService.updateDocument(
                        created.getDocumentId(),
                        updateRequest
                );

        assertEquals(
                "VERIFIED",
                updated.getVerificationStatus()
        );

        assertEquals(
                verifiedBy,
                updated.getVerifiedBy()
        );

        assertNotNull(updated.getVerifiedAt());
    }

    @Test
    void updateDocumentShouldAllowKeepingSameDocumentIdentity() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest request =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        CustomerDocumentResponse created =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        request
                );

        request.setIssuingCountry("USA");

        CustomerDocumentResponse updated =
                customerDocumentService.updateDocument(
                        created.getDocumentId(),
                        request
                );

        assertEquals(
                created.getDocumentId(),
                updated.getDocumentId()
        );

        assertEquals(
                created.getDocumentNumber(),
                updated.getDocumentNumber()
        );

        assertEquals(
                "USA",
                updated.getIssuingCountry()
        );
    }

    @Test
    void updateDocumentShouldRejectDuplicateDocumentIdentity() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentRequest firstRequest =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        CustomerDocumentRequest secondRequest =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        CustomerDocumentResponse first =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        firstRequest
                );

        CustomerDocumentResponse second =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        secondRequest
                );

        CustomerDocumentRequest duplicateUpdate =
                buildDocumentRequest(
                        second.getDocumentNumber()
                );

        duplicateUpdate.setDocumentType(
                second.getDocumentType()
        );

        assertThrows(
                DuplicateRecordException.class,
                () -> customerDocumentService.updateDocument(
                        first.getDocumentId(),
                        duplicateUpdate
                )
        );
    }

    @Test
    void updateDocumentShouldThrowWhenDocumentDoesNotExist() {

        UUID missingDocumentId = UUID.randomUUID();

        CustomerDocumentRequest request =
                buildDocumentRequest(
                        uniqueDocumentNumber()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService.updateDocument(
                        missingDocumentId,
                        request
                )
        );
    }

    @Test
    void deleteDocumentShouldRemoveExistingDocument() {

        CustomerResponse customer = createCustomer();

        CustomerDocumentResponse created =
                customerDocumentService.createDocument(
                        customer.getCustomerId(),
                        buildDocumentRequest(
                                uniqueDocumentNumber()
                        )
                );

        customerDocumentService.deleteDocument(
                created.getDocumentId()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService.getDocumentById(
                        created.getDocumentId()
                )
        );
    }

    @Test
    void deleteDocumentShouldThrowWhenDocumentDoesNotExist() {

        UUID missingDocumentId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDocumentService.deleteDocument(
                        missingDocumentId
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

    private CustomerDocumentRequest buildDocumentRequest(
            String documentNumber) {

        CustomerDocumentRequest request =
                new CustomerDocumentRequest();

        request.setDocumentType("NATIONAL_ID");
        request.setDocumentNumber(documentNumber);
        request.setIssuingCountry("GTM");

        request.setIssueDate(
                LocalDate.of(2020, 1, 15)
        );

        request.setExpirationDate(
                LocalDate.of(2030, 1, 15)
        );

        return request;
    }

    private String uniqueDocumentNumber() {

        return "DOC-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 12);
    }
}