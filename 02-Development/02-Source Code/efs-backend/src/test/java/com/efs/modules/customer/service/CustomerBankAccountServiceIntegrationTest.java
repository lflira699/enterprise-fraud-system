package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBankAccountRequest;
import com.efs.modules.customer.dto.CustomerBankAccountResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.CustomerBankAccount;
import com.efs.modules.customer.repository.CustomerBankAccountRepository;
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
class CustomerBankAccountServiceIntegrationTest {

    @Autowired
    private CustomerBankAccountServiceInterface customerBankAccountService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerBankAccountRepository customerBankAccountRepository;

    @Test
    void createBankAccountShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountRequest request =
                buildBankAccountRequest(uniqueAccountNumber());

        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();

        request.setPrimary(Boolean.TRUE);
        request.setVerified(Boolean.TRUE);
        request.setVerificationStatus("VERIFIED");
        request.setCreatedBy(createdBy);
        request.setUpdatedBy(updatedBy);

        CustomerBankAccountResponse response =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getCustomerBankAccountId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals("EFS Test Bank", response.getBankName());

        assertEquals(
                request.getAccountNumber(),
                response.getAccountNumber()
        );

        assertEquals(
                "CHECKING",
                response.getAccountType()
        );

        assertEquals(
                "GTQ",
                response.getCurrencyCode()
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
                Boolean.TRUE,
                response.getVerified()
        );

        assertEquals(
                "VERIFIED",
                response.getVerificationStatus()
        );

        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(updatedBy, response.getUpdatedBy());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertNull(response.getDeletedAt());
        assertNull(response.getDeletedBy());

