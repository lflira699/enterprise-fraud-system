package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBankAccountRequest;
import com.efs.modules.customer.dto.CustomerBankAccountResponse;
import com.efs.modules.customer.entity.CustomerBankAccount;
import com.efs.modules.customer.mapper.CustomerBankAccountMapper;
import com.efs.modules.customer.repository.CustomerBankAccountRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerBankAccountService
        implements CustomerBankAccountServiceInterface {

    private final CustomerBankAccountRepository customerBankAccountRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBankAccountMapper customerBankAccountMapper;

    public CustomerBankAccountService(
            CustomerBankAccountRepository customerBankAccountRepository,
            CustomerRepository customerRepository,
            CustomerBankAccountMapper customerBankAccountMapper) {

        this.customerBankAccountRepository = customerBankAccountRepository;
        this.customerRepository = customerRepository;
        this.customerBankAccountMapper = customerBankAccountMapper;
    }

    @Override
    @Transactional
    public CustomerBankAccountResponse createBankAccount(
            UUID customerId,
            CustomerBankAccountRequest request) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        CustomerBankAccount bankAccount =
                customerBankAccountMapper.toEntity(request);

        bankAccount.setCustomerId(customerId);

        if (bankAccount.getPrimary() == null) {
            bankAccount.setPrimary(Boolean.FALSE);
        }

        if (bankAccount.getVerified() == null) {
            bankAccount.setVerified(Boolean.FALSE);
        }

        LocalDateTime now = LocalDateTime.now();
        bankAccount.setCreatedAt(now);
        bankAccount.setUpdatedAt(now);

        CustomerBankAccount savedBankAccount =
                customerBankAccountRepository.save(bankAccount);

        return customerBankAccountMapper.toResponse(savedBankAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBankAccountResponse getBankAccountById(
            UUID customerBankAccountId) {

        CustomerBankAccount bankAccount =
                customerBankAccountRepository
                        .findByCustomerBankAccountIdAndDeletedAtIsNull(
                                customerBankAccountId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer bank account not found: "
                                                + customerBankAccountId
                                )
                        );

        return customerBankAccountMapper.toResponse(bankAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBankAccountResponse> getBankAccountsByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return customerBankAccountRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerBankAccountMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerBankAccountResponse updateBankAccount(
            UUID customerBankAccountId,
            CustomerBankAccountRequest request) {

        CustomerBankAccount bankAccount =
                customerBankAccountRepository
                        .findByCustomerBankAccountIdAndDeletedAtIsNull(
                                customerBankAccountId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer bank account not found: "
                                                + customerBankAccountId
                                )
                        );

        customerBankAccountMapper.updateEntity(request, bankAccount);

        if (bankAccount.getPrimary() == null) {
            bankAccount.setPrimary(Boolean.FALSE);
        }

        if (bankAccount.getVerified() == null) {
            bankAccount.setVerified(Boolean.FALSE);
        }

        bankAccount.setUpdatedAt(LocalDateTime.now());

        CustomerBankAccount savedBankAccount =
                customerBankAccountRepository.save(bankAccount);

        return customerBankAccountMapper.toResponse(savedBankAccount);
    }

    @Override
    @Transactional
    public void deleteBankAccount(
            UUID customerBankAccountId,
            UUID deletedBy) {

        CustomerBankAccount bankAccount =
                customerBankAccountRepository
                        .findByCustomerBankAccountIdAndDeletedAtIsNull(
                                customerBankAccountId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer bank account not found: "
                                                + customerBankAccountId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        bankAccount.setDeletedAt(now);
        bankAccount.setDeletedBy(deletedBy);
        bankAccount.setUpdatedAt(now);

        customerBankAccountRepository.save(bankAccount);
    }
}