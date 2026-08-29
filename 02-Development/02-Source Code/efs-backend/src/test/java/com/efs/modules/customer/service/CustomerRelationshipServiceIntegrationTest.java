package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRelationshipRequest;
import com.efs.modules.customer.dto.CustomerRelationshipResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerRelationship;
import com.efs.modules.customer.repository.CustomerRelationshipRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
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
class CustomerRelationshipServiceIntegrationTest {

    @Autowired
    private CustomerRelationshipServiceInterface customerRelationshipService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerRelationshipRepository customerRelationshipRepository;

    @Test
    void createRelationshipShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipRequest request =
                buildRelationshipRequest(
                        relatedCustomer.getCustomerId()
                );

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerRelationshipResponse response =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerRelationshipId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                relatedCustomer.getCustomerId(),
                response.getRelatedCustomerId()
        );

        assertEquals(
                "FAMILY_MEMBER",
                response.getRelationshipType()
        );

        assertEquals(
                "ACTIVE",
                response.getRelationshipStatus()
        );

        assertEquals(
                "Integration test relationship",
                response.getRelationshipDescription()
        );

        assertEquals(
                LocalDate.of(2026, 1, 1),
                response.getEffectiveDate()
        );

        assertEquals(
                LocalDate.of(2030, 12, 31),
                response.getExpirationDate()
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
        assertNull(response.getDeletedBy());

        assertTrue(
                customerRelationshipRepository.existsById(
                        response.getCustomerRelationshipId()
                )
        );
    }

    @Test
    void createRelationshipShouldAllowNullRelatedCustomer() {

        CustomerResponse customer = createCustomer();

        CustomerRelationshipRequest request =
                buildRelationshipRequest(null);

        CustomerRelationshipResponse response =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response.getCustomerRelationshipId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertNull(response.getRelatedCustomerId());
    }

    @Test
    void createRelationshipShouldThrowWhenCustomerDoesNotExist() {

        CustomerResponse relatedCustomer = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.createRelationship(
                        UUID.randomUUID(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                )
        );
    }

    @Test
    void createRelationshipShouldRejectSelfRelationship() {

        CustomerResponse customer = createCustomer();

        CustomerRelationshipRequest request =
                buildRelationshipRequest(
                        customer.getCustomerId()
                );

        assertThrows(
                ValidationException.class,
                () -> customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        request
                )
        );
    }

    @Test
    void createRelationshipShouldThrowWhenRelatedCustomerDoesNotExist() {

        CustomerResponse customer = createCustomer();

        CustomerRelationshipRequest request =
                buildRelationshipRequest(
                        UUID.randomUUID()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        request
                )
        );
    }

    @Test
    void createRelationshipShouldRejectExpirationBeforeEffectiveDate() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipRequest request =
                buildRelationshipRequest(
                        relatedCustomer.getCustomerId()
                );

        request.setEffectiveDate(
                LocalDate.of(2026, 8, 29)
        );

        request.setExpirationDate(
                LocalDate.of(2026, 8, 28)
        );

        assertThrows(
                ValidationException.class,
                () -> customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        request
                )
        );
    }

    @Test
    void getRelationshipByIdShouldReturnExistingRelationship() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        CustomerRelationshipResponse found =
                customerRelationshipService.getRelationshipById(
                        created.getCustomerRelationshipId()
                );

        assertEquals(
                created.getCustomerRelationshipId(),
                found.getCustomerRelationshipId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                relatedCustomer.getCustomerId(),
                found.getRelatedCustomerId()
        );
    }

    @Test
    void getRelationshipByIdShouldThrowWhenRelationshipDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.getRelationshipById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getRelationshipsByCustomerIdShouldReturnCustomerRelationships() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomerOne = createCustomer();
        CustomerResponse relatedCustomerTwo = createCustomer();

        CustomerRelationshipResponse first =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomerOne.getCustomerId()
                        )
                );

        CustomerRelationshipRequest secondRequest =
                buildRelationshipRequest(
                        relatedCustomerTwo.getCustomerId()
                );

        secondRequest.setRelationshipType("BUSINESS_PARTNER");

        CustomerRelationshipResponse second =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerRelationshipResponse> relationships =
                customerRelationshipService
                        .getRelationshipsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, relationships.size());

        assertTrue(
                relationships.stream()
                        .anyMatch(relationship ->
                                first.getCustomerRelationshipId()
                                        .equals(
                                                relationship
                                                        .getCustomerRelationshipId()
                                        )
                        )
        );

        assertTrue(
                relationships.stream()
                        .anyMatch(relationship ->
                                second.getCustomerRelationshipId()
                                        .equals(
                                                relationship
                                                        .getCustomerRelationshipId()
                                        )
                        )
        );
    }

    @Test
    void getRelationshipsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService
                        .getRelationshipsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updateRelationshipShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();
        CustomerResponse originalRelatedCustomer = createCustomer();
        CustomerResponse updatedRelatedCustomer = createCustomer();

        CustomerRelationshipRequest createRequest =
                buildRelationshipRequest(
                        originalRelatedCustomer.getCustomerId()
                );

        UUID createdBy = UUID.randomUUID();

        createRequest.setCreatedBy(createdBy);

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        createRequest
                );

        CustomerRelationshipRequest updateRequest =
                buildRelationshipRequest(
                        updatedRelatedCustomer.getCustomerId()
                );

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setRelationshipType(
                "BUSINESS_PARTNER"
        );

        updateRequest.setRelationshipStatus(
                "INACTIVE"
        );

        updateRequest.setRelationshipDescription(
                "Updated integration test relationship"
        );

        updateRequest.setEffectiveDate(
                LocalDate.of(2027, 1, 1)
        );

        updateRequest.setExpirationDate(
                LocalDate.of(2032, 12, 31)
        );

        updateRequest.setUpdatedBy(updatedBy);

        CustomerRelationshipResponse updated =
                customerRelationshipService.updateRelationship(
                        created.getCustomerRelationshipId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerRelationshipId(),
                updated.getCustomerRelationshipId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                updatedRelatedCustomer.getCustomerId(),
                updated.getRelatedCustomerId()
        );

        assertEquals(
                "BUSINESS_PARTNER",
                updated.getRelationshipType()
        );

        assertEquals(
                "INACTIVE",
                updated.getRelationshipStatus()
        );

        assertEquals(
                "Updated integration test relationship",
                updated.getRelationshipDescription()
        );

        assertEquals(
                LocalDate.of(2027, 1, 1),
                updated.getEffectiveDate()
        );

        assertEquals(
                LocalDate.of(2032, 12, 31),
                updated.getExpirationDate()
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
    void updateRelationshipShouldRejectSelfRelationship() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        CustomerRelationshipRequest updateRequest =
                buildRelationshipRequest(
                        customer.getCustomerId()
                );

        assertThrows(
                ValidationException.class,
                () -> customerRelationshipService.updateRelationship(
                        created.getCustomerRelationshipId(),
                        updateRequest
                )
        );
    }

    @Test
    void updateRelationshipShouldThrowWhenRelatedCustomerDoesNotExist() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        CustomerRelationshipRequest updateRequest =
                buildRelationshipRequest(
                        UUID.randomUUID()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.updateRelationship(
                        created.getCustomerRelationshipId(),
                        updateRequest
                )
        );
    }

    @Test
    void updateRelationshipShouldRejectExpirationBeforeEffectiveDate() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        CustomerRelationshipRequest updateRequest =
                buildRelationshipRequest(
                        relatedCustomer.getCustomerId()
                );

        updateRequest.setEffectiveDate(
                LocalDate.of(2028, 1, 2)
        );

        updateRequest.setExpirationDate(
                LocalDate.of(2028, 1, 1)
        );

        assertThrows(
                ValidationException.class,
                () -> customerRelationshipService.updateRelationship(
                        created.getCustomerRelationshipId(),
                        updateRequest
                )
        );
    }

    @Test
    void updateRelationshipShouldThrowWhenRelationshipDoesNotExist() {

        CustomerResponse relatedCustomer = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.updateRelationship(
                        UUID.randomUUID(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                )
        );
    }

    @Test
    void deleteRelationshipShouldSoftDeleteExistingRelationship() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        UUID deletedBy = UUID.randomUUID();

        customerRelationshipService.deleteRelationship(
                created.getCustomerRelationshipId(),
                deletedBy
        );

        CustomerRelationship persisted =
                customerRelationshipRepository.findById(
                        created.getCustomerRelationshipId()
                ).orElseThrow();

        assertNotNull(persisted.getDeletedAt());

        assertEquals(
                deletedBy,
                persisted.getDeletedBy()
        );

        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerRelationshipRepository.existsById(
                        created.getCustomerRelationshipId()
                )
        );
    }

    @Test
    void getRelationshipByIdShouldNotReturnSoftDeletedRelationship() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        customerRelationshipService.deleteRelationship(
                created.getCustomerRelationshipId(),
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.getRelationshipById(
                        created.getCustomerRelationshipId()
                )
        );
    }

    @Test
    void getRelationshipsByCustomerIdShouldExcludeSoftDeletedRelationships() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomerOne = createCustomer();
        CustomerResponse relatedCustomerTwo = createCustomer();

        CustomerRelationshipResponse activeRelationship =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomerOne.getCustomerId()
                        )
                );

        CustomerRelationshipResponse deletedRelationship =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomerTwo.getCustomerId()
                        )
                );

        customerRelationshipService.deleteRelationship(
                deletedRelationship.getCustomerRelationshipId(),
                UUID.randomUUID()
        );

        List<CustomerRelationshipResponse> relationships =
                customerRelationshipService
                        .getRelationshipsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(1, relationships.size());

        assertEquals(
                activeRelationship.getCustomerRelationshipId(),
                relationships.getFirst()
                        .getCustomerRelationshipId()
        );

        assertFalse(
                relationships.stream()
                        .anyMatch(relationship ->
                                deletedRelationship
                                        .getCustomerRelationshipId()
                                        .equals(
                                                relationship
                                                        .getCustomerRelationshipId()
                                        )
                        )
        );
    }

    @Test
    void repositoryShouldFindRelationshipsByRelatedCustomerId() {

        CustomerResponse customer = createCustomer();
        CustomerResponse relatedCustomer = createCustomer();

        CustomerRelationshipResponse created =
                customerRelationshipService.createRelationship(
                        customer.getCustomerId(),
                        buildRelationshipRequest(
                                relatedCustomer.getCustomerId()
                        )
                );

        List<CustomerRelationship> relationships =
                customerRelationshipRepository
                        .findByRelatedCustomerIdAndDeletedAtIsNull(
                                relatedCustomer.getCustomerId()
                        );

        assertEquals(1, relationships.size());

        assertEquals(
                created.getCustomerRelationshipId(),
                relationships.getFirst()
                        .getCustomerRelationshipId()
        );

        assertEquals(
                customer.getCustomerId(),
                relationships.getFirst().getCustomerId()
        );
    }

    @Test
    void deleteRelationshipShouldThrowWhenRelationshipDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerRelationshipService.deleteRelationship(
                        UUID.randomUUID(),
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

    private CustomerRelationshipRequest buildRelationshipRequest(
            UUID relatedCustomerId) {

        CustomerRelationshipRequest request =
                new CustomerRelationshipRequest();

        request.setRelatedCustomerId(
                relatedCustomerId
        );

        request.setRelationshipType(
                "FAMILY_MEMBER"
        );

        request.setRelationshipStatus(
                "ACTIVE"
        );

        request.setRelationshipDescription(
                "Integration test relationship"
        );

        request.setEffectiveDate(
                LocalDate.of(2026, 1, 1)
        );

        request.setExpirationDate(
                LocalDate.of(2030, 12, 31)
        );

        return request;
    }
}