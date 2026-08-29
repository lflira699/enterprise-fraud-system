package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerHistoryRequest;
import com.efs.modules.customer.dto.CustomerHistoryResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerHistory;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
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
class CustomerHistoryServiceIntegrationTest {

    @Autowired
    private CustomerHistoryServiceInterface customerHistoryService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerHistoryRepository customerHistoryRepository;

    @Test
    void createHistoryShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryRequest request =
                buildHistoryRequest(
                        LocalDateTime.of(
                                2026, 8, 29, 8, 30, 0
                        )
                );

        UUID createdBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);

        CustomerHistoryResponse response =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerHistoryId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                "RISK_PROFILE_CHANGED",
                response.getEventType()
        );

        assertEquals(
                "Integration test customer history event",
                response.getEventDescription()
        );

        assertEquals(
                "ACTIVE",
                response.getPreviousStatus()
        );

        assertEquals(
                "REVIEW",
                response.getNewStatus()
        );

        assertEquals(
                "LOW",
                response.getPreviousRiskLevel()
        );

        assertEquals(
                "HIGH",
                response.getNewRiskLevel()
        );

        assertBigDecimalEquals(
                "20.00",
                response.getPreviousRiskScore()
        );

        assertBigDecimalEquals(
                "85.50",
                response.getNewRiskScore()
        );

        assertEquals(
                LocalDateTime.of(
                        2026, 8, 29, 8, 30, 0
                ),
                response.getEventTimestamp()
        );

        assertEquals(
                "SOURCE-REF-001",
                response.getSourceReference()
        );

        assertEquals(
                createdBy,
                response.getCreatedBy()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                customerHistoryRepository.existsById(
                        response.getCustomerHistoryId()
                )
        );
    }

    @Test
    void createHistoryShouldGenerateEventTimestampWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryRequest request =
                buildHistoryRequest(null);

        CustomerHistoryResponse response =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(
                response.getEventTimestamp()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createHistoryShouldAllowOptionalFieldsToBeNull() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryRequest request =
                new CustomerHistoryRequest();

        request.setEventType("CUSTOMER_EVENT");

        CustomerHistoryResponse response =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(
                response.getCustomerHistoryId()
        );

        assertEquals(
                "CUSTOMER_EVENT",
                response.getEventType()
        );

        assertNull(
                response.getEventDescription()
        );

        assertNull(
                response.getPreviousStatus()
        );

        assertNull(
                response.getNewStatus()
        );

        assertNull(
                response.getPreviousRiskLevel()
        );

        assertNull(
                response.getNewRiskLevel()
        );

        assertNull(
                response.getPreviousRiskScore()
        );

        assertNull(
                response.getNewRiskScore()
        );

        assertNull(
                response.getSourceReference()
        );

        assertNotNull(
                response.getEventTimestamp()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void createHistoryShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerHistoryService.createHistory(
                        UUID.randomUUID(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 29, 8, 30, 0
                                )
                        )
                )
        );
    }

    @Test
    void getHistoryByIdShouldReturnExistingHistory() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryResponse created =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 29, 8, 30, 0
                                )
                        )
                );

        CustomerHistoryResponse found =
                customerHistoryService.getHistoryById(
                        created.getCustomerHistoryId()
                );

        assertEquals(
                created.getCustomerHistoryId(),
                found.getCustomerHistoryId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getEventType(),
                found.getEventType()
        );

        assertEquals(
                created.getEventTimestamp(),
                found.getEventTimestamp()
        );
    }

    @Test
    void getHistoryByIdShouldThrowWhenHistoryDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerHistoryService.getHistoryById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getHistoryByCustomerIdShouldReturnHistoryOrderedByEventTimestampDescending() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryResponse oldest =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 27, 10, 0, 0
                                )
                        )
                );

        CustomerHistoryResponse newest =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 29, 10, 0, 0
                                )
                        )
                );

        CustomerHistoryResponse middle =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 28, 10, 0, 0
                                )
                        )
                );

        List<CustomerHistoryResponse> history =
                customerHistoryService.getHistoryByCustomerId(
                        customer.getCustomerId()
                );

        assertEquals(
                3,
                history.size()
        );

        assertEquals(
                newest.getCustomerHistoryId(),
                history.get(0).getCustomerHistoryId()
        );

        assertEquals(
                middle.getCustomerHistoryId(),
                history.get(1).getCustomerHistoryId()
        );

        assertEquals(
                oldest.getCustomerHistoryId(),
                history.get(2).getCustomerHistoryId()
        );
    }

    @Test
    void getHistoryByCustomerIdShouldReturnEmptyListWhenNoHistoryExists() {

        CustomerResponse customer = createCustomer();

        List<CustomerHistoryResponse> history =
                customerHistoryService.getHistoryByCustomerId(
                        customer.getCustomerId()
                );

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistoryByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerHistoryService.getHistoryByCustomerId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void repositoryShouldFindHistoryByEventTypeOrderedDescending() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryRequest oldestRequest =
                buildHistoryRequest(
                        LocalDateTime.of(
                                2026, 8, 27, 9, 0, 0
                        )
                );

        oldestRequest.setEventType(
                "STATUS_CHANGED"
        );

        CustomerHistoryResponse oldest =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        oldestRequest
                );

        CustomerHistoryRequest newestRequest =
                buildHistoryRequest(
                        LocalDateTime.of(
                                2026, 8, 29, 9, 0, 0
                        )
                );

        newestRequest.setEventType(
                "STATUS_CHANGED"
        );

        CustomerHistoryResponse newest =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        newestRequest
                );

        CustomerHistoryRequest otherRequest =
                buildHistoryRequest(
                        LocalDateTime.of(
                                2026, 8, 30, 9, 0, 0
                        )
                );

        otherRequest.setEventType(
                "RISK_CHANGED"
        );

        customerHistoryService.createHistory(
                customer.getCustomerId(),
                otherRequest
        );

        List<CustomerHistory> statusChanges =
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customer.getCustomerId(),
                                "STATUS_CHANGED"
                        );

        assertEquals(
                2,
                statusChanges.size()
        );

        assertEquals(
                newest.getCustomerHistoryId(),
                statusChanges.get(0)
                        .getCustomerHistoryId()
        );

        assertEquals(
                oldest.getCustomerHistoryId(),
                statusChanges.get(1)
                        .getCustomerHistoryId()
        );
    }

    @Test
    void repositoryShouldFindHistoryByCustomerHistoryId() {

        CustomerResponse customer = createCustomer();

        CustomerHistoryResponse created =
                customerHistoryService.createHistory(
                        customer.getCustomerId(),
                        buildHistoryRequest(
                                LocalDateTime.of(
                                        2026, 8, 29, 8, 30, 0
                                )
                        )
                );

        CustomerHistory history =
                customerHistoryRepository
                        .findByCustomerHistoryId(
                                created.getCustomerHistoryId()
                        )
                        .orElseThrow();

        assertEquals(
                created.getCustomerHistoryId(),
                history.getCustomerHistoryId()
        );

        assertEquals(
                customer.getCustomerId(),
                history.getCustomerId()
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

    private CustomerHistoryRequest buildHistoryRequest(
            LocalDateTime eventTimestamp) {

        CustomerHistoryRequest request =
                new CustomerHistoryRequest();

        request.setEventType(
                "RISK_PROFILE_CHANGED"
        );

        request.setEventDescription(
                "Integration test customer history event"
        );

        request.setPreviousStatus(
                "ACTIVE"
        );

        request.setNewStatus(
                "REVIEW"
        );

        request.setPreviousRiskLevel(
                "LOW"
        );

        request.setNewRiskLevel(
                "HIGH"
        );

        request.setPreviousRiskScore(
                new BigDecimal("20.00")
        );

        request.setNewRiskScore(
                new BigDecimal("85.50")
        );

        request.setEventTimestamp(
                eventTimestamp
        );

        request.setSourceReference(
                "SOURCE-REF-001"
        );

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