        assertTrue(
                customerBankAccountRepository.existsById(
                        response.getCustomerBankAccountId()
                )
        );
    }

    @Test
    void createBankAccountShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountRequest request =
                buildBankAccountRequest(uniqueAccountNumber());

        request.setPrimary(null);
        request.setVerified(null);

        CustomerBankAccountResponse response =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getPrimary()
        );

        assertEquals(
                Boolean.FALSE,
                response.getVerified()
        );
    }

    @Test
    void createBankAccountShouldPreserveFalseValues() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountRequest request =
                buildBankAccountRequest(uniqueAccountNumber());

        request.setPrimary(Boolean.FALSE);
        request.setVerified(Boolean.FALSE);

        CustomerBankAccountResponse response =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getPrimary()
        );

        assertEquals(
                Boolean.FALSE,
                response.getVerified()
        );
    }

    @Test
    void createBankAccountShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService.createBankAccount(
                        UUID.randomUUID(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                )
        );
    }

    @Test
    void getBankAccountByIdShouldReturnExistingBankAccount() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        CustomerBankAccountResponse found =
                customerBankAccountService.getBankAccountById(
                        created.getCustomerBankAccountId()
                );

        assertEquals(
                created.getCustomerBankAccountId(),
                found.getCustomerBankAccountId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getAccountNumber(),
                found.getAccountNumber()
        );
    }

    @Test
    void getBankAccountByIdShouldThrowWhenBankAccountDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService.getBankAccountById(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getBankAccountsByCustomerIdShouldReturnCustomerBankAccounts() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse first =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        CustomerBankAccountRequest secondRequest =
                buildBankAccountRequest(
                        uniqueAccountNumber()
                );

        secondRequest.setBankName("Second Test Bank");
        secondRequest.setAccountType("SAVINGS");
        secondRequest.setCurrencyCode("USD");

        CustomerBankAccountResponse second =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        secondRequest
                );

        List<CustomerBankAccountResponse> accounts =
                customerBankAccountService
                        .getBankAccountsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, accounts.size());

        assertTrue(
                accounts.stream()
                        .anyMatch(account ->
                                first.getCustomerBankAccountId()
                                        .equals(
                                                account.getCustomerBankAccountId()
                                        )
                        )
        );

        assertTrue(
                accounts.stream()
                        .anyMatch(account ->
                                second.getCustomerBankAccountId()
                                        .equals(
                                                account.getCustomerBankAccountId()
                                        )
                        )
        );
    }

    @Test
    void getBankAccountsByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService
                        .getBankAccountsByCustomerId(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updateBankAccountShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        CustomerBankAccountRequest updateRequest =
                buildBankAccountRequest(
                        uniqueAccountNumber()
                );

        UUID updatedBy = UUID.randomUUID();

        updateRequest.setBankName("Updated Test Bank");
        updateRequest.setAccountType("SAVINGS");
        updateRequest.setCurrencyCode("USD");
        updateRequest.setCountryCode("USA");
        updateRequest.setPrimary(Boolean.TRUE);
        updateRequest.setVerified(Boolean.TRUE);
        updateRequest.setVerificationStatus("VERIFIED");
        updateRequest.setUpdatedBy(updatedBy);

        CustomerBankAccountResponse updated =
                customerBankAccountService.updateBankAccount(
                        created.getCustomerBankAccountId(),
                        updateRequest
                );

        assertEquals(
                created.getCustomerBankAccountId(),
                updated.getCustomerBankAccountId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                "Updated Test Bank",
                updated.getBankName()
        );

        assertEquals(
                updateRequest.getAccountNumber(),
                updated.getAccountNumber()
        );

        assertEquals(
                "SAVINGS",
                updated.getAccountType()
        );

        assertEquals(
                "USD",
                updated.getCurrencyCode()
        );

        assertEquals(
                "USA",
                updated.getCountryCode()
        );

        assertEquals(
                Boolean.TRUE,
                updated.getPrimary()
        );

        assertEquals(
                Boolean.TRUE,
                updated.getVerified()
        );

        assertEquals(
                "VERIFIED",
                updated.getVerificationStatus()
        );

        assertEquals(
                updatedBy,
                updated.getUpdatedBy()
        );

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateBankAccountShouldDefaultPrimaryAndVerifiedToFalseWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountRequest createRequest =
                buildBankAccountRequest(
                        uniqueAccountNumber()
                );

        createRequest.setPrimary(Boolean.TRUE);
        createRequest.setVerified(Boolean.TRUE);

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(
                Boolean.TRUE,
                created.getPrimary()
        );

        assertEquals(
                Boolean.TRUE,
                created.getVerified()
        );

        CustomerBankAccountRequest updateRequest =
                buildBankAccountRequest(
                        created.getAccountNumber()
                );

        updateRequest.setPrimary(null);
        updateRequest.setVerified(null);

        CustomerBankAccountResponse updated =
                customerBankAccountService.updateBankAccount(
                        created.getCustomerBankAccountId(),
                        updateRequest
                );

        assertEquals(
                Boolean.FALSE,
                updated.getPrimary()
        );

        assertEquals(
                Boolean.FALSE,
                updated.getVerified()
        );
    }

    @Test
    void updateBankAccountShouldThrowWhenBankAccountDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService.updateBankAccount(
                        UUID.randomUUID(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                )
        );
    }

    @Test
    void deleteBankAccountShouldSoftDeleteExistingBankAccount() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        UUID deletedBy = UUID.randomUUID();

        customerBankAccountService.deleteBankAccount(
                created.getCustomerBankAccountId(),
                deletedBy
        );

        CustomerBankAccount persisted =
                customerBankAccountRepository.findById(
                        created.getCustomerBankAccountId()
                ).orElseThrow();

        assertNotNull(persisted.getDeletedAt());

        assertEquals(
                deletedBy,
                persisted.getDeletedBy()
        );

        assertNotNull(persisted.getUpdatedAt());

        assertTrue(
                customerBankAccountRepository.existsById(
                        created.getCustomerBankAccountId()
                )
        );
    }

    @Test
    void getBankAccountByIdShouldNotReturnSoftDeletedBankAccount() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        customerBankAccountService.deleteBankAccount(
                created.getCustomerBankAccountId(),
                UUID.randomUUID()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService.getBankAccountById(
                        created.getCustomerBankAccountId()
                )
        );
    }

    @Test
    void getBankAccountsByCustomerIdShouldExcludeSoftDeletedAccounts() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountResponse active =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        CustomerBankAccountResponse deleted =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                uniqueAccountNumber()
                        )
                );

        customerBankAccountService.deleteBankAccount(
                deleted.getCustomerBankAccountId(),
                UUID.randomUUID()
        );

        List<CustomerBankAccountResponse> accounts =
                customerBankAccountService
                        .getBankAccountsByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(1, accounts.size());

        assertEquals(
                active.getCustomerBankAccountId(),
                accounts.getFirst()
                        .getCustomerBankAccountId()
        );

        assertFalse(
                accounts.stream()
                        .anyMatch(account ->
                                deleted.getCustomerBankAccountId()
                                        .equals(
                                                account.getCustomerBankAccountId()
                                        )
                        )
        );
    }

    @Test
    void repositoryShouldReturnOnlyPrimaryNonDeletedBankAccounts() {

        CustomerResponse customer = createCustomer();

        CustomerBankAccountRequest primaryRequest =
                buildBankAccountRequest(
                        uniqueAccountNumber()
                );

        primaryRequest.setPrimary(Boolean.TRUE);

        CustomerBankAccountResponse primary =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        primaryRequest
                );

        CustomerBankAccountRequest secondaryRequest =
                buildBankAccountRequest(
                        uniqueAccountNumber()
                );

        secondaryRequest.setPrimary(Boolean.FALSE);

        customerBankAccountService.createBankAccount(
                customer.getCustomerId(),
                secondaryRequest
        );

        List<CustomerBankAccount> primaryAccounts =
                customerBankAccountRepository
                        .findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
                                customer.getCustomerId()
                        );

        assertEquals(1, primaryAccounts.size());

        assertEquals(
                primary.getCustomerBankAccountId(),
                primaryAccounts.getFirst()
                        .getCustomerBankAccountId()
        );
    }

    @Test
    void repositoryShouldFindBankAccountByCustomerAndAccountNumber() {

        CustomerResponse customer = createCustomer();

        String accountNumber =
                uniqueAccountNumber();

        CustomerBankAccountResponse created =
                customerBankAccountService.createBankAccount(
                        customer.getCustomerId(),
                        buildBankAccountRequest(
                                accountNumber
                        )
                );

        CustomerBankAccount found =
                customerBankAccountRepository
                        .findByCustomerIdAndAccountNumberAndDeletedAtIsNull(
                                customer.getCustomerId(),
                                accountNumber
                        )
                        .orElseThrow();

        assertEquals(
                created.getCustomerBankAccountId(),
                found.getCustomerBankAccountId()
        );
    }

    @Test
    void deleteBankAccountShouldThrowWhenBankAccountDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerBankAccountService.deleteBankAccount(
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

    private CustomerBankAccountRequest buildBankAccountRequest(
            String accountNumber) {

        CustomerBankAccountRequest request =
                new CustomerBankAccountRequest();

        request.setBankName("EFS Test Bank");
        request.setAccountNumber(accountNumber);
        request.setAccountType("CHECKING");
        request.setCurrencyCode("GTQ");
        request.setCountryCode("GTM");

        request.setPrimary(Boolean.FALSE);
        request.setVerified(Boolean.FALSE);
        request.setVerificationStatus("PENDING");

        return request;
    }

    private String uniqueAccountNumber() {

        return "ACC-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16);
    }
}