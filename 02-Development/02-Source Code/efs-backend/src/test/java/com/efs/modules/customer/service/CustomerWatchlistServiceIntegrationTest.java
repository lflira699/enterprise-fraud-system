package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.dto.CustomerWatchlistRequest;
import com.efs.modules.customer.dto.CustomerWatchlistResponse;
import com.efs.modules.customer.entity.CustomerWatchlist;
import com.efs.modules.customer.repository.CustomerWatchlistRepository;
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
class CustomerWatchlistServiceIntegrationTest {

    @Autowired
    private CustomerWatchlistServiceInterface customerWatchlistService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerWatchlistRepository customerWatchlistRepository;

    @Test
    void createWatchlistShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistRequest request =
                buildWatchlistRequest();

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerWatchlistResponse response =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getWatchlistId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                "SANCTIONS",
                response.getWatchlistType()
        );

        assertEquals(
                "EFS_TEST_SOURCE",
                response.getWatchlistSource()
        );

        assertEquals(
                "POTENTIAL_MATCH",
                response.getMatchStatus()
        );

        assertBigDecimalEquals(
                "92.50",
                response.getMatchScore()
        );

        assertEquals(
                "Integration Test Customer",
                response.getMatchedName()
        );

        assertEquals(
                "REF-TEST-001",
                response.getReferenceId()
        );

        assertEquals(
                LocalDateTime.of(
                        2026, 8, 29, 8, 0, 0
                ),
                response.getDetectedAt()
        );

        assertEquals(
                LocalDateTime.of(
                        2026, 8, 29, 8, 15, 0
                ),
                response.getLastCheckedAt()
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
                customerWatchlistRepository.existsById(
                        response.getWatchlistId()
                )
        );
    }

    @Test
    void createWatchlistShouldDefaultDetectedAtAndActiveWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistRequest request =
                buildWatchlistRequest();

        request.setDetectedAt(null);
        request.setActive(null);

        CustomerWatchlistResponse response =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response.getDetectedAt());

        assertEquals(
                Boolean.TRUE,
                response.getActive()
        );
    }

    @Test
    void createWatchlistShouldPreserveFalseActiveValue() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistRequest request =
                buildWatchlistRequest();

        request.setActive(Boolean.FALSE);

        CustomerWatchlistResponse response =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getActive()
        );
    }

    @Test
    void createWatchlistShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService.createWatchlist(
                        UUID.randomUUID(),
                        buildWatchlistRequest()
                )
        );
    }

    @Test
    void getWatchlistByIdShouldReturnExistingWatchlist() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        CustomerWatchlistResponse found =
                customerWatchlistService.getWatchlistById(
                        created.getWatchlistId()
                );

        assertEquals(
                created.getWatchlistId(),
                found.getWatchlistId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getReferenceId(),
                found.getReferenceId()
        );
    }

    @Test
    void getWatchlistByIdShouldThrowWhenWatchlistDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService.getWatchlistById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getWatchlistsByCustomerIdShouldReturnCustomerWatchlists() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse first =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        CustomerWatchlistRequest secondRequest =
                buildWatchlistRequest();

        secondRequest.setWatchlistType("PEP");
        secondRequest.setWatchlistSource("SECOND_TEST_SOURCE");
        secondRequest.setReferenceId("REF-TEST-002");

        CustomerWatchlistResponse second =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerWatchlistResponse> watchlists =
                customerWatchlistService
                        .getWatchlistsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, watchlists.size());

        assertTrue(
                watchlists.stream()
                        .anyMatch(watchlist ->
                                first.getWatchlistId()
                                        .equals(
                                                watchlist.getWatchlistId()
                                        )
                        )
        );

        assertTrue(
                watchlists.stream()
                        .anyMatch(watchlist ->
                                second.getWatchlistId()
                                        .equals(
                                                watchlist.getWatchlistId()
                                        )
                        )
        );
    }

    @Test
    void getWatchlistsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService
                        .getWatchlistsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updateWatchlistShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistRequest createRequest =
                buildWatchlistRequest();

        UUID createdBy = UUID.randomUUID();
        createRequest.setCreatedBy(createdBy);

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        createRequest
                );

        CustomerWatchlistRequest updateRequest =
                new CustomerWatchlistRequest();

        UUID updatedBy = UUID.randomUUID();

        LocalDateTime detectedAt =
                LocalDateTime.of(
                        2026, 8, 28, 12, 0, 0
                );

        LocalDateTime lastCheckedAt =
                LocalDateTime.of(
                        2026, 8, 29, 8, 30, 0
                );

        updateRequest.setWatchlistType("PEP");

        updateRequest.setWatchlistSource(
                "UPDATED_TEST_SOURCE"
        );

        updateRequest.setMatchStatus(
                "CONFIRMED_MATCH"
        );

        updateRequest.setMatchScore(
                new BigDecimal("99.25")
        );

        updateRequest.setMatchedName(
                "Updated Test Customer"
        );

        updateRequest.setReferenceId(
                "REF-UPDATED-001"
        );

        updateRequest.setDetectedAt(detectedAt);
        updateRequest.setLastCheckedAt(lastCheckedAt);
        updateRequest.setActive(Boolean.FALSE);
        updateRequest.setUpdatedBy(updatedBy);

        CustomerWatchlistResponse updated =
                customerWatchlistService.updateWatchlist(
                        created.getWatchlistId(),
                        updateRequest
                );

        assertEquals(
                created.getWatchlistId(),
                updated.getWatchlistId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                "PEP",
                updated.getWatchlistType()
        );

        assertEquals(
                "UPDATED_TEST_SOURCE",
                updated.getWatchlistSource()
        );

        assertEquals(
                "CONFIRMED_MATCH",
                updated.getMatchStatus()
        );

        assertBigDecimalEquals(
                "99.25",
                updated.getMatchScore()
        );

        assertEquals(
                "Updated Test Customer",
                updated.getMatchedName()
        );

        assertEquals(
                "REF-UPDATED-001",
                updated.getReferenceId()
        );

        assertEquals(
                detectedAt,
                updated.getDetectedAt()
        );

        assertEquals(
                lastCheckedAt,
                updated.getLastCheckedAt()
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
    void updateWatchlistShouldDefaultDetectedAtAndActiveWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        CustomerWatchlistRequest updateRequest =
                buildWatchlistRequest();

        updateRequest.setDetectedAt(null);
        updateRequest.setActive(null);

        CustomerWatchlistResponse updated =
                customerWatchlistService.updateWatchlist(
                        created.getWatchlistId(),
                        updateRequest
                );

        assertNotNull(updated.getDetectedAt());

        assertEquals(
                Boolean.TRUE,
                updated.getActive()
        );
    }

    @Test
    void updateWatchlistShouldThrowWhenWatchlistDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService.updateWatchlist(
                        UUID.randomUUID(),
                        buildWatchlistRequest()
                )
        );
    }

    @Test
    void deleteWatchlistShouldSoftDeleteAndDeactivateExistingWatchlist() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        customerWatchlistService.deleteWatchlist(
                created.getWatchlistId()
        );

        CustomerWatchlist persisted =
                customerWatchlistRepository.findById(
                        created.getWatchlistId()
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
                customerWatchlistRepository.existsById(
                        created.getWatchlistId()
                )
        );
    }

    @Test
    void getWatchlistByIdShouldNotReturnSoftDeletedWatchlist() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        customerWatchlistService.deleteWatchlist(
                created.getWatchlistId()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService.getWatchlistById(
                        created.getWatchlistId()
                )
        );
    }

    @Test
    void getWatchlistsByCustomerIdShouldExcludeSoftDeletedWatchlists() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse active =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        CustomerWatchlistRequest deletedRequest =
                buildWatchlistRequest();

        deletedRequest.setWatchlistType("PEP");
        deletedRequest.setReferenceId("REF-DELETE-001");

        CustomerWatchlistResponse deleted =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        deletedRequest
                );

        customerWatchlistService.deleteWatchlist(
                deleted.getWatchlistId()
        );

        List<CustomerWatchlistResponse> watchlists =
                customerWatchlistService
                        .getWatchlistsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(1, watchlists.size());

        assertEquals(
                active.getWatchlistId(),
                watchlists.getFirst().getWatchlistId()
        );

        assertFalse(
                watchlists.stream()
                        .anyMatch(watchlist ->
                                deleted.getWatchlistId()
                                        .equals(
                                                watchlist.getWatchlistId()
                                        )
                        )
        );
    }

    @Test
    void repositoryShouldReturnOnlyActiveNonDeletedWatchlists() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistRequest activeRequest =
                buildWatchlistRequest();

        activeRequest.setActive(Boolean.TRUE);

        CustomerWatchlistResponse active =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        activeRequest
                );

        CustomerWatchlistRequest inactiveRequest =
                buildWatchlistRequest();

        inactiveRequest.setReferenceId(
                "REF-INACTIVE-001"
        );

        inactiveRequest.setActive(Boolean.FALSE);

        customerWatchlistService.createWatchlist(
                customer.getCustomerId(),
                inactiveRequest
        );

        List<CustomerWatchlist> activeWatchlists =
                customerWatchlistRepository
                        .findByCustomerIdAndActiveTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertEquals(
                1,
                activeWatchlists.size()
        );

        assertEquals(
                active.getWatchlistId(),
                activeWatchlists.getFirst()
                        .getWatchlistId()
        );
    }

    @Test
    void repositoryShouldExcludeSoftDeletedWatchlistFromActiveLookup() {

        CustomerResponse customer = createCustomer();

        CustomerWatchlistResponse created =
                customerWatchlistService.createWatchlist(
                        customer.getCustomerId(),
                        buildWatchlistRequest()
                );

        customerWatchlistService.deleteWatchlist(
                created.getWatchlistId()
        );

        List<CustomerWatchlist> activeWatchlists =
                customerWatchlistRepository
                        .findByCustomerIdAndActiveTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertTrue(
                activeWatchlists.isEmpty()
        );
    }

    @Test
    void deleteWatchlistShouldThrowWhenWatchlistDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerWatchlistService.deleteWatchlist(
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

    private CustomerWatchlistRequest buildWatchlistRequest() {

        CustomerWatchlistRequest request =
                new CustomerWatchlistRequest();

        request.setWatchlistType(
                "SANCTIONS"
        );

        request.setWatchlistSource(
                "EFS_TEST_SOURCE"
        );

        request.setMatchStatus(
                "POTENTIAL_MATCH"
        );

        request.setMatchScore(
                new BigDecimal("92.50")
        );

        request.setMatchedName(
                "Integration Test Customer"
        );

        request.setReferenceId(
                "REF-TEST-001"
        );

        request.setDetectedAt(
                LocalDateTime.of(
                        2026, 8, 29, 8, 0, 0
                )
        );

        request.setLastCheckedAt(
                LocalDateTime.of(
                        2026, 8, 29, 8, 15, 0
